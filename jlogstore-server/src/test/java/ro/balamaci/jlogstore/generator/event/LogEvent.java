package ro.balamaci.jlogstore.generator.event;

public class LogEvent {

    private String threadName;

    private String loggerName;

    private Level level;

    private String message;


    public LogEvent() {
    }

    public LogEvent(String message) {
        this.message = message;
        level = Level.INFO;
        loggerName = this.getClass().getName();
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
