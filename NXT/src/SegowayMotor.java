import lejos.nxt.NXTRegulatedMotor;


public class SegowayMotor extends Thread {
	
	private int speed = 1;
	NXTRegulatedMotor motor;
	
	public SegowayMotor(NXTRegulatedMotor motor) {
		this.motor = motor;
		motor.forward();
	}
	
	@Override
	public void run() {
		while(true) {
			if (speed > 0) {
				motor.forward();
			} else {
				motor.backward();
			}
		}
	}
	
	public int getSpeed() {
		return this.speed;
	}
	
	public void setSpeed(int speed) {
		this.speed = speed;
		motor.setSpeed(speed);
	}
	
	public void stop() {
		motor.setSpeed(0);
		motor.stop();
	}
}
