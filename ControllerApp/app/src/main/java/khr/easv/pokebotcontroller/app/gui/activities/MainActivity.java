package khr.easv.pokebotcontroller.app.gui.activities;

import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
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
import khr.easv.pokebotcontroller.app.gui.fragments.ButtonControlFragment;


public class MainActivity extends FragmentActivity {

    // Device address MUST be uppercase hex.. :o
    public static final String DEVICE_ADDRESS = "00:16:53:1A:05:C1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if( savedInstanceState != null ) return; // Prevent overlapping fragments
        ButtonControlFragment buttonControl = new ButtonControlFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.controllerFragmentContainer, buttonControl)
                .commit();
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
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
