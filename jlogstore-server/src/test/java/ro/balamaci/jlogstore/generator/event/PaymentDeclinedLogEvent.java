package ro.balamaci.jlogstore.generator.event;

public class PaymentDeclinedLogEvent extends UserLogEvent {

    public PaymentDeclinedLogEvent() {
    }

    public PaymentDeclinedLogEvent(String username) {
        super(String.format("Payment declined for user=%s", username), username);
    }

}
