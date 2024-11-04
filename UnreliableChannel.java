import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

public class UnreliableChannel {
    static ArrayList<String> losts = new ArrayList<>();

    public static void SendingThroughServer(int port, DatagramSocket ds, InetAddress ip, byte[] buf, String message,
            char server)
            throws IOException {

        int lost = 0;
        message += " passed through Unreliable Channel";
        for (int i = 0; i < Utilities.packetNumber; i++) {
            buf = message.getBytes();
            DatagramPacket dps = new DatagramPacket(buf, buf.length, ip, port);

            double r = Math.random();
            if (r < Utilities.lossFactor)
                lost++;
            ds.send(dps);
        }

        losts.add("Packets received from user " + server + ": " + Utilities.packetNumber
                + " | Lost: " + lost
                + " | Delayed: " + (Utilities.packetNumber - lost));

        if (losts.size() == Utilities.clientNumber) {
            for (int i = 0; i < Utilities.clientNumber; i++)
                System.out.println(losts.get(i));
        }
    }
}