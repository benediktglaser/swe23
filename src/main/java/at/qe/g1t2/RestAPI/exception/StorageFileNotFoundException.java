package at.qe.g1t2.RestAPI.exception;

public class StorageFileNotFoundException extends RuntimeException{

    public StorageFileNotFoundException(String msg){
        super(msg);
    }
}
