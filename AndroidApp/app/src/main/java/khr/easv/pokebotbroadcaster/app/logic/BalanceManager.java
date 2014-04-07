package khr.easv.pokebotbroadcaster.app.logic;

import khr.easv.pokebotbroadcaster.app.data.OrientationWrapper;

public class BalanceManager implements OrientationWrapper.OrientationListener {

    float _pitch = 0; // initial pitch

    Thread _thread;
    boolean done = false;

    public BalanceManager() {

    }

    public void start() {
        _thread = new Thread(new Runnable() {
            @Override
            public void run() {
                loop();
            }
        });
        _thread.start();
    }

    public void loop() {
        while (!done) {
            PID();
        }
    }

    /*
    This algorithm calculates the required motor-power for brick to get into equilibrium by using the angle from the orientation sensors.
    Note: PID stands for Proportional-Integral-Derivative controller

    At a later point, we might want to tweak it a bit to take the pendulum more into account. Calculating the necessary acceleration for
    the pendulum would be:

    d2tetha / dt2 = 12 ( g * cos tetha - a * sin tetha ) / L

    where   tetha = pitch
            g     = gravity
            a     = acceleration
            L     = pendulum length
            t     = time
            d     = shorthand for delta

    then we set d2tetha / dt2 to 0 (zero ), and we get the following formula:

    a = g cos tetha

    Conclusion: required acceleration is the product between the gravity and the cosine value of the pitch.

     */
    public int PID() {
        //      TODO: Fine-tune these values between (-1..1)
        //      Mathematical gyrations occur during Ziegler-Nichols tuning. With this technique, I and D gains are set to zero and then P gain is increased until the loop output starts to oscillate.
        //
        //      Proportional - The product of gain and measured error (ε), where offset is inevitable
        double  PFactor=0.5;                     // Higher will overshoot, creating oscillation; lower creates negligible output
        //      Integral - Eliminate steady state offset, by collecting error (ε) until it's large enough.
        double  IFactor=0.25;                    // The shorter the integral factor, the more aggressive the integral.
        //      Derivative - Corrects present error (ε) compared to the error from last time we checked, a.k.a. the rate of change of the error Δε.
        double  DFactor=-0.3;                    // The larger the derivative factor, the longer the derivative time, but also dampens P and I.

        // Needed variables
        double
                prev_pos,
                pos = 0,                          // The 'P' in PID
                integral_sum = 0,                 // The 'I'
                differential;	                  // The 'D'

        // The output
        int     motor_power = 0;

        //
        while   (Math.abs(pos) < 15)              // TODO: Come up with a better angle after some experiments / or consider automating
        {
            prev_pos = pos;
            pos = Math.ceil(_pitch);
            differential = pos - prev_pos;        // difference between current and previous angles for ex. 15-14
            integral_sum = integral_sum + pos;

            // The actual algorithm
            motor_power = (int)(PFactor*pos + DFactor*differential + IFactor*integral_sum);
//            motor_power = motor_power * 10  // increase precision
//            Logger.debug("Power:" + motor_power);
        }

        return motor_power;
    }

//    public int createPacketFromOrientation(double  azimuth, double pitch, double roll){
//        int leftWheelPower = (int) pitch;
//        int rightWheelPower = (int) pitch;
//
//        int packet = new PacketCreator()
//                .setLeftMotorSpeed(leftWheelPower)
//                .setRightMotorSpeed(rightWheelPower)
//                .setRiderAngle(0) // Not really used anymore TODO: Consider taking it out completely.
//                .getPacket();
//
//        return packet;
//    }

    public int createPacketFromController(int power){
        int leftWheelPower = (int) power;
        int rightWheelPower = (int) power;

        int packet = new PacketCreator()
                .setLeftMotorSpeed(leftWheelPower)
                .setRightMotorSpeed(rightWheelPower)
                .setRiderAngle(0) // Not really used anymore TODO: Consider taking it out completely.
                .getPacket();

        return packet;
    }

    /*
    Sets the pitch on each tiniest orientation change.
     */
    @Override
    public void onOrientationChanged(float azimuth, float pitch, float roll) {
        this._pitch = pitch;
    }
}
