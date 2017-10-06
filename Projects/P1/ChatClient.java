
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public final class ChatClient {

    public static void main(String[] args) throws Exception {
        try (Socket socket = new Socket("18.221.102.182", 38001)) {           
            PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedReader stdBR = new BufferedReader(new InputStreamReader(System.in));
            ChatHelper ch = new ChatHelper(socket);
            ch.start();
            
            String userInput;
            System.out.print("Username: ");
            while ((userInput = stdBR.readLine()) != null) {
                pw.println(userInput);
                if (userInput.equalsIgnoreCase("exit")) {
                    pw.close();
                    stdBR.close();
                    socket.close();
                    System.exit(0);
                }
            }
        }
    }
    
    static class ChatHelper extends Thread {
        Socket server;
        ChatHelper(Socket socket) {
            this.server = socket;
        }
        
        public void run() {
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(server.getInputStream()));
                while(true) {
                        System.out.println(br.readLine());
                }
            } catch (Exception e) {
                System.out.println(e);
            }
            
        }
    }
}