package model.dto.frontend;

import lombok.Data;

@Data
public class BranchInfoFull {
    private int id;
    private String branchPrefix;
    private String name;

    private String description;

    private String agentName;
    private String publishState;
    private String publishDate;
}
