package at.qe.g1t2.restapi.exception;

/**
 * This exception is thrown if an error occurred when creating the QR-Code.
 */
public class QRException extends RuntimeException{

    public QRException(String msg){
        super(msg);
    }
}
