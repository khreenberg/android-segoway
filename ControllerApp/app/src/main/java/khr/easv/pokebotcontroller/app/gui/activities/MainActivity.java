package khr.easv.pokebotcontroller.app.gui.activities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import khr.easv.pokebotcontroller.app.R;
import khr.easv.pokebotcontroller.app.entities.LogEntry;
import khr.easv.pokebotcontroller.app.gui.Logger;
import khr.easv.pokebotcontroller.app.gui.adapters.BluetoothDeviceListAdapter;
import khr.easv.pokebotcontroller.app.gui.fragments.AccelerometerControlFragment;
import khr.easv.pokebotcontroller.app.gui.fragments.ButtonControlFragment;
import khr.easv.pokebotcontroller.app.gui.fragments.InputDeviceSelectionFragment;
import khr.easv.pokebotcontroller.app.gui.fragments.JoystickControlFragment;
import khr.easv.pokebotcontroller.app.gui.fragments.LogFragment;


public class MainActivity extends FragmentActivity implements LogFragment.OnLogEntryClickedListener {

    private boolean _isConnectedToBrain = false;

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
        switch (id){
            case R.id.menu_exit: return true;
            case R.id.menu_controlButton:
                switchControlFragment(new ButtonControlFragment());
                break;
            case R.id.menu_controlJoystick:
                switchControlFragment(new JoystickControlFragment());
                break;
            case R.id.menu_controlAccelerometer:
                switchControlFragment(new AccelerometerControlFragment());
                break;
            case R.id.menu_controlExternal:
                Logger.warn("External control options not yet supported!", "");
                break;
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
        InputDeviceSelectionFragment inputSelectionFragment = new InputDeviceSelectionFragment();
        LogFragment logFragment = new LogFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.controllerFragmentContainer, inputSelectionFragment)
                .add(R.id.logFragmentContainer, logFragment)
                .commit();
    }

    void switchControlFragment(Fragment newFragment){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.controllerFragmentContainer, newFragment)
                .commit();
    }

    @Override
    public void onLogEntryClicked(LogEntry entry) {
        if( entry.getDetails().isEmpty() ) return;
        Toast.makeText(this, entry.getDetails(), Toast.LENGTH_SHORT).show();
    }
}
