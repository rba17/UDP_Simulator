import java.time.LocalTime;

public class Utilities {
    final static double lossFactor = 0.3;
    final static int packetNumber = 1000;
    final static int portB = 9999;
    final static int clientNumber = 2;

    public static void printSendTime(char server) {
        LocalTime currentTime = LocalTime.now();
        System.out.println("\n\nSending packets from " + server + " at " + currentTime + "\n\n");
    }

    public static void printReceiveTime(char server) {
        LocalTime currentTime = LocalTime.now();
        System.out.println("\n\nRecieved packets from " + server + " at " + currentTime + "\n\n");
    }

}