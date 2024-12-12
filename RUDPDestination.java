import java.io.*;
import java.net.*;
import java.util.*;

public class RUDPDestination {
    public static void main(String[] args) throws IOException {
        DatagramSocket socket = new DatagramSocket(Utils.destinationPort);
        Map<Integer, byte[]> packetMap = new HashMap<>();
        int totalPackets = 0;

        // Handshake: Receive total packets from UC2
        byte[] buf = new byte[Utils.BUFFER_SIZE];
        DatagramPacket handshakePacket = new DatagramPacket(buf, buf.length);
        socket.receive(handshakePacket);

        // Parse handshake message to determine total packets
        String handshakeMsg = new String(handshakePacket.getData(), 0, handshakePacket.getLength());
        try {
            totalPackets = Integer.parseInt(handshakeMsg.trim());
            System.out.println("Handshake received from channel: " + totalPackets);
        } catch (NumberFormatException e) {
            System.out.println("Error parsing handshake message. Terminating.");
            socket.close();
            return;
        }

        // Send handshake acknowledgment to UC2
        String responseMsg = "Handshake ACK";
        byte[] responseBytes = responseMsg.getBytes();
        DatagramPacket responsePacket = new DatagramPacket(
                responseBytes,
                responseBytes.length,
                handshakePacket.getAddress(),
                handshakePacket.getPort());
        socket.send(responsePacket);
        System.out.println("Handshake acknowledgment sent to channel.");

        // Start receiving packets
        while (packetMap.size() < totalPackets) {
            DatagramPacket dpr = new DatagramPacket(buf, buf.length);
            socket.receive(dpr);

            // Extract and store the packet data
            byte[] data = Arrays.copyOf(dpr.getData(), dpr.getLength());
            int packetId = packetMap.size(); // Use packet map size as packet ID
            packetMap.put(packetId, data);
            System.out.println("Packet " + packetId + " received.");

            // Send ACK for the received packet
            String ackMsg = "ACK" + packetId;
            byte[] ackBytes = ackMsg.getBytes();
            DatagramPacket ackPacket = new DatagramPacket(
                    ackBytes,
                    ackBytes.length,
                    dpr.getAddress(),
                    dpr.getPort());
            socket.send(ackPacket);
        }

        // Write all received data to file
        try (FileOutputStream writeToFile = new FileOutputStream("New Hussein.txt")) {
            for (int i = 0; i < packetMap.size(); i++) {
                writeToFile.write(packetMap.get(i));
            }
        }
        System.out.println("File received and written successfully.");

        socket.close();
    }
}
