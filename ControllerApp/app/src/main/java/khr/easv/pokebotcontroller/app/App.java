package khr.easv.pokebotcontroller.app;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

/** This class allows access to context and resources outside of GUI classes */
public class App extends Application {

    private static Context _context;

    @Override
    public void onCreate() {
        super.onCreate();
        _context = this;
    }

    public static Context getContext(){
        return _context;
    }

}
