package ro.balamaci.jlogstore.generator.event;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.function.Supplier;

public class ExceptionLogEvent extends LogEvent {

    private String stackTrace;

    public ExceptionLogEvent(Supplier<Exception> exceptionSupplier) {
        super("Unhandled Exception caught");
        this.stackTrace = generateStackTrace(exceptionSupplier);
        this.setLevel(Level.ERROR);
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    private static String generateStackTrace(Supplier<Exception> exceptionSupplier) {
        try {
            throw exceptionSupplier.get();
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            return sw.toString();
        }
    }

}
