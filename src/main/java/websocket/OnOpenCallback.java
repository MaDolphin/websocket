package websocket;

@FunctionalInterface
public interface OnOpenCallback<S> {
    void onOpen(S session);
}
