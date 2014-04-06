package khr.easv.pokebotbroadcaster.app.logic;

public class BalanceManager {
//      SUGGESTION:
//    start loop
//    read from the sensor and pass the values through the PID method then send power data to brick
//    wait 3 ms
//    restart loop

    public static final int DUMMY_MULTIPLIER = 10;

//    TODO: Apply measurements
    public static final double WEIGHT_OBJECT = 0; // wheel weight
    public static final double WEIGHT_PENDULUM = 0; // two motors plus brick
    public static final double HEIGHT_PENDULUM = 0; // from mid-wheel point to the end of brick
//    public static final double GRAVITY = Constants.g.getEstimatedValue(); TODO: Remove the library if it's not necessary


    public static int[] CalculateSpeed() {

        int motor_power = PID();

        // Inverted pendulum
        // here

        int[] speedArray = new int[2];

        speedArray[0] = 0;
        speedArray[1] = 1;

        return speedArray;
    }

    public static int PID() {
        double PFactor=0.5; // how much of the P should we take in?
        double IFactor=0.25;// -- I --
        double DFactor=-0.3;// -- D --

        int prev_pos = 0,
            pos = 0,                          // The 'P' in PID
            integral_sum = 0,                 // The 'I'
            differential = 0;	              // The 'D'

        int motor_power = 0;                  // Our output.  The RCX

        while(Math.abs(pos)/100 < 40)         // while it hasn't tipped
        // over hopelessly far
        {
            prev_pos = pos;
//            pos = pitch * 10;               // increase precision TODO: We need to access pitch here.
            differential = pos - prev_pos;
            integral_sum = integral_sum + pos;


            motor_power = (int)(PFactor*pos + DFactor*differential + IFactor*integral_sum);  // Apply the PID
//            motor_power = motor_power / 100;  // hack
        }

        return motor_power;
    }


    public static int createPacketFromOrientation(double  azimuth, double pitch, double roll){
        // This is just a dummy example. Should use PID algorithm for determining exact wheel speeds
        int leftWheelSpeed = (int) (pitch * DUMMY_MULTIPLIER);
        int rightWheelSpeed = (int) (pitch * DUMMY_MULTIPLIER);

        int packet = new PacketCreator()
                .setLeftMotorSpeed(leftWheelSpeed)
                .setRightMotorSpeed(rightWheelSpeed)
                .setRiderAngle(0) // Not really used anymore TODO: Consider taking it out completely.
                .getPacket();

        return packet;
    }

}
