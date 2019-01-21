package ro.balamaci.jlogstore.generator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import ro.balamaci.jlogstore.generator.event.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FakeJsonLogEventGenerator {

    private ObjectMapper objectMapper = new ObjectMapper();
    {
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ISO_DATE_TIME));
        objectMapper.registerModule(javaTimeModule);
    }

    public List<String> generateJsonLogEvents(int events) {
        try {
            List<String> jsonEvents = new ArrayList<>();

            for(int i=0; i < events; i++) {
                LogEvent logEvent = generate();
                jsonEvents.add(objectMapper.writeValueAsString(logEvent));
            }
            return jsonEvents;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private LogEvent generate() {
        int event = new Random().nextInt(5);
        switch (event) {
            case 0: return new LoginFailedLogEvent(randomEmail(), randomIpv4());
            case 1: return new LoginSuccessfulLogEvent(randomEmail(), randomIpv4());
            case 2: return new ExceptionLogEvent(NullPointerException::new);
            case 3: return new ProductAddedLogEvent(randomEmail(), randomInt(5000));
            case 4: return new ExceptionLogEvent(() -> new IllegalArgumentException("Value must be a number"));

            default: return new PaymentDeclinedLogEvent(randomEmail());
        }
    }

    private String randomEmail() {
        String[] emails = new String[] {"john.snow@got.com", "cersei.lannister@got.com", "tyrion.lannister@got.com",
                "harry.potter@potter.com",
                "hermione.granger@potter.com", "albus.dumbledore@potter.com", "tom.riddle@potter.com"};
        int event = new Random().nextInt(5);

        return emails[event];
    }

    private String randomIpv4() {
        Random r = new Random();
        return r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256);
    }

    private int randomInt(int max) {
        Random r = new Random();
        return r.nextInt(max);
    }

}
