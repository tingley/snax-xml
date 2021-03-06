package net.sundell.snax;

import javax.xml.stream.Location;

/**
 * Base exception class for exceptions generated by ElementHandlers due
 * to invalid or unexpected data, bad state, etc.  Note that standard XML 
 * errors will be reported through standard STaX mechanisms (XMLReporter, XMLStreamException); 
 * SNAXUserException is intended to report errors that originate in custom logic. 
 *
 * The value of {@link #getLocation()} will be populated
 * automatically by the framework based on the current stream location that invoked
 * the handler that threw this exception.
 */
public class SNAXUserException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private Location location;

    public SNAXUserException(String message) {
        super(message);
    }
    public SNAXUserException(Throwable t) {
        super(t);
    }
    public SNAXUserException(String message, Throwable t) {
        super(message, t);
    }
    
    void setLocation(Location location) {
        this.location = location;
    }
    
    public Location getLocation() {
        return location;
    }
}
