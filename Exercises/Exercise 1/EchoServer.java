import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public final class EchoServer {

    public static void main(String[] args) throws Exception {
        try (ServerSocket serverSocket = new ServerSocket(22222)) {
            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    String address = socket.getInetAddress().getHostAddress();
                    System.out.printf("Client connected: %s%n", address);
                    EchoHelper eh = new EchoHelper(socket);
                    eh.start();
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }
    }
    
    static class EchoHelper extends Thread {
        Socket client;
        String address;
        EchoHelper(Socket socket) {
            this.client = socket;
        }
        
        public void run() {
            try {
                PrintWriter pw = new PrintWriter(client.getOutputStream(), true);
                BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
                String inputLine;
                while ((inputLine = br.readLine()) != null) {
                    if (inputLine.equalsIgnoreCase("exit")) {
                        System.out.printf("Client disconnected: %s%n", address);
                        break;
                    }
                    pw.println("Server> " + inputLine);
                }
            } catch (Exception e) {
                System.out.println(e);
            } finally {
                try {
                    client.close();
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
            
        }
    }
}