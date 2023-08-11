package model.dto.orchestra;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.json.bind.annotation.JsonbNillable;
import java.util.List;
import java.util.TimeZone;

@Data
@JsonbNillable
public class Branch {

    private String branchPrefix;
    @JsonProperty("enabled")
    private boolean enabled;
    private int operationProfileId;
    private int equipmentProfileId;
    private String name;
    private int id;
    List<BranchParameter> branchParameters;
    private String agentId;
    @JsonProperty("mobileEnabled")
    private boolean mobileEnabled;
    private TimeZone timeZone;
    private String description;

    @Data
    private static class BranchParameter {
        private String validationPattern;
        private String value;
        private String key;
    }

}
