package ro.balamaci.jlogstore;

import com.fasterxml.jackson.core.JsonGenerator;

public class LogEvent {

    private String threadName;

    private String loggerName;

    private Level level;

    private String message;

    public LogEvent() {
    }

    public LogEvent(String message) {
        this.message = message;
        this.level = Level.INFO;
        this.loggerName = this.getClass().getName();
        this.threadName = "main";
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public String getLoggerName() {
        return loggerName;
    }

    public void setLoggerName(String loggerName) {
        this.loggerName = loggerName;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public enum Level {
        DEBUG,
        INFO,
        WARN,
        ERROR
    }

}
