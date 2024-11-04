import java.io.IOException;
import java.net.*;
import java.time.LocalTime;

public class Client_B {

    public static void main(String[] args) throws IOException {

        // B recieving

        DatagramSocket ds = new DatagramSocket(Utilities.portB);
        InetAddress ip = InetAddress.getLocalHost();
        byte[] buf = new byte[1024];

        DatagramPacket dpr = new DatagramPacket(buf, buf.length);

        for (int i = 0; i < Utilities.packetNumber; i++) {
            dpr = new DatagramPacket(buf, buf.length);
            ds.receive(dpr);
            String message = new String(dpr.getData(), 0, dpr.getLength());
            System.out.println(message + " " + (i + 1));
        }

        Utilities.printReceiveTime('B');

        // B sending

        Utilities.printSendTime('B');
        
        for (int i = 0; i < Utilities.packetNumber; i++) {
            String message = "hi";
            buf = message.getBytes();
            DatagramPacket dps = new DatagramPacket(buf, buf.length, ip, dpr.getPort());
            ds.send(dps);
        }

    }

}