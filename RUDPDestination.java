import java.io.*;
import java.net.*;
import java.util.*;

public class RUDPDestination {
    public static void main(String[] args) throws IOException, InterruptedException {
        DatagramSocket socket = new DatagramSocket(80);
        byte[] buffer = new byte[512];
        DatagramPacket Hrecieved = new DatagramPacket(buffer, buffer.length);
        socket.receive(Hrecieved);
        System.out.println(Hrecieved.getData());

        String hmessage = new String(Hrecieved.getData(), 0, Hrecieved.getLength());
        int totalPackets = Integer.parseInt(hmessage.trim());
        System.out.println("Handshake received: Total packets = " + totalPackets);
        
        String responsemessage = "Received";
        byte[] responsebytes = responsemessage.getBytes();
        DatagramPacket responsepacket = new DatagramPacket(responsebytes, responsebytes.length, Hrecieved.getAddress(),
                Hrecieved.getPort());
        socket.send(responsepacket);
        System.out.println("Handshake acknowledgment sent to sender.");

        FileOutputStream fout = new FileOutputStream("New Hussein");
        Map<Integer, byte[]> pmap = new HashMap<>();

        for (int i = 0; i < totalPackets; i++) {
            DatagramPacket dpr = new DatagramPacket(buffer, buffer.length);
            socket.receive(dpr);
            byte[] data = Arrays.copyOf(dpr.getData(), dpr.getLength());
            pmap.put(i, data);
            String ack = "ACK" + i;
            byte[] ackm = ack.getBytes();
            DatagramPacket dps = new DatagramPacket(ackm, ackm.length, Hrecieved.getAddress(), Hrecieved.getPort());
            socket.send(dps);

        }

        for (int i = 0; i < totalPackets; i++) {
            if (pmap.containsKey(i)) {
                fout.write(pmap.get(i));
            }
        }

    }
}