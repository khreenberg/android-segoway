import java.io.DataInputStream;
import java.io.IOException;

import lejos.nxt.Motor;
import lejos.nxt.NXTMotor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.robotics.navigation.DifferentialPilot;

public class Receiver extends Thread {

	private DataInputStream input;

	DifferentialPilot pilot;
	
	public Receiver(DataInputStream input) {
		this.setInput(input);
	}
	
	@Override
	public void run() {
		Motor.B.setSpeed(0.001f);
		Motor.C.setSpeed(0.001f);
		Motor.B.forward();
		Motor.C.forward();
		while(true) {
			handleInput();
		}
	}
	

    void handleInput(){
        try{
            int packet = input.readInt();
    		NXTRegulatedMotor motorLeft = Motor.B;
    		NXTRegulatedMotor motorRight = Motor.C;
    		
    		motorLeft.setSpeed(CommandParser.leftMotor(packet));
    		motorRight.setSpeed(CommandParser.rightMotor(packet));	
        }
        catch( IOException e ){
            System.out.println("Error while reading from stream.\n"+e);
        }
    }

    private void setInput(DataInputStream input) {
    	this.input = input;
    }
}
