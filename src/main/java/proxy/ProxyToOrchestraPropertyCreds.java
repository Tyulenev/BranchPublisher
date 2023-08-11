package proxy;

import controller.exceptions.AuthFailedException;
import lombok.Data;
import lombok.extern.java.Log;
import model.dto.frontend.BranchInfoFull;
import model.dto.orchestra.AccountInfo;
import model.dto.orchestra.Branch;
import model.dto.orchestra.BranchPublishStatus;
import model.dto.orchestra.SmallBranch;
import request.RequestHelper;
import requestWithCookies.RequestHelperWithCookies;
import url.UrlCreator;

import javax.ejb.EJBException;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.util.HashSet;
import java.util.Set;

@Log
@Singleton
@Data
public class ProxyToOrchestraPropertyCreds {

    @Inject
    private UrlCreator urlCreator;
    @Inject
    private RequestHelper requestHelper;


    public Set<SmallBranch> getAllBranches() {
        return requestHelper.getRequestAndReceiveGeneric(new GenericType<Set<SmallBranch>>() {}
        , urlCreator.getAllBranchesUrl());
    }

    public Set<BranchInfoFull> getAllBranchesInfoFull() throws AuthFailedException {
        Set resultList = new HashSet();
        Set<SmallBranch> branchesList = getAllBranches();
        for (SmallBranch sb:branchesList) {
            BranchInfoFull branchInfoFull = new BranchInfoFull();
            branchInfoFull.setId(sb.getId());
            branchInfoFull.setName(sb.getName());
            branchInfoFull.setBranchPrefix(sb.getBranchPrefix());

            Branch br = getBranch(sb.getId());
            branchInfoFull.setDescription(br.getDescription());

            BranchPublishStatus brPS = getBranchPublishStatus(sb.getId());
            branchInfoFull.setAgentName(brPS.getAgentName());

            String configState = brPS.getConfigState();
            if (configState.equals("OK")) {
                    branchInfoFull.setPublishState("ВКЛ");}
                else if (configState.equals("DISABLED")) {
                    branchInfoFull.setPublishState("ВЫКЛ");}
                else  branchInfoFull.setPublishState(configState);

            branchInfoFull.setPublishDate(brPS.getDeployedDate());
            resultList.add(branchInfoFull);
        }

        return resultList;
    }


    public Branch getBranch(int branchId) throws EJBException, AuthFailedException {
        return requestHelper.getRequestAndReceiveObject(Branch.class, urlCreator.getBranchUrl(branchId));
    }

    public BranchPublishStatus getBranchPublishStatus(int branchId)  {
        return requestHelper.getRequestAndReceiveObject(BranchPublishStatus.class
                , urlCreator.getBranchPublishStatusUrl(branchId));
    }

    public Set<BranchPublishStatus> getAllBranchPublishStatus() {
        return requestHelper.getRequestAndReceiveGeneric(new GenericType<Set<BranchPublishStatus>>(){}
            , urlCreator.getAllBranchesPublishStatusUrl());
    }

    public Response publishBranch(int id) {
        Response responseFromOrchestra = requestHelper.postRequest(urlCreator.getBranchPublishUrl(id)
                , null);
        if(responseFromOrchestra.getHeaders().containsKey("Location")) {
            return Response.ok().build();
        } else {
            return Response.status(responseFromOrchestra.getStatus()).entity(responseFromOrchestra.getEntity()).build();
        }
    }

}
