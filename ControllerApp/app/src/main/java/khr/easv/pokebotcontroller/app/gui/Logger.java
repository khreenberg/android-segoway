package khr.easv.pokebotcontroller.app.gui;

import android.util.Log;

import java.util.HashSet;
import khr.easv.pokebotcontroller.app.entities.LogEntry;

public class Logger {

    static HashSet<LoggerListener> observers = new HashSet<LoggerListener>();

    public static void log(String title, String details, LogEntry.LogTag tag){
        LogEntry entry = new LogEntry(title, details, tag);
        notify(entry);
        Log.d(tag.toString(), title);
    }

    public static void log( Exception e ){
        error(e.toString(), convertStackTraceToDetails(e.getStackTrace()));
    }

    public static void debug(String title){
        debug(title, "");
    }

    public static void debug(String title, String details){
        log(title, details, LogEntry.LogTag.DEBUG);
    }

    public static void info(String title, String details){
        log(title, details, LogEntry.LogTag.INFO);
    }

    public static void warn(String title, String details){
        log(title, details, LogEntry.LogTag.WARNING);
    }

    public static void error(String title, String details){
        log(title, details, LogEntry.LogTag.ERROR);
    }

    static String convertStackTraceToDetails(StackTraceElement[] stack){
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < stack.length; i++){
            StackTraceElement element = stack[i];
            if( i > 0 ) sb.append("\n");
            sb.append(element.toString());
        }
        return sb.toString();
    }

    public static interface LoggerListener{
        void onLog(LogEntry entry);
    }

    public static void addObserver(LoggerListener observer){
        observers.add(observer);
    }

    public static void removeObserver(LoggerListener observer){
        observers.remove(observer);
    }

    static void notify(LogEntry entry){
        for( LoggerListener observer : observers ) observer.onLog(entry);
    }
}
