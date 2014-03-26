import java.io.DataOutputStream;
import java.io.IOException;


public class OutputThread extends Thread{

    private InputThread inputThread;
    private DataOutputStream output;
    String _messageToSend = "";
    boolean done = false;

    public OutputThread( DataOutputStream output, InputThread inputThread ) {
        this.output = output;
        this.inputThread = inputThread;
        this.inputThread.setOutputThread( this );
    }

    @Override
    public void run(){
        while( !done ) {
            if( _messageToSend.isEmpty() ) yield();
            else
                try{
                    output.writeChars( _messageToSend );
                    output.flush();
                    System.out.println("Message sent!");
                    _messageToSend = "";
                }
                catch( IOException e ){
                    e.printStackTrace();
                }
        }
    }

    public void sendMessage( String message ) {
        _messageToSend = message;
    }
}
