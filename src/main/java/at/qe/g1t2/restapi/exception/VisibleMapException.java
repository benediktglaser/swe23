package at.qe.g1t2.restapi.exception;

/**
 * This exception is thrown when an error occurs when showing the visibleMap.
 * This visibileMap is used in the coupling-View on the webserver,
 * to indicate SensorStations which are ready to be coupled.
 */
public class VisibleMapException extends RuntimeException{

    public VisibleMapException(String msg){
        super(msg);
    }
}
