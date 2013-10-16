package game;


import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * This class is a port to Java of the Matlab NetStation function.
 * It allows the client to create a connection to a NetStation machine and to send various
 * commands and get information back. The connection is via a TCP/IP socket.
 * 
 * @author tjhickey@brandeis.edu
 * 
 * 
 */
public class NetStation {
	boolean nsrecording = false; // true if the machine is recording ...
	
	static final boolean VERBOSE=true;

	long startTime = System.nanoTime();

	String host;
	int port;

	Socket con;
	BufferedInputStream in;
	BufferedOutputStream out;
	
	static final String DEFAULT_HOST =  "10.0.0.42";
	static final int DEFAULT_PORT =  55513;
	static final double DEFAULT_SYNCH_LIMIT = 2.5; // ms

	/**
	 * Create a NetStation proxy with the default host and port
	 */
	public NetStation() {
		this(DEFAULT_HOST, DEFAULT_PORT);
	}

	/**
	 * Create a NetStation proxy with the specified host and ort
	 * @param host an IP address
	 * @param port a port number
	 */
	public NetStation(String host, int port) {
		this.host = host;
		this.port = port;
	}

	/**
	 * Open a connection to the NetStation.
	 * on the host/port stored when the NetStation object was created
	 * This creates the socket, gets the input and output streams
	 * Sends the initialization string "QMAC-" to the NetStation
	 * and receives the "all good" two byte response ('I' 1) 
	 * If anything goes wrong it throws an error and there are lots of places
	 * for something to go wrong!
	 * 
	 * @throws NetStationError
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public void connectNS() throws NetStationError, InterruptedException, IOException {

			if (con != null) {
				this.disconnectNS();
			}

			try {
				log("open connection to "+host+"/"+port);
				con = new Socket(host, port);
			} catch (UnknownHostException e) {				
				log("Error:unknow host:" + host + "/" + port);
				throw new NetStationError(con, "unknown host" + host + "/"
						+ port + " e=" + e);
			} catch (IOException e) {				
				throw new NetStationError(con, "IOException:" + e
						+ " when trying to connect");
			}

			
			
			
			// open the input stream to read from NetStation
			try {
				log("get input stream from NS");
				InputStream in_raw = con.getInputStream();
				in = new BufferedInputStream(in_raw);
			} catch (IOException e) {				
				throw new NetStationError(con,
						"can't open input stream from NetStation, e=" + e);
			}

			// open the output stream to write to NetStation
			try {
				log("get output stream from NS");
				OutputStream out_raw = con.getOutputStream();
				out = new BufferedOutputStream(out_raw);

			} catch (IOException e) {				
				throw new NetStationError(con,
						"can't open output stream to NetStation, e=" + e);
			}

			// send the ECCType string "QMAC-" to the NetStation
			byte[] buffer = new byte[100];
			buffer = "QMAC-".getBytes();
			try {
				log("Send QMAC- to NS");
				out.write(buffer, 0, 5);
				out.flush();
			} catch (IOException e) {				
				throw new NetStationError(con,
						"problem sending QMAC- identifier to NetStation, e="
								+ e);
			}

			int b = -1;
			int vers = -1;
			// read the result
			try {
				b = in.read(); // returns a number in range 0-255, should be 'I'
				log("recv response from NetStation:"+(char)b);
				if (b == 'I') {
					vers = in.read(); // return the version number
				}
				log("recv '" + vers + "' from NetStation");
			} catch (IOException e) {				
				throw new NetStationError(con,
						"problem reading status from QMAC- message, e=" + e);
			}

			if (b != 'I') {
				throw new NetStationError(con,
						"NetStation ECI error: didn't like QMAC- message, expected "+ 'I' +" as acknowledgement but found "+ b);
			}
			if (vers != 1) {
				throw new NetStationError(con,
						"NetStation: Unknown ECI version, expected 1 got "+vers);
			}



	}

	/**
	 * Disconnect the proxy from the NetStation.
	 * It does this by sending the message "EX"
	 * and reading the two byte response. It also waits a half second or so before
	 * sending each character.
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public void disconnectNS() throws InterruptedException, IOException {

			if (nsrecording) {
				Thread.sleep(500L);
				out.write('E');
				out.flush();
				log("Sent 'E' to NetStation");
				int rep = in.read();
				log("Recv '" + rep + "' from NetStation");
				// just ignore this ...
				nsrecording = false;
			}
			Thread.sleep(1000L);
			out.write('X');
			out.flush();
			log("Sent 'X' to NetStation");
			int rep = in.read();
			log("Recv '" + rep + "' from NetStation");
			Thread.sleep(500L);
			con.close();
			con = null;


	}

	/**
	 * Synchronize clocks with the NetStation.
	 * This attempts to synchronize the time with the NetStation. So that they both agree on the
	 * current time within a few milliseconds
	 * It does this by 
	 * <ul><li>
	 *   sending 'X' and receiving a character
	 *   </li><li>
	 *   calculating some representation of the current time x in milliseconds, 
	 *   for example, where x is the number of ms since the connection was made
	 *   </li><li>
	 *   sending the five bytes ('T' x0 x1 x2 x3) where x0 x1 x2 x3 is the four byte little endian
	 *   representation of x
	 *   </li><li>
	 *   reading a response from the NetStation and checking that no more that 2.5 ms have elapsed
	 *   since the time x
	 *   </li><li>
	 *   if more than 2.5 seconds have elapsed, then it repeats the process up to 100 times before giving up
	 *   </li></ul>
	 *   
	 *   
	 * @throws IOException
	 * @throws NetStationError 
	 */
	public void synchronizeNS() throws IOException, NetStationError {
		double ns_synch_limit = DEFAULT_SYNCH_LIMIT;
		double df = 10000;
		int n = 0;
		int tmp;

			while ((df > ns_synch_limit) && (n < 100)) {
				out.write('A');
				out.flush();
				log("Sent 'A' to NetStation");
				tmp = in.read();
				log("Recv '" + tmp + "' from NetStation");

				int now_ms = getTime_ms();
				log("now_ms= "+now_ms);
				byte[] b = int_to_big_endian4(now_ms);
				
				out.write('T');
				out.flush();
				log("Sent 'T' to NetStation"+b[0]+","+b[1]+","+b[2]+","+b[3]);
				out.write(b[0]);
				out.write(b[1]);
				out.write(b[2]);
				out.write(b[3]);
				
				out.flush();
				System.out.println("Sent '" + b[0] + " " + b[1] + " " + b[2]
								+ " " + b[3] + " " + "' = " + now_ms
								+ " to NetStation");
				tmp = in.read();
				log("Recv '" + tmp + "' from NetStation");

				double now2_sec = getTime_sec();
				df = (now2_sec - now_ms / 1000.0) * 1000;
				n += 1;
				log("Trying to Synch." + " n=" + n + " df = "
						+ df + " ms");
			}
		if (n >= 100) {
			log("NetStation synchronization did not succeed!");
			throw new NetStationError(con,"NetStation sychronization failure");
		} else
			log("NetStation synched ...");

	}

	/**
	 * Tell the NetStation to start recording
	 * by sending 'B' and receiving a byte
	 * @throws IOException
	 */
	public void startRecordingNS() throws IOException {
		int tmp;

			if (!nsrecording) {
				out.write('B');
				out.flush();
				log("Send 'B' to NetStation");
				tmp = in.read();
				log("Recv '" + tmp + "' from NetStation");
				nsrecording = true;
			}

	}

	/**
	 * Tell the NetStation to stop recording 
	 * by sending 'E' and receiving a byte
	 * @throws IOException
	 */
	public void stopRecordingNS() throws IOException {
		int tmp;

			if (nsrecording) {
				out.write('E');
				out.flush();
				log("Send 'E' to NetStation");
				tmp = in.read();
				log("Recv '" + tmp + "' from NetStation");
			}

	}
	
	
	

	/**
	 * Send a simple event to the NetStation with no key/value pairs.
	 * @param code
	 * @param startMS
	 * @param durationMS
	 * @throws IOException
	 */
	public void eventNS0(String code, int startMS, int durationMS) throws IOException{
		String[] keyCodes = new String[0];
		int[] keyValues = new int[0];
		eventNS(code,startMS,durationMS,keyCodes,keyValues);
	}
	
	/**
	 * Send an event to the NetStation with one key/value pair.
	 * @param code
	 * @param startMS
	 * @param durationMS
	 * @param key
	 * @param value
	 * @throws IOException
	 */
	public void eventNS1(String code, int startMS, int durationMS,String key, int value) throws IOException{
		String[] keyCodes = new String[1];
		int[] keyValues = new int[1];
		keyCodes[0]=key;
		keyValues[0]=value;
		eventNS(code,startMS,durationMS,keyCodes,keyValues);
	}

	/**
	 * Send an event to the NetStation with 2 key/value pairs.
	 * @param code
	 * @param startMS
	 * @param durationMS
	 * @param key1
	 * @param value1
	 * @param key2
	 * @param value2
	 * @throws IOException
	 */
	public void eventNS2(String code, int startMS, int durationMS,String key1, int value1,String key2, int value2) throws IOException{
		String[] keyCodes = new String[2];
		int[] keyValues = new int[2];
		keyCodes[0]=key1;
		keyValues[0]=value1;
		keyCodes[1]=key2;
		keyValues[1]=value2;
		eventNS(code,startMS,durationMS,keyCodes,keyValues);
	}
	
	/**
	 * Sends an event to the NetStation 
	 * You have to be careful to send data
	 * in little endian form..
	 * 
	 * @param code a 4 character event code
	 * @param startMS  the time that the event starts
	 * @param durationMS the duration of the event
	 * @param keyCodes an array of 4 character key codes
	 * @param keyValues an array of unsigned 16 bit integer values
	 * @throws IOException 
	 */
	public void eventNS(String code, int startMS, int durationMS,
			String[] keyCodes, int[] keyValues) throws IOException {

			int keyvaluepairs = keyCodes.length;
			int numBytes = 15 + keyvaluepairs * 12; 
												
			if (durationMS > 120000) {
				durationMS = 1;
			}
			log("sending an event:"+code+" "+numBytes+" "+ startMS+" "+durationMS+" "+keyvaluepairs);
			
			// first we write out 3+15 bytes specifying the event and the
			// number of keys to come!
			out.write('D');
			out.write(int_to_big_endian2(numBytes)); // num bytes to send after this line 
			
			out.write(int_to_big_endian4(startMS));
			out.write(int_to_big_endian4(durationMS));
			out.write(string_to_bytes4(code).getBytes()); // Event code
			out.write(int_to_big_endian2(0));
			out.write(int_to_big_endian1(keyvaluepairs));
			out.flush();

			for (int i = 0; i < keyvaluepairs; i++) {
				String keyCode = keyCodes[i];
				int keyValue = keyValues[i];
				log("sending key/value pairs "+keyCode+"/"+keyValue);

				// now we write out 12 bytes for the key/value pair
				out.write(string_to_bytes4(keyCode).getBytes()); // 4 byte key code
				out.write("shor".getBytes()); // 4 byte code "shor"
				out.write(int_to_big_endian2(2)); // 2 byte representation of number of bytes for keyValue
				out.write(int_to_big_endian2(keyValue)); // 2 bytes for Value
				out.flush();
			}
			int b = in.read();

	}
	
	/*
	 * Helper methods ...
	 */
	
	String string_to_bytes4(String s){
		return (s+"    ").substring(0,4);
	}

	
	
	static final double billion = 1000000000.0;
	static final double million = 1000000.0;
	
	public int getTime_ms() {
		long now_ns = System.nanoTime() - startTime; // time in ns
		int now_ms = (int) (now_ns / 1000000L); // tim in ms
		return now_ms;
	}



	double getTime_sec() {
		long now_ns = System.nanoTime() - startTime; // time in ns
		double now_sec = (double) (now_ns / billion); // time in sec
		return now_sec;
	}

	/**
	 * this converts positive integers to little endian 4-byte arrays
	 * 
	 * @param x
	 * @return
	 */
	public static byte[] int_to_big_endian4(int x) {
		byte[] b = new byte[4];
		b[3] = (byte) (x & 0xFF); // throws away higher order bits
		x = x >> 8; // shift x by 8 bits
		b[2] = (byte) (x & 0xFF); // throws away higher order bits
		x = x >> 8; // shift x by 8 bits
		b[1] = (byte) (x & 0xFF); // throws away higher order bits
		x = x >> 8; // shift x by 8 bits
		b[0] = (byte) (x & 0xFF); // throws away higher order bits
		return b;
	}

	/**
	 * this converts positive integers to little endian 1-byte arrays
	 * 
	 * @param x
	 * @return
	 */
	public static byte[] int_to_big_endian1(int x) {
		byte[] b = new byte[1];
		b[0] = (byte) (x & 0xFF); // throws away higher order bits
		return b;
	}
	
	/**
	 * this converts positive integers to little endian 2-byte arrays
	 * 
	 * @param x
	 * @return
	 */
	public static byte[] int_to_big_endian2(int x) {
		byte[] b = new byte[2];
		b[1] = (byte) (x & 0xFF); // throws away higher order bits
		x = x >> 8; // shift x by 8 bits
		b[0] = (byte) (x & 0xFF); // throws away higher order bits
		return b;
	}
	
	private static void log(String s){
		if (VERBOSE) System.out.println(s);
	}


	/**
	 * Run a demo that connects with the NetStation, sends some events, and disconnects.
	 * @param args
	 */
	public static void main(String[] args) {
		demo1();
		
	}
	
	static void demo1(){
		try {
			NetStation ns = new NetStation();
			ns.connectNS();
			Thread.sleep(1000L);
			ns.synchronizeNS();
			Thread.sleep(1000L);
			ns.startRecordingNS();
			Thread.sleep(1000L);
			ns.eventNS0("FISH", ns.getTime_ms(), 1); 
			Thread.sleep(1000L);
			ns.eventNS0("Clik", ns.getTime_ms(), 100); 
			Thread.sleep(1000L);
			ns.eventNS2("FISH", ns.getTime_ms(), 100,"VIS",2,"AUD",1); 
			Thread.sleep(1000L);
			ns.eventNS2("FISH", ns.getTime_ms(), 100,"VIS",2,"AUD",1); 
			Thread.sleep(1000L);
			ns.stopRecordingNS();
			ns.disconnectNS();
			//ns.out.write(ns.string_to_bytes4("\n").getBytes());
			ns.out.flush();
		} catch (Exception e) {
			log("demo1 threw an exception: " + e);
		}
	}

}
