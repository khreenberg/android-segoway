package src;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * @author anthonymonori, khreenberg
 * Class for receiving and handling the input coming from an external bluetooth connection through a DataInputStream.
 * Runs in it's own thread to ensure that it's not blocking anything else.
 */
public class Receiver extends Thread {

	private DataInputStream input; 
	private Pilot pilot; 

	/**
	 * Constructor for this Receiver object.
	 * @param input the input stream for the bluetooth connection.
	 * @param pilot the pilot that controls the motors.
	 */
	public Receiver(DataInputStream input, Pilot pilot) {
		this.setInput(input); // set the input using the setter.
		this.pilot = pilot; 
	}

	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		// Handle the input until the thread stops
		while(true) {
			handleInput();
		}
	}


    /**
     * Method for handling the actual input from the data stream channel.
     */
    void handleInput(){
        try{
            short packet = input.readShort(); // reading a short packet which is sent from the controller (phone).
            pilot.update(packet); // update the pilot with the newly arrived packet from the controller (phone).
        }
        catch( IOException e ){
            System.out.println("Error while reading from stream.\n"+e.getLocalizedMessage());
        }
    }

    /**
     * Setter for the input global var.
     * @param input DataInputStream to set the property
     */
    private void setInput(DataInputStream input) {
    	this.input = input;
    }
}
