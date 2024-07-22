package me.redstoner2019;

import org.java_websocket.server.WebSocketServer;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.InetSocketAddress;

public class WebServer extends WebSocketServer {

    public WebServer(InetSocketAddress address) {
        super(address);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("New connection: " + handshake.getResourceDescriptor());
        conn.send("Welcome to the server!"); // Send a message to the client
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("Closed connection: " + reason);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        JSONObject packet = new JSONObject(message);

        JSONObject response = getErrorJSON("Not a valid request.");

        try{
            if(packet.has("header")){
                response = getErrorJSON("'" + packet.getString("header") + "' not implemented.");

                switch (packet.getString("header")) {
                    case "add-entry" -> {
                        if(packet.has("game")){
                            if(packet.has("challenge")){
                                if(packet.has("data")){
                                    StatisticServer.addEntryToChallenge(packet.getString("game"),packet.getString("challenge"),packet.getJSONObject("data"));
                                    response = getSuccessJSON();
                                } else {
                                    response = getErrorJSON("'data' is missing.");
                                }
                            } else {
                                response = getErrorJSON("'challenge' is missing.");
                            }
                        } else {
                            response = getErrorJSON("'game' is missing.");
                        }
                    }
                    case "create-challenge" -> {
                        if(packet.has("game")){
                            if(packet.has("challenge")){
                                if(packet.has("configuration")){
                                    StatisticServer.createChallenge(packet.getString("game"),packet.getString("challenge"), packet.getJSONObject("configuration"));
                                } else {
                                    response = getErrorJSON("'configuration' is missing.");
                                }
                            } else {
                                response = getErrorJSON("'challenge' is missing.");
                            }
                        } else {
                            response = getErrorJSON("'game' is missing.");
                        }
                    }
                    case "create-game" -> {
                        if(packet.has("game")){
                            StatisticServer.createGame(packet.getString("game"));
                            response = getSuccessJSON();
                        } else {
                            response = getErrorJSON("'game' is missing.");
                        }
                    }
                    case "get-entry" -> {
                        if(packet.has("uuid")){
                            response = StatisticServer.getEntry(packet.getString("uuid"));
                            if(response == null) response = getErrorJSON("Could'nt find entry '" + packet.getString("uuid") + "'.");
                        } else {
                            response = getErrorJSON("'uuid' is missing.");
                        }
                    }
                    case "get-challenges" -> {
                        if(packet.has("game")){
                            response = getSuccessJSON();
                            response.put("challenges",StatisticServer.getChallenges(packet.getString("game")));
                        } else {
                            response = getErrorJSON("'game' is missing.");
                        }
                    }
                    case "get-games" -> {
                        response = getSuccessJSON();
                        response.put("games",StatisticServer.getGames());
                    }
                    case "get-challenge" -> {
                        if(packet.has("game")){
                            if(packet.has("challenge")){
                                StatisticServer.createChallenge(packet.getString("game"),packet.getString("challenge"),new JSONObject());
                                JSONArray entries = StatisticServer.getChallengeEntries(packet.getString("game"),packet.getString("challenge"));
                                response = new JSONObject();
                                response.put("entries",entries);
                                response.put("configuration",StatisticServer.getChallengeConfiguration(packet.getString("game"),packet.getString("challenge")));
                            } else {
                                response = getErrorJSON("'challenge' is missing.");
                            }
                        } else {
                            response = getErrorJSON("'game' is missing.");
                        }
                    }
                }
            }
        } catch (Exception e ){

        }

        conn.send(response.toString()); // Echo the message back to the client
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
    }

    @Override
    public void onStart() {
        System.out.println("Server started!");
    }

    public static void main(String[] args) {
        WebSocketServer server = new WebServer(new InetSocketAddress("localhost", 8887));
        server.start();
        System.out.println("WebSocket server started on port: " + server.getPort());
    }

    public JSONObject getErrorJSON(String error){
        return new JSONObject("{header : \"error\", message : \"" + error +"\"}");
    }
    public JSONObject getSuccessJSON(){
        return new JSONObject("{header : \"success\"}");
    }
}

