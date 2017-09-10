package hub;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Jordan
 */
public class Hub {

    private final ArrayList<User> users;
    private final HashMap<User, Room> connectionsRoom;
    private final ArrayList<Room> rooms;

    public Hub() {
        users = new ArrayList<>();
        connectionsRoom = new HashMap<>();
        rooms = new ArrayList<>();
    }

    public void disconnect(User u) {
        if (connectionsRoom.containsKey(u)) {
            Room room = connectionsRoom.get(u);
            connectionsRoom.remove(u);
            room.getMembers().remove(u);
            room.send(("disconnectedUser::" + u));
            u.send("unconnected::");
            if (room.getMembers().isEmpty()) {
                rooms.remove(room);
            }
        }
    }

    public void send(User from, String msg) {
        if (connectionsRoom.containsKey(from)) {
            connectionsRoom.get(from).send("sent::" + from + "::" + msg);
        }
    }

    public String createRoom(User u, String msg) {
        String[] params = msg.split("::");
        if (!connectionsRoom.containsKey(u) && (params.length == 2 || params.length == 3)) {
            try {
                int max = Integer.parseInt(params[0]);
                String name = params[1];
                String password = (params.length == 3) ? params[2] : null;
                Room room = new Room(name, max, password);
                if (name != null && name.length() >= 3 && name.length() <= 15 && isValid(name) && !rooms.contains(room) && max > 0) {
                    rooms.add(room);
                    connectionsRoom.put(u, room);
                    room.addUser(u, password);
                }
            } catch (NumberFormatException e) {
            }
        }
        return connected(u);
    }

    public String connect(User u, String msg) {
        String[] params = msg.split("::");
        String name = params[0];
        String password = (params.length > 1) ? msg.replaceFirst(name + "::", "") : null;
        Room r = new Room(name, 1, password);
        if (!connectionsRoom.containsKey(u) && rooms.contains(r) && rooms.get(rooms.indexOf(r)).addUser(u, password)) {
            r = rooms.get(rooms.indexOf(r));
            r.send("connectedUser::" + u);
            connectionsRoom.put(u, r);
        }
        return connected(u);
    }

    public String rooms() {
        String res = "rooms";
        for (Room room : rooms) {
            res += "::" + room;
        }
        return (!res.equals("rooms")) ? res : "rooms::";
    }

    public String connected(User u) {
        if (connectionsRoom.containsKey(u)) {
            Room r = connectionsRoom.get(u);
            return "connected::" + r;
        }
        return "unconnected::";
    }

    public boolean isValid(String s) {
        return s.matches("^[a-zA-Z0-9-.]+$");
    }

    public ArrayList<User> getUsers() {
        return users;
    }
    
    public boolean isEmpty() {
        return users.isEmpty();
    }
}
