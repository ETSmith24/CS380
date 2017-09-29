import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public final class EchoServer {

    public static void main(String[] args) throws Exception {
        try (ServerSocket serverSocket = new ServerSocket(22222)) {
            while (true) {
                try (Socket socket = serverSocket.accept()) {
                    String address = socket.getInetAddress().getHostAddress();
                    System.out.printf("Client connected: %s%n", address);
                    
                    PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
                    BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    String inputLine;
                    while ((inputLine = br.readLine()) != null) {
                        if (inputLine.equalsIgnoreCase("exit")) {
                            System.out.printf("Client disconnected: %s%n", address);
                            break;
                        }
                        pw.println("Server> " + inputLine);
                    }
                }
            }
        }
    }
}