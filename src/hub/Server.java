package hub;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;

/**
 *
 * @author Jordan
 */
public class Server {

    private ServerSocket socket;
    private boolean running;
    private HashMap<String, Hub> hubs;
    private int timeout;

    public Server(int port, int timeout) throws IOException {
        this.timeout = timeout;
        socket = new ServerSocket(port);
        running = false;
        hubs = new HashMap<>();
    }

    public void start() {
        running = true;
        new Thread(() -> {
            while (running) {
                acceptUser();
                clearEmptyHubs();
            }
        }).start();
    }

    public boolean stop() {
        boolean res = running;
        running = false;
        return res;
    }

    public boolean isRunning() {
        return running;
    }

    private void acceptUser() {
        try {
            User u = new User(socket.accept(), timeout);
            if (hubs.containsKey(u.getApp())) {
                Hub hub = hubs.get(u.getApp());
                if (u.getPseudo().length() > 15 || u.getPseudo().length() < 3
                        || !hub.isValid(u.getPseudo()) || hub.getUsers().contains(u)) {
                    u.getSocket().close();
                } else {
                    u.start(hub);
                    hub.getUsers().add(u);
                }
            } else {
                Hub hub = new Hub();
                hub.getUsers().add(u);
                u.start(hub);
                hubs.put(u.getApp(), hub);
            }
        } catch (IOException ex) {
        }
    }

    private void clearEmptyHubs() {
        for (String app : hubs.keySet()) {
            if (hubs.get(app).isEmpty()) {
                hubs.remove(app);
            }
        }
    }
}
