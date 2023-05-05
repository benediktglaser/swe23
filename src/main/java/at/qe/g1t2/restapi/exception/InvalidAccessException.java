package at.qe.g1t2.restapi.exception;

public class InvalidAccessException extends RuntimeException{

    public InvalidAccessException(String msg){
        super(msg);
    }
}
