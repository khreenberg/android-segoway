package khr.easv.pokebotcontroller.app.gui.activities;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;

import khr.easv.pokebotcontroller.app.R;
import khr.easv.pokebotcontroller.app.data.ControllerConnection;
import khr.easv.pokebotcontroller.app.entities.LogEntry;
import khr.easv.pokebotcontroller.app.entities.Logger;
import khr.easv.pokebotcontroller.app.gui.fragments.AccelerometerControlFragment;
import khr.easv.pokebotcontroller.app.gui.fragments.BluetoothDeviceSelectionFragment;
import khr.easv.pokebotcontroller.app.gui.fragments.ButtonControlFragment;
import khr.easv.pokebotcontroller.app.gui.fragments.ExternalDeviceControlFragment;
import khr.easv.pokebotcontroller.app.gui.fragments.JoystickControlFragment;
import khr.easv.pokebotcontroller.app.gui.fragments.LogEntryDetailsFragment;
import khr.easv.pokebotcontroller.app.gui.fragments.LogFragment;
import khr.easv.pokebotcontroller.app.gui.views.AbstractKnobView;
import khr.easv.pokebotcontroller.app.gui.views.ExternalDeviceControlView;


public class MainActivity extends FragmentActivity implements LogFragment.OnLogEntryClickedListener, BluetoothDeviceSelectionFragment.OnDeviceSelectedListener, AbstractKnobView.KnobUpdateListener {

    private ControllerConnection _connection;

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
                switchControlFragment(new ExternalDeviceControlFragment());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    void setup(){
        _connection = new ControllerConnection();
        setupFragments();
        setupActionBar();
    }

    void setupActionBar(){
        // TODO: Use or remove!
    }

    void setupFragments(){
        BluetoothDeviceSelectionFragment inputSelectionFragment = new BluetoothDeviceSelectionFragment();
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
        LogEntryDetailsFragment detailsFragment = new LogEntryDetailsFragment();
        Bundle fragmentExtras = new Bundle(1);
        fragmentExtras.putSerializable(LogEntryDetailsFragment.BUNDLE_KEY_ENTRY, entry);
        detailsFragment.setArguments(fragmentExtras);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.logFragmentContainer, detailsFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void OnDeviceSelected(BluetoothDevice device) { _connection.connect(device); }

    @Override
    public void onKnobUpdate(float x, float y) {
        Logger.debug(String.format("Input received: (%.3f, %.3f)", x, y));
        _connection.write(x,y);
    }
}
