package websocket;

import java.net.URI;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.websocket.*;
import java.io.IOException;

import static org.junit.Assert.fail;

@ClientEndpoint
public class WebSocketClientEndpoint {
  private Session currentSession = null;
  private MessageHandler messageHandler;
  private OnOpenCallback<WebSocketClientEndpoint> onOpenCallback;

  public static WebSocketClientEndpoint connect(URI endpointURI, OnOpenCallback<WebSocketClientEndpoint> onOpenCallback, MessageHandler msgHandler) {
    WebSocketClientEndpoint endpoint = new WebSocketClientEndpoint();

    try {
      final WebSocketContainer container = ContainerProvider.getWebSocketContainer();

      System.out.println("creating websocket");
      endpoint.onOpenCallback = onOpenCallback;
      endpoint.messageHandler = msgHandler;
      endpoint.currentSession = container.connectToServer(endpoint, endpointURI);

      return endpoint;
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Callback hook for Connection open events.
   *
   * @param userSession the userSession which is opened.
   */
  @OnOpen
  public void onOpen(Session userSession) {
    System.out.println("opening websocket");
    this.currentSession = userSession;

    final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
    executor.schedule(() -> {
      this.onOpenCallback.onOpen(this);
    }, 500, TimeUnit.MILLISECONDS);
  }

  /**
   * Callback hook for Connection close events.
   *
   * @param userSession the userSession which is getting closed.
   * @param reason      the reason for connection close
   */
  @OnClose
  public void onClose(Session userSession, CloseReason reason) {
    System.out.println("closing websocket");
    this.currentSession = null;
  }

  /**
   * Callback hook for Message Events. This method will be invoked when a client send a message.
   *
   * @param message The text message
   */
  @OnMessage
  public void onMessage(String message) {
    if (this.messageHandler != null) {
      this.messageHandler.handleMessage(message);
    }
  }

  /**
   * Callback when any error occurs
   *
   * @param throwable
   */
  @OnError
  public void onError(Throwable throwable) {
    fail("onError in session: " + throwable);
  }

  /**
   * register message handler
   *
   * @param msgHandler
   */
  public void setMessageHandler(MessageHandler msgHandler) {
    this.messageHandler = msgHandler;
  }

  /**
   * Send a message.
   *
   * @param message
   */
  public void sendMessage(String message) {
    this.currentSession.getAsyncRemote().sendText(message);
  }

  /**
   * close the existing session
   *
   * @throws IOException
   */
  public void close() throws IOException {
    this.currentSession.close();
  }
}