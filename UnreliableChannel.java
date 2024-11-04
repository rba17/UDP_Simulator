import java.io.IOException;
import java.net.*;

public class UnreliableChannel {
    public static void SendingThroughServer(int port, DatagramSocket ds, InetAddress ip, byte[] buf, String message)
            throws IOException {
        message += " passed through Unreliable Channel";
        for (int i = 0; i < Utilities.packetNumber; i++) {
            buf = message.getBytes();
            DatagramPacket dps = new DatagramPacket(buf, buf.length, ip, port);
            ds.send(dps);
        }
    }
}