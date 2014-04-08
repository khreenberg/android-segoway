import lejos.nxt.MotorPort;
import lejos.nxt.NXTMotor;


public class SegowayMotor extends Thread {

	NXTMotor motor;

	public SegowayMotor(MotorPort motorPort) {
		motor = new NXTMotor(motorPort);
		motor.forward();
	}

	@Override
	public void run() {
		while(true) {

		}
	}

	public int getPower() {
		return motor.getPower();
	}

	public void setPower(int power) {
		motor.setPower(power);
	}
}
