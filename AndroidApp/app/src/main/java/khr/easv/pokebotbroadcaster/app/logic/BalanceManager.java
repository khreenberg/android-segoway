package khr.easv.pokebotbroadcaster.app.logic;

public class BalanceManager {

    public static int createPacketFromAcceleration(float x, float y, float z){

        // This is just a dummy example. Should use PID algorithm for determining exact wheel speeds
        int leftWheelSpeed  = y < 0 ? 600 : -600,
            rightWheelSpeed = y < 0 ? 600 : -600;

        int packet = new PacketCreator()
                .setLeftMotorSpeed(leftWheelSpeed)
                .setRightMotorSpeed(rightWheelSpeed)
                .getPacket();

        return packet;
    }

}
