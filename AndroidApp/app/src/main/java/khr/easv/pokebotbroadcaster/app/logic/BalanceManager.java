package khr.easv.pokebotbroadcaster.app.logic;

import java.util.HashSet;

import khr.easv.pokebotbroadcaster.app.data.OrientationWrapper;

public class BalanceManager implements OrientationWrapper.IOrientationListener {

    // Observers
    private HashSet<IPidListener> _listeners    = new HashSet<IPidListener>();

    // Timing and delay
    private long                _lastCalled;
    private static final long   PID_DELAY       = 10;

    // The pitch at which the robot is in perfect balance. (Depends on the robot)
    private final double        OPTIMAL_INPUT   = 0.442;

    // Maximum and minimum possible outputs
    private final short         MIN_POWER       = -100,
                                MAX_POWER       =  100;

    // PID related variables
    private double              _input          = 0,             // pitch
                                _previousInput  = OPTIMAL_INPUT, // last pitch
                                _error          = 0,             // The 'P'
                                _integral = 0,             // The 'I'
                                _differential   = 0;             // The 'D'

    //    TODO: Fine-tune these values
    //    Mathematical gyrations occur during Ziegler-Nichols tuning. With this technique, I and
    //    D gains are set to zero and then P gain is increased until the loop output starts to
    //    oscillate.

    /** Proportional - The product of gain and measured _error (ε), where offset is inevitable.
      * Higher will overshoot, creating oscillation; lower creates negligible output. */
    private final double    K_P = 0.3;

    /** Integral - Eliminate steady state offset, by collecting _error (ε) until it's large
      * enough. The shorter the integral factor, the more aggressive the integral. */
    private final double    K_I = 0;

    /** Derivative - Corrects present _error (ε) compared to the _error from last time we checked,
      * a.k.a. the rate of change of the _error Δε. The larger the derivative factor, the longer
      * the derivative time, but also dampens P and I. */
    private final double    K_D = -.3;

    // Thread related variables
    private Thread _thread;
    private boolean _done = false;

    // Thread start function
    public void start() {
        _thread = new Thread(new Runnable() {
            @Override
            public void run() {
                loop(); // Run the PID algorithm continuously in a loop
            }
        });
        _thread.start(); // Start!
    }

    // Main function
    public void loop() {
        // Initialize _lastCalled with a value that will cause a PID update
        _lastCalled = System.currentTimeMillis() - PID_DELAY;

        while (!_done) {
            // Due to high amount of packets were being sent to the brick which created lag,
            // we limited the frequency of calculations.
            long  currentTime = System.currentTimeMillis();
            long  deltaTime = currentTime - _lastCalled;

            if( deltaTime > PID_DELAY ){
                PID();
                _lastCalled = currentTime;
            }
            Thread.yield();
        }
    }

    /**
        This algorithm calculates the required motor-power for brick to get into equilibrium by
        using the angle from the orientation sensors.
        Note: PID stands for Proportional-Integral-Derivative controller

        At a later point, we might want to tweak it a bit to take the pendulum more into account.
        Calculating the necessary acceleration fo the pendulum would be:

        d2tetha / dt2 = 12 ( g * cos tetha - a * sin tetha ) / L

        where   tetha = pitch
                g     = gravity
                a     = acceleration
                L     = pendulum length
                t     = time
                d     = shorthand for delta

        then we set d2tetha / dt2 to 0 (zero ), and we get the following formula:

        a = g cos tetha

        Conclusion: The acceleration of the pendulum is the product between the gravity and the
        cosine value of the pitch.
     */
    private void PID() {

        // IO variables
        short motorPower = 0;
        double input = _input; // _input might change during calculations, so we cache it

        double p = _error           = calculateProportional(input);
        double i = _integral        = calculateIntegral();
        double d = _differential    = calculateDifferential(input);

        // The actual algorithm
        motorPower = (short)(p + i - d);

        // Clamping the output
        motorPower = clamp(motorPower, MIN_POWER, MAX_POWER);

        // Preparing for the next run
        _previousInput = input;

        // Flip the motor powers to fit our model / Direction fix
        motorPower *= -1;

        // Create packet and send output
        notifyListeners(PacketCreator.createPacket(motorPower, motorPower));
    }

    private double calculateProportional(double input) {
        return OPTIMAL_INPUT - input;
    }

    private double calculateDifferential(double input) {
        return K_D * (input - _previousInput);
    }

    private double calculateIntegral() {
        double integral = _integral + _error * K_I;
        integral  = clamp(integral, MIN_POWER, MAX_POWER);
        return integral;
    }

    /** Clamps a value between a given minimum and maximum. (inclusive) */
    private double clamp(double value, double min, double max) {
        if( value < min ) return min;
        if( value > max ) return max;
        return value;
    }

    /** Clamps a value between a given minimum and maximum. (inclusive) */
    private short clamp(short value, short min, short max) {
        if( value < min ) return min;
        if( value > max ) return max;
        return value;
    }

    /* When the orientation changes on the device, update the stored input */
    @Override
    public void onOrientationChanged(float azimuth, float pitch, float roll) {
        _input = pitch;
    }

    /** Adds a listener to the PID update subscription list */
    public void addListener(IPidListener listener) {
        _listeners.add(listener);
    }

    /** Removes a listener from the PID update subscription list */
    public void removeListener(IPidListener listener) {
        _listeners.remove(listener);
    }

    /** Notifies all listeners of a PID result */
    private void notifyListeners(short packet){
        for(IPidListener listener : _listeners) {
            listener.onPID(packet);
        }
    }

    /* INTERFACE */
    public interface IPidListener {
        void onPID(short packet);
    }
}
