package te.organizationservice.service;

import org.springframework.stereotype.Service;
import te.organizationservice.model.Organization;

import java.util.concurrent.TimeoutException;

@Service
public class OrganizationService {


    public Organization findById(String organizationId) throws TimeoutException {
        Organization org = new Organization();
        org.setId(organizationId);
        org.setName("Wild boars");
        org.setContactName("Angry Stiff");
        org.setContactEmail("sunglasses@sea.com");
        org.setContactPhone("+999 376 324 453");
        if ("breakOrganization".equals(organizationId)) {
            throw new TimeoutException();
        }
        return org;
    }


}