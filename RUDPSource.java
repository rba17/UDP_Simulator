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

        int totalNumPackets = (fileToBytes.length + Utils.packetSize - 1) / Utils.packetSize;

        System.out.println(fileToBytes.length);
        System.out.println(totalNumPackets);

        String handShakeMSG = "" + totalNumPackets;
        byte[] handShakeBytes = handShakeMSG.getBytes();
        DatagramPacket handShakePacket = new DatagramPacket(handShakeBytes, handShakeBytes.length, destinationIP,
                Utils.destinationPort);
        socket.send(handShakePacket);

        byte[] rbuffer = new byte[1024];
        DatagramPacket response = new DatagramPacket(rbuffer, rbuffer.length);
        socket.receive(response);

        for (int i = 0; i < totalNumPackets; i++) {
            int start = i * Utils.packetSize;
            int end = 0;
            if (start + Utils.packetSize < fileToBytes.length)
                end = start + Utils.packetSize;
            else
                end = fileToBytes.length;
            byte[] packetToSend = Arrays.copyOfRange(fileToBytes, start, end);
            DatagramPacket dps = new DatagramPacket(packetToSend, packetToSend.length, response.getAddress(),
                    response.getPort());
            socket.send(dps);

            byte[] ACKBuffer = new byte[1024];
            DatagramPacket ACKPacket = new DatagramPacket(ACKBuffer, ACKBuffer.length);
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