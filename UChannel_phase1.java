import java.io.*;
import java.util.*;
import java.net.*;

public class UChannel_phase1 {

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
                    " | Delayed: " + successfulSends + "\n" +
                    "Average delay from " + server + " to " + destination +
                    ": " + getAverageDelay() + " ms.\n\n";
        }
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        System.out.println("\nUnreliableChannel started. Listening on port " + Utils.channelPort + "...\n");
        HashMap<String, Info> map = new HashMap<>();

        Random rand = new Random();

        InetAddress localAddress = InetAddress.getLocalHost();

        DatagramSocket ds = new DatagramSocket(new InetSocketAddress(localAddress, Utils.channelPort));

        ds.setSoTimeout(5000);

        byte[] receiveBuffer = new byte[Utils.BUFFER_SIZE];
        DatagramPacket dpr = new DatagramPacket(receiveBuffer, receiveBuffer.length);

        String receivedMessage, message = "";

        int seqNum = 0;
        char server = '#', destination = '&';

        while (true) {
            try {
                ds.receive(dpr);
                InetAddress ip = dpr.getAddress();
                int senderPort = dpr.getPort();

                receivedMessage = new String(dpr.getData(), 0, dpr.getLength());

                server = receivedMessage.charAt(receivedMessage.length() - 2);
                destination = receivedMessage.charAt(receivedMessage.length() - 1);

                receivedMessage = receivedMessage.substring(0, receivedMessage.length() - 2);
                message = receivedMessage.substring(0, receivedMessage.indexOf("$"));
                seqNum = Integer.parseInt(receivedMessage.substring(receivedMessage.indexOf("$") + 1));

                String pairKey = server + "-" + destination;
                Info info = map.get(pairKey);
                if (info == null) {
                    info = new Info(server, destination);
                    map.put(pairKey, info);
                }

                double r = rand.nextDouble();

                if (r < Utils.lossFactor) {
                    info.incrementLost();
                    System.out.println("Packet " + seqNum + " from " + server + " to " + destination + " lost.");
                    System.out.println("Content: \"" + message + "\" | Sender IP: " + ip.getHostAddress()
                            + " | Receiver IP: " + ds.getLocalAddress().getHostAddress());
                    continue;
                } else {
                    int d = rand.nextInt(201);
                    double delay = d / 10.0;
                    info.addDelay(delay);
                    info.incrementSuccess();

                    System.out.println("Packet " + seqNum + " from " + server + " to " + destination + " received.");
                    System.out.println("Content: \"" + message + "\" | Sender IP: " + ip.getHostAddress()
                            + " | Receiver IP: " + ds.getLocalAddress().getHostAddress());

                    try {
                        Thread.sleep((long) delay);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                String responseMessage = "ACK(" + seqNum + ") : " + message + " from " + server + " to " + destination;
                byte[] sendBuffer = responseMessage.getBytes();

                DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, ip, senderPort);
                ds.send(sendPacket);
            } catch (SocketTimeoutException e) {
                System.out.println("\nNo activity detected for 5 seconds. Shutting down UnreliableChannel.");
                break;
            }
        }
        ds.close();

        for (Info info : map.values()) {
            Utils.writeToFile(info.toString());
        }

        System.out.println("\nUnreliableChannel has been stopped.\n");
        Utils.printFile(Utils.f);
    }
}
