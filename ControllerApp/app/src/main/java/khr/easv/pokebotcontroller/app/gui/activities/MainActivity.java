package khr.easv.pokebotcontroller.app.gui.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import khr.easv.pokebotcontroller.app.R;
import khr.easv.pokebotcontroller.app.entities.LogEntry;
import khr.easv.pokebotcontroller.app.gui.fragments.AccelerometerControlFragment;
import khr.easv.pokebotcontroller.app.gui.fragments.ButtonControlFragment;
import khr.easv.pokebotcontroller.app.gui.fragments.JoystickControlFragment;
import khr.easv.pokebotcontroller.app.gui.fragments.LogFragment;


public class MainActivity extends FragmentActivity implements LogFragment.OnLogEntryClickedListener {

    // Device address MUST be uppercase hex.. :o
    public static final String DEVICE_ADDRESS = "00:16:53:1A:05:C1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if( savedInstanceState != null ) return; // Prevent overlapping fragments
        setup();
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

    void setup(){
        setupFragments();
        setupActionBar();
    }

    void setupActionBar(){

    }

    void setupFragments(){
        // TODO: Clean this up when done testing
        ButtonControlFragment buttonControlFragment = new ButtonControlFragment();
        JoystickControlFragment joystickControlFragment = new JoystickControlFragment();
        AccelerometerControlFragment accelerometerControlFragment = new AccelerometerControlFragment();
        LogFragment logFragment = new LogFragment();
        getSupportFragmentManager()
                .beginTransaction()
//                .add(R.id.controllerFragmentContainer, buttonControlFragment)
//                .add(R.id.controllerFragmentContainer, joystickControlFragment)
                .add(R.id.controllerFragmentContainer, accelerometerControlFragment)
                .add(R.id.logFragmentContainer, logFragment)
                .commit();
    }

    @Override
    public void onLogEntryClicked(LogEntry entry) {
        if( entry.getDetails().isEmpty() ) return;
        Toast.makeText(this, entry.getDetails(), Toast.LENGTH_SHORT).show();
    }
}
