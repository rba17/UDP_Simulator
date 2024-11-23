import java.io.*;
import java.util.*;
import java.net.*;

public class UnreliableChannel {
    private static double totalDelay = 0;
    private static double successfulSends = 0;
    private static int lost = 0;

    public static void main(String[] args) throws InterruptedException, IOException {
        System.out.println("UnreliableChannel started. Listening on port " + Utilities.channelPort + "...");
        Random rand = new Random();

        DatagramSocket ds = new DatagramSocket(Utilities.channelPort);
        byte[] receiveBuffer = new byte[Utilities.BUFFER_SIZE];
        DatagramPacket dpr = new DatagramPacket(receiveBuffer, receiveBuffer.length);

        int pairs = Utilities.countPairs();
        char server = '#', destination = '&';

        for (int i = 0; i < pairs; i++) {

            for (int j = 0; j < Utilities.packetNumber; j++) {

                ds.receive(dpr);
                InetAddress ip = dpr.getAddress();
                int senderPort = dpr.getPort();

                String receivedMessage = new String(dpr.getData(), 0, dpr.getLength());

                // System.out.println("Received from " + ip + ":" + senderPort + " - " +
                // receivedMessage);
                server = receivedMessage.charAt(receivedMessage.length() - 2);
                destination = receivedMessage.charAt(receivedMessage.length() - 1);

                double r = rand.nextDouble();
                if (r < Utilities.lossFactor)
                    lost++;
                else {
                    int d = rand.nextInt(0, 201);
                    double delay = d / 10.0;
                    totalDelay += delay;
                    successfulSends++;
                }

                String responseMessage = receivedMessage.substring(0, receivedMessage.length() - 2) + " $$ ";
                byte[] sendBuffer = responseMessage.getBytes();

                DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, ip,
                        senderPort);

                ds.send(sendPacket);

                // System.out.println("Sent response to " + ip + ":" + senderPort + " - " +
                // responseMessage);

            }
            double averageDelay = successfulSends > 0 ? totalDelay / successfulSends : 0;
            String result = "Packets received from user " + server + ": " +
                    Utilities.packetNumber +
                    " | Lost: " + lost +
                    " | Delayed: " + (Utilities.packetNumber - lost) + "\n";

            String delayMessage = "Average delay from " + server + " to " + destination +
                    ": " + averageDelay + " ms.\n\n";

            Utilities.writeToFile(result);

            Utilities.writeToFile(delayMessage);

        }
        ds.close();

        System.out.println("UnreliableChannel has been stopped.");

        Utilities.printFile(Utilities.f);

    }
}
