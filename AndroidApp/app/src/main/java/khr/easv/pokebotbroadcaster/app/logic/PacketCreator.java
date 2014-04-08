package khr.easv.pokebotbroadcaster.app.logic;

public class PacketCreator {

    static final int
            MAX_MOTOR_POWER        = 1023; //TODO: Figure out what to do after updating the protocol.

    static final int
            LEFT_MOTOR_START_BIT   =   0,
            RIGHT_MOTOR_START_BIT  =  11;

    static final int
            POSITIVE_BIT           =   1,
            NEGATIVE_BIT           =   0;

    public static int createPacket(int leftMotorPower, int rightMotorPower) {
        int packet = 0;
        packet = setLeftMotorPower(packet, leftMotorPower);
        packet = setRightMotorPower(packet, rightMotorPower);
        return packet;
    }

    private static int setLeftMotorPower(int packet, int speed){
        int signBit = speed < 0 ? NEGATIVE_BIT : POSITIVE_BIT;
        int absSpeed = Math.abs(speed);
        if (absSpeed > MAX_MOTOR_POWER)
            throw new IllegalArgumentException("Absolute value of speed parameter must be less than " + MAX_MOTOR_POWER);
        packet = setValueAt(packet, signBit, LEFT_MOTOR_START_BIT, 1);
        packet = setValueAt(packet, absSpeed, LEFT_MOTOR_START_BIT+1, MAX_MOTOR_POWER);
        return packet;
    }

    private static int setRightMotorPower(int packet, int speed){
        int signBit = speed < 0 ? NEGATIVE_BIT : POSITIVE_BIT;
        int absSpeed = Math.abs(speed);
        if (absSpeed > MAX_MOTOR_POWER)
            throw new IllegalArgumentException("Absolute value of speed parameter must be less than " + MAX_MOTOR_POWER);
        packet = setValueAt(packet, signBit, RIGHT_MOTOR_START_BIT, 1);
        packet = setValueAt(packet, absSpeed, RIGHT_MOTOR_START_BIT+1, MAX_MOTOR_POWER);
        return packet;
    }

    private static int setValueAt(int packet, int value, int bitIndex, int mask){
        packet &= ~(mask << bitIndex); // Clear the bits
        packet |= value << bitIndex;   // Set the value
        return packet;
    }
}
