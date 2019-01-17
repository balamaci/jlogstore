package ro.balamaci.jlogstore.storage;

/**
 * @author sbalamaci
 */
public interface IStorage {

    void store(String loggerId, String json);

    void close();

}
