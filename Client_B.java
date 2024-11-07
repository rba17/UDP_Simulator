import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;

public class Client_B {
    final static char server = 'B';

    public static void main(String[] args) throws IOException {

        // B receiving
        DatagramSocket ds = new DatagramSocket(Utilities.portB);
        InetAddress ip = InetAddress.getLocalHost();
        byte[] buf = new byte[1024];
        DatagramPacket dpr = new DatagramPacket(buf, buf.length);

        // Receive multiple packets from Client_A
        for (int i = 0; i < Utilities.packetNumber; i++) {
            ds.receive(dpr);
            // Optional: Print or process each received message if needed
            // String message = new String(dpr.getData(), 0, dpr.getLength());
            // System.out.println(message + " " + (i + 1));
        }

        // Extract the counter from the first 4 bytes of the buffer
        int counter = ByteBuffer.wrap(buf).getInt(0);
        Utilities.printReceiveTime(server);

        // Increment the counter to 2
        counter++;

        // Update the buffer with the new counter value
        ByteBuffer.wrap(buf).putInt(counter);

        // Prepare the message to send back
        String send = "Hi from B";
        System.arraycopy(send.getBytes(), 0, buf, 4, send.length());

        // B sending
        Utilities.printSendTime('B');
        UnreliableChannel.SendingThroughServer(dpr.getPort(), ds, ip, buf, send, server);
    }
}
