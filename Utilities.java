import java.time.LocalTime;

public class Utilities {
    final static double lossFactor = 0.3;
    final static int packetNumber = 1000;
    final static int portB = 9999;

    public static void printSendTime(char server) {
        LocalTime currentTime = LocalTime.now();
        System.out.println("Sending packets from " + server + " at " + currentTime);
    }

    public static void printReceiveTime(char server) {
        LocalTime currentTime = LocalTime.now();
        System.out.println("Recieved packets from " + server + " at " + currentTime);
    }

}