import java.io.*;
import java.net.*;
import java.util.Random;

public class UC2 {
    private int channelPort;
    private InetAddress destinationIP;
    private int destinationPort;
    private Random random;

    public UC2(int channelPort, String destinationIP, int destinationPort) throws UnknownHostException {
        this.channelPort = channelPort;
        this.destinationIP = InetAddress.getByName(destinationIP);
        this.destinationPort = destinationPort;
        this.random = new Random();
    }

    public void start() throws IOException, InterruptedException {
        DatagramSocket channelSocket = new DatagramSocket(channelPort);
        channelSocket.setSoTimeout(10000); // 10-second timeout for inactivity
        System.out.println("Unreliable Channel started on port " + channelPort);

        while (true) {
            try {
                // Receive packet from source
                byte[] receiveBuffer = new byte[Utils.BUFFER_SIZE];
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                channelSocket.receive(receivePacket);

                String receivedMsg = new String(receivePacket.getData(), 0, receivePacket.getLength());

                // Handshake Handling
                if (receivedMsg.matches("\\d+")) { // Handshake message (total packets)
                    System.out.println("Handshake received from source: " + receivedMsg);

                    // Forward handshake to destination
                    DatagramPacket forwardHandshake = new DatagramPacket(
                            receivePacket.getData(),
                            receivePacket.getLength(),
                            destinationIP,
                            destinationPort);
                    channelSocket.send(forwardHandshake);

                    // Wait for handshake acknowledgment from destination
                    byte[] handshakeAckBuffer = new byte[Utils.BUFFER_SIZE];
                    DatagramPacket handshakeAckPacket = new DatagramPacket(handshakeAckBuffer,
                            handshakeAckBuffer.length);
                    channelSocket.receive(handshakeAckPacket);

                    // Relay acknowledgment back to source
                    DatagramPacket sourceAck = new DatagramPacket(
                            handshakeAckPacket.getData(),
                            handshakeAckPacket.getLength(),
                            receivePacket.getAddress(),
                            receivePacket.getPort());
                    channelSocket.send(sourceAck);
                    System.out.println("Handshake acknowledgment relayed to source.");
                    continue;
                }

                // Simulate packet loss
                if (random.nextDouble() < Utils.lossFactor) {
                    System.out.println("Packet lost.");
                    continue;
                }

                // Simulate network delay
                int delay = random.nextInt(Utils.maxDelay);
                Thread.sleep(delay);

                // Forward packet to destination
                DatagramPacket forwardPacket = new DatagramPacket(
                        receivePacket.getData(),
                        receivePacket.getLength(),
                        destinationIP,
                        destinationPort);
                channelSocket.send(forwardPacket);
                System.out.println("Packet forwarded to destination. Delay: " + delay + "ms");

                // Handle acknowledgments
                byte[] ackBuffer = new byte[Utils.BUFFER_SIZE];
                DatagramPacket ackPacket = new DatagramPacket(ackBuffer, ackBuffer.length);

                channelSocket.setSoTimeout(10000); // Reset timeout for activity
                try {
                    // Receive ACK from destination
                    channelSocket.receive(ackPacket);

                    // Forward ACK back to source
                    DatagramPacket sourceAckPacket = new DatagramPacket(
                            ackPacket.getData(),
                            ackPacket.getLength(),
                            receivePacket.getAddress(),
                            receivePacket.getPort());
                    channelSocket.send(sourceAckPacket);
                    System.out.println("ACK forwarded back to source.");
                } catch (SocketTimeoutException e) {
                    System.out.println("No ACK received from destination.");
                }
            } catch (SocketTimeoutException e) {
                System.out.println("\nNo activity detected for 10 seconds. Shutting down UnreliableChannel.");
                break;
            }
        }
        channelSocket.close();
    }

    public static void main(String[] args) throws InterruptedException {
        try {
            UC2 channel = new UC2(
                    Utils.channelPort,
                    Utils.destinationIP_string,
                    Utils.destinationPort);
            channel.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
