package model.dto.frontend;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.extern.java.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

@Data
@Log
public class ScheduleRequest {

    private String scheduledTime;
    @JsonProperty("branchIds")
    private Set<Integer> branchIds;
    private long batchConfigurationId;

    public void setScheduledTime(String scheduledTime) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            if(simpleDateFormat.parse(scheduledTime).before(new Date())) {
                throw new IllegalArgumentException("Date can not be earlier than now!");
            }
            this.scheduledTime = scheduledTime;
        } catch (ParseException exception) {
            log.severe("Can not parse date="+scheduledTime);
            throw exception;
        }
    }
}
