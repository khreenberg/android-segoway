package khr.easv.pokebotbroadcaster.app.logic;

public class PacketCreator {

    static final short
            MOTOR_BIT_MASK         =  127, // 2^7-1
            MAX_MOTOR_POWER        =  100; // See: http://www.lejos.org/nxt/nxj/api/lejos/robotics/DCMotor.html#setPower(int)

    static final short
            LEFT_MOTOR_START_BIT   =    0, // [|--------------]
            RIGHT_MOTOR_START_BIT  =    8; // [-------|-------]

    static final short
            POSITIVE_BIT           =    1, // +
            NEGATIVE_BIT           =    0; // -

    public static short createPacket(short leftMotorPower, short rightMotorPower) {

        short packet = 0;

        packet = setLeftMotorPower(packet, leftMotorPower);
        packet = setRightMotorPower(packet, rightMotorPower);

        return packet;
    }

    private static short setLeftMotorPower(short packet, short power){

        short signBit = power < 0 ? NEGATIVE_BIT : POSITIVE_BIT;
        short absPower = (short)Math.abs(power);

        // If the absolute value of the motor power is bigger than the constant, throw an exception
        if (absPower > MAX_MOTOR_POWER)
            throw new IllegalArgumentException("Absolute value of speed parameter must be less than " + MAX_MOTOR_POWER);

        packet = setValueAt(packet, signBit, LEFT_MOTOR_START_BIT, 1);
        packet = setValueAt(packet, absPower, LEFT_MOTOR_START_BIT+1, MOTOR_BIT_MASK);

        return packet;
    }

    private static short setRightMotorPower(short packet, short power){

        short signBit = power < 0 ? NEGATIVE_BIT : POSITIVE_BIT;
        short absPower = (short)Math.abs(power);

        // If the absolute value of the motor power is bigger than the constant, throw an exception
        if (absPower > MAX_MOTOR_POWER)
            throw new IllegalArgumentException("Absolute value of power parameter must be less than " + MAX_MOTOR_POWER);

        packet = setValueAt(packet, signBit, RIGHT_MOTOR_START_BIT, 1);
        packet = setValueAt(packet, absPower, RIGHT_MOTOR_START_BIT+1, MOTOR_BIT_MASK);

        return packet;
    }

    private static short setValueAt(short packet, short value, int bitIndex, int mask){

        packet &= ~(mask << bitIndex); // Clear the bits
        packet |= value << bitIndex;   // Set the value

        return packet;
    }
}
