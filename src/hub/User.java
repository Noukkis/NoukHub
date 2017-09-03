package hub;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Objects;

/**
 *
 * @author Jordan
 */
public class User {

    private final String app;
    private String pseudo;
    private final BufferedReader in;
    private final PrintWriter out;
    private boolean running;
    private final Socket socket;
    private int udpPort;

    public User(Socket socket, int timeout) throws IOException, NumberFormatException {
        this.socket = socket;
        running = false;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream());
        socket.setSoTimeout(timeout);
        String[] params = in.readLine().split("::");
        if (params.length == 3) {
            pseudo = params[0];
            app = params[1];
            udpPort = Integer.parseInt(params[2]);
        } else {
            socket.close();
            throw new IOException();
        }
        socket.setSoTimeout(0);
    }

    public Socket getSocket() {
        return socket;
    }

    public void start(Hub hub) {
        running = true;
        new Thread(() -> {
            while (running) {
                try {
                    proceed(hub, in.readLine());
                } catch (IOException ex) {
                    running = false;
                    hub.disconnect(this);
                    hub.getUsers().remove(this);
                    try {
                        socket.close();
                    } catch (IOException ex1) {
                    }
                }
            }
        }).start();
    }

    private void proceed(Hub hub, String line) {
        if (line != null) {
            if (line.contains("::")) {
                String command = line.split("::")[0];
                String msg = line.replaceFirst(command + "::", "");
                switch (command) {
                    case "disconnect":
                        hub.disconnect(this);
                        break;
                    case "rooms":
                        send(hub.rooms());
                        break;
                    case "connect":
                        send(hub.connect(this, msg));
                        break;
                    case "create":
                        send(hub.createRoom(this, msg));
                        break;
                    case "connected":
                        send(hub.connected(this));
                        break;
                    case "send":
                        hub.send(this, msg);
                        break;
                }
            }
        } else {
            hub.disconnect(this);
        }
    }

    public void send(String msg) {
        out.println(msg);
        out.flush();
    }

    public String getPseudo() {
        return pseudo;
    }

    public String getApp() {
        return app;
    }

    @Override
    public String toString() {
        return pseudo;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 31 * hash + Objects.hashCode(pseudo);
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
        final User other = (User) obj;
        return Objects.equals(this.pseudo, other.pseudo);
    }

    public int getUdpPort() {
        return udpPort;
    }

}
