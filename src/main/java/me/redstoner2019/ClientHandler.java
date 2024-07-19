package me.redstoner2019;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler {
    private Socket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        try {
            this.ois = new ObjectInputStream(socket.getInputStream());
            this.oos = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                boolean running = true;
                while (running){
                    try{
                        JSONObject packet = new JSONObject((String) ois.readObject());

                        JSONObject response = getErrorJSON("Not a valid request.");

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
                                            StatisticServer.createChallenge(packet.getString("game"),packet.getString("challenge"));
                                            response = getSuccessJSON();
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
                                case "get-challenge" -> {
                                    if(packet.has("game")){
                                        if(packet.has("challenge")){
                                            StatisticServer.createChallenge(packet.getString("game"),packet.getString("challenge"));
                                            JSONArray entries = StatisticServer.getChallengeEntries(packet.getString("game"),packet.getString("challenge"));
                                            response = new JSONObject();
                                            response.put("entries",entries);
                                        } else {
                                            response = getErrorJSON("'challenge' is missing.");
                                        }
                                    } else {
                                        response = getErrorJSON("'game' is missing.");
                                    }
                                }
                            }
                        }
                        oos.writeObject(response.toString());
                    }catch (Exception e){
                        running = false;
                    }
                }
            }
        });
        t.start();
    }

    public JSONObject getErrorJSON(String error){
        return new JSONObject("{header : \"error\", message : \"" + error +"\"}");
    }
    public JSONObject getSuccessJSON(){
        return new JSONObject("{header : \"success\"}");
    }
}
