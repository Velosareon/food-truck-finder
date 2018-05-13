import org.json.JSONArray;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

/**
 * Program displays all the San Fransisco food trucks open at the time its run.
 * Rest Request url - https://data.sfgov.org/Economy-and-Community/Mobile-Food-Schedule/jjew-r69b
 */
public class FoodTruckFinder
{
    private static DateTimeFormatter hourandminuteFormatter = DateTimeFormatter.ofPattern("HH:mm");

    public static void main(String[] args)
    {
        getOpenFoodTrucks();
    }

    private static void getOpenFoodTrucks()
    {
        try
        {
            URL url = new URL("https://data.sfgov.org/resource/bbb8-hzi6");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

            StringBuilder sb = new StringBuilder();
            String output;
            while ((output = br.readLine()) != null)
            {
                sb.append(output);
            }

            conn.disconnect();

            printOpenFoodTrucks(new JSONArray(sb.toString()));
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }

    private static void printOpenFoodTrucks(JSONArray jsonArrayAllTrucks)
    {
        for(int i=0; i<jsonArrayAllTrucks.length()-1; i++)
        {
            boolean openNow = CheckOpenNow(
                    LocalTime.parse(jsonArrayAllTrucks.getJSONObject(i).get("start24").toString(), hourandminuteFormatter),
                    LocalTime.parse(jsonArrayAllTrucks.getJSONObject(i).get("end24").toString(), hourandminuteFormatter),
                    jsonArrayAllTrucks.getJSONObject(i).get("dayofweekstr").toString());

            if(openNow)
            {
                System.out.println(jsonArrayAllTrucks.getJSONObject(i).get("applicant").toString());
            }
        }
    }

    private static boolean CheckOpenNow(LocalTime startTime, LocalTime endTime, String dayOfWeek)
    {
        LocalTime currentTime = LocalTime.parse(LocalTime.now().format(hourandminuteFormatter), hourandminuteFormatter);
        String todaysDate = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(Calendar.getInstance().getTime());

        return (currentTime.isAfter(startTime) && currentTime.isBefore(endTime) && todaysDate.equals(dayOfWeek));
    }
}