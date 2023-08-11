package model.dto.orchestra;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jdk.nashorn.internal.ir.annotations.Ignore;
import lombok.Data;

import javax.persistence.Transient;
import java.util.List;

@Data
public class AccountInfo {
    private int id;

    private String userName;

    private String firstName;

    private String lastName;

    @Transient
    private String locale;

    @Transient
    private String direction;

    private List<Integer> branchIds;

    @Transient
    private List<String> roles;

    private List<String> modules;

    @Transient
    private List<String> permissions;

    @Transient
    private boolean ldapUser;

    private String fullName;
}


