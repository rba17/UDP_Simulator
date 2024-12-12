import java.io.*;
import java.net.*;
import java.util.*;

public class RUDPSource {
    public static void main(String[] args) throws IOException, InterruptedException {
        DatagramSocket socket = new DatagramSocket();
        socket.setSoTimeout(5000);

        FileInputStream f = new FileInputStream("Hussein.txt");
        byte[] fileToBytes = f.readAllBytes(); // Convert file to bytes
        f.close();

        int totalPackets = (fileToBytes.length + Utils.packetSize - 1) / Utils.packetSize;

        // Handshake with UC2 (Channel)
        String handShakeMSG = "" + totalPackets;
        byte[] handShakeBytesSend = handShakeMSG.getBytes();
        DatagramPacket handShakePacket = new DatagramPacket(
                handShakeBytesSend,
                handShakeBytesSend.length,
                Utils.UCIP(),
                Utils.channelPort);
        socket.send(handShakePacket);

        // Wait for handshake acknowledgment
        byte[] handShakeBytesReceive = new byte[1024];
        DatagramPacket responsePacket = new DatagramPacket(handShakeBytesReceive, handShakeBytesReceive.length);
        socket.receive(responsePacket);
        System.out.println("Handshake acknowledged by UC2.");

        // Send packets
        for (int i = 0; i < totalPackets; i++) {
            int start = i * Utils.packetSize;
            int end = Math.min(start + Utils.packetSize, fileToBytes.length);
            byte[] packetToSend = Arrays.copyOfRange(fileToBytes, start, end);

            DatagramPacket dps = new DatagramPacket(packetToSend, packetToSend.length, Utils.UCIP(), Utils.channelPort);
            socket.send(dps);

            byte[] ACKrcv = new byte[1024];
            DatagramPacket ACKPacket = new DatagramPacket(ACKrcv, ACKrcv.length);
            try {
                socket.receive(ACKPacket);
                System.out.println("ACK received for packet " + i);
            } catch (SocketTimeoutException e) {
                System.out.println("Resending packet " + i + " after timeout.");
                i--; // Retry sending the same packet
            }
        }
        socket.close();
    }
}
