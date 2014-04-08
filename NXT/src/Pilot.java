import java.util.Random;

import lejos.nxt.MotorPort;
import lejos.nxt.NXTMotor;

public class Pilot {

//	SegowayMotor leftMotor;
//	SegowayMotor rightMotor;
    NXTMotor leftMotor, rightMotor;
	Random rand;

	public Pilot() {
//		this.leftMotor = new SegowayMotor(MotorPort.A); // hardcoded
//		this.rightMotor = new SegowayMotor(MotorPort.C); // hardcoded
//		this.leftMotor.start();
//		this.rightMotor.start();

	    leftMotor = new NXTMotor( MotorPort.A );
	    rightMotor = new NXTMotor( MotorPort.C );
	}

	public void updateSpeed(int packet) {
	    int leftPower = CommandParser.leftMotor(packet);
	    int rightPower = CommandParser.rightMotor(packet);
		leftMotor.setPower(leftPower);
		rightMotor.setPower(rightPower);
		String s = "Left:  "+leftMotor.getPower()+":"+leftPower+
		         "\nRight: "+rightMotor.getPower()+":"+rightPower;
		System.out.println(s);
	}

//	public void setLeftPower(int power) {
//		this.leftMotor.setPower(power);
//	}
//
//	public void setRightPower(int power) {
//		this.rightMotor.setPower(power);
//	}
}