package net.sourceforge.symba;

import net.sourceforge.fuge.service.EntityService;
import net.sourceforge.fuge.ServiceLocator;
import net.sourceforge.fuge.common.audit.Person;
import net.sourceforge.fuge.common.audit.Organization;
import net.sourceforge.fuge.common.description.Description;
import net.sourceforge.fuge.common.description.NameValueType;
import net.sourceforge.fuge.common.description.Uri;
import net.sourceforge.symba.mapping.hibernatejaxb2.DatabaseObjectHelper;
import org.testng.annotations.Test;

import java.util.Set;
import java.util.HashSet;

public class StoreAndRetrieveTest {

    @Test( groups = { "pojos" } )
    public void createDescribable() {

        // Assume the test database has already been created

        EntityService es = ServiceLocator.instance().getEntityService();

        // Create a Describable object: normally here you would use the return value in some other code
        es.createDescribable( "net.sourceforge.fuge.common.description.Description" );
    }

    @Test( groups = { "hibernate" } )
    public void saveDescribable() {

        // Assume the test database has already been created

        EntityService es = ServiceLocator.instance().getEntityService();

        // Create a Describable object locally
        Description description = ( Description ) es.createDescribable( "net.sourceforge.fuge.common.description.Description" );

        // Save the Describable object in the database
        DatabaseObjectHelper.save( "net.sourceforge.fuge.common.description.Description", description, null );
    }

    @Test( groups = { "pojos" } )
    public void createIdentifiable() {

        // Assume the test database has already been created

        // Create an Identifiable Object locally: normally here you would use the return value in some other code
        DatabaseObjectHelper.getOrCreate( "createIdentifiable:Organization:" + String.valueOf( Math.random() ), "createIdentifiableWithPerformer", "net.sourceforge.fuge.common.audit.Organization" );
    }

    @Test( groups = { "hibernate" } )
    public void saveIdentifiable() {

        // Assume the test database has already been created

        // Create the Identifiable Object locally
        Organization organization = ( Organization ) DatabaseObjectHelper.getOrCreate( "saveIdentifiable:Organization:" + String.valueOf( Math.random() ), "saveIdentifiableWithPerformer", "net.sourceforge.fuge.common.audit.Organization" );

        // Save the Identifiable object in the database
        organization = ( Organization ) DatabaseObjectHelper.save( "net.sourceforge.fuge.common.audit.Organization", organization, null );

        assert ( organization.getId() != null ) : "organization must have a database id.";
    }

    @Test( groups = { "hibernate" } )
    public void saveDescribableWithPerformer() {

        // Assume the test database has already been created

        EntityService es = ServiceLocator.instance().getEntityService();

        // Create the performer
        Person performer = ( Person ) DatabaseObjectHelper.getOrCreate( "saveDescribableWithPerformer:Performer:" + String.valueOf( Math.random() ), "saveDescribableWithPerformer", "net.sourceforge.fuge.common.audit.Person" );

        // Save the Performer
        performer = ( Person ) DatabaseObjectHelper.save( "net.sourceforge.fuge.common.audit.Person", performer, null );

        assert ( performer.getId() != null ) : "performer must have a database id.";

        // Create the Describable Object locally
        Description description = ( Description ) es.createDescribable( "net.sourceforge.fuge.common.description.Description" );

        // Save the Describable object in the database
        description = ( Description ) DatabaseObjectHelper.save( "net.sourceforge.fuge.common.description.Description", description, performer );

        assert ( description.getId() != null ) : "description must have a database id.";
    }

    // in the past there were problems with the URI mapping. This method will flag them if the mappings are not
    // set correctly.
    @Test( groups = { "hibernate" } )
    public void saveIdentifiableWithURI() {

        // Assume the test database has already been created

        EntityService es = ServiceLocator.instance().getEntityService();

        // Create the performer
        Uri uri = ( Uri ) es.createDescribable( "net.sourceforge.fuge.common.description.Uri" );

        uri.setLocation( "http://some.random.url/saveIdentifiableWithURI/" + String.valueOf( Math.random() ) );

        // load fuge object into database
        DatabaseObjectHelper.save( "net.sourceforge.fuge.common.description.Uri", uri, null );

        // Create the Identifiable Object locally
        Organization organization = ( Organization ) DatabaseObjectHelper.getOrCreate( "saveIdentifiableWithPerformer:Organization:" + String.valueOf( Math.random() ), "saveIdentifiableWithPerformer", "net.sourceforge.fuge.common.audit.Organization" );

        // load fuge object into organization
        organization.setUri( uri );

        // Save the Identifiable object in the database
        organization = ( Organization ) DatabaseObjectHelper.save( "net.sourceforge.fuge.common.audit.Organization", organization, null );

        assert ( organization.getId() != null ) : "organization must have a database id.";
        assert ( organization.getUri() != null ) : "organization must have a URI.";

        // retrieve the organization again, to ensure it can retrieve without errors
        Organization retrievedOrganization = ( Organization ) es.getIdentifiable( organization.getIdentifier() );

        assert ( retrievedOrganization.getId() != null ) : "retrievedOrganization must have a database id.";
        assert ( retrievedOrganization.getUri() != null ) : "retrievedOrganization must have a URI.";
    }

    @Test( groups = { "hibernate" } )
    public void saveIdentifiableWithPerformer() {

        // Assume the test database has already been created

        // Create the performer
        Person performer = ( Person ) DatabaseObjectHelper.getOrCreate( "saveIdentifiableWithPerformer:Performer:" + String.valueOf( Math.random() ), "saveIdentifiableWithPerformer", "net.sourceforge.fuge.common.audit.Person" );

        // Save the Performer
        performer = ( Person ) DatabaseObjectHelper.save( "net.sourceforge.fuge.common.audit.Person", performer, null );

        assert ( performer.getId() != null ) : "performer must have a database id.";

        // Create the Identifiable Object locally
        Organization organization = ( Organization ) DatabaseObjectHelper.getOrCreate( "saveIdentifiableWithPerformer:Organization:" + String.valueOf( Math.random() ), "saveIdentifiableWithPerformer", "net.sourceforge.fuge.common.audit.Organization" );

        // Save the Identifiable object in the database
        organization = ( Organization ) DatabaseObjectHelper.save( "net.sourceforge.fuge.common.audit.Organization", organization, performer );

        assert ( organization.getId() != null ) : "organization must have a database id.";
    }

    @Test( groups = { "hibernate" } )
    public void getIdentifiableById() {

        // Assume the test database has already been created

        EntityService es = ServiceLocator.instance().getEntityService();

        // Create the Identifiable Object locally: we're choosing the identifier explicitly here so we can get it in the
        // next test
        Organization organization = ( Organization ) DatabaseObjectHelper.getOrCreate( "getIdentifiableById:Organization:" + String.valueOf( Math.random() ), "getIdentifiableById", "net.sourceforge.fuge.common.audit.Organization" );

        // Save the Identifiable object in the database
        organization = ( Organization ) DatabaseObjectHelper.save( "net.sourceforge.fuge.common.audit.Organization", organization, null );

        assert ( organization.getId() != null ) : "organization must have a database id.";

        // retrieve the same object from the database
        Organization retrievedOrganization = ( Organization ) es.getIdentifiable( organization.getId() );

        assert ( retrievedOrganization.getId() != null ) : "retrievedOrganization must have a database id.";
    }

    @Test( groups = { "hibernate" } )
    public void getIdentifiableByIdentifier() {

        // Assume the test database has already been created

        EntityService es = ServiceLocator.instance().getEntityService();

        // Create the Identifiable Object locally: we're choosing the identifier explicitly here so we can get it in the
        // next test
        Organization organization = ( Organization ) DatabaseObjectHelper.getOrCreate( "getIdentifiableByIdentifier:Organization:" + String.valueOf( Math.random() ), "getIdentifiableByIdentifier", "net.sourceforge.fuge.common.audit.Organization" );

        // Save the Identifiable object in the database
        organization = ( Organization ) DatabaseObjectHelper.save( "net.sourceforge.fuge.common.audit.Organization", organization, null );

        assert ( organization.getId() != null ) : "organization must have a database id.";

        // retrieve the same object from the database
        Organization retrievedOrganization = ( Organization ) es.getIdentifiable( organization.getIdentifier() );

        assert ( retrievedOrganization.getId() != null ) : "retrievedOrganization must have a database id.";
    }

    @Test( groups = { "hibernate" } )
    public void saveOrganizationWithNewNVTSet() {

        // Assume the test database has already been created

        EntityService es = ServiceLocator.instance().getEntityService();

        // Create the Identifiable Object locally: we're choosing the identifier explicitly here so we can get it in the
        // next test
        Organization organization = ( Organization ) DatabaseObjectHelper.getOrCreate( "saveOrganizationWithNewNVTSet:Organization:" + String.valueOf( Math.random() ), "saveOrganizationWithNewNVTSet", "net.sourceforge.fuge.common.audit.Organization" );

        // add any attribute that links to another FuGE object rather than a plain java type
        // create fuge object for name value type
        NameValueType nameValueType = makeNVT( "1:saveOrganizationWithNewNVTSet" );

        // load fuge object into collection
        Set<NameValueType> propertySets = new HashSet<NameValueType>();
        propertySets.add( nameValueType );
        organization.setPropertySets( propertySets );

        // Save the Identifiable object in the database
        organization = ( Organization ) DatabaseObjectHelper.save( "net.sourceforge.fuge.common.audit.Organization", organization, null );

        assert ( organization.getId() != null ) : "organization must have a database id.";

        // retrieve the same object from the database
        Organization retrievedOrganization = ( Organization ) es.getIdentifiable( organization.getIdentifier() );

        assert ( retrievedOrganization.getId() != null ) : "retrievedOrganization must have a database id.";
        assert ( retrievedOrganization.getPropertySets().size() == 1 ) : "retrievedOrganization must have one item in the property set.";
    }

    @Test( groups = { "hibernate" } )
    public void saveOrganizationWithPreLoadedNVTSet() {

        // Assume the test database has already been created

        EntityService es = ServiceLocator.instance().getEntityService();

        // Create the Identifiable Object locally: we're choosing the identifier explicitly here so we can get it in the
        // next test
        Organization organization = ( Organization ) DatabaseObjectHelper.getOrCreate( "saveOrganizationWithPreLoadedNVTSet:Organization:" + String.valueOf( Math.random() ), "saveOrganizationWithPreLoadedNVTSet", "net.sourceforge.fuge.common.audit.Organization" );

        NameValueType nameValueType = makeNVT( "1:saveOrganizationWithPreLoadedNVTSet:FirstNVT" );

        // load fuge object into collection
        Set<NameValueType> propertySets = ( Set<NameValueType> ) organization.getPropertySets();
        propertySets.add( nameValueType );
        organization.setPropertySets( propertySets );

        // Save the Identifiable object in the database
        organization = ( Organization ) DatabaseObjectHelper.save( "net.sourceforge.fuge.common.audit.Organization", organization, null );

        assert ( organization.getId() != null ) : "organization must have a database id.";

        // retrieve the same object from the database
        Organization retrievedOrganization = ( Organization ) es.getIdentifiable( organization.getIdentifier() );

        assert ( retrievedOrganization.getId() != null ) : "retrievedOrganization must have a database id.";
        assert ( retrievedOrganization.getPropertySets().size() == 1 ) : "retrievedOrganization must have one item in the property set.";

        // add any attribute that links to another FuGE object rather than a plain java type
        // create fuge object for name value type
        NameValueType nameValueType2 = makeNVT( "1:saveOrganizationWithPreLoadedNVTSet:SecondNVT" );

        // load fuge object into collection
        propertySets = ( Set<NameValueType> ) retrievedOrganization.getPropertySets();
        propertySets.add( ( NameValueType ) es.getDescribable( nameValueType2.getId() ) );
        retrievedOrganization.setPropertySets( propertySets );

        retrievedOrganization = ( Organization ) DatabaseObjectHelper.save( "net.sourceforge.fuge.common.audit.Organization", retrievedOrganization, null );

        assert ( retrievedOrganization.getId() != null ) : "retrievedOrganization must have a database id.";
        assert ( retrievedOrganization.getPropertySets().size() == 2 ) : "retrievedOrganization must have 2 items in the property set.";

        Organization retrievedOrganization2 = ( Organization ) es.getIdentifiable( retrievedOrganization.getIdentifier() );

        assert ( retrievedOrganization2.getId() != null ) : "retrievedOrganization2 must have a database id.";
        assert ( retrievedOrganization2.getPropertySets().size() == 2 ) : "retrievedOrganization2 must have 2 items in the property set.";
    }

    private NameValueType makeNVT( String name ) {

        EntityService es = ServiceLocator.instance().getEntityService();

        NameValueType nameValueType = ( NameValueType ) es.createDescribable(
                "net.sourceforge.fuge.common.description.NameValueType" );

        // set fuge object
        nameValueType.setName( name );
        nameValueType.setType( "2" );
        nameValueType.setValue( "3" );

        // load fuge object into database
        DatabaseObjectHelper.save( "net.sourceforge.fuge.common.description.NameValueType", nameValueType, null );

        return nameValueType;
    }
}
