import java.io.*;
import java.net.*;
import java.util.*;

public class RUDPDestination {
    public static void main(String[] args) throws IOException, InterruptedException {
        DatagramSocket socket = new DatagramSocket(Utils.destinationPort);
        byte[] buf = new byte[512];
        DatagramPacket handShakeRecieved = new DatagramPacket(buf, buf.length);
        socket.receive(handShakeRecieved);
        System.out.println(handShakeRecieved.getData());

        String handShakeMSG = new String(handShakeRecieved.getData(), 0, handShakeRecieved.getLength());
        int totalPackets = Integer.parseInt(handShakeMSG.trim());
        System.out.println("Handshake received: Total packets = " + totalPackets);

        String responseMSG = "Received";
        byte[] responseMSGBytes = responseMSG.getBytes();
        DatagramPacket responsePacket = new DatagramPacket(responseMSGBytes, responseMSGBytes.length,
                handShakeRecieved.getAddress(),
                handShakeRecieved.getPort());
        socket.send(responsePacket);
        System.out.println("Handshake acknowledgment sent to sender.");

        FileOutputStream writeToFile = new FileOutputStream("New Hussein");
        Map<Integer, byte[]> packetMap = new HashMap<>();

        for (int i = 0; i < totalPackets; i++) {
            DatagramPacket dpr = new DatagramPacket(buf, buf.length);
            socket.receive(dpr);
            byte[] data = Arrays.copyOf(dpr.getData(), dpr.getLength());
            packetMap.put(i, data);
            String ACK = "ACK" + i;
            byte[] ACKMSG = ACK.getBytes();
            DatagramPacket dps = new DatagramPacket(ACKMSG, ACKMSG.length, handShakeRecieved.getAddress(),
                    handShakeRecieved.getPort());
            socket.send(dps);
        }

        for (int i = 0; i < totalPackets; i++) {
            if (packetMap.containsKey(i)) {
                writeToFile.write(packetMap.get(i));
            }
        }
        writeToFile.close();
        socket.close();

    }
}