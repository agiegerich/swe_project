package exceptions;

public class RoleConfirmationDoesNotExist extends Exception {
    public RoleConfirmationDoesNotExist( String message, Throwable t ) {
        super( message, t );
    }

    public RoleConfirmationDoesNotExist( String message ) {
        super( message );
    }

    public RoleConfirmationDoesNotExist() {
        super();
    }
}
