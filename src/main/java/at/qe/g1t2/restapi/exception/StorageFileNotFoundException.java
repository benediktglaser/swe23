package at.qe.g1t2.restapi.exception;

public class StorageFileNotFoundException extends RuntimeException{

    public StorageFileNotFoundException(String msg){
        super(msg);
    }
}
