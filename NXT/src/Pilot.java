import java.util.Random;

import lejos.nxt.MotorPort;
import lejos.nxt.NXTMotor;

public class Pilot {

    NXTMotor leftMotor, rightMotor;
	Random rand;

	public Pilot() {
	    leftMotor = new NXTMotor( MotorPort.A );
	    rightMotor = new NXTMotor( MotorPort.C );
	}

	public void update(int packet) {
	    int leftPower = CommandParser.leftMotor(packet);
	    int rightPower = CommandParser.rightMotor(packet);
		leftMotor.setPower(leftPower);
		rightMotor.setPower(rightPower);
		String s = "Left:  "+leftMotor.getPower()+":"+leftPower+
		         "\nRight: "+rightMotor.getPower()+":"+rightPower;
		System.out.println(s);
	}
}