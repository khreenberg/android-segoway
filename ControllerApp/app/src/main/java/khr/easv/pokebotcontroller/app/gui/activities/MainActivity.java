package khr.easv.pokebotcontroller.app.gui.activities;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;

import khr.easv.pokebotcontroller.app.R;
import khr.easv.pokebotcontroller.app.data.ControllerConnection;
import khr.easv.pokebotcontroller.app.data.IInputListener;
import khr.easv.pokebotcontroller.app.entities.LogEntry;
import khr.easv.pokebotcontroller.app.entities.Logger;
import khr.easv.pokebotcontroller.app.gui.fragments.AccelerometerControlFragment;
import khr.easv.pokebotcontroller.app.gui.fragments.BluetoothDeviceSelectionFragment;
import khr.easv.pokebotcontroller.app.gui.fragments.ButtonControlFragment;
import khr.easv.pokebotcontroller.app.gui.fragments.ExternalDeviceControlFragment;
import khr.easv.pokebotcontroller.app.gui.fragments.JoystickControlFragment;
import khr.easv.pokebotcontroller.app.gui.fragments.LogEntryDetailsFragment;
import khr.easv.pokebotcontroller.app.gui.fragments.LogFragment;


public class MainActivity extends FragmentActivity implements LogFragment.ILogEntryClickedListener,
        BluetoothDeviceSelectionFragment.IDeviceSelectedListener, IInputListener{

    private ControllerConnection _connection;

    private Class _currentControlFragment;
    private LogFragment _logFragment;

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
        int id = item.getItemId();
        switch (id){
            case R.id.menu_exit:
                killApp();
                break;
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
                switchControlFragment(new ExternalDeviceControlFragment());
                break;
            case R.id.menu_select_brain_device:
                switchControlFragment(new BluetoothDeviceSelectionFragment());
                break;
            case R.id.menu_clearLog:
                clearLog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setup(){
        _connection = new ControllerConnection();
        setupFragments();
    }

    private void setupFragments(){
        BluetoothDeviceSelectionFragment inputSelectionFragment = new BluetoothDeviceSelectionFragment();
        _currentControlFragment = ((Object)inputSelectionFragment).getClass();
        _logFragment = new LogFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.controllerFragmentContainer, inputSelectionFragment)
                .add(R.id.logFragmentContainer, _logFragment)
                .commit();
    }

    private void switchControlFragment(Fragment newFragment){
        // Check that the user is not trying to switch to the current fragment..
        if (!shouldReplaceFragment(newFragment)) return;
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.controllerFragmentContainer, newFragment)
                .commit();
    }

    private boolean shouldReplaceFragment(Object newFragment) {
        Class newFragmentClass = newFragment.getClass();
        if( _currentControlFragment == newFragmentClass ) return false;
        _currentControlFragment = newFragmentClass;
        return true;
    }

    private void clearLog() {
        Logger.clearEntries();
        _logFragment.clear();
    }

    private void killApp() {
        System.runFinalization();
        System.exit(0);
    }

    /////////// CALL-BACKS ////////////
    @Override
    public void onLogEntryClicked(LogEntry entry) {
        if( entry.getDetails().isEmpty() ) return;
        LogEntryDetailsFragment detailsFragment = new LogEntryDetailsFragment();
        Bundle fragmentExtras = createLogDetailsBundle(entry);
        detailsFragment.setArguments(fragmentExtras);
        showLogDetails(detailsFragment);
    }

    private Bundle createLogDetailsBundle(LogEntry entry) {
        Bundle fragmentExtras = new Bundle(1);
        fragmentExtras.putSerializable(LogEntryDetailsFragment.BUNDLE_KEY_ENTRY, entry);
        return fragmentExtras;
    }

    private void showLogDetails(LogEntryDetailsFragment detailsFragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.logFragmentContainer, detailsFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onDeviceSelected(BluetoothDevice device) { _connection.connect(device); }

    @Override
    public void onInput(float x, float y) {
        // Send the input to the brain
        _connection.write(x,y);
    }
}
