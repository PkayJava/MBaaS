package com.angkorteam.mbaas.magento2;

import com.angkorteam.magento2.integration.GenericFaultException;
import com.angkorteam.magento2.integration.IntegrationAdminTokenServiceV1ServiceStub;
import com.angkorteam.magento2.integration.IntegrationCustomerTokenServiceV1ServiceStub;
import net.ddns.pkayjava.magento2.soap._default.*;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.NamedValue;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by socheat on 2/24/16.
 */
public class BaseTest {

    protected final String formatDate(Date date) {
        return DateFormatUtils.format(date, "yyyy-MM-dd HH:mm:ss");
    }

    protected final String getAdminLogin() {
        return "admin";
    }

    protected final String getAdminPassword() {
        return "admin123";
    }

    protected final String getCustomerLogin() {
        return "roni_cost@example.com";
    }

    protected final String getCustomerPassword() {
        return "roni_cost@example.com";
    }

    protected final String getAdminToken() {
        String token = null;
        try {
            IntegrationAdminTokenServiceV1ServiceStub service = new IntegrationAdminTokenServiceV1ServiceStub();

            IntegrationAdminTokenServiceV1CreateAdminAccessTokenRequestDocument requestDocument = IntegrationAdminTokenServiceV1CreateAdminAccessTokenRequestDocument.Factory.newInstance();
            IntegrationAdminTokenServiceV1CreateAdminAccessTokenRequest tokenRequest = requestDocument.addNewIntegrationAdminTokenServiceV1CreateAdminAccessTokenRequest();
            tokenRequest.setUsername(getAdminLogin());
            tokenRequest.setPassword(getAdminPassword());

            IntegrationAdminTokenServiceV1CreateAdminAccessTokenResponseDocument responseDocument = service.integrationAdminTokenServiceV1CreateAdminAccessToken(requestDocument);
            IntegrationAdminTokenServiceV1CreateAdminAccessTokenResponse tokenResponse = responseDocument.getIntegrationAdminTokenServiceV1CreateAdminAccessTokenResponse();
            token = tokenResponse.getResult();
        } catch (RemoteException | GenericFaultException e) {
            e.printStackTrace();
        }
        return token;
    }

    protected final String getCustomerToken() {
        String token = null;
        try {
            IntegrationCustomerTokenServiceV1ServiceStub service = new IntegrationCustomerTokenServiceV1ServiceStub();

            IntegrationCustomerTokenServiceV1CreateCustomerAccessTokenRequestDocument requestDocument = IntegrationCustomerTokenServiceV1CreateCustomerAccessTokenRequestDocument.Factory.newInstance();
            IntegrationCustomerTokenServiceV1CreateCustomerAccessTokenRequest request = requestDocument.addNewIntegrationCustomerTokenServiceV1CreateCustomerAccessTokenRequest();
            request.setUsername(getCustomerLogin());
            request.setPassword(getCustomerPassword());

            IntegrationCustomerTokenServiceV1CreateCustomerAccessTokenResponseDocument responseDocument = service.integrationCustomerTokenServiceV1CreateCustomerAccessToken(requestDocument);
            IntegrationCustomerTokenServiceV1CreateCustomerAccessTokenResponse tokenResponse = responseDocument.getIntegrationCustomerTokenServiceV1CreateCustomerAccessTokenResponse();
            token = tokenResponse.getResult();
        } catch (RemoteException | GenericFaultException e) {
            e.printStackTrace();
        }
        return token;
    }

    protected final void setToken(ServiceClient serviceClient, String token) {
        List<NamedValue> namedValues = new ArrayList<>();
        NamedValue header = new NamedValue("Authorization", "Bearer " + token);
        namedValues.add(header);
        serviceClient.getOptions().setProperty(org.apache.axis2.transport.http.HTTPConstants.HTTP_HEADERS, namedValues);
    }
}
