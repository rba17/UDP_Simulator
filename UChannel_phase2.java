import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Random;

public class UChannel_phase2 {
    private static class Info {
        private double totalDelay;
        private int successfulSends;
        private int lost;
        private char server;
        private char destination;

        public Info(char server, char destination) {
            this.server = server;
            this.destination = destination;
            this.totalDelay = 0.0;
            this.successfulSends = 0;
            this.lost = 0;
        }

        public void addDelay(double delay) {
            this.totalDelay += delay;
        }

        public void incrementSuccess() {
            this.successfulSends++;
        }

        public void incrementLost() {
            this.lost++;
        }

        public double getAverageDelay() {
            return this.successfulSends > 0 ? this.totalDelay / this.successfulSends : 0.0;
        }

        public String toString() {
            return "Packets received from user " + server + " to " + destination + ": " +
                    (successfulSends + lost) +
                    " | Lost: " + lost +
                    " | Successful: " + successfulSends + "\n" +
                    "Average delay from " + server + " to " + destination +
                    ": " + getAverageDelay() + " ms.\n\n";
        }
    }

    private int channelPort;
    private InetAddress destinationIP;
    private int destinationPort;
    private HashMap<String, Info> packetStats;
    private Random random;

    public UChannel_phase2(int channelPort, String destinationIP, int destinationPort) throws UnknownHostException {
        this.channelPort = channelPort;
        this.destinationIP = InetAddress.getByName(destinationIP);
        this.destinationPort = destinationPort;
        this.packetStats = new HashMap<>();
        this.random = new Random();
    }

    public void start() throws IOException {
        DatagramSocket channelSocket = new DatagramSocket(channelPort);
        Utils.resetFile(); // Reset the log file at the start

        System.out.println("Unreliable Channel started on port " + channelPort);

        while (true) {
            try {
                // Receive packet from source
                byte[] receiveBuffer = new byte[Utils.BUFFER_SIZE];
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                channelSocket.receive(receivePacket);

                // Extract sender and destination information
                String receivedMessage = new String(receivePacket.getData(), 0, receivePacket.getLength());
                char server = receivedMessage.charAt(receivedMessage.length() - 2);
                char destination = receivedMessage.charAt(receivedMessage.length() - 1);

                // Create or retrieve stats for this connection
                String pairKey = server + "-" + destination;
                Info info = packetStats.get(pairKey);
                if (info == null) {
                    info = new Info(server, destination);
                    packetStats.put(pairKey, info);
                }

                // Simulate packet loss
                if (random.nextDouble() < Utils.lossFactor) {
                    info.incrementLost();
                    System.out.println("Packet from " + server + " to " + destination + " lost.");
                    Utils.writeToFile("Packet from " + server + " to " + destination + " lost.\n");
                    continue;
                }

                // Simulate network delay
                int delay = random.nextInt(Utils.maxDelay);
                info.addDelay(delay);
                info.incrementSuccess();

                Thread.sleep(delay);

                // Forward packet to destination
                DatagramPacket forwardPacket = new DatagramPacket(
                        receivePacket.getData(),
                        receivePacket.getLength(),
                        destinationIP,
                        destinationPort);
                channelSocket.send(forwardPacket);

                System.out
                        .println("Packet from " + server + " to " + destination + " forwarded. Delay: " + delay + "ms");
                Utils.writeToFile(
                        "Packet from " + server + " to " + destination + " forwarded. Delay: " + delay + "ms\n");

                // Handle acknowledgments
                byte[] ackBuffer = new byte[Utils.BUFFER_SIZE];
                DatagramPacket ackPacket = new DatagramPacket(ackBuffer, ackBuffer.length);

                channelSocket.setSoTimeout(5000);

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

                    System.out.println("ACK forwarded back to source");
                } catch (SocketTimeoutException e) {
                    System.out.println("No ACK received from destination");
                }
                channelSocket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void printFinalStats() throws IOException {
        for (Info info : packetStats.values()) {
            Utils.writeToFile(info.toString());
        }
        Utils.printFile(Utils.f);
    }

    public static void main(String[] args) {
        try {
            UChannel_phase2 channel = new UChannel_phase2(
                    Utils.channelPort,
                    Utils.destinationIP_string,
                    Utils.destinationPort);
            channel.start();

            // This line will only be reached if the channel is stopped
            channel.printFinalStats();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}