package game;

import java.io.IOException;
import java.net.Socket;

/**
 * This is a class to store errors that arise when interacting with the NetStation over TCP/IP
 * @author tim
 *
 */
class NetStationError extends java.lang.Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5411876867285427186L;

	NetStationError(Socket con, String err) {
		super(err);
		if (con != null)
			try {
				con.close();
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Exception closing the NetStation socket:"
						+ e);
			}
	}
}
