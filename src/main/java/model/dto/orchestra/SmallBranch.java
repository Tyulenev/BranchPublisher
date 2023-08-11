package model.dto.orchestra;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SmallBranch {

    private String branchPrefix;
    @JsonProperty("enabled")
    private boolean enabled;
    private int operationProfileId;
    private int equipmentProfileId;
    private String name;
    private int id;

}
