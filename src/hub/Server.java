package hub;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;

/**
 *
 * @author Jordan
 */
public class Server {

    private final ServerSocket socket;
    private final HashMap<String, Hub> hubs;
    private final int timeout;

    public Server(int port, int timeout) throws IOException {
        this.timeout = timeout;
        socket = new ServerSocket(port);
        hubs = new HashMap<>();
    }

    public void start() {
        while (true) {
            acceptUser();
            clearEmptyHubs();
        }
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
            ex.printStackTrace();
        }
    }

    private void clearEmptyHubs() {
//        Iterator<String> i = hubs.keySet().iterator();
//        while(i.hasNext()) {
//            String app = i.next();
//            if (hubs.get(app).isEmpty()) {
//                i.remove();
//            }
//        }
    }
}
