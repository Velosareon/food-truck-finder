import org.json.JSONArray;

import java.net.MalformedURLException;
import java.text.MessageFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * Program displays all the San Fransisco food trucks open at the time its run.
 * Rest Request url - https://data.sfgov.org/Economy-and-Community/Mobile-Food-Schedule/jjew-r69b
 */
public class FoodTruckFinder
{
    private static String baseURL = "https://data.sfgov.org/resource/bbb8-hzi6.json";
    private static DateTimeFormatter hourAndMinuteFormatter = DateTimeFormatter.ofPattern("HH:mm");

    public static void main(String[] args)
    {
        Scanner scan = new Scanner(System.in);
        int itemOffset = 0;

        JSONArray currentTruckPage;
        do
        {
            currentTruckPage = getOpenFoodTrucks(getURL(itemOffset));

            for(int i=0; i<currentTruckPage.length(); i++)
            {
                System.out.printf("%s\n%s\n",
                        "NAME: " + currentTruckPage.getJSONObject(i).get("applicant").toString(),
                        "  ADDRESS: " + currentTruckPage.getJSONObject(i).get("location").toString());
            }

            if(currentTruckPage.length() == 0)
            {
                System.out.println("No more food trucks to display.");
            }
            else
            {
                System.out.print("prompt: Next Page[n]: Quit[q]: ");
                if(scan.next().charAt(0) == 'q') {break;}
                itemOffset = itemOffset + 10;
            }
        }
        while(currentTruckPage.length() != 0);
    }

    private static URL getURL(int itemOffset)
    {
        LocalTime currentTime = LocalTime.parse(LocalTime.now().format(hourAndMinuteFormatter), hourAndMinuteFormatter);

        int itemsPerPage = 10;
        int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)-1;

        String route =
                MessageFormat.format(
                        "{0}?$limit={1}&" + "$offset={2}&dayorder=" + "{3}&$order=applicant&" +
                                "$where=start24<=''{4}''AND%20end24>=''{4}''",
                        baseURL, itemsPerPage, itemOffset, day, currentTime, currentTime);
        try
        {
            return new URL(route);
        }
        catch(MalformedURLException e)
        {
            System.out.println(e);
            return null;
        }
    }

    private static JSONArray getOpenFoodTrucks(URL url)
    {
        try
        {
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

            return new JSONArray(sb.toString());
        }
        catch(Exception e)
        {
            System.out.println(e);
            return null;
        }
    }
}