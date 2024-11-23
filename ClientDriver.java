import java.io.*;
import java.net.*;

public class ClientDriver {

    private static final String MESSAGE = "Hi from";

    public static void main(String[] args) throws IOException {

        Utilities.resetFile();

        InetAddress ip = InetAddress.getLocalHost();

        DatagramSocket ds = new DatagramSocket();

        for (int i = 0; i < Utilities.pairs.length; i++) {
            char sender = (char) (i + 'A');
            for (char destination : Utilities.pairs[i]) {
                sendAndReceivePackets(ds, ip, Utilities.channelPort, sender, destination);
            }
        }
        ds.close();
    }

    private static void sendAndReceivePackets(DatagramSocket socket, InetAddress serverAddress, int serverPort,
            char sender, char destination) throws IOException {

        Utilities.printSendTime(sender, destination, Utilities.t0, System.nanoTime());

        String fullMessage = MESSAGE + sender + destination;
        byte[] messageBytesCopy = fullMessage.getBytes();

        byte[] receiveBuffer = new byte[Utilities.BUFFER_SIZE];
        DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);

        for (int i = 0; i < Utilities.packetNumber; i++) {

            DatagramPacket sendPacket = new DatagramPacket(messageBytesCopy, messageBytesCopy.length, serverAddress,
                    serverPort);

            socket.send(sendPacket);

            socket.receive(receivePacket);

            String receivedMessage = new String(receivePacket.getData(), 0, receivePacket.getLength()).trim();

            System.out.println((i + 1) + ". " + receivedMessage + " " + sender + " to " + destination);

        }

        Utilities.printReceiveTime(sender, destination, Utilities.t0, System.nanoTime());
    }
}
