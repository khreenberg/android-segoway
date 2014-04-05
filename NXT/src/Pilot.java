import java.util.Random;

import lejos.nxt.Motor;


public class Pilot {

	SegowayMotor leftMotor;
	SegowayMotor rightMotor;
	Random rand;

	public Pilot() {
		this.leftMotor = new SegowayMotor(Motor.A); // hardcoded
		this.rightMotor = new SegowayMotor(Motor.C); // hardcoded
		this.leftMotor.start();
		this.rightMotor.start();
		rand = new Random();
	}

	public void updateSpeed(int packet) {
		setLeftSpeed(CommandParser.leftMotor(packet));
		setRightSpeed(CommandParser.rightMotor(packet));
		System.out.println("Left: " + getLeftSpeed());
		System.out.println("Right: " + getRightSpeed());
	}
	public int getLeftSpeed() {
		return this.leftMotor.getSpeed();
	}
	public void setLeftSpeed(int leftSpeed) {
		this.leftMotor.setSpeed(leftSpeed);
	}
	public int getRightSpeed() {
		return this.rightMotor.getSpeed();
	}
	public void setRightSpeed(int rightSpeed) {
		this.rightMotor.setSpeed(rightSpeed);
	}
}