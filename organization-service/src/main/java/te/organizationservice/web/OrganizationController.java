package te.organizationservice.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import te.organizationservice.model.Organization;
import te.organizationservice.service.OrganizationService;

import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping(value = "v1/organization")
public class OrganizationController {
    @Autowired
    private OrganizationService service;


    @RequestMapping(value = "/{organizationId}", method = RequestMethod.GET)
    public ResponseEntity<Organization> getOrganization(@PathVariable("organizationId") String organizationId) throws TimeoutException {
        return ResponseEntity.ok(service.findById(organizationId));
    }


}
