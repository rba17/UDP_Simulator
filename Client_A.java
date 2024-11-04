import java.io.IOException;
import java.net.*;
import java.time.LocalTime;

public class Client_A {

    public static void main(String[] args) throws IOException {

        // A sending
        Utilities.printSendTime('A');

        DatagramSocket ds = new DatagramSocket();
        InetAddress ip = InetAddress.getLocalHost();
        byte[] buf = new byte[1024];

        for (int i = 0; i < Utilities.packetNumber; i++) {
            String message = "hi";
            buf = message.getBytes();
            DatagramPacket dps = new DatagramPacket(buf, buf.length, ip, Utilities.portB);
            ds.send(dps);
        }

        // A recieving

        for (int i = 0; i < Utilities.packetNumber; i++) {
            DatagramPacket dpr = new DatagramPacket(buf, buf.length);
            ds.receive(dpr);
            String message = new String(dpr.getData(), 0, dpr.getLength());
            System.out.println(message + " " + (i + 1));
        }

        Utilities.printReceiveTime('A');
    }
}
