import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Ex3Client {
	public static void main(String[] args) {
		try (Socket socket = new Socket("18.221.102.182",  38103)) { 
        	System.out.println("Connected to the server");
        	InputStream is = socket.getInputStream();
            OutputStream out = socket.getOutputStream();
            
            int byteSize = is.read();
            System.out.println("Reading " + byteSize + " bytes");
            
            byte[] incoming = new byte[byteSize];
            for (int i = 0; i < byteSize; i++) {
            	incoming[i] = (byte) (is.read() & 0xFF);
            }
            
            System.out.print("Data Received:");
            for (int i = 0; i < byteSize; i++) {
                if(i%10==0){
                    System.out.println();
                }
                System.out.printf("%01X", incoming[i]);
            }
            System.out.println();
            
            short check1 = checksum(incoming);
            System.out.printf("Checksum calculated: %01X\n", check1);
            
            byte[] outgoing = new byte[2];
            
            outgoing[0] = (byte)((check1 & 0xFF00)>>>8); //first byte of checksum
			outgoing[1] = (byte)(check1 & 0x00FF); //second byte of checksum
			out.write(outgoing);
			
			int outcome = is.read();
			if (outcome == 0) {
				System.out.println("incorrect checksum");
			} else if (outcome == 1) {
				System.out.println("Response good");
			} else {
				System.out.println("improper server response");
			}
			
            
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	public static short checksum(byte[] b) {
		int length = b.length;
        int i = 0;
        long returnValue = 0;
        long sum = 0;

        while (length > 1) {
            sum += ((b[i] << 8 & 0xFF00) | ((b[i + 1]) & 0x00FF));
            i += 2;
            length -= 2;
            if ((sum & 0xFFFF0000) > 0) {
                sum &= 0xFFFF;
                sum++;
            }
        }
        //if statement triggers if we are given an odd number of bytes
        if (length > 0) {
            sum += b[i] << 8 & 0xFF00;
            if ((sum & 0xFFFF0000) > 0) {
                sum &= 0xFFFF;
                sum++;
            }
        }
        returnValue = (~((sum & 0xFFFF) + (sum >> 16))) & 0xFFFF;
        return (short) returnValue;
	}
}
