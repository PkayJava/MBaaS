package com.angkorteam.mbaas.magento2;

import com.angkorteam.magento2.backend.BackendModuleServiceV1ServiceStub;
import com.angkorteam.magento2.backend.GenericFaultException;
import com.angkorteam.magento2.integration.IntegrationAdminTokenServiceV1ServiceStub;
import net.ddns.pkayjava.magento2.soap._default.*;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.NamedValue;
import org.junit.Assert;
import org.junit.Test;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by socheat on 2/24/16.
 */
public class BackendTest extends BaseTest {

    @Test
    public void backendModuleServiceTest() throws RemoteException, com.angkorteam.magento2.integration.GenericFaultException, GenericFaultException {

        String token = null;
        {
            IntegrationAdminTokenServiceV1ServiceStub service = new IntegrationAdminTokenServiceV1ServiceStub();

            IntegrationAdminTokenServiceV1CreateAdminAccessTokenRequestDocument requestDocument = IntegrationAdminTokenServiceV1CreateAdminAccessTokenRequestDocument.Factory.newInstance();
            IntegrationAdminTokenServiceV1CreateAdminAccessTokenRequest tokenRequest = requestDocument.addNewIntegrationAdminTokenServiceV1CreateAdminAccessTokenRequest();
            tokenRequest.setUsername(getAdminLogin());
            tokenRequest.setPassword(getAdminPassword());

            IntegrationAdminTokenServiceV1CreateAdminAccessTokenResponseDocument responseDocument = service.integrationAdminTokenServiceV1CreateAdminAccessToken(requestDocument);
            IntegrationAdminTokenServiceV1CreateAdminAccessTokenResponse tokenResponse = responseDocument.getIntegrationAdminTokenServiceV1CreateAdminAccessTokenResponse();
            token = tokenResponse.getResult();
        }
        {
            BackendModuleServiceV1ServiceStub service = new BackendModuleServiceV1ServiceStub();

            ServiceClient serviceClient = service._getServiceClient();
            setToken(serviceClient, token);

            BackendModuleServiceV1GetModulesRequestDocument document = BackendModuleServiceV1GetModulesRequestDocument.Factory.newInstance();
            document.addNewBackendModuleServiceV1GetModulesRequest();
            BackendModuleServiceV1GetModulesResponseDocument responseDocument = service.backendModuleServiceV1GetModules(document);
            BackendModuleServiceV1GetModulesResponse response = responseDocument.getBackendModuleServiceV1GetModulesResponse();
            ArrayOfString backendModules = response.getResult();

            for (String backendModule : backendModules.getItemArray()) {
                Assert.assertNotNull(backendModule);
            }
        }
    }
}
