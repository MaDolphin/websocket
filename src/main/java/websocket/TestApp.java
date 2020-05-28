package websocket;

import org.json.JSONObject;
import java.net.URI;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import static org.junit.Assert.fail;

/*
Webclient to get data updates from backend/send data to backend
If messageHandler == Add then the client is registered to send data to the backend (usage is irrelevant)
If messageHandler == Sensor then the client is registered to receive data updates from backend (usage specifies which type of sensor data the client will be updated about)
Example JSON to send data to backend:

{ "data": [{"sensorId":"22", "type":"ANGLE", "value":88, "timeStamp":"2020-01-01 08:00:00"},{"sensorId":"22", "type":"ANGLE", "value":89, "timeStamp":"2020-01-01 08:00:00"},{"sensorId":"1", "type":"CO2", "value":90, "timeStamp":"2020-01-01 08:00:00"},{"sensorId":"1", "type":"CO2", "value":91, "timeStamp":"2020-01-01 08:00:00"},{"sensorId":"1", "type":"CO2", "value":92, "timeStamp":"2020-01-01 08:00:00"},{"sensorId":"1", "type":"CO2", "value":93, "timeStamp":"2020-01-01 08:00:00"}]}

*/

public class TestApp {

  protected static String loginURL = "http://localhost:8080/montigem-be/api/auth/login";
  protected static String bodyText = "{\"username\":\"admin\",\"password\":\"passwort\",\"resource\":\"TestDB\"}";

  public static void main(String[] args) throws Exception {
    Util util = new Util();
    JSONObject obj = util.getToken(loginURL,bodyText);
    String jwt = obj.get("jwt").toString();
    MessageHandlerType messageHandlerType = MessageHandlerType.Add;
    String usage = "ANGLE";
    String websocketURL = "ws://localhost:8080/montigem-be/websocket/"+jwt+"/"+messageHandlerType.toString()+"/"+usage;

    CountDownLatch latch = new CountDownLatch(2);
    WebSocketClientEndpoint ws = WebSocketClientEndpoint.connect(new URI(websocketURL), session -> {

    }, msg -> {
      switch(messageHandlerType){
        case Sensor:
          if (msg.contains("\"entries\":[]")) {
            System.out.print("No recent updates - Last checked: " + msg.substring(msg.indexOf("timestamp\":\"") + 12, msg.indexOf("\",\"typeName")) + "\r");
          } else {
            System.out.println(msg);
          }
        case Add:
        case None:

        default:
          System.out.println(msg);
          break;
      }
      latch.countDown();
      return msg;
    });

    if(messageHandlerType == MessageHandlerType.Add) {
      System.out.println("Send client");
      System.out.println("To send values to backend press any key");

      while(true) {
        System.out.println("Enter JSONArray of values as JSONObject in one line: \n");
        Scanner in = new Scanner(System.in);
        String input = in.nextLine();
        ws.sendMessage(input);
        Thread.sleep(1000);
      }
    }
    System.out.println("Update client");
    Thread.sleep(1000);
    if (!latch.await(100, TimeUnit.SECONDS)) {
      fail("should get message in time");
    }

  }
}


