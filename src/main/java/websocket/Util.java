package websocket;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class Util {

  public JSONObject getToken(String URL, String bodyText) {
    String obj = bodyText;
    try {
      String postUrl = URL;
      HttpClient httpClient = HttpClientBuilder.create().build();
      HttpPost post = new HttpPost(postUrl);
      StringEntity postingString = new StringEntity(obj, ContentType.APPLICATION_JSON);
      post.setEntity(postingString);
      post.addHeader("Content-type", "application/json");
      HttpResponse response = httpClient.execute(post);
      HttpEntity entity = response.getEntity();
      String responseString = EntityUtils.toString(entity, "UTF-8");
      JSONObject jsonObject = new JSONObject(responseString);
      System.out.println(jsonObject.get("jwt"));
      return jsonObject;
    } catch (Exception e) {
      System.out.println(e);
    }
    return null;
  }
}
