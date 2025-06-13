package tup.pps.exceptions;

public class ResourceAlreadyExistsException extends RuntimeException {

    public ResourceAlreadyExistsException(String mensaje) {
        super(mensaje);
    }
}
