package khr.easv.pokebotbroadcaster.app.logic;

import java.util.HashSet;

import khr.easv.pokebotbroadcaster.app.data.OrientationWrapper;

public class BalanceManager implements OrientationWrapper.OrientationListener {

    // Observer pattern related variables
    HashSet<PIDListener>    _listeners;

    // PID related variables
    double                  _input = 0; // initial pitch
    final double             OPTIMAL_INPUT = 0.442; // optimal angle for the perfect balance

    long                    _lastCalled; // last time the calculation was done in millis.
    double                  _previousInput = OPTIMAL_INPUT, // last input
                            _integralSum = 0, // The 'I'
                            _differential = 0, // The 'D'
                            _error = 0;

    private static final long PID_DELAY = 10;

    final short             MIN_POWER = -100,
                            MAX_POWER = 100;

    //    TODO: Fine-tune these values
    //    Mathematical gyrations occur during Ziegler-Nichols tuning. With this technique, I and
    //    D gains are set to zero and then P gain is increased until the loop output starts to
    //    oscillate.


    //    Proportional - The product of gain and measured _error (ε), where offset is inevitable
    final double            K_P = .5;               // Higher will overshoot, creating
                                                    // oscillation; lower creates negligible output.

    //    Integral - Eliminate steady state offset, by collecting _error (ε) until it's large enough.
    final double            K_I = .25;              // The shorter the integral factor, the more
                                                    // aggressive the integral.

    //      Derivative - Corrects present _error (ε) compared to the _error from last time we
    //      checked, a.k.a. the rate of change of the _error Δε.
    final double            K_D = -.3;              // The larger the derivative factor, the longer
                                                    // the derivative time, but also dampens P and I.


    // Thread related variables
    Thread _thread;
    boolean done = false;

    /*
        Constructor
     */
    public BalanceManager() {
        _listeners = new HashSet<PIDListener>();
    }


    // Thread start function
    public void start() {
        _thread = new Thread(new Runnable() { // Create new thread
            @Override
            public void run() {
                loop(); // Call main function
            }
        });
        _thread.start(); // Start!
    }


    // Main function
    public void loop() {
        // Checking if it was ever called before, and because it most likely wasn't we set it to the
        // current time to not get an immense delta time.
        _lastCalled = System.currentTimeMillis();

        // Set to 0 in the first call;
        _previousInput = 0;

        while (!done) {
            PID();
        }
    }

    /*
    This algorithm calculates the required motor-power for brick to get into equilibrium by using
    the angle from the orientation sensors.
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

    Conclusion: The acceleration of the pendulum is the product between the gravity and the cosine
    value of the pitch.

     */
    private void PID() {

        // Needed variables
        double input = _input;         // The underscore input might change during calculations

        long
                currentTime = System.currentTimeMillis(),
                deltaTime = currentTime - _lastCalled;

        // The output variable
        short motor_power = 0;

        // Due to high amount of packets were being sent to the brick which created lag, we limited
        // the number of calculations done by the algorithm
        if (deltaTime > PID_DELAY){

            _error = OPTIMAL_INPUT - input;

            _integralSum = _integralSum + _error * K_I;

            // Clamping the integral sum
            if (_integralSum > MAX_POWER) {
                _integralSum = MAX_POWER;
            } else if (_integralSum < MIN_POWER){
                _integralSum = MIN_POWER;
            }

            _differential = input - _previousInput;

            // The actual algorithm
            motor_power = (short)((K_P * _error) + (_integralSum) - (K_D *_differential));

            // Clamping the output
            if (motor_power > MAX_POWER) {
                motor_power = MAX_POWER;
            } else if (motor_power < MIN_POWER){
                motor_power = MIN_POWER;
            }

            // Preparing for the next run
            _previousInput = input;
            _lastCalled = currentTime;

            // Flip the motor powers to fit our model / Direction fix
            motor_power *= -1;

            // Create packet and send output
            notifyListeners(PacketCreator.createPacket(motor_power, motor_power));
        }
    }

    /* When the orientation changes on the device, update the stored input */
    @Override
    public void onOrientationChanged(float azimuth, float pitch, float roll) {
        this._input = pitch;
    }

    /* For adding a new listener */
    public void addListener(PIDListener listener) {
        _listeners.add(listener);
    }

    /* For removing listeners*/
    public void removeListener(PIDListener listener) {
        _listeners.remove(listener);
    }

    // Observer pattern applied
    private void notifyListeners(short packet){
        for(PIDListener listener : _listeners) {
            listener.onPID(packet);
        }
    }

    /* INTERFACE */
    public interface PIDListener {
        void onPID(short packet);
    }
}
