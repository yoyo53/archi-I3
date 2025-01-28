package time.service.timeservice.dto;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class TimeDTO {
    private String date_to_move;
    private Integer number_day_to_skip;

    public TimeDTO() {
    }

    public TimeDTO(String date_to_move, Integer number_day_to_skip) {
        setDate_to_move(date_to_move);
        this.number_day_to_skip = number_day_to_skip;
    }

    public String getDate_to_move() {
        return date_to_move;
    }

    public void setDate_to_move(String date_to_move) {
        if (date_to_move == null || isValidISODate(date_to_move)) {
            this.date_to_move = date_to_move;
        } else {
            throw new IllegalArgumentException("date_to_move must be a string of ISO date format or null");
        }
    }

    public Integer getNumber_day_to_skip() {
        return number_day_to_skip;
    }

    public void setNumber_day_to_skip(Integer number_day_to_skip) {
        this.number_day_to_skip = number_day_to_skip;
    }

    private boolean isValidISODate(String dateStr) {
        try {
            DateTimeFormatter.ISO_DATE.parse(dateStr);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
