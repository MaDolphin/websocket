package websocket;

import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

public class TestApp {

  protected static String loginURL = "http://localhost:8080/montigem-be/api/auth/login";
  protected static String bodyText = "{\"username\":\"admin\",\"password\":\"passwort\",\"resource\":\"TestDB\"}";

  public static void main(String[] args) {

    Util util = new Util();
    JSONObject obj = util.getToken(loginURL,bodyText);
    String jwt = obj.get("jwt").toString();
    String websocketURL = "ws://localhost:8080/montigem-be/websocket/"+jwt+"/Sensor/All";

    try {
      // open websocket
      final WebsocketClientEndpoint clientEndPoint = new WebsocketClientEndpoint(new URI(websocketURL));

      // add listener
      clientEndPoint.addMessageHandler(new WebsocketClientEndpoint.MessageHandler() {
        public void handleMessage(String message) {
          System.out.println(message);
        }
      });

      // send message to websocket
      clientEndPoint.sendMessage("{'event':'addChannel','channel':'ok_btccny_ticker'}");

      // wait 5 seconds for messages from websocket
      Thread.sleep(5000);

    } catch (InterruptedException ex) {
      System.err.println("InterruptedException exception: " + ex.getMessage());
    } catch (URISyntaxException ex) {
      System.err.println("URISyntaxException exception: " + ex.getMessage());
    }





  }
}
