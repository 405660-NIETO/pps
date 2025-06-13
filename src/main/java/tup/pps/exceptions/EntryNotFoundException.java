package tup.pps.exceptions;

public class EntryNotFoundException extends RuntimeException {

    public EntryNotFoundException(String mensaje) {
        super(mensaje);
    }
}
