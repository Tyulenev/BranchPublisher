package controller.Interceptors;

import controller.exceptions.NoReqModulesException;
import lombok.extern.java.Log;
import model.dto.orchestra.AccountInfo;
import property.annotation.Property;
import proxy.ProxyToOrchestra;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import javax.ws.rs.core.HttpHeaders;

@Log
public class ProxyInterceptor {

    @Inject
    private ProxyToOrchestra proxyToOrchestra;

    @Inject
    @Property("params.moduleRequiredName")
    private String moduleRequiredName;

    @AroundInvoke
    public Object setHttpHeadersInProxy(InvocationContext context) throws Exception {
        Object[] params = context.getParameters();
        for (Object obj:params) {
            if (obj instanceof HttpHeaders) {
                proxyToOrchestra.setHttpHeaders((HttpHeaders) obj);
                AccountInfo accountInfo = proxyToOrchestra.getModulesForUser();
                if (!accountInfo.getModules().contains(moduleRequiredName)
                        && !accountInfo.getModules().contains("*")) {
                    throw new NoReqModulesException("У текущего пользователя недостаточно прав. Требуемый модуль - "
                            + moduleRequiredName);
                }
                return context.proceed();
            }
        }
        return null;
    }
}
