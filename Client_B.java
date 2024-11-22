import java.io.*;
import java.net.*;

public class Client_B {
    final static char server = 'B';

    public static void main(String[] args) throws IOException, InterruptedException {
        String message = "Hi from B";
        DatagramSocket ds = new DatagramSocket(Utilities.port);
        InetAddress ip = InetAddress.getLocalHost();
        byte[] buf = new byte[1024];

        // B recieving
        DatagramPacket dpr = new DatagramPacket(buf, buf.length);

        for (int i = 0; i < Utilities.packetNumber; i++) {
            dpr = new DatagramPacket(buf, buf.length);
            ds.receive(dpr);
            // String message = new String(dpr.getData(), 0, dpr.getLength());
            // System.out.println(message + " " + (i + 1));
        }

        // B sending
        UnreliableChannel.SendingThroughServer(dpr.getPort(), ds, ip, buf, message, server);

    }

}