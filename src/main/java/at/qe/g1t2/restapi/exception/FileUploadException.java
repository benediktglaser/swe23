package at.qe.g1t2.restapi.exception;

/**
 * This Exception is thrown, when something with the File-Upload has not worked as expected.
 */
public class FileUploadException extends RuntimeException{

    public FileUploadException(String msg){
        super(msg);
    }

}
