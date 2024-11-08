import java.io.*;
import java.net.*;

public class Client_B {
    final static char server = 'B';

    public static void main(String[] args) throws IOException, InterruptedException {
        Utilities.resetFile();
        String message = "Hi from B";
        DatagramSocket ds = new DatagramSocket(Utilities.portB);
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
        Thread.sleep(100);
        UnreliableChannel.SendingThroughServer(dpr.getPort(), ds, ip, buf, message, server);

        Thread.sleep(1000);
    }

}