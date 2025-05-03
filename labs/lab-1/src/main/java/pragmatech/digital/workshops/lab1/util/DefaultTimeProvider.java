package pragmatech.digital.workshops.lab1.util;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Default implementation of TimeProvider that returns the current system time.
 */
@Component
public class DefaultTimeProvider implements TimeProvider {
    
    @Override
    public LocalDate getCurrentDate() {
        return LocalDate.now();
    }
    
    @Override
    public LocalDateTime getCurrentDateTime() {
        return LocalDateTime.now();
    }
}