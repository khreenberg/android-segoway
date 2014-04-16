package khr.easv.pokebotbroadcaster.app.entities.logger;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Logger {

    static HashSet<ILoggerListener> observers = new HashSet<ILoggerListener>();
    static List<LogEntry> entries = new ArrayList<LogEntry>(32);

    public static synchronized void log(LogEntry entry){
        entries.add(entry);
        notify(entry);
        Log.d(entry.getTag().toString(), entry.getTitle());
    }

    public static void log(String title, String details, LogEntry.LogTag tag){
        LogEntry entry = new LogEntry(title, details, tag);
        log(entry);
    }

    public static void exception(String title, Exception e){
        String t = String.format("%s (%s)", title, e.toString());
        error(t, convertStackTraceToDetails(e.getStackTrace()));
    }

    public static void exception(Exception e){
        error(e.toString(), convertStackTraceToDetails(e.getStackTrace()));
    }

    public static void debug(String title){
        debug(title, "");
    }
    public static void debug(String title, String details){
        log(title, details, LogEntry.LogTag.DEBUG);
    }

    public static void info(String title){ info(title, ""); }
    public static void info(String title, String details){
        log(title, details, LogEntry.LogTag.INFO);
    }

    public static void warn(String title){ warn(title, ""); }
    public static void warn(String title, String details){
        log(title, details, LogEntry.LogTag.WARNING);
    }

    public static void error(String title){ error(title, ""); }
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

    public static interface ILoggerListener {
        void onLog(LogEntry entry);
    }

    public static void addObserver(ILoggerListener observer){
        observers.add(observer);
    }

    public static void removeObserver(ILoggerListener observer){
        observers.remove(observer);
    }

    private static void notify(LogEntry entry){
        for( ILoggerListener observer : observers ) observer.onLog(entry);
    }

    public static List<LogEntry> getEntries() {
        return entries;
    }

    public static void clearEntries() {
        entries.clear();
    }

}
