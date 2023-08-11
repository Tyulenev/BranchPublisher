package model.dto.frontend;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Set;

@Data
public class PublishRequestFromFrontEnd {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private String scheduledTime;
    @JsonProperty("branchIds")
    private Set<Integer> branchIds;
    private long batchConfigurationId;

}
