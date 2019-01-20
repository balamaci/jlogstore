package ro.balamaci.jlogstore.storage;

/**
 * @author sbalamaci
 */
public interface Storage {

    void store(String loggerId, String json);

    void close();

}
