package src;

/**
 * @author anthonymonori, khreenberg
 * CommandParser static class that parses a 16b/2B short packet which contains 
 * left and right motor speed with a sign information in it. The motor power 
 * value is between -100 and 100 and is stored as an int, as performance is 
 * only an issue through the bluetooth connection. It is built upon a bit 
 * manipulation technique called bit-shifting.
 * 
 *  @see https://github.com/khreenberg/android-segoway/wiki/Protocol
 *  @see http://docs.oracle.com/javase/tutorial/java/nutsandbolts/op3.html
 */
public class CommandParser{

    static final int LEFT_MOTOR_START_BIT   = 	0, // [|--------------]
                     RIGHT_MOTOR_START_BIT  = 	8; // [-------|-------]

    static final int MOTOR_POWER_MASK 		= 127, // 1111111 (7 1's)
                     SIGN_MASK        		=   1; //       1

    static final int POSITIVE_BIT 			= 	1, // +
                     NEGATIVE_BIT 			= 	0; // -

    /**
     * Method to parse left motor power throught bit-shifting, by using some constant masks declared in the class.
     * Read more about the protocol here: http://docs.oracle.com/javase/tutorial/java/nutsandbolts/op3.html
     * 
     * @param packet the short packet received from the controller (phone).
     * @return int representing motor value.
     */
    public static int leftMotor(short packet) {
        int signBit = getValue(packet, LEFT_MOTOR_START_BIT, SIGN_MASK);
        int sign = signBit == NEGATIVE_BIT ? -1 : 1;
        int motorSpeed = getValue( packet, LEFT_MOTOR_START_BIT + 1, MOTOR_POWER_MASK );
        return motorSpeed * sign; // apply the sign to the absolute value of the motor power.
    }

    /**
     * Method to parse right motor power throught bit-shifting, by using some constant masks declared in the class.
     * Read more about the protocol here: http://docs.oracle.com/javase/tutorial/java/nutsandbolts/op3.html
     * 
     * @param packet the short packet received from the controller (phone).
     * @return int representing the motor value
     */
    public static int rightMotor(short packet) {
        int signBit = getValue(packet, RIGHT_MOTOR_START_BIT, SIGN_MASK);
        int sign = signBit == NEGATIVE_BIT ? -1 : 1; // get value by comparing the mask to the value in the packet.
        int motorSpeed = getValue( packet, RIGHT_MOTOR_START_BIT + 1, MOTOR_POWER_MASK );
        return motorSpeed * sign; // apply the sign to the absolute value of the motor power.
    }

    /**
     * @param packet the packet to parse.
     * @param offset the offset number where to start from.
     * @param mask mask to apply.
     * @return int parsed value from packet.
     */
    private static int getValue(short packet, int offset, int mask) {
        return mask & packet >> offset;
    }
}
