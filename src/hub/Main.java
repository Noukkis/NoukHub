package hub;

import java.io.IOException;

/**
 *
 * @author Jordan
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Veuillez donner le numero de port et le timeout de connexion en argument");
            System.exit(1);
        }
        try {
            int port = Integer.parseInt(args[0]);
            int timeout = Integer.parseInt(args[1]);
            if (!start(port, timeout)) {
                System.err.println("Veuillez donner le numero de port et le timeout de connexion en argument");
                System.exit(3);
            }
        } catch (NumberFormatException e) {
            System.err.println("Veuillez donner le numero de port et le timeout de connexion en argument");
            System.exit(2);
        }
    }

    private static boolean start(int port, int timeout) {
        boolean ok = false;
        if (port < 1 || port > 65535 || timeout < 1) {
            return false;
        }
        try {
            Server server  = new Server(port, timeout);
            System.out.println("Serveur demarré sur le port " + port + " avec un timeout de connexion de " + timeout + "ms");
            server.start();
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
            return false;
        }
        return true;
    }

}
