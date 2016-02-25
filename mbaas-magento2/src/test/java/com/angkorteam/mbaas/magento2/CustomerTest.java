package com.angkorteam.mbaas.magento2;

import com.angkorteam.magento2.customer.CustomerAccountManagementV1ServiceStub;
import com.angkorteam.magento2.customer.CustomerCustomerRepositoryV1ServiceStub;
import com.angkorteam.magento2.customer.CustomerGroupRepositoryV1ServiceStub;
import com.angkorteam.magento2.customer.GenericFaultException;
import net.ddns.pkayjava.magento2.soap._default.*;
import org.apache.axis2.client.ServiceClient;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.junit.Test;

import java.rmi.RemoteException;
import java.util.Date;

/**
 * Created by socheat on 2/24/16.
 */
public class CustomerTest extends BaseTest {

    /**
     * customer search
     *
     * @throws RemoteException
     * @throws GenericFaultException
     */
    @Test
    public void customerCustomerRepositoryTest() throws RemoteException, GenericFaultException {
        String token = getAdminToken();

        CustomerCustomerRepositoryV1ServiceStub service = new CustomerCustomerRepositoryV1ServiceStub();
        ServiceClient serviceClient = service._getServiceClient();
        setToken(serviceClient, token);

        {
            CustomerCustomerRepositoryV1GetListRequestDocument requestDocument = CustomerCustomerRepositoryV1GetListRequestDocument.Factory.newInstance();
            CustomerCustomerRepositoryV1GetListRequest request = requestDocument.addNewCustomerCustomerRepositoryV1GetListRequest();
            FrameworkSearchCriteriaInterface searchCriteria = request.addNewSearchCriteria();
            {
                ArrayOfFrameworkSearchFilterGroup filterGroups = searchCriteria.addNewFilterGroups();
                FrameworkSearchFilterGroup filterGroup = filterGroups.addNewItem();
                ArrayOfFrameworkFilter filters = filterGroup.addNewFilters();
//                FrameworkFilter filter = filters.addNewItem();
            }
            {
                ArrayOfFrameworkSortOrder sortOrders = searchCriteria.addNewSortOrders();
//                FrameworkSortOrder sortOrder = sortOrders.addNewItem();
            }

            CustomerCustomerRepositoryV1GetListResponseDocument responseDocument = service.customerCustomerRepositoryV1GetList(requestDocument);
            CustomerCustomerRepositoryV1GetListResponse response = responseDocument.getCustomerCustomerRepositoryV1GetListResponse();
            CustomerDataCustomerSearchResultsInterface results = response.getResult();
            ArrayOfCustomerDataCustomerInterface items = results.getItems();
            for (CustomerDataCustomerInterface item : items.getItemArray()) {
                System.out.println(item.getEmail());
            }
        }

        {
            CustomerCustomerRepositoryV1SaveRequestDocument requestDocument = CustomerCustomerRepositoryV1SaveRequestDocument.Factory.newInstance();
            CustomerCustomerRepositoryV1SaveRequest request = requestDocument.addNewCustomerCustomerRepositoryV1SaveRequest();
            CustomerDataCustomerInterface customer = request.addNewCustomer();

            customer.setConfirmation("true");
            customer.setCreatedAt(formatDate(new Date()));
            customer.setCreatedIn(formatDate(new Date()));
            customer.setDefaultBilling("DefaultBilling");
            customer.setDefaultShipping("DefaultShipping");
            customer.setDisableAutoGroupChange(1);
            customer.setDob(formatDate(new Date()));
            customer.setEmail("m3@home.com.kh");
            customer.setFirstname("S");
            customer.setGender(1);
            customer.setGroupId(1);
            customer.setLastname("Lastname");
            customer.setMiddlename("Middlename");
            customer.setPrefix("Prefix");
            customer.setSuffix("Suffix");
            customer.setStoreId(1);
            customer.setTaxvat("Taxvat");
            customer.setUpdatedAt(formatDate(new Date()));
            customer.setWebsiteId(1);

            {
                ArrayOfCustomerDataAddressInterface addresses = customer.addNewAddresses();
                CustomerDataAddressInterface address = addresses.addNewItem();
                address.setRegionId(1);
                address.setCity("City");
                address.setCompany("Company");
                address.setCountryId("KH");
                address.setCustomerId(3);
                address.setDefaultBilling(true);
                address.setDefaultShipping(true);
                address.setFax("Fax");
                address.setFirstname("SSSSSSSSSss");
                address.setLastname("Lastname");
                address.setMiddlename("Middlename");
                address.setPostcode("Postcode");
                address.setPrefix("Prefix");
                address.setSuffix("Suffix");
                address.setTelephone("Telephone");
                address.setVatId("1");
                {
                    ArrayOfString streets = address.addNewStreet();
                    XmlString street = streets.addNewItem();
                    street.setStringValue("#104");
                }
                {
                    CustomerDataRegionInterface region = address.addNewRegion();
                    region.setRegion("Region");
                    region.setRegionCode("RegionCode");
                    region.setRegionId(1);
                    {
                        CustomerDataRegionExtensionInterface extensionAttributes = region.addNewExtensionAttributes();
                        XmlString extensionAttribute = XmlString.Factory.newInstance();
                        extensionAttribute.setStringValue("extensionAttribute");
                        extensionAttributes.set(extensionAttribute);
                    }
                }
                {
                    CustomerDataAddressExtensionInterface extensionAttributes = address.addNewExtensionAttributes();
                    XmlString extensionAttribute = XmlString.Factory.newInstance();
                    extensionAttribute.setStringValue("extensionAttribute");
                    extensionAttributes.set(extensionAttribute);
                }
                {
                    ArrayOfFrameworkAttributeInterface customAttributes = address.addNewCustomAttributes();
                    {
                        FrameworkAttributeInterface customAttribute = customAttributes.addNewItem();
                        customAttribute.setAttributeCode("ABC");
                        {
                            XmlObject object = customAttribute.addNewValue();
                            XmlString value = XmlString.Factory.newInstance();
                            value.setStringValue("extensionAttribute");
                            object.set(value);
                        }
                    }
                }
            }

            {
                ArrayOfFrameworkAttributeInterface customAttributes = customer.addNewCustomAttributes();
                FrameworkAttributeInterface customAttribute = customAttributes.addNewItem();
                customAttribute.setAttributeCode("attribute code");
                {
                    XmlObject object = customAttribute.addNewValue();
                    XmlString value = XmlString.Factory.newInstance();
                    value.setStringValue("extensionAttribute");
                    value.set(value);
                }
            }
            {
                CustomerDataCustomerExtensionInterface extensionAttributes = customer.addNewExtensionAttributes();
                XmlString value = XmlString.Factory.newInstance();
                value.setStringValue("extensionAttribute");
                extensionAttributes.set(value);
            }

            CustomerCustomerRepositoryV1SaveResponseDocument responseDocument = service.customerCustomerRepositoryV1Save(requestDocument);
            CustomerCustomerRepositoryV1SaveResponse response = responseDocument.getCustomerCustomerRepositoryV1SaveResponse();
        }
    }

    /**
     * customer search
     *
     * @throws RemoteException
     * @throws GenericFaultException
     */
    @Test
    public void customerGroupRepositoryTest() throws RemoteException, GenericFaultException {
        String token = getAdminToken();
        {
            CustomerGroupRepositoryV1ServiceStub service = new CustomerGroupRepositoryV1ServiceStub();
            ServiceClient serviceClient = service._getServiceClient();
            setToken(serviceClient, token);


        }
    }

    @Test
    public void customerAccountManagementTest() throws RemoteException, GenericFaultException {
        CustomerAccountManagementV1ServiceStub service = new CustomerAccountManagementV1ServiceStub();
        CustomerAccountManagementV1CreateAccountRequestDocument requestDocument = CustomerAccountManagementV1CreateAccountRequestDocument.Factory.newInstance();
        CustomerAccountManagementV1CreateAccountRequest request = requestDocument.addNewCustomerAccountManagementV1CreateAccountRequest();
        service.customerAccountManagementV1CreateAccount(requestDocument);
    }

}
