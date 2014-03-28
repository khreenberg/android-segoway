package khr.easv.pokebotcontroller.app.gui.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import khr.easv.pokebotcontroller.app.R;
import khr.easv.pokebotcontroller.app.data.BluetoothConnector;
import khr.easv.pokebotcontroller.app.data.ReaderThread;


public class TestActivity extends ActionBarActivity implements Observer {

    // Device address MUST be uppercase hex.. :o
    public static final String DEVICE_ADDRESS = "00:16:53:1A:05:C1";
    public TestActivity self;

    BluetoothConnector connector;

    EditText txtInput;
    TextView txtOutput;
    Button btnSend;
    Button btnConnect;
    Button btnClear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
            initialize();
            self = this;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.menu_exit) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void initialize(){
        initializeViews();
        connector = new BluetoothConnector(DEVICE_ADDRESS);
        initializeButtons();
    }

    void initializeViews(){
        txtInput = (EditText) findViewById(R.id.txtInput);
        txtOutput = (TextView) findViewById(R.id.txtOutput);
        btnSend = (Button) findViewById(R.id.btnSend);
        btnConnect = (Button) findViewById(R.id.btnConnect);
        btnClear = (Button) findViewById(R.id.btnClear);
        clearOutput();
    }

    void initializeButtons(){
        writeOutput("Setting up send button...");
        setupSendButton();
        writeOutput("Setting up clear button...");
        setupClearButton();
        writeOutput("Setting up connect button...");
        setupConnectButton();
        writeOutput("All buttons set up.");
    }

    void setupSendButton(){
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = txtInput.getText().toString();
                if( message.isEmpty() ) return;
                try {
                    connector.sendMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                txtInput.setText("");
            }
        });
    }

    void setupConnectButton(){
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    connector.connect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                writeOutput("Connected!\n");
                ReaderThread t = new ReaderThread(connector);
                t.addObserver(self);
                t.start();
            }
        });
    }

    void setupClearButton(){
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearOutput();
            }
        });
    }

    public void writeOutput( String str ){
        txtOutput.append(str + "\n");
    }

    public void clearOutput(){
        txtOutput.setText("");
    }

    @Override
    public void update(Observable observable, final Object data) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String msg = (String) data;
                writeOutput(msg + "\n");
            }
        });
    }
}
