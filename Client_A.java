import java.io.*;
import java.net.*;

public class Client_A {
    final static char server = 'A';

    public static void main(String[] args) throws IOException, InterruptedException {
       
        String message = "Hi from A";
        DatagramSocket ds = new DatagramSocket();
        InetAddress ip = InetAddress.getLocalHost();
        byte[] buf = new byte[1024];

        // A sending
        UnreliableChannel.SendingThroughServer(Utilities.port, ds, ip, buf, message, server);
       
        // A recieving
        DatagramPacket dpr = new DatagramPacket(buf, buf.length);

        for (int i = 0; i < Utilities.packetNumber; i++) {
            dpr = new DatagramPacket(buf, buf.length);
            ds.receive(dpr);
            // String rcv = new String(dpr.getData(), 0, dpr.getLength());
            // System.out.println(rcv + " " + (i + 1));
        }

        Utilities.printFile(Utilities.f);
    }
}