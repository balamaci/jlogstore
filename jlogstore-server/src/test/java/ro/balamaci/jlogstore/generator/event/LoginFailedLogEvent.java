package ro.balamaci.jlogstore.generator.event;

public class LoginFailedLogEvent extends UserLogEvent {

    private String remoteIp;

    public LoginFailedLogEvent() {
    }

    public LoginFailedLogEvent(String username, String remoteIp) {
        super(String.format("FAILED Login for user=%s, from ip=%s", username, remoteIp), username);

        this.remoteIp = remoteIp;
        this.setLevel(Level.WARN);
    }

    public String getRemoteIp() {
        return remoteIp;
    }

    public void setRemoteIp(String remoteIp) {
        this.remoteIp = remoteIp;
    }
}
