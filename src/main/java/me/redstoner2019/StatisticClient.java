package me.redstoner2019;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class StatisticClient {
    private Socket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private List<JSONObject> requests = new ArrayList<>();
    private JSONObject response = new JSONObject();

    public StatisticClient(String ip, int port) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    socket = new Socket(ip,port);
                    oos = new ObjectOutputStream(socket.getOutputStream());
                    ois = new ObjectInputStream(socket.getInputStream());
                    boolean running = true;

                    while (running){
                        try{
                            if(requests.size() > 0){
                                JSONObject toSend = requests.get(0);
                                oos.writeObject(toSend.toString());
                                JSONObject responseObject = new JSONObject((String) ois.readObject());
                                response = responseObject;
                                synchronized (toSend) {
                                    toSend.notifyAll();
                                }
                                synchronized (response){
                                    response.wait();
                                }
                                requests.remove(0);
                            }
                        }catch (Exception e){
                            running = false;
                            System.out.println(e.getLocalizedMessage());
                        }
                    }
                }catch (Exception e){

                }
            }
        });
        t.start();
    }
    public JSONObject sendRequest(JSONObject request) throws InterruptedException {
        requests.add(request);
        synchronized (request){
            request.wait();
        }
        JSONObject localResponse = new JSONObject(response.toString());
        synchronized (response) {
            response.notifyAll();
        }
        return localResponse;
    }

    public JSONObject createEntry(String game, String challenge, long time, String username, float powerLeft, long timeLasted) throws Exception {
        JSONObject header = new JSONObject();
        JSONObject entry = new JSONObject();

        entry.put("time",time);
        entry.put("username",username);
        entry.put("power-left",powerLeft);
        entry.put("time-lasted",timeLasted);

        header.put("data",entry);
        header.put("game",game);
        header.put("challenge",challenge);
        header.put("header","add-entry");
        return sendRequest(header);
    }

    public JSONObject createEntry(String game, String challenge, JSONObject data) throws Exception {
        JSONObject header = new JSONObject();

        header.put("data",data);
        header.put("game",game);
        header.put("challenge",challenge);
        header.put("header","add-entry");
        return sendRequest(header);
    }

    public JSONObject createChallenge(String game, String challenge, JSONObject configuration) throws Exception{
        JSONObject packet = new JSONObject();
        packet.put("header","create-challenge");
        packet.put("game",game);
        packet.put("challenge",challenge);
        packet.put("configuration",configuration);
        return sendRequest(packet);
    }

    public JSONObject createGame(String game) throws Exception{
        JSONObject packet = new JSONObject();
        packet.put("header","create-game");
        packet.put("game",game);
        return sendRequest(packet);
    }
    public JSONObject getEntry(String uuid) throws Exception{
        JSONObject packet = new JSONObject();
        packet.put("header","get-entry");
        packet.put("uuid",uuid);
        return sendRequest(packet);
    }
    public JSONObject getEntries(String game, String challenge) throws Exception {
        JSONObject packet = new JSONObject();
        packet.put("header", "get-challenge");
        packet.put("game", game);
        packet.put("challenge", challenge);
        return sendRequest(packet);
    }
    public JSONArray getGames() throws Exception {
        JSONObject packet = new JSONObject();
        packet.put("header", "get-games");
        return sendRequest(packet).getJSONArray("games");
    }
    public JSONArray getChallenges(String game) throws Exception {
        JSONObject packet = new JSONObject();
        packet.put("header", "get-challenges");
        packet.put("game",game);
        return sendRequest(packet).getJSONArray("challenges");
    }
}
