public class CommandParser{

    static final int LEFT_MOTOR_START_BIT   =  0,
                     RIGHT_MOTOR_START_BIT  = 11,
                     RIDER_MOTOR_START_BIT  = 22;

    static final int WHEEL_MOTOR_MASK = 1023, // 1111111111 (10 1's)
                     RIDER_MOTOR_MASK =   63, //     111111 ( 6 1's)
                     SIGN_MASK        =    1; //          1

    static final int POSITIVE_BIT = 1,
                     NEGATIVE_BIT = 0;

    public static int leftMotor(int command) {
        int signBit = getValue(command, LEFT_MOTOR_START_BIT, SIGN_MASK);
        int sign = signBit == NEGATIVE_BIT ? -1 : 1;
        int motorSpeed = getValue( command, LEFT_MOTOR_START_BIT + 1, WHEEL_MOTOR_MASK );
        return motorSpeed * sign;
    }

    public static int rightMotor(int command) {
        int signBit = getValue(command, RIGHT_MOTOR_START_BIT, SIGN_MASK);
        int sign = signBit == NEGATIVE_BIT ? -1 : 1;
        int motorSpeed = getValue( command, RIGHT_MOTOR_START_BIT + 1, WHEEL_MOTOR_MASK );
        return motorSpeed * sign;
    }

    public static int riderAngle(int command) {
        int signBit = getValue(command, RIDER_MOTOR_START_BIT, SIGN_MASK);
        int sign = signBit == NEGATIVE_BIT ? -1 : 1;
        int riderAngle = getValue( command, RIDER_MOTOR_START_BIT + 1, RIDER_MOTOR_MASK );
        return riderAngle * sign;
    }

    private static int getValue(int command, int offset, int mask) {
        return mask & command >> offset;
    }
}
