import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Ipv6Client {
	public static void main(String[] args) throws UnknownHostException, IOException {
		try (Socket socket = new Socket("18.221.102.182", 38004)) {
			InputStream is = socket.getInputStream();
			OutputStream out = socket.getOutputStream();
            InetAddress address = socket.getInetAddress();
            
            byte[] dstAdr = address.getAddress();
            int len = 40;
			int data = 1;
			
			for (int i = 0; i < 12; i++) {
				data *= 2;
				int size = len + data;
				byte [] packet = new byte [size];
				
				packet[0] = 0x6 << 4; //version
				packet[1] = 0; //traffic class
				packet[2] = 0; //flow label
				packet[3] = 0; //flow label
				packet[4] = (byte) ((data >>> 8) & 0xFF); //payload length
				packet[5] = (byte) (data & 0xFF); //payload length
				packet[6] = 0x11; //next header
				packet[7] = 20; //hop limit
				
                //source address extended
                for(int j = 8; j < 18; j++){
                    packet[j] = 0;
                }
                
				packet[18] = (byte) 0xFF; //fill in source address 1's
				packet[19] = (byte) 0xFF; 
				packet[20] = 1; //fill in source address
				packet[21] = 2; 
				packet[22] = 3; 
				packet[23] = 4; 
				
                //destination address extended
                for(int k = 24; k < 34; k++){
                    packet[k] = 0;
                }
                
				packet[34] = (byte) 0xFF; //fill in destination address 1's
				packet[35] = (byte) 0xFF; 
				packet[36] = dstAdr[0]; //fill in destination address
				packet[37] = dstAdr[1]; 
				packet[38] = dstAdr[2]; 
				packet[39] = dstAdr[3]; 
				
				out.write(packet);
				System.out.println("Data Length: " + data);
                String response = "";
                
				for (int l = 0; l < 4; l++) {
                    response += Integer.toHexString(is.read()).toUpperCase();
				}
				
                System.out.println("Response: 0x" + response + "\n");	
			}
		}
		catch (Exception e) {
		}
		
	}
}
	
