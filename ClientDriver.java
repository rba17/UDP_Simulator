import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ClientDriver {
    public static void main(String[] args) throws IOException {

        Utilities.resetFile();

        String message = "Hi from";
        DatagramSocket ds = new DatagramSocket(Utilities.port);
        InetAddress ip = InetAddress.getLocalHost();
        byte[] buf = new byte[1024];

        // filling in the clients
        Utilities.addPair('A', 'B');

        for (int i = 0; i < Utilities.pairs.length; i++) {
            for (int j = 0; j < Utilities.pairs[i].size(); j++) {
                
            }
        }

        Utilities.printFile(Utilities.f);

    }
}
