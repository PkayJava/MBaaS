package com.angkorteam.mbaas.magento2;

import com.angkorteam.magento2.integration.IntegrationAdminTokenServiceV1ServiceStub;
import com.angkorteam.magento2.integration.IntegrationCustomerTokenServiceV1ServiceStub;
import net.ddns.pkayjava.magento2.soap._default.*;
import org.junit.Assert;
import org.junit.Test;

import java.rmi.RemoteException;

/**
 * Created by socheat on 2/24/16.
 */
public class IntegrationTest extends BaseTest {

    @Test
    public void integrationAdminTokenServiceTest() throws RemoteException, com.angkorteam.magento2.integration.GenericFaultException {
        {
            IntegrationAdminTokenServiceV1ServiceStub service = new IntegrationAdminTokenServiceV1ServiceStub();

            IntegrationAdminTokenServiceV1CreateAdminAccessTokenRequestDocument requestDocument = IntegrationAdminTokenServiceV1CreateAdminAccessTokenRequestDocument.Factory.newInstance();
            IntegrationAdminTokenServiceV1CreateAdminAccessTokenRequest tokenRequest = requestDocument.addNewIntegrationAdminTokenServiceV1CreateAdminAccessTokenRequest();
            tokenRequest.setUsername(getAdminLogin());
            tokenRequest.setPassword(getAdminPassword());

            IntegrationAdminTokenServiceV1CreateAdminAccessTokenResponseDocument responseDocument = service.integrationAdminTokenServiceV1CreateAdminAccessToken(requestDocument);
            IntegrationAdminTokenServiceV1CreateAdminAccessTokenResponse tokenResponse = responseDocument.getIntegrationAdminTokenServiceV1CreateAdminAccessTokenResponse();
            String token = tokenResponse.getResult();
            Assert.assertNotNull(token);
        }
    }

    @Test
    public void integrationCustomerTokenServiceTest() throws RemoteException, com.angkorteam.magento2.integration.GenericFaultException {
        {
            IntegrationCustomerTokenServiceV1ServiceStub service = new IntegrationCustomerTokenServiceV1ServiceStub();

            IntegrationCustomerTokenServiceV1CreateCustomerAccessTokenRequestDocument requestDocument = IntegrationCustomerTokenServiceV1CreateCustomerAccessTokenRequestDocument.Factory.newInstance();
            IntegrationCustomerTokenServiceV1CreateCustomerAccessTokenRequest request = requestDocument.addNewIntegrationCustomerTokenServiceV1CreateCustomerAccessTokenRequest();
            request.setUsername(getCustomerLogin());
            request.setPassword(getCustomerPassword());

            IntegrationCustomerTokenServiceV1CreateCustomerAccessTokenResponseDocument responseDocument = service.integrationCustomerTokenServiceV1CreateCustomerAccessToken(requestDocument);
            IntegrationCustomerTokenServiceV1CreateCustomerAccessTokenResponse tokenResponse = responseDocument.getIntegrationCustomerTokenServiceV1CreateCustomerAccessTokenResponse();
            String token = tokenResponse.getResult();
            Assert.assertNotNull(token);
        }
    }
}
