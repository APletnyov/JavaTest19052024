import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Main {
  private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yy HH:mm");

  public static void main(String[] args) {
    String filePath = "src/ext/tickets.json";

    try {
      // читаем файл
      FileReader reader = new FileReader(filePath);

      // парсим файл в специальный объект
      JSONParser parser = new JSONParser();
      JSONObject jsonObject = (JSONObject) parser.parse(reader);

      // заполним массив билетов и начнем его перебирать,
      // чтобы рассчитать минимальное время
      JSONArray tickets = (JSONArray) jsonObject.get("tickets");

      long minimalTime = Long.MAX_VALUE;
      String carrierWithMinimalTime = "";

      for (Object ticketObj : tickets) {
        JSONObject ticket = (JSONObject) ticketObj;
        String originName = (String) ticket.get("origin_name");
        String destinationName = (String) ticket.get("destination_name");
        String carrier = (String) ticket.get("carrier");
        String departureDateTime = (String) ticket.get("departure_date") + " " + (String) ticket.get("departure_time");
        String arrivalDateTime = (String) ticket.get("arrival_date") + " " + (String) ticket.get("arrival_time");

        if (originName.equals("Владивосток") && destinationName.equals("Тель-Авив")) {

          Date departureDate = DATE_FORMAT.parse(departureDateTime);
          Date arrivalDate = DATE_FORMAT.parse(arrivalDateTime);
          long timeOfFlight = arrivalDate.getTime() - departureDate.getTime();

          if (timeOfFlight < minimalTime) {
            minimalTime = timeOfFlight;
            carrierWithMinimalTime = carrier;
          }
        }
      }

      // время получили в милисекундах,
      // надо пересчитать в часы и минуты.
      long hours = minimalTime / (1000 * 60 * 60);
      long minutes = (minimalTime % (1000 * 60 * 60)) / (1000 * 60);

      System.out.println("Minimal time of flight: " + hours + " hrs " + minutes + " min");
      System.out.println("Carrier: " + carrierWithMinimalTime);

      reader.close();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}