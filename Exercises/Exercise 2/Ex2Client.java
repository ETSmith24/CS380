import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.zip.CRC32;


public class Ex2Client {
	public static void main(String[] args) throws Exception {
        try (Socket socket = new Socket("18.221.102.182", 38102)) { 
        	System.out.println("Connected to the server");
        	
            PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            int temp, temp2;
            byte combination;
            int count = 0;
            byte[] message = new byte[100];
            
            while (count < 100) {
            	temp = br.read();
            	temp =  ((temp & 0xFF) << 4);
            	temp2 = (br.read() & 0xFF);
            	combination = (byte) (temp | temp2);
            	message[count] = combination;
            	count++;
            } 
              
            System.out.println("Received bytes: ");
            for (int i = 0; i < 100; i++) {
            	System.out.print(Integer.toHexString(message[i]).toUpperCase());
            	if (i > 1 && i%10 == 0) {
            		System.out.println();
            	}
            }
            System.out.println();
            CRC32 errorCode = new CRC32();
            errorCode.update(message, 0, message.length);
            long crc = errorCode.getValue();
            System.out.println("Generated CRC: " + Integer.toHexString((int) crc));
            
            ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
            buffer.putLong(crc);
            
            for (int i = 0; i < 4; i++) {
            	pw.println(buffer.get(i));
            }
            if (br.read() == 0) {
            	System.out.println("CRC does not match");
            } else if (br.read() == 1) {
            	System.out.println("Response good.");
            } else {
            	System.out.println("not a proper response");
            }
            
        }
    }
}
