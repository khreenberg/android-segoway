package khr.easv.pokebotbroadcaster.app.logic;

/*
 * Suggested use:
 * int packet = new PacketCreator()
 *                    .setLeftMotorSpeed(600)
 *                    .setRightMotorSpeed(-600)
 *                    .setRiderAngle(0)
 *                    .getPacket();
 */
public class PacketCreator {

    private int _packet;
    static final int
            MAX_WHEEL_MOTOR_SPEED = 1023,
            MAX_RIDER_ANGLE       =   63;

    static final int
            LEFT_MOTOR_START_BIT   =  0,
            RIGHT_MOTOR_START_BIT  = 11,
            RIDER_MOTOR_START_BIT  = 22;

    static final int
            POSITIVE_BIT = 1,
            NEGATIVE_BIT = 0;

    public PacketCreator(){
        _packet = 0;
    }

    public PacketCreator(int packet){ _packet = packet; }

    public PacketCreator setLeftMotorSpeed( int speed ){
        int signBit = speed < 0 ? NEGATIVE_BIT : POSITIVE_BIT;
        int absSpeed = Math.abs(speed);
        if (absSpeed > MAX_WHEEL_MOTOR_SPEED)
            throw new IllegalArgumentException("Absolute value of speed parameter must be less than " + MAX_WHEEL_MOTOR_SPEED);
        setValueAt(signBit, LEFT_MOTOR_START_BIT, 1);
        setValueAt(absSpeed, LEFT_MOTOR_START_BIT+1, MAX_WHEEL_MOTOR_SPEED);
        return this;
    }

    public PacketCreator setRightMotorSpeed( int speed ){
        int signBit = speed < 0 ? NEGATIVE_BIT : POSITIVE_BIT;
        int absSpeed = Math.abs(speed);
        if (absSpeed > MAX_WHEEL_MOTOR_SPEED)
            throw new IllegalArgumentException("Absolute value of speed parameter must be less than " + MAX_WHEEL_MOTOR_SPEED);
        setValueAt(signBit, RIGHT_MOTOR_START_BIT, 1);
        setValueAt(absSpeed, RIGHT_MOTOR_START_BIT+1, MAX_WHEEL_MOTOR_SPEED);
        return this;
    }

    public PacketCreator setRiderAngle( int angle ){
        int signBit = angle < 0 ? NEGATIVE_BIT : POSITIVE_BIT;
        int absAngle = Math.abs(angle);
        if (absAngle > MAX_RIDER_ANGLE)
            throw new IllegalArgumentException("Absolute value of angle parameter must be less than " + MAX_RIDER_ANGLE);
        setValueAt(signBit, RIDER_MOTOR_START_BIT, 1);
        setValueAt(absAngle, RIDER_MOTOR_START_BIT+1, MAX_RIDER_ANGLE);
        return this;
    }

    public PacketCreator clear(){
        _packet = 0;
        return this;
    }
    public int getPacket(){ return _packet; }

    private void setValueAt(int value, int bitIndex, int mask){
        _packet &= ~(mask << bitIndex); // Clear the bits
        _packet |= value << bitIndex;   // Set the value
    }
}
