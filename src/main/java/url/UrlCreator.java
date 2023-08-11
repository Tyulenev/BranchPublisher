package url;

import property.annotation.Property;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

@RequestScoped
public class UrlCreator {

    @Inject
    @Property("orchestra.configuration.rest.api.url")
    private String orchestraConfigurationRestApiUrl;

    public String getAllBranchesUrl() {
        return orchestraConfigurationRestApiUrl + "/branches";
    }

    public String getAllBranchesPublishUrl() {
        return getAllBranchesUrl() + "/publish";
    }

    public String getAllBranchesPublishUrl(int fromId, int toId) {
        return getAllBranchesPublishUrl() + "?fromId=" + fromId + "&toId=" + toId;
    }

    public String getAllBranchesPublishStatusUrl() {
        return getAllBranchesPublishUrl() + "/status";
    }

    public String getBranchUrl(int branchId) {
        return getAllBranchesUrl() + "/" + branchId;
    }

    public String getBranchPublishUrl(int branchId){
        return getBranchUrl(branchId) + "/publish";
    }

    public String getBranchPublishStatusUrl(int branchId) {
        return getBranchPublishUrl(branchId) + "/status";
    }

    public String getModulesForUserUrl() {
        return  getUrlFromOrchConfig(orchestraConfigurationRestApiUrl) + "/rest/entrypoint/account/";
    }

    private String getUrlFromOrchConfig(String inputStr) {
        String[] arrStr = inputStr.split("/");
        String outputStr = arrStr[0] + "/" + arrStr[1] + "/" + arrStr[2];
        return outputStr;
    }
}
