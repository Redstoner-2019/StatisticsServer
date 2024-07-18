import me.redstoner2019.StatisticClient;
import org.json.JSONObject;

public class Test {
    public static void main(String[] args) throws Exception {
        StatisticClient client = new StatisticClient("localhost",8000);
        System.out.println(client.createGame("fnaf-3"));
        System.out.println(client.createChallenge("fnaf-3","endless-mode"));
        System.out.println(client.createEntry("fnaf-3","endless-mode",System.currentTimeMillis(),"halulzen",2.5f,60000L));

        JSONObject entries = client.getEntries("fnaf-3","endless-mode");
        System.out.println(entries);
        for (int i = 0; i < entries.getJSONArray("entries").length(); i++) {
            System.out.println(client.getEntry(entries.getJSONArray("entries").getString(i)));
        }
    }
}
