import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;

public class Client_A {
    final static char server = 'A';

    public static void main(String[] args) throws IOException {
        int counter = 0;
        //A sending
        DatagramSocket ds = new DatagramSocket();
        InetAddress ip = InetAddress.getLocalHost();
        byte[] buf = new byte[1024];
        counter++;
        ByteBuffer.wrap(buf).putInt(counter);
        String send = "Hi from A";
        System.arraycopy(send.getBytes(), 0, buf, 4, send.length());
        Utilities.printSendTime(server);
        UnreliableChannel.SendingThroughServer(Utilities.portB, ds, ip, buf, send, server);

        // A recieving

        for (int i = 0; i < Utilities.packetNumber; i++) {
            DatagramPacket dpr = new DatagramPacket(buf, buf.length);
            ds.receive(dpr);

            // String rcv = new String(dpr.getData(), 0, dpr.getLength());
            // System.out.println(rcv + " " + (i + 1));
        }
        Utilities.printReceiveTime('A');
    }
}
