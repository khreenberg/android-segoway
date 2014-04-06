import java.io.DataInputStream;
import java.io.IOException;

public class Receiver extends Thread {

	private DataInputStream input;
	private Pilot pilot;

	public Receiver(DataInputStream input, Pilot pilot) {
		this.setInput(input);
		this.pilot = pilot;
	}

	@Override
	public void run() {
		// Handle the input until the thread stops
		while(true) {
			handleInput();
		}
	}


    void handleInput(){
        try{
            int packet = input.readInt();
            pilot.updateSpeed(packet);
        }
        catch( IOException e ){
            System.out.println("Error while reading from stream.\n"+e.getLocalizedMessage());
        }
    }

    private void setInput(DataInputStream input) {
    	this.input = input;
    }
}
