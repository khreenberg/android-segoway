package src;

import lejos.nxt.MotorPort;
import lejos.nxt.NXTMotor;

/**
 * @author anthonymonori, khreenberg
 * Custom made pilot class that controls the two unregulated motors for the segoway.
 */
public class Pilot {

    NXTMotor leftMotor, rightMotor; // left and right unregulated motors.

	/**
	 * Constructor that instantiates the two motors throught the connected ports.
	 */
	public Pilot() {
	    leftMotor 	= new NXTMotor( MotorPort.B );
	    rightMotor 	= new NXTMotor( MotorPort.C );
	}

	/**
	 * Updating the motor power through the packet that was passed down from the 
	 * Receiver class, and then parsed by the CommandParser static methods which 
	 * generates a short from 0-100 after the bit-shifting on the packet.
	 * 
	 * @param packet sent through the bluetooth connection as a 16b/2B short packet which contains motor power info.
	 */
	public void update(short packet) {
	    int leftPower 	= CommandParser.leftMotor(packet); // parse left motor power from the packet
	    int rightPower 	= CommandParser.rightMotor(packet); // parse right motor power from the packet
	    
	    // set the motorpower on the unregulated motors
	    leftMotor.setPower(leftPower); 
		rightMotor.setPower(rightPower);
		
		// Printout on the screen for debugging purposes
		String s = "Left:  "+leftMotor.getPower()+":"+leftPower+
		         "\nRight: "+rightMotor.getPower()+":"+rightPower;
		System.out.println(s);
	}
}