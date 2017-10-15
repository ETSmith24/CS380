import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class PhysLayerClient {
	public static void main(String[] args) throws Exception {
        try (Socket socket = new Socket("18.221.102.182", 38002)) { 
        	System.out.println("Connected to the server");
        	PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            int total = 0;
            
            for(int i = 0; i < 64; i++) {
            	total += br.read() & 0xFF;
            }
            double average = total/64;
            System.out.println("Baseline established from preamble: " + average);
            
            int[] incoming = new int[320];
            for (int i = 0; i < 320; i++) {
            	incoming[i] = (br.read() & 0xFF);
            	NRZIdecode(Integer.toBinaryString(incoming[i]));
            }
            
            for (int i=0;i<320;i++) {
            	System.out.print(Integer.toHexString(incoming[i]).toUpperCase());
            	if (i > 1 && i%10 == 0) {
            		System.out.println();
            	}
            }
        }
	}
	
	public static int[] NRZIdecode(String b) {
		boolean flip = false;
		if (b.charAt(0) == 0) {
			flip = true;
		}
		for (int i = 1; i < b.length(); i++) {
			if (flip == true) {
				if (b.charAt(i) == 0) {
					b.charAt(i) = '1';
				} else {
					b.charAt(i) = '0';
				}
			}
			if (b.charAt(i) == 0) {
				flip = !flip;
			}
		}
		
	}
}
