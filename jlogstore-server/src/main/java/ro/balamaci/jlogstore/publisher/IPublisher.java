package ro.balamaci.jlogstore.publisher;

public interface IPublisher {

    void publish(String logId, String json);

}
