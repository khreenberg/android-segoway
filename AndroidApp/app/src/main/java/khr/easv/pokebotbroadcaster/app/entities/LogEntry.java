package khr.easv.pokebotbroadcaster.app.entities;

public class LogEntry {

    public enum LogTag{
        DEBUG, INFO, ERROR, WARNING
    }

    LogTag tag;
    String title, details;

    public LogEntry(String title, String details, LogTag tag) {
        this.title = title;
        this.details = details;
        this.tag = tag;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public LogTag getTag() {
        return tag;
    }

    public void setTag(LogTag tag) {
        this.tag = tag;
    }
}
