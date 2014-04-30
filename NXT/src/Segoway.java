package src;

import java.io.DataInputStream;
import java.io.IOException;

import lejos.nxt.Button;
import lejos.nxt.ButtonListener;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.NXTConnection;

public class Segoway {

	private static NXTConnection connection;
	private static Receiver receiverObj;
	private static DataInputStream input;
	private static Pilot pilot;

	public static void main(String[] args) {

		// Add button listener for grey exit button.
		Button.ESCAPE.addButtonListener(new ButtonListener() {
            @Override public void buttonPressed(Button button) { System.exit(0); }
    		@Override public void buttonReleased(Button b) {}
		});

		// Try connecting and start receiving packets until the receiver stops.
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

    		receiverObj.start();

            receiverObj.join(); // wait for thread to finish before continuing the main thread.
        }
        catch( InterruptedException e ){}
        finally{ // Close down everything
        	try {
				input.close();
			} catch (IOException e) {
				System.out.println("Error closing steam: " + e.getLocalizedMessage());
			}
            if( connection != null ) connection.close();
        }
	}
}
