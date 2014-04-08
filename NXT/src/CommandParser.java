public class CommandParser{

    static final int LEFT_MOTOR_START_BIT   = 0,
                     RIGHT_MOTOR_START_BIT  = 8;

    static final int WHEEL_MOTOR_MASK = 127, // 1111111 (7 1's)
                     SIGN_MASK        =   1; //       1

    static final int POSITIVE_BIT = 1,
                     NEGATIVE_BIT = 0;

    public static int leftMotor(short command) {
        int signBit = getValue(command, LEFT_MOTOR_START_BIT, SIGN_MASK);
        int sign = signBit == NEGATIVE_BIT ? -1 : 1;
        int motorSpeed = getValue( command, LEFT_MOTOR_START_BIT + 1, WHEEL_MOTOR_MASK );
        return motorSpeed * sign;
    }

    public static int rightMotor(short command) {
        int signBit = getValue(command, RIGHT_MOTOR_START_BIT, SIGN_MASK);
        int sign = signBit == NEGATIVE_BIT ? -1 : 1;
        int motorSpeed = getValue( command, RIGHT_MOTOR_START_BIT + 1, WHEEL_MOTOR_MASK );
        return motorSpeed * sign;
    }

    private static int getValue(short command, int offset, int mask) {
        return mask & command >> offset;
    }
}
