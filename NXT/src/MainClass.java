import lejos.nxt.Button;

public class MainClass {
	
	private static Connector connection;
	
	/*
	 * Main method for running the Segoway-robot
	 * 
	 * This main method is responsible starting up the worker threads on the brick and stopping them.
	 */
	public static void main(String[] args) {
	
		connection.start();
		
		while(!Button.ESCAPE.isDown()){ // isPressed() is depracated.
			// Run the threads until Escape is pressed
		}
				
		System.exit(0);
	}

}
