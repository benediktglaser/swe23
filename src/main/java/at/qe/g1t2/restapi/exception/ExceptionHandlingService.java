package at.qe.g1t2.restapi.exception;

import at.qe.g1t2.restapi.controller.AccessPointConnectionController;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ExceptionHandlingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccessPointConnectionController.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationException(@NotNull MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = "null is not allowed";
            errors.put(fieldName, errorMessage);
            LOGGER.error("Request Body not complete "+errorMessage);
        });

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleAccessPointNotFound(@NotNull EntityNotFoundException ex) {
        LOGGER.error("Entity not Found exception / Message: "+ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(),HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(FileUploadException.class)
    public ResponseEntity<String> handleFileUploadException(@NotNull FileUploadException ex){
        LOGGER.error("File Upload exception / Message: "+ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(),HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(JsonProcessingException.class)
    public String handleJsonProcessingException(@NotNull JsonProcessingException ex){
        LOGGER.error("Processing JSON Error / Message: "+ex.getMessage());
        return "Error JSON Processing";
    }
    @ExceptionHandler(QRException.class)
    public void handleQRException(@NotNull QRException ex){
        LOGGER.error(ex.getMessage());
    }

    @ExceptionHandler(InvalidAccessException.class)
    public ResponseEntity<String> handleInvalidAccess(@NotNull InvalidAccessException ex){
        LOGGER.error("Try invalid access: "+ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(),HttpStatus.BAD_REQUEST);
    }



}
