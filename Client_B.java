import java.io.IOException;
import java.net.*;

public class Client_B {
    final static char server = 'B';

    public static void main(String[] args) throws IOException {

        // B recieving
        DatagramSocket ds = new DatagramSocket(Utilities.portB);
        InetAddress ip = InetAddress.getLocalHost();
        byte[] buf = new byte[1024];
        DatagramPacket dpr = new DatagramPacket(buf, buf.length);

        for (int i = 0; i < Utilities.packetNumber; i++) {
            dpr = new DatagramPacket(buf, buf.length);
            ds.receive(dpr);

            // String message = new String(dpr.getData(), 0, dpr.getLength());
            // System.out.println(message + " " + (i + 1));
        }
        Utilities.printReceiveTime(server);

        // B sending
        String send = "Hi from B";
        Utilities.printSendTime(server);
        UnreliableChannel.SendingThroughServer(dpr.getPort(), ds, ip, buf, send, server);

    }

}