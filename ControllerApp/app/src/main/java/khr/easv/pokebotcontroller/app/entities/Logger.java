package khr.easv.pokebotcontroller.app.entities;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Logger {

    private static HashSet<ILoggerListener> _observers = new HashSet<ILoggerListener>();
    private static List<LogEntry> _entries = new ArrayList<LogEntry>(32);

    public static synchronized void log(LogEntry entry){
        _entries.add(entry);
        notify(entry);
        Log.d(entry.getTag().toString(), entry.getTitle());
    }

    public static LogEntry log(String title, String details, LogEntry.LogTag tag){
        LogEntry entry = new LogEntry(title, details, tag);
        log(entry);
        return entry;
    }

    public static LogEntry exception(String title, Exception e){
        return error(title, convertExceptionToDetails(e));
    }

    public static LogEntry exception(Exception e){
        return error(convertExceptionToTitle(e),
                     convertStackTraceToDetails(e.getStackTrace()));
    }

    public static LogEntry debug(String title){ return debug(title, ""); }
    public static LogEntry debug(String title, String details){
        return log(title, details, LogEntry.LogTag.DEBUG);
    }

    public static LogEntry info(String title){ return info(title, ""); }
    public static LogEntry info(String title, String details){
        return log(title, details, LogEntry.LogTag.INFO);
    }

    public static LogEntry warn(String title){ return warn(title, ""); }
    public static LogEntry warn(String title, String details){
        return log(title, details, LogEntry.LogTag.WARNING);
    }

    public static LogEntry error(String title){ return error(title, ""); }
    public static LogEntry error(String title, String details){
        return log(title, details, LogEntry.LogTag.ERROR);
    }

    private static String convertStackTraceToDetails(StackTraceElement[] stack){
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < stack.length; i++){
            StackTraceElement element = stack[i];
            if( i > 0 ) sb.append("\n");
            sb.append(element.toString());
        }
        return sb.toString();
    }

    private static String convertExceptionToTitle(Exception e) {
        return String.format("%s: %s", e.getClass().getSimpleName(), e.getMessage());
    }

    private static String convertExceptionToDetails(Exception e){
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Exception: %s\nCause:     %s\n\n", e.getClass().getName(), e.getMessage()));
        sb.append(convertStackTraceToDetails(e.getStackTrace()));
        return sb.toString();
    }

    public static List<LogEntry> getEntries() { return _entries; }
    public static void clearEntries() { _entries.clear(); }

    // Observer pattern stuff
    public static interface ILoggerListener { void onLog(LogEntry entry); }
    public static void addObserver(ILoggerListener observer){ _observers.add(observer); }
    public static void removeObserver(ILoggerListener observer){ _observers.remove(observer); }

    private static void notify(LogEntry entry){
        for( ILoggerListener observer : _observers) observer.onLog(entry);
    }
}
