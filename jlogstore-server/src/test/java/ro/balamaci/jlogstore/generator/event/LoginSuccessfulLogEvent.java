package ro.balamaci.jlogstore.generator.event;

public class LoginSuccessfulLogEvent extends UserLogEvent {

    private String remoteIp;

    public LoginSuccessfulLogEvent() {
    }

    public LoginSuccessfulLogEvent(String username, String remoteIp) {
        super(String.format("SUCCESSFUL Login for user=%s, from ip=%s", username, remoteIp), username);
        this.remoteIp = remoteIp;
    }

    public String getRemoteIp() {
        return remoteIp;
    }

    public void setRemoteIp(String remoteIp) {
        this.remoteIp = remoteIp;
    }
}
