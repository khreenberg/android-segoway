import lejos.nxt.MotorPort;
import lejos.nxt.NXTMotor;


public class SegowayMotor extends Thread {
	
	private int power = 0;
	NXTMotor motor;
	
	public SegowayMotor(MotorPort motorPort) {
		this.motor = new NXTMotor(motorPort);
		setPower(power);
		motor.forward();
	}
	
	@Override
	public void run() {
		while(true) {

		}
	}

	public int getPower() {
		return power;
	}

	public void setPower(int power) {
//		if (power > 100){
//			this.power = 100;
//		} else if (power > -100) {
//			this.power = -100;
//		} else {
			this.power = power;
//		}
		motor.setPower(this.power);
	}
}
