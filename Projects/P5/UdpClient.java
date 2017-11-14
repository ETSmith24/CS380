import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Random;

public class UdpClient {

	public static void main(String[] args) throws Exception {
		try (Socket socket = new Socket("18.221.102.182", 38005)) {
			InputStream is = socket.getInputStream();
			OutputStream os = socket.getOutputStream();
			double avgRTT = 0;

			//handshake and response
			byte[] hsData = { (byte) 0xDE, 
					          (byte) 0xAD, 
					          (byte) 0xBE, 
					          (byte) 0xEF };
			byte hsPacket[] = ipv4(hsData);
			os.write(hsPacket);
			System.out.print("Handshake response: 0x" + Integer.toHexString(is.read()).toUpperCase());
			for(int i = 0; i < 3; i++){
				System.out.print(Integer.toHexString(is.read()).toUpperCase());
			}
			int port = 0;
			port = (is.read() << 8);
			port += is.read();
			System.out.println("\nPort number received: " + port + "\n");

			int data = 2;
			for (int i = 0; i < 12; i++) {
				byte[] udpPacket = udp(port, data);
				byte[] ipv4Packet = ipv4(udpPacket);
				
				long receive = 0;
				long rtt = 0;
				long send = System.currentTimeMillis();

				os.write(ipv4Packet);
				System.out.println("Sending packet with " + data + " bytes of data");
				System.out.print("Response: 0x" + Integer.toHexString(is.read()).toUpperCase());
				for(int j = 0; j < 3; j++) {
					System.out.print(Integer.toHexString(is.read()).toUpperCase());
				}
				receive = System.currentTimeMillis();
				rtt = receive - send;
				avgRTT += rtt;
				System.out.println("\nRTT: " + rtt + "ms\n");
				data *= 2;
			}
			avgRTT /= 12;
			System.out.println("Average RTT: " + avgRTT + "ms");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static byte[] ipv4(byte[] data) {
		byte[] packet = new byte[20 + data.length];
		short size = (short) (20 + data.length);

		packet[0] = 0x45; //version 4 and header length 5
		packet[1] = 0; //ToS
		packet[2] = (byte) ((size >> 8) & 0xff); //length
		packet[3] = (byte) (size & 0xff);
		packet[4] = 0; //Ident
		packet[5] = 0; 
		packet[6] = 0x40; //flags
		packet[7] = 0; //offset
		packet[8] = 50; //TTL
		packet[9] = 17; //UDP protocol
		packet[12] = (byte) 0; //source address
		packet[13] = (byte) 0; 
		packet[14] = (byte) 0; 
		packet[15] = (byte) 0; 
		packet[16] = (byte) 18; //destination address 18.221.102.182
		packet[17] = (byte) 221;
		packet[18] = (byte) 102;
		packet[19] = (byte) 182; 
		short check = checksum(packet);
		packet[10] = (byte) (check >>> 8); //checksum
		packet[11] = (byte) check;
		for (int i = 0; i < data.length; i++) { //fill in data portion of packet
			packet[20 + i] = (byte) data[i];
		}
		return packet;
	}

	private static byte[] udp(int destPort, int size) {
		byte[] packet = new byte[8 + size];
		packet[0] = 0; //source port
		packet[1] = 0; 
		packet[2] = (byte) (destPort >> 8); //destination port
		packet[3] = (byte) destPort; 
		packet[4] = (byte) (size >> 8); //length
		packet[5] = (byte) size; 
		Random rand = new Random();
		for (int i = 0; i < size; i++) {
			packet[i + 8] = (byte) rand.nextInt();
		}
		byte[] pseudoHeader = new byte[12];
		pseudoHeader[0] = (byte) 0; //source address
		pseudoHeader[1] = (byte) 0; 
		pseudoHeader[2] = (byte) 0; 
		pseudoHeader[3] = (byte) 0; 
		pseudoHeader[4] = (byte) 18; //destination address 18.221.102.182
		pseudoHeader[5] = (byte) 221;
		pseudoHeader[6] = (byte) 102; 
		pseudoHeader[7] = (byte) 182; 
		pseudoHeader[8] = 0; //reserved
		pseudoHeader[9] = 17; //UDP protocol
		pseudoHeader[10] = packet[4];
		pseudoHeader[11] = packet[5];

		//checksum
		byte[] checkUDP = new byte[8 + size + 12];
		for (int i = 0; i < checkUDP.length; i++) {
			if (i < (8 + size)) {
				checkUDP[i] = packet[i];
			} else {
				checkUDP[i] = pseudoHeader[i - (size + 8)];
			}
		}
		short check = checksum(checkUDP);
		packet[6] = (byte) ((check >> 8) & 0xff); //insert checksum
		packet[7] = (byte) (check & 0xff);
		return packet;
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