package at.qe.g1t2.restapi.exception;

/**
 * This exception is thrown if a user does not have the according roles to do a certain action.
 */
public class InvalidAccessException extends RuntimeException{

    public InvalidAccessException(String msg){
        super(msg);
    }
}
