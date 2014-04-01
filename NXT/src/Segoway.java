import java.io.DataInputStream;
import java.io.IOException;

import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.NXTConnection;

public class Segoway extends Thread{

	private static NXTConnection connection;
	private static Receiver receiverObj;
	private static DataInputStream input;

	public static void main(String[] args) {
		try{
            System.out.println("Waiting for connection...");
            connection = Bluetooth.waitForConnection();
            System.out.println("Connection established!");
            connection.setIOMode( NXTConnection.RAW );
            
            input = connection.openDataInputStream();
    		// Initialize receiver.
    		receiverObj = new Receiver(input);

    		receiverObj.start();
            
            receiverObj.join(); // wait for thread to finish before continuing the main thread.
        }
        catch( InterruptedException e ){
        }
        finally{
        	try {
				input.close();
			} catch (IOException e) {
				System.out.println("Error closing steam: " + e.getLocalizedMessage());
			}
            if( connection != null ) connection.close();
        }
	}
}
