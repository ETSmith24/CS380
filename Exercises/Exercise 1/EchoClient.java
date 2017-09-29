
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public final class EchoClient {

    public static void main(String[] args) throws Exception {
        try (Socket socket = new Socket("localhost", 22222)) {           
            PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedReader stdBR = new BufferedReader(new InputStreamReader(System.in));
            
            String userInput;
            System.out.print("Client> ");
            while ((userInput = stdBR.readLine()) != null) {
                pw.println(userInput);
                if (userInput.equalsIgnoreCase("exit")) {
                    pw.close();
                    stdBR.close();
                    socket.close();
                    System.exit(0);
                }
                System.out.println(br.readLine());
                System.out.print("Client> ");
            }
        }
    }
}