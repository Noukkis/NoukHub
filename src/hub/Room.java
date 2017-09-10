package hub;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 * @author Jordan
 */
public class Room {

    private final int max;
    private final String name;
    private final ArrayList<User> members;
    private String password;

    public Room(String name, int max, String password) {
        this.name = name;
        this.max = max;
        this.password = password;
        members = new ArrayList<>();
    }

    public boolean addUser(User u, String password) {
        if (members.size() < max && (this.password == null || this.password.equals(password))) {
            members.add(u);
            return true;
        }
        return false;
    }

    public void send(String msg) {
        for (User member : new ArrayList<>(members)) {
            member.send(msg);
        }
    }

    public ArrayList<User> getMembers() {
        return members;
    }

    public int getMax() {
        return max;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        String res = name + ":" + max + ":" + (password != null);
        for (User member : members) {
            res += ":" + member;
        }
        return res;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.name);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Room other = (Room) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return true;
    }    
}
