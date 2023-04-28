package at.qe.g1t2.restapi.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
class ExceptionHandlingServiceTest {

    private ExceptionHandlingService exceptionHandlingService;
    @Mock
    private BindingResult bindingResult;

    @BeforeEach
    public void setUp() {
        exceptionHandlingService = new ExceptionHandlingService();
    }

    @Test
    public void handleValidationExceptionTest() {
        FieldError fieldError = new FieldError("testObject", "testField", "null is not allowed");
        when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError));

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException((MethodParameter) null, bindingResult);

        ResponseEntity<Object> response = exceptionHandlingService.handleValidationException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        Map<String, String> errors = (Map<String, String>) response.getBody();
        assertEquals(1, errors.size());
        assertEquals("null is not allowed", errors.get("testField"));
    }
    @Test
    public void testHandleAccessPointNotFound() {
        EntityNotFoundException ex = new EntityNotFoundException("Entity not found");
        ResponseEntity<String> response = exceptionHandlingService.handleAccessPointNotFound(ex);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Entity not found", response.getBody());
    }

    @Test
    public void testHandleFileUploadException() {
        FileUploadException ex = new FileUploadException("File upload error");
        ResponseEntity<String> response = exceptionHandlingService.handleFileUploadException(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("File upload error", response.getBody());
    }

    @Test
    public void testHandleJsonProcessingException() {
        JsonProcessingException ex = new JsonProcessingException("JSON processing error") {
            private static final long serialVersionUID = 1L;
        };
        String response = exceptionHandlingService.handleJsonProcessingException(ex);
        assertEquals("Error JSON Processing", response);
    }

    @Test
    public void testHandleQRException() {
        QRException ex = new QRException("QR code error");
        exceptionHandlingService.handleQRException(ex);
    }
    @Test
    public void testVisibleMapException() {
        VisibleMapException ex = new VisibleMapException("SensorStation is not registered");
        exceptionHandlingService.handleVisibleMapException(ex);
    }
}