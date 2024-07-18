package me.redstoner2019;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.UUID;

public class Server {
    public static int PORT = 8000;
    public static void main(String[] args) throws IOException {
        if(!new File("statsServer").exists()){
            new File("statsServer").mkdirs();
            createChallenge("fnaf-1","endless-mode");
        }
        ServerSocket serverSocket = new ServerSocket(PORT);

        while (serverSocket.isBound()){
            Socket socket = serverSocket.accept();
            ClientHandler handler = new ClientHandler(socket);
        }
    }

    public static void addEntryToChallenge(String game, String challenge, JSONObject entry) throws IOException {
        createChallenge(game,challenge);
        String entryUUID = createEntry(game,challenge,entry);

        File file = new File("statsServer/" + game + "/" + challenge + ".json");

        JSONObject challengeData = new JSONObject(readFile(file));
        JSONArray entries = challengeData.getJSONArray("entries");
        entries.put(entryUUID);
        challengeData.put("entries",entries);

        writeStringToFile(challengeData,file);
    }

    public static JSONArray getChallengeEntries(String game, String challenge) throws IOException {
        createChallenge(game,challenge);
        File file = new File("statsServer/" + game + "/" + challenge + ".json");
        return new JSONObject(readFile(file)).getJSONArray("entries");
    }

    public static JSONObject getEntry(String entryUUID) throws IOException {
        File file = new File("statsServer/entries/" + entryUUID + ".json");
        if(file.exists()){
            return new JSONObject(readFile(file));
        } else {
            return null;
        }
    }

    public static void createGame(String game) {
        if(gameExists(game)) return;
        File file = new File("statsServer/" +game);
        file.mkdirs();
    }

    public static void createChallenge(String game, String challenge) throws IOException {
        if(!gameExists(game)) createGame(game);
        if(challengeExists(game, challenge)) return;
        File file = new File("statsServer/" + game + "/" + challenge + ".json");
        file.createNewFile();
        JSONObject object = new JSONObject();
        JSONArray entries = new JSONArray();
        object.put("entries",entries);
        writeStringToFile(object,file);
    }

    public static boolean gameExists(String game){
        File file = new File(game);
        return file.exists();
    }

    public static boolean challengeExists(String game, String challenge){
        File file = new File("statsServer/" + game + "/" + challenge + ".json");
        return file.exists();
    }

    public static String createEntry(String game, String challenge, JSONObject entry) throws IOException {
        String uuid = UUID.randomUUID().toString();
        JSONObject header = new JSONObject();

        header.put("data",entry);
        header.put("game",game);
        header.put("challenge",challenge);

        File entryFile = new File("statsServer/entries/" + uuid + ".json");
        if(!entryFile.exists()){
            entryFile.getParentFile().mkdirs();
            entryFile.createNewFile();
        }
        writeStringToFile(header,entryFile);
        return uuid;
    }

    public static String prettyJSON(String uglyJsonString) {
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            Object jsonObject = objectMapper.readValue(uglyJsonString, Object.class);
            String prettyJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);
            return prettyJson;
        }catch (Exception e){
            return null;
        }
    }
    public static void writeStringToFile(String str, File file) throws IOException {
        FileOutputStream outputStream = new FileOutputStream(file);
        byte[] strToBytes = str.getBytes();
        outputStream.write(strToBytes);

        outputStream.close();
    }
    public static void writeStringToFile(JSONObject str, File file) throws IOException {
        FileOutputStream outputStream = new FileOutputStream(file);
        byte[] strToBytes = prettyJSON(str.toString()).getBytes();
        outputStream.write(strToBytes);

        outputStream.close();
    }
    public static String readFile(File path) throws IOException {
        byte[] encoded = Files.readAllBytes(path.toPath());
        return new String(encoded, Charset.defaultCharset());
    }
}
