package com.angkorteam.mbaas.configuration;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by socheat on 2/24/16.
 */
public enum MSoapAPI {

    // Backend
    // ========================================================================================================================================================================
    BackendModuleServiceV1("backendModuleServiceV1"),

    // Bundle
    // ========================================================================================================================================================================
    BundleProductLinkManagementV1("bundleProductLinkManagementV1"),
    BundleProductOptionRepositoryV1("bundleProductOptionRepositoryV1"),
    BundleProductOptionTypeListV1("bundleProductOptionTypeListV1"),
    BundleProductOptionManagementV1("bundleProductOptionManagementV1"),

    // Catalog
    // ========================================================================================================================================================================
    CatalogProductRepositoryV1("catalogProductRepositoryV1"),
    CatalogProductAttributeTypesListV1("catalogProductAttributeTypesListV1"),
    CatalogProductAttributeRepositoryV1("catalogProductAttributeRepositoryV1"),
    CatalogCategoryAttributeRepositoryV1("catalogCategoryAttributeRepositoryV1"),
    CatalogCategoryAttributeOptionManagementV1("catalogCategoryAttributeOptionManagementV1"),
    CatalogProductTypeListV1("catalogProductTypeListV1"),
    CatalogAttributeSetRepositoryV1("catalogAttributeSetRepositoryV1"),
    CatalogAttributeSetManagementV1("catalogAttributeSetManagementV1"),
    CatalogProductAttributeManagementV1("catalogProductAttributeManagementV1"),
    CatalogProductAttributeGroupRepositoryV1("catalogProductAttributeGroupRepositoryV1"),
    CatalogProductAttributeOptionManagementV1("catalogProductAttributeOptionManagementV1"),
    CatalogProductMediaAttributeManagementV1("catalogProductMediaAttributeManagementV1"),
    CatalogProductAttributeMediaGalleryManagementV1("catalogProductAttributeMediaGalleryManagementV1"),
    CatalogProductGroupPriceManagementV1("catalogProductGroupPriceManagementV1", false),
    CatalogProductTierPriceManagementV1("catalogProductTierPriceManagementV1"),
    CatalogCategoryRepositoryV1("catalogCategoryRepositoryV1"),
    CatalogCategoryManagementV1("catalogCategoryManagementV1"),
    CatalogProductCustomOptionTypeListV1("catalogProductCustomOptionTypeListV1"),
    CatalogProductCustomOptionRepositoryV1("catalogProductCustomOptionRepositoryV1"),
    CatalogProductLinkTypeListV1("catalogProductLinkTypeListV1"),
    CatalogProductLinkManagementV1("catalogProductLinkManagementV1"),
    CatalogProductLinkRepositoryV1("catalogProductLinkRepositoryV1"),
    CatalogCategoryLinkManagementV1("catalogCategoryLinkManagementV1"),
    catalogCategoryLinkRepositoryV1("catalogCategoryLinkRepositoryV1"),
    CatalogInventoryStockRegistryV1("catalogInventoryStockRegistryV1"),

    // Checkout
    // ========================================================================================================================================================================
    CheckoutAgreementsCheckoutAgreementsRepositoryV1("checkoutAgreementsCheckoutAgreementsRepositoryV1"),

    // Configurable
    // ========================================================================================================================================================================
    ConfigurableProductLinkManagementV1("configurableProductLinkManagementV1"),
    ConfigurableProductConfigurableProductManagementV1("configurableProductConfigurableProductManagementV1"),
    ConfigurableProductOptionRepositoryV1("configurableProductOptionRepositoryV1"),

    // Customer
    // ========================================================================================================================================================================
    CustomerGroupRepositoryV1("customerGroupRepositoryV1"),
    CustomerGroupManagementV1("customerGroupManagementV1"),
    CustomerCustomerMetadataV1("customerCustomerMetadataV1"),
    CustomerAddressMetadataV1("customerAddressMetadataV1"),
    CustomerCustomerRepositoryV1("customerCustomerRepositoryV1"),
    CustomerAccountManagementV1("customerAccountManagementV1"),
    CustomerAddressRepositoryV1("customerAddressRepositoryV1"),

    // Downloadable
    // ========================================================================================================================================================================
    DownloadableLinkRepositoryV1("downloadableLinkRepositoryV1"),
    DownloadableSampleRepositoryV1("downloadableSampleRepositoryV1"),

    // Eav
    // ========================================================================================================================================================================
    EavAttributeSetRepositoryV1("eavAttributeSetRepositoryV1"),
    EavAttributeSetManagementV1("eavAttributeSetManagementV1"),

    // Gift Message
    // ========================================================================================================================================================================
    GiftMessageCartRepositoryV1("giftMessageCartRepositoryV1"),
    GiftMessageItemRepositoryV1("giftMessageItemRepositoryV1"),

    // Integration
    // ========================================================================================================================================================================
    IntegrationAdminTokenServiceV1("integrationAdminTokenServiceV1"),
    IntegrationCustomerTokenServiceV1("integrationCustomerTokenServiceV1"),

    // Quote
    // ========================================================================================================================================================================
    QuoteCartRepositoryV1("quoteCartRepositoryV1"),
    QuoteCartManagementV1("quoteCartManagementV1"),
    QuoteGuestCartRepositoryV1("quoteGuestCartRepositoryV1"),
    QuoteGuestCartManagementV1("quoteGuestCartManagementV1"),
    QuoteShippingMethodManagementV1("quoteShippingMethodManagementV1"),
    QuoteGuestShippingMethodManagementV1("quoteGuestShippingMethodManagementV1"),
    QuoteCartItemRepositoryV1("quoteCartItemRepositoryV1"),
    QuoteGuestCartItemRepositoryV1("quoteGuestCartItemRepositoryV1"),
    QuotePaymentMethodManagementV1("quotePaymentMethodManagementV1"),
    QuoteGuestPaymentMethodManagementV1("quoteGuestPaymentMethodManagementV1"),
    QuoteBillingAddressManagementV1("quoteBillingAddressManagementV1"),
    QuoteGuestBillingAddressManagementV1("quoteGuestBillingAddressManagementV1"),
    QuoteGuestAddressDetailsManagementV1("quoteGuestAddressDetailsManagementV1", false),
    QuoteCouponManagementV1("quoteCouponManagementV1"),
    QuoteGuestCouponManagementV1("quoteGuestCouponManagementV1"),
    QuoteShippingAddressManagementV1("quoteShippingAddressManagementV1", false),
    QuoteGuestShippingAddressManagementV1("quoteGuestShippingAddressManagementV1", false),
    QuoteAddressDetailsManagementV1("quoteAddressDetailsManagementV1", false),
    QuoteCartTotalRepositoryV1("quoteCartTotalRepositoryV1"),
    QuoteGuestCartTotalManagementV1("quoteGuestCartTotalManagementV1"),
    QuoteGuestCartTotalRepositoryV1("quoteGuestCartTotalRepositoryV1"),
    QuoteCartTotalManagementV1("quoteCartTotalManagementV1"),

    // Sales
    // ========================================================================================================================================================================
    SalesOrderRepositoryV1("salesOrderRepositoryV1"),
    SalesOrderManagementV1("salesOrderManagementV1"),
    SalesOrderAddressRepositoryV1("salesOrderAddressRepositoryV1"),
    SalesInvoiceRepositoryV1("salesInvoiceRepositoryV1"),
    SalesInvoiceManagementV1("salesInvoiceManagementV1"),
    SalesInvoiceCommentRepositoryV1("salesInvoiceCommentRepositoryV1"),
    SalesCreditMemoManagementV1("salesCreditmemoManagementV1"),
    SalesCreditMemoRepositoryV1("salesCreditmemoRepositoryV1"),
    SalesCreditMemoCommentRepositoryV1("salesCreditmemoCommentRepositoryV1"),
    SalesShipmentRepositoryV1("salesShipmentRepositoryV1"),
    SalesShipmentManagementV1("salesShipmentManagementV1"),
    SalesShipmentCommentRepositoryV1("salesShipmentCommentRepositoryV1"),
    SalesShipmentTrackRepositoryV1("salesShipmentTrackRepositoryV1"),
    SalesTransactionRepositoryV1("salesTransactionRepositoryV1"),

    //Tax
    // ========================================================================================================================================================================
    TaxTaxRateRepositoryV1("taxTaxRateRepositoryV1"),
    TaxTaxRuleRepositoryV1("taxTaxRuleRepositoryV1"),
    TaxTaxClassRepositoryV1("taxTaxClassRepositoryV1");

    private final String name;

    private final boolean available;

    MSoapAPI(String name) {
        this(name, true);
    }

    MSoapAPI(String name, boolean available) {
        this.available = available;
        this.name = name;
    }

    public final boolean isAvailable() {
        return available;
    }

    public final String getName() {
        return name;
    }

    private static final String getAddressWSDL(String service) {
        String baseurl = "http://pkayjava.ddns.net:90/magento2";
        String soapurl = "/soap/default?wsdl&services";
        List<String> services = new LinkedList<>();
        for (MSoapAPI soapAPI : MSoapAPI.values()) {
            if (soapAPI.getName().startsWith(service)) {
                if (soapAPI.isAvailable()) {
                    services.add(soapAPI.getName());
                }
            }
        }
        return baseurl + soapurl + "=" + org.apache.commons.lang3.StringUtils.join(services, ",");
    }

    public static final String getBackendAddressWSDL() {
        return getAddressWSDL("backend");
    }

    public static final String getIntegrationAddressWSDL() {
        return getAddressWSDL("integration");
    }

    public static final String getBundleAddressWSDL() {
        return getAddressWSDL("bundle");
    }

    public static final String getCatalogAddressWSDL() {
        return getAddressWSDL("catalog");
    }

    public static final String getDownloadableAddressWSDL() {
        return getAddressWSDL("downloadable");
    }

    public static final String getCheckoutAddressWSDL() {
        return getAddressWSDL("checkout");
    }

    public static final String getConfigurableAddressWSDL() {
        return getAddressWSDL("configurable");
    }

    public static final String getCustomerAddressWSDL() {
        return getAddressWSDL("customer");
    }

    public static final String getEavAddressWSDL() {
        return getAddressWSDL("eav");
    }

    public static final String getGiftAddressWSDL() {
        return getAddressWSDL("gift");
    }

    public static final String getQuoteAddressWSDL() {
        return getAddressWSDL("quote");
    }

    public static final String getSalesAddressWSDL() {
        return getAddressWSDL("sales");
    }

    public static final String getTaxAddressWSDL() {
        return getAddressWSDL("tax");
    }

    public static void main(String[] args) {
        System.out.println(getIntegrationAddressWSDL());
        System.out.println(getBackendAddressWSDL());
        System.out.println(getBundleAddressWSDL());
        System.out.println(getCatalogAddressWSDL());
        System.out.println(getCheckoutAddressWSDL());
        System.out.println(getConfigurableAddressWSDL());
        System.out.println(getCustomerAddressWSDL());
        System.out.println(getDownloadableAddressWSDL());
        System.out.println(getEavAddressWSDL());
        System.out.println(getGiftAddressWSDL());
        System.out.println(getQuoteAddressWSDL());
        System.out.println(getSalesAddressWSDL());
        System.out.println(getTaxAddressWSDL());
    }
}
