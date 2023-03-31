package at.qe.g1t2.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "The SensorData with the given id does not exist")
public class SensorDataNotFoundException extends Exception {

    public SensorDataNotFoundException() {

    }
}
