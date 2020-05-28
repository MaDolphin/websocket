package websocket;

@FunctionalInterface
public interface MessageHandler {
    String handleMessage(String msg);
}
