package src;

import java.io.DataInputStream;
import java.io.IOException;

import lejos.nxt.Button;
import lejos.nxt.ButtonListener;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.NXTConnection;

/**
 * @author anthonymonori, khreenberg
 * Main class. This is the one that gets started on the robot.
 */
public class Segoway {

	private static NXTConnection connection; // for async bluetooth connection.
	private static Receiver receiverObj; // the object that handles the incoming data.
	private static DataInputStream input; // input stream for the bluetooth connection.
	private static Pilot pilot; // custom-made pilot controller class.

	/**
	 * @param args cmd-line arguments
	 */
	public static void main(String[] args) {

		// Add button listener for grey exit button.
		Button.ESCAPE.addButtonListener(new ButtonListener() {
            @Override public void buttonPressed(Button button) { System.exit(0); } // exit the program.
    		@Override public void buttonReleased(Button b) {}
		});

		// Try connecting and start receiving packets until the receiver stops.
		// @todo: might be a good idea to wrap this in a while statement, 
		// 	      so when the phone disconnects it would start all over again
		try{
            System.out.println("Waiting for connection...");
            connection = Bluetooth.waitForConnection();
            System.out.println("Connection established!");
            connection.setIOMode( NXTConnection.RAW );

            input = connection.openDataInputStream();

            // Initialize pilot
    		pilot = new Pilot();

            // Initialize receiver.
    		receiverObj = new Receiver(input, pilot);

    		receiverObj.start(); // start the thread.

            receiverObj.join(); // wait for thread to finish before continuing the main thread. 
            // This doesn't let the program to go to the finally case until the receiver thread is alive.
        }
        catch( InterruptedException e ){System.out.println(e.getLocalizedMessage());}
        finally{ // Close down everything
        	try {
				input.close(); // the input stream
			} catch (IOException e) {
				System.out.println("Error closing steam: " + e.getLocalizedMessage());
			}
            if ( connection != null ) connection.close(); // the bluetooth connection
        }
	}
}
