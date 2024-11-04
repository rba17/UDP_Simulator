import java.io.IOException;
import java.net.*;

public class Client_A {

    public static void main(String[] args) throws IOException {

        final int packetNumber = 1000;
        
        // A recieving 

        DatagramSocket ds = new DatagramSocket();
        InetAddress ip = InetAddress.getLocalHost();

        for (int i = 0; i < packetNumber; i++) {
            String message = "hi";
            byte[] buf = message.getBytes();
            DatagramPacket dps = new DatagramPacket(buf, buf.length, ip, Client_B.portB);
            ds.send(dps);
        }
    }
}
