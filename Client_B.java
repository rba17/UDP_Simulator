import java.io.IOException;
import java.net.*;

public class Client_B {
    static final int portB = 9999;

    public static void main(String[] args) throws IOException {

        final int packetNumber = 1000;

        // B recieving

        DatagramSocket ds = new DatagramSocket(portB);
        InetAddress ip = InetAddress.getLocalHost();

        byte[] buf = new byte[1024];

        for (int i = 0; i < packetNumber; i++) {
            DatagramPacket dpr = new DatagramPacket(buf, buf.length);
            ds.receive(dpr);
            String message = new String(dpr.getData(), 0, dpr.getLength());
            System.out.println(message + " " + (i + 1));
        }

        // B sending

    }
}
