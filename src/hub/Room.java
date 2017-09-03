package hub;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Objects;

/**
 *
 * @author Jordan
 */
public class Room {

    private final int max;
    private final String name;
    private final ArrayList<User> members;
    private final int dtLength;
    private boolean running;
    private DatagramSocket udpSocket;
    private String password;

    public Room(String name, int max, int dtLength, String password) {
        this.name = name;
        this.max = max;
        this.dtLength = dtLength;
        try {
            udpSocket = new DatagramSocket();
        } catch (SocketException ex) {
            udpSocket = null;
        }
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
        for (User member : members) {
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
        String res = name + ":" + max + ":" + udpSocket.getPort() + ":" + (password != null);
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

    public void receiveUDP() {
        running = true;
        new Thread(() -> {
            while(running) {
                DatagramPacket dtp = new DatagramPacket(new byte[dtLength], dtLength);
                try {
                    udpSocket.receive(dtp);
                    for (User member : members) {
                        udpSocket.send(new DatagramPacket(dtp.getData(), dtLength, member.getSocket().getInetAddress(), member.getUdpPort()));
                    }
                } catch (IOException ex) {
                }
            }
        }).start();
    }
    
    public void stop() {
        running = false;
    }    
}
