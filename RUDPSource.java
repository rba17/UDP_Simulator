import java.io.*;
import java.net.*;
import java.util.*;

public class RUDPSource {
    public static void main(String[] args) throws IOException, InterruptedException {

        InetAddress destinationIP = InetAddress.getByName(Utils.destinationIP_string);

        DatagramSocket socket = new DatagramSocket();
        socket.setSoTimeout(5000);

        FileInputStream f = new FileInputStream("Hussein.txt");
        byte[] fileToBytes = f.readAllBytes(); // making the file into bites
        f.close();

        int totalPackets = (fileToBytes.length + Utils.packetSize - 1) / Utils.packetSize;

        System.out.println(fileToBytes.length);
        System.out.println(totalPackets);

        String handShakeMSG = "" + totalPackets;
        byte[] handShakeBytesSend = handShakeMSG.getBytes();
        DatagramPacket handShakePacket = new DatagramPacket(handShakeBytesSend, handShakeBytesSend.length,
                destinationIP,
                Utils.destinationPort);
        socket.send(handShakePacket);

        byte[] handShakeBytesRecieve = new byte[1024];
        DatagramPacket responsePacket = new DatagramPacket(handShakeBytesRecieve, handShakeBytesRecieve.length);
        socket.receive(responsePacket);

        for (int i = 0; i < totalPackets; i++) {
            int start = i * Utils.packetSize;
            int end = 0;
            if (start + Utils.packetSize < fileToBytes.length)
                end = start + Utils.packetSize;
            else
                end = fileToBytes.length;
            byte[] packetToSend = Arrays.copyOfRange(fileToBytes, start, end);
            DatagramPacket dps = new DatagramPacket(packetToSend, packetToSend.length, responsePacket.getAddress(),
                    responsePacket.getPort());
            socket.send(dps);

            byte[] ACKRCV = new byte[1024];
            DatagramPacket ACKPacket = new DatagramPacket(ACKRCV, ACKRCV.length);
            try {
                socket.receive(ACKPacket);
            } catch (Exception error) {
                System.out.println("Resending packet after timeout");
                --i;
            }
        }
        socket.close();
    }
}