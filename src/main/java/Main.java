import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;

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

      String originName = "";
      String destinationName = "";
      String carrier = "";
      String departureDateTime = "";
      String arrivalDateTime = "";
      // создадим массив с ценами для расчета средней и медианы
      ArrayList<Long> arrPrices = new ArrayList<Long>();

      HashMap<String, Long> hashMap = new HashMap<>();

      for (Object ticketObj : tickets) {

        JSONObject ticket = (JSONObject) ticketObj;

        originName = (String) ticket.get("origin_name");
        destinationName = (String) ticket.get("destination_name");
        carrier = (String) ticket.get("carrier");
        departureDateTime = (String) ticket.get("departure_date") + " " + (String) ticket.get("departure_time");
        arrivalDateTime = (String) ticket.get("arrival_date") + " " + (String) ticket.get("arrival_time");

        if (originName.equals("Владивосток") && destinationName.equals("Тель-Авив")) {

          arrPrices.add((Long)ticket.get("price"));
          
          if (!hashMap.containsKey(carrier)) {
            hashMap.put(carrier, Long.MAX_VALUE);
          }

          Date departureDate = DATE_FORMAT.parse(departureDateTime);
          Date arrivalDate = DATE_FORMAT.parse(arrivalDateTime);
          long timeOfFlight = arrivalDate.getTime() - departureDate.getTime();

          if (timeOfFlight < hashMap.get(carrier)) {
            hashMap.put(carrier, timeOfFlight);
          }
        }
      }

      // время получили в милисекундах,
      // надо пересчитать в часы и минуты.
      hashMap.forEach((key, value) -> {
        long hours = value / (1000 * 60 * 60);
        long minutes = (value % (1000 * 60 * 60)) / (1000 * 60);
        System.out.println("Carrier: " + key + ", Time: " + hours + " hrs " + minutes + " min");
      });


      int size = arrPrices.size();
      //посчитаем среднее
      long valueAverage = 0l;
      if (size != 0)
      {
        for (Long price : arrPrices)
        {
            valueAverage += price;
        }
      }
      valueAverage = valueAverage / (long) size;

      //посчитаем медиану
      Collections.sort(arrPrices);
      
      Long median = 0l;
      if (size % 2 == 0) {
          Long middle1 = arrPrices.get(size / 2 - 1);
          Long middle2 = arrPrices.get(size / 2);
          median = (middle1 + middle2) / 2;
      } else {
          median = arrPrices.get((size - 1) / 2);
      }
      Long difference = valueAverage - median;

      System.out.println("Difference between avg and median prices (VVO-TLV): " + difference);
   
      reader.close();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}