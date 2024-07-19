import me.redstoner2019.StatisticClient;
import me.redstoner2019.StatisticServer;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.net.URLConnection;

public class Test {
    public static void main(String[] args) throws Exception {
        /*StatisticClient client = new StatisticClient("localhost",8000);
        System.out.println(client.createGame("fnaf-3"));
        System.out.println(client.createChallenge("fnaf-3","endless-mode"));
        System.out.println(client.createEntry("fnaf-3","endless-mode",System.currentTimeMillis(),"halulzen",2.5f,60000L));

        JSONObject entries = client.getEntries("fnaf-3","endless-mode");
        System.out.println(entries);
        for (int i = 0; i < entries.getJSONArray("entries").length(); i++) {
            System.out.println(client.getEntry(entries.getJSONArray("entries").getString(i)));
        }*/

        /*URL url = new URL("https://redstoner-2019.github.io/serverdata.html");
        URLConnection connection = url.openConnection();
        connection.connect();
        System.out.println(new String(connection.getInputStream().readAllBytes()));*/

        /*JSONObject data = new JSONObject("{ \"statistics-server\" : \"localhost\", \"auth-server\" : \"localhost\", \"statistics-server-port\" : 8000, \"auth-server-port\" : 8009 }");

        JSONObject supportedRepos = new JSONObject();
        JSONArray repos = new JSONArray();
        repos.put("ODServerAPI");
        repos.put("FNaF");
        supportedRepos.put("Redstoner-2019",repos);
        repos = new JSONArray();
        repos.put("toetaktic");
        supportedRepos.put("HaLuLzEn",repos);

        JSONObject fullData = new JSONObject();
        fullData.put("repos",supportedRepos);
        fullData.put("ips",data);

        System.out.println(StatisticServer.prettyJSON(fullData.toString()));*/
    }
}
