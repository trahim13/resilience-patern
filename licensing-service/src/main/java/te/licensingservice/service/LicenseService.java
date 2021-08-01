package te.licensingservice.service;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import te.licensingservice.model.License;
import te.licensingservice.model.Organization;
import te.licensingservice.service.client.OrganizationDiscoveryClient;
import te.licensingservice.service.client.OrganizationFeignClient;
import te.licensingservice.service.client.OrganizationRestTemplateClient;

@Service
public class LicenseService {

    @Autowired
    OrganizationFeignClient organizationFeignClient;

    @Autowired
    OrganizationRestTemplateClient organizationRestClient;

    @Autowired
    OrganizationDiscoveryClient organizationDiscoveryClient;

    private static final Logger logger = LoggerFactory.getLogger(LicenseService.class);

    @CircuitBreaker(name = "licenseService", fallbackMethod = "buildFallbackLicenseCircuitBreaker")
    @Retry(name = "retryLicenseService", fallbackMethod = "buildFallbackLicenseRetry")
//    @Bulkhead(name = "bulkheadLicenseService", type= Bulkhead.Type.THREADPOOL, fallbackMethod = "buildFallbackLicense")
    public License getLicense(String licenseId, String organizationId, String clientType) {
        License license = this.findByOrganizationId(organizationId);

        Organization organization = this.retrieveOrganizationInfo(organizationId, clientType);
        if (null != organization) {
            license.setOrganizationName(organization.getName());
            license.setContactName(organization.getContactName());
            license.setContactEmail(organization.getContactEmail());
            license.setContactPhone(organization.getContactPhone());
        }

        return license.withComment("It's normal way");
    }


    private License findByOrganizationId(String organizationId) {
        License license = new License();
        license.setLicenseId("#1000000-90-40000");
        license.setDescription("Hunting license");
        license.setOrganizationId(organizationId);
        license.setProductName("Bird hunting license");
        license.setLicenseType("COMIC");
        license.setComment("Take your guns");
        license.setOrganizationName("Horns and hooves");
        license.setContactPhone("+999 777 896 781");
        license.setContactPhone("liners@happy.com");
        return license;
    }


    private Organization retrieveOrganizationInfo(String organizationId, String clientType) {
        Organization organization = null;

        switch (clientType) {
            case "feign":
                System.out.println("I am using the feign client");
                organization = organizationFeignClient.getOrganization(organizationId);
                break;
            case "rest":
                System.out.println("I am using the rest client");
                organization = organizationRestClient.getOrganization(organizationId);
                break;
            case "discovery":
                System.out.println("I am using the discovery client");
                organization = organizationDiscoveryClient.getOrganization(organizationId);
                break;
            default:
                organization = organizationRestClient.getOrganization(organizationId);
                break;
        }

        return organization;
    }


    @SuppressWarnings("unused")
    private License buildFallbackLicenseCircuitBreaker(String licenseId, String organizationId, String clientType, Throwable t) {
        License license = new License();
        license.setLicenseId("0000000-00-00000");
        license.setOrganizationId(organizationId);
        license.setProductName("Sorry no licensing information currently available: CircuitBreaker");
        return license;
    }


    @SuppressWarnings("unused")
    private License buildFallbackLicenseRetry(String licenseId, String organizationId, String clientType, Throwable t) {
        License license = new License();
        license.setLicenseId("0000000-00-00000");
        license.setOrganizationId(organizationId);
        license.setProductName("Sorry no licensing information currently available:Retry");
        return license;
    }
}
