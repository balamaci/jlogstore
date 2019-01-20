package ro.balamaci.jlogstore.generator.event;

public class UserLogEvent extends LogEvent {

    private String username;

    public UserLogEvent() {
    }

    public UserLogEvent(String username, String message) {
        super(message);
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
