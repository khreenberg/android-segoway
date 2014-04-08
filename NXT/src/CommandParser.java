public class CommandParser{

    static final int LEFT_MOTOR_START_BIT   = 0,
                     RIGHT_MOTOR_START_BIT  = 8;

    static final int MOTOR_POWER_MASK = 127, // 1111111 (7 1's)
                     SIGN_MASK        =   1; //       1

    static final int POSITIVE_BIT = 1,
                     NEGATIVE_BIT = 0;

    public static int leftMotor(int packet) {
        int signBit = getValue(packet, LEFT_MOTOR_START_BIT, SIGN_MASK);
        int sign = signBit == NEGATIVE_BIT ? -1 : 1;
        int motorSpeed = getValue( packet, LEFT_MOTOR_START_BIT + 1, MOTOR_POWER_MASK );
        return motorSpeed * sign;
    }

    public static int rightMotor(int packet) {
        int signBit = getValue(packet, RIGHT_MOTOR_START_BIT, SIGN_MASK);
        int sign = signBit == NEGATIVE_BIT ? -1 : 1;
        int motorSpeed = getValue( packet, RIGHT_MOTOR_START_BIT + 1, MOTOR_POWER_MASK );
        return motorSpeed * sign;
    }

    private static int getValue(int packet, int offset, int mask) {
        return mask & packet >> offset;
    }
}
