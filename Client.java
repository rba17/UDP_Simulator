import java.io.*;
import java.net.*;
import java.util.*;

public class Client {

    public static void main(String[] args) throws IOException, InterruptedException {
        Utils.resetFile();

        InetAddress ip = InetAddress.getLocalHost();

        DatagramSocket socket = new DatagramSocket();
        socket.setSoTimeout(1000);

        Thread com_A2B = new Thread(
                new Communication(socket, ip, Utils.channelPort, 'A', 'B'));
        Thread com_C2F = new Thread(
                new Communication(socket, ip, Utils.channelPort, 'C', 'F'));

        com_A2B.start();
        com_C2F.start();

        com_A2B.join();
        com_C2F.join();

        socket.close();
    }

    static class Communication implements Runnable {
        private DatagramSocket ds;
        private InetAddress ip;
        private int port;
        private char sender;
        private char receiver;

        public Communication(DatagramSocket ds, InetAddress ip, int port, char sender, char receiver) {
            this.ds = ds;
            this.ip = ip;
            this.port = port;
            this.sender = sender;
            this.receiver = receiver;
        }

        public void run() {
            try {
                long sendStartTime = System.nanoTime();
                String fullMessage = "";
                byte[] buf;
                Random rand = new Random();
                Utils.printSendTime(sender, receiver, sendStartTime, System.nanoTime());

                for (int i = 0; i < Utils.packetNumber; i++) {
                    
                    // send
                    fullMessage = Utils.generateRandomMessage(rand.nextInt(6) + 5) + "$" + i + sender + receiver;
                    // messages between 5 and 10 characters long ^
                    buf = fullMessage.getBytes();
                    // buffers of varied sizes ^
                    DatagramPacket dps = new DatagramPacket(buf, buf.length, ip, port);
                    // packets of varied sizes ^
                    ds.send(dps);

                    // recieve
                    // we put try catch block incase unrelaible channel didnt send (lost) and
                    // timer timed out after 1000ms

                    try {

                        byte[] rcv = new byte[Utils.BUFFER_SIZE];
                        DatagramPacket dpr = new DatagramPacket(rcv, rcv.length);
                        ds.receive(dpr);
                        String receivedMessage = new String(dpr.getData(), 0, dpr.getLength()).trim();
                        System.out.println(receivedMessage);

                    } catch (SocketTimeoutException e) {
                        System.out.println("NACK(" + i + ")");
                        //when channel doesnt send ^
                    }
                }

                long sendEndTime = System.nanoTime();
                Utils.printReceiveTime(sender, receiver, sendStartTime, sendEndTime);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
