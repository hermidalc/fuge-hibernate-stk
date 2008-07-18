package net.sourceforge.symba.mapping.hibernatejaxb2.helper;

import org.testng.annotations.Test;
import net.sourceforge.fuge.service.EntityService;
import net.sourceforge.fuge.ServiceLocator;
import net.sourceforge.fuge.common.protocol.GenericSoftware;
import net.sourceforge.fuge.common.protocol.GenericEquipment;
import net.sourceforge.symba.mapping.hibernatejaxb2.DatabaseObjectHelper;

import java.util.Set;
import java.util.HashSet;

public class GenericSoftwareMappingHelperTest {

    // the problem comes from the many2many relationship between Software and Equipment. When used within
    // the database, though both Software and Equipment are present in the database, the Software2Equipment
    // table is EMPTY! It may be a hibernate-specific problem.
    @Test( groups = { "hibernate" } )
    public void testSoftware2EquipmentLink() {

        // Assume the test database has already been created

        EntityService es = ServiceLocator.instance().getEntityService();

        // Create a GenericSoftware object locally
        GenericSoftware genericSoftware = ( GenericSoftware ) DatabaseObjectHelper.getOrCreate( null,
                "Test GenericSoftware for testSoftware2EquipmentLink() " + String.valueOf( Math.random() ),
                "net.sourceforge.fuge.common.protocol.GenericSoftware" );

        // Create a GenericEquipment object locally
        GenericEquipment genericEquipment = ( GenericEquipment ) DatabaseObjectHelper.getOrCreate( null,
                "Test GenericEquipment for testSoftware2EquipmentLink() " + String.valueOf( Math.random() ),
                "net.sourceforge.fuge.common.protocol.GenericEquipment" );

        // Save the GenericEquipment object in the database
        es.save( "net.sourceforge.fuge.common.protocol.GenericEquipment", genericEquipment, null );

        Set<GenericEquipment> genericEquipments = new HashSet<GenericEquipment>();
        genericEquipments.add( genericEquipment );
        genericSoftware.setEquipment( genericEquipments );

        // Save the GenericSoftware object in the database
        genericSoftware = ( GenericSoftware ) es.save( "net.sourceforge.fuge.common.protocol.GenericSoftware",
                genericSoftware, null );

        // retrieve the software from the database, and check for the link to the equipment
        GenericSoftware retrievedSoftware = ( GenericSoftware ) es.getIdentifiable( genericSoftware.getId() );
        GenericEquipment retrievedEquipment = ( GenericEquipment ) es.getIdentifiable( genericEquipment.getId() );

        assert ( retrievedEquipment != null ) :
                "The GenericEquipment should be loaded in the DB, and it is not.";

        assert ( retrievedSoftware != null ) :
                "The GenericSoftware should be loaded in the DB, and it is not.";

        assert ( !retrievedSoftware.getEquipment().isEmpty() ) :
                "The GenericSoftware should have a link to 1 Equipment, and it is instead linked to none.";
    }

    // this is the same as the testSoftware2EquipmentLink, but with an attempt at a session.flush(), as per
    // http://www.17od.com/2006/11/09/problem-with-hibernate-many-to-many-association/
    @Test( groups = { "hibernate" } )
    public void testSoftware2EquipmentLinkWithFlush() {

        // Assume the test database has already been created
        GenericSoftware genericSoftware;
        GenericEquipment genericEquipment;
        {
        EntityService es = ServiceLocator.instance().getEntityService();

        // Create a GenericSoftware object locally
        genericSoftware = ( GenericSoftware ) DatabaseObjectHelper.getOrCreate( null,
                "Test GenericSoftware for testSoftware2EquipmentLink() " + String.valueOf( Math.random() ),
                "net.sourceforge.fuge.common.protocol.GenericSoftware" );

        // Create a GenericEquipment object locally
        genericEquipment = ( GenericEquipment ) DatabaseObjectHelper.getOrCreate( null,
                "Test GenericEquipment for testSoftware2EquipmentLink() " + String.valueOf( Math.random() ),
                "net.sourceforge.fuge.common.protocol.GenericEquipment" );

        // Save the GenericEquipment object in the database
        es.save( "net.sourceforge.fuge.common.protocol.GenericEquipment", genericEquipment, null );

        Set<GenericEquipment> genericEquipments = new HashSet<GenericEquipment>();
        genericEquipments.add( genericEquipment );
        genericSoftware.setEquipment( genericEquipments );

        // Save the GenericSoftware object in the database
        genericSoftware = ( GenericSoftware ) es.save( "net.sourceforge.fuge.common.protocol.GenericSoftware",
                genericSoftware, null );

        // force session flush by closing the entity service. 
        }

        EntityService es = ServiceLocator.instance().getEntityService();
        // retrieve the software from the database, and check for the link to the equipment
        GenericSoftware retrievedSoftware = ( GenericSoftware ) es.getIdentifiable( genericSoftware.getId() );
        GenericEquipment retrievedEquipment = ( GenericEquipment ) es.getIdentifiable( genericEquipment.getId() );

        assert ( retrievedEquipment != null ) :
                "The GenericEquipment should be loaded in the DB, and it is not.";

        assert ( retrievedSoftware != null ) :
                "The GenericSoftware should be loaded in the DB, and it is not.";

        assert ( !retrievedSoftware.getEquipment().isEmpty() ) :
                "The GenericSoftware should have a link to 1 Equipment, and it is instead linked to none.";
    }

}
