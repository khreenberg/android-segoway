package khr.easv.pokebotbroadcaster.app.logic;

public class BalanceManager {

    public static final int DUMMY_MULTIPLIER = 10;

    public static int createPacketFromOrientation(double  azimuth, double pitch, double roll){

        // Logic!

        // This is just a dummy example. Should use PID algorithm for determining exact wheel speeds
        int wheelSpeed = (int) (pitch * DUMMY_MULTIPLIER);


        int packet = new PacketCreator()
                .setLeftMotorSpeed(wheelSpeed)
                .setRightMotorSpeed(wheelSpeed)
                .setRiderAngle(0)
                .getPacket();

        return packet;
    }

}
