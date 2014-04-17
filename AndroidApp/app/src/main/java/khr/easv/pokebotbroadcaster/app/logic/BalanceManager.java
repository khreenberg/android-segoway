package khr.easv.pokebotbroadcaster.app.logic;

import android.util.Log;

import java.util.HashSet;

import khr.easv.pokebotbroadcaster.app.data.OrientationWrapper;

public class BalanceManager implements OrientationWrapper.OrientationListener {

    HashSet<PIDListener> _listeners;

    float _pitch = 0; // initial pitch
    final float OPTIMAL_PITCH = 0;

    long lastCalled = -1; // for checking
    double prev_pos;

    final short MIN_POWER = 0;
    final short MAX_POWER = 100;

    Thread _thread;
    boolean done = false;

    public BalanceManager() {
        _listeners = new HashSet<PIDListener>();
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
        // Checking if it was ever called before, and because it most likely wasn't we set it to the current time to not get an imense delta time
        lastCalled = System.currentTimeMillis();
        prev_pos = 0;

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
    private void PID() {


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
                pos             = _pitch,         // The 'P' in PID
                integral_sum    = 0,              // The 'I'
                differential,	                  // The 'D'
                error;

        double
                currentTime = System.currentTimeMillis(),
                deltaTime = currentTime - lastCalled;


        // The output
        short   motor_power     = 0;

        int     counter         = 0;

        //
        if (deltaTime > 100){

            //P
            error = OPTIMAL_PITCH - pos;


            //I
            integral_sum = integral_sum + error * IFactor;
            if (integral_sum > MAX_POWER) {
                integral_sum = MAX_POWER;
            } else if (integral_sum < MIN_POWER){
                integral_sum = MIN_POWER;
            }

            //D
            differential = pos - prev_pos;        // difference between current and previous angles for ex. 15-14

            // The actual algorithm
            motor_power = (short)((PFactor*error) + (integral_sum) - (DFactor*differential));

            if (motor_power > MAX_POWER) {
                motor_power = MAX_POWER;
            } else if (motor_power < MIN_POWER){
                motor_power = MIN_POWER;
            }

            prev_pos = pos;

            // DEBUGGING
            counter++;

                Log.d("P", "Pout:" + PFactor*error);
                Log.d("I", "Iout::" + integral_sum);
                Log.d("D", "Dout:" + DFactor*differential);
                Log.d("PID", "Power:" + motor_power);
            

            notifyListeners(PacketCreator.createPacket(motor_power, motor_power));
        }
    }

    @Override
    public void onOrientationChanged(float azimuth, float pitch, float roll) {
        this._pitch = pitch;
    }


    public interface PIDListener {
        void onPID(short packet);
    }

    public void addListener(PIDListener listener) {
        _listeners.add(listener);
    }

    public void removeListener(PIDListener listener) {
        _listeners.remove(listener);
    }

    private void notifyListeners(short packet){
        for(PIDListener listener : _listeners) {
            listener.onPID(packet);
        }
    }
}
