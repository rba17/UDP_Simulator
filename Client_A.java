import java.io.IOException;
import java.net.*;

public class Client_A {

    public static void main(String[] args) throws IOException {

        // A sending
        DatagramSocket ds = new DatagramSocket();
        InetAddress ip = InetAddress.getLocalHost();
        byte[] buf = new byte[1024];
        String send = "Hi from A";
        Utilities.printSendTime('A');
        UnreliableChannel.SendingThroughServer(Utilities.portB, ds, ip, buf, send);

        // A recieving
        for (int i = 0; i < Utilities.packetNumber; i++) {
            DatagramPacket dpr = new DatagramPacket(buf, buf.length);
            ds.receive(dpr);
            String rcv = new String(dpr.getData(), 0, dpr.getLength());
            System.out.println(rcv + " " + (i + 1));
        }
        Utilities.printReceiveTime('A');
    }
}
