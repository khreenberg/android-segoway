import java.util.Random;

import lejos.nxt.MotorPort;

public class Pilot {

	SegowayMotor leftMotor;
	SegowayMotor rightMotor;
	Random rand;

	public Pilot() {
		this.leftMotor = new SegowayMotor(MotorPort.A); // hardcoded
		this.rightMotor = new SegowayMotor(MotorPort.C); // hardcoded
		this.leftMotor.start();
		this.rightMotor.start();
		rand = new Random();
	}

	public void updateSpeed(short packet) {
//		setLeftPower(rand.nextInt(200)-100);
//		setRightPower(rand.nextInt(200)-100);
		setLeftPower(CommandParser.leftMotor(packet));
		setRightPower(CommandParser.rightMotor(packet));
		System.out.println("Left:  " + this.leftMotor.getPower());
		System.out.println("Right: " + this.rightMotor.getPower());
	}
	public void setLeftPower(int power) {
		this.leftMotor.setPower(power);
	}
	public void setRightPower(int power) {
		this.rightMotor.setPower(power);
	}
}