package net.sourceforge.symba.mapping.hibernatejaxb2;

import org.testng.annotations.Test;
import net.sourceforge.fuge.service.EntityService;
import net.sourceforge.fuge.ServiceLocator;
import net.sourceforge.fuge.util.generatedJAXB2.FuGECommonAuditOrganizationType;
import net.sourceforge.fuge.common.audit.Organization;

public class DatabaseObjectHelperTest {

    @Test( groups = { "hibernate" } )
    public void getOrCreateTest() {

        // Assume the test database has already been created

        // run getOrCreate twice: once to create, the second time it will get.

        EntityService es = ServiceLocator.instance().getEntityService();

        Organization organization = ( Organization ) DatabaseObjectHelper.getOrCreate( null, "Test Organization for getOrCreate()", "net.sourceforge.fuge.common.audit.Organization" );

        assert (organization.getId() == null) : "organization's database id should be null until the object is loaded into the database";

        // Save the object in the database
        organization = ( Organization ) es.save( "net.sourceforge.fuge.common.audit.Organization", organization, null );

        assert ( organization.getId() != null ) : "organization must have a database id.";

        // do the same again. This time, getOrCreate() should get from the database
        Organization secondOrganization = ( Organization ) DatabaseObjectHelper.getOrCreate( organization.getIdentifier(), "Nothing should be loaded from the name attribute on a get from the database", "net.sourceforge.fuge.common.audit.Organization" );

        // the name should match the original name, and there should be a database id
        assert ( secondOrganization.getName().equals("Test Organization for getOrCreate()") ) : "secondOrganization must have name matching \"Test Organization for getOrCreate()\".";
        assert ( secondOrganization.getId() != null ) : "secondOrganization must have a database id.";
    }
}
