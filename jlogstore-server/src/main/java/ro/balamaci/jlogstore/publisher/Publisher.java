package ro.balamaci.jlogstore.publisher;

public interface Publisher {

    void publish(String logId, String json);

}
