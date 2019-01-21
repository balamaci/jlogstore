package ro.balamaci.jlogstore.generator.event;

import java.time.LocalDateTime;

public class LogEvent {

    private String threadName;

    private String loggerName;

    private Level level;

    private String message;

    private LocalDateTime timestamp;


    public LogEvent() {
    }

    public LogEvent(String message) {
        this.message = message;
        level = Level.INFO;
        loggerName = this.getClass().getName();
        timestamp = LocalDateTime.now();
    }

    public LogEvent(String message, LocalDateTime timestamp) {
        this(message);
        this.timestamp = timestamp;
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

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public enum Level {
        DEBUG,
        INFO,
        WARN,
        ERROR
    }



}
