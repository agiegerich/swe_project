package exceptions;

public class EncryptorException extends Exception {

    public EncryptorException() {
        super();
    }
    public EncryptorException(String message) {
        super(message);
    }
    public EncryptorException(String message, Throwable cause) {
        super(message, cause);
    }


}
