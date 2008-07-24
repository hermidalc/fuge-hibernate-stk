package net.sourceforge.symba.mapping.hibernatejaxb2.helper;


import net.sourceforge.fuge.collection.AuditCollection;
import net.sourceforge.fuge.common.audit.*;
import net.sourceforge.fuge.common.ontology.OntologyIndividual;
import net.sourceforge.fuge.service.EntityServiceException;
import net.sourceforge.fuge.util.generatedJAXB2.*;
import net.sourceforge.symba.mapping.hibernatejaxb2.DatabaseObjectHelper;

import javax.xml.bind.JAXBElement;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Copyright Notice
 *
 * The MIT License
 *
 * Copyright (c) 2008 2007-8 Proteomics Standards Initiative / Microarray and Gene Expression Data Society
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 * Acknowledgements
 *  The authors wish to thank the Proteomics Standards Initiative for
 *  the provision of infrastructure and expertise in the form of the PSI
 *  Document Process that has been used to formalise this document.
 * <p/>
 * $LastChangedDate$
 * $LastChangedRevision$
 * $Author$
 * $HeadURL: $
 */
public class AuditCollectionMappingHelper implements MappingHelper<AuditCollection, FuGECollectionAuditCollectionType> {
    private final int NUMBER_ELEMENTS = 2;
    private final DescribableMappingHelper cd;
    private final IdentifiableMappingHelper ci;

    public AuditCollectionMappingHelper() {
        this.ci = new IdentifiableMappingHelper();
        this.cd = ci.getCisbanDescribableHelper();
    }

    public AuditCollection unmarshal(
            FuGECollectionAuditCollectionType auditCollXML, AuditCollection auditColl, Person performer )
            throws EntityServiceException {

        // set describable information for the AuditCollection
        auditColl = ( AuditCollection ) cd.unmarshal( auditCollXML, auditColl, performer );
        auditColl = unmarshalCollectionContents( auditCollXML, auditColl, performer );

        // Once all of the AuditCollection is full, add it to the database.
        DatabaseObjectHelper.save( "net.sourceforge.fuge.collection.AuditCollection", auditColl, performer );

        return auditColl;
    }

    public AuditCollection unmarshalCollectionContents( FuGECollectionAuditCollectionType auditCollXML,
                                                        AuditCollection auditColl, Person performer )
            throws EntityServiceException {
        // Get contacts from AuditCollection
        List<JAXBElement<? extends FuGECommonAuditContactType>> contactsXML = auditCollXML.getContact();
        Set<Contact> contacts = new HashSet<Contact>();

        // An inherent limitation is that all Organizations MUST be loaded before Persons,
        // as Person can reference an organization. This means, unfortunately, two for-loops to
        // guarantee the order.
        for ( JAXBElement<? extends FuGECommonAuditContactType> contactElementXML : contactsXML ) {
            // create jaxb object
            FuGECommonAuditContactType contactXML = contactElementXML.getValue();

            // Discover if Person or Organization
            if ( contactXML instanceof FuGECommonAuditOrganizationType ) {
                // create jaxb object
                FuGECommonAuditOrganizationType organizationXML = ( FuGECommonAuditOrganizationType ) contactXML;

                // Retrieve from the database or create a new local instance.
                Organization organization = ( Organization ) DatabaseObjectHelper.getOrCreate(
                        organizationXML.getIdentifier(),
                        organizationXML.getName(),
                        "net.sourceforge.fuge.common.audit.Organization" );

                // set fuge object
                organization = unmarshalOrganization( organizationXML, organization, performer );

                // load fuge object into database
                DatabaseObjectHelper.save( "net.sourceforge.fuge.common.audit.Organization", organization, performer );

                // add fuge object into collection of objects
                contacts.add( organization );

            }
        }
        for ( JAXBElement<? extends FuGECommonAuditContactType> contactElementXML : contactsXML ) {
            // create jaxb object
            FuGECommonAuditContactType contactXML = contactElementXML.getValue();
            if ( contactXML instanceof FuGECommonAuditPersonType ) {
                // create jaxb object
                FuGECommonAuditPersonType personXML = ( FuGECommonAuditPersonType ) contactXML;

                // Retrieve from the database or create a new local instance.
                Person person = ( Person ) DatabaseObjectHelper.getOrCreate(
                        personXML.getIdentifier(),
                        personXML.getName(),
                        "net.sourceforge.fuge.common.audit.Person" );

                // set fuge object
                person = unmarshalPerson( personXML, person, performer );

                // load fuge object into database
                DatabaseObjectHelper.save( "net.sourceforge.fuge.common.audit.Person", person, performer );

                // add fuge object into collection of objects
                contacts.add( person );
            }
        }
        // set collection of contacts as the contacts for this AuditCollection.
        auditColl.setAllContacts( contacts );

        // set the security and security group
        Set<SecurityGroup> sgs = new HashSet<SecurityGroup>();
        for ( FuGECommonAuditSecurityGroupType sgXML : auditCollXML.getSecurityGroup() ) {
            SecurityGroup sg = ( SecurityGroup ) DatabaseObjectHelper.getOrCreate(
                    sgXML.getIdentifier(),
                    sgXML.getName(),
                    "net.sourceforge.fuge.common.audit.SecurityGroup" );
            sg = ( SecurityGroup ) ci.unmarshal( sgXML, sg, performer );

            Set<Contact> cs = new HashSet<Contact>();
            for ( FuGECommonAuditSecurityGroupType.Members memXML : sgXML.getMembers() ) {
                for ( Contact c : contacts ) {
                    if ( c.getIdentifier().equals( memXML.getContactRef() ) ) {
                        cs.add( c );
                        break;
                    }
                }
                cs.add( ( Contact ) entityService.getIdentifiable( memXML.getContactRef() ) );
            }
            sg.setMembers( cs );

            // load fuge object into database
            DatabaseObjectHelper.save( "net.sourceforge.fuge.common.audit.SecurityGroup", sg, performer );
            sgs.add( sg );
        }
        auditColl.setSecurityGroups( sgs );

        // fixme should owner really be a collection??
        Set<Security> securities = new HashSet<Security>();
        for ( FuGECommonAuditSecurityType securityXML : auditCollXML.getSecurity() ) {
            // Retrieve from the database or create a new local instance.
            Security security = ( Security ) DatabaseObjectHelper.getOrCreate(
                    securityXML.getIdentifier(),
                    securityXML.getName(),
                    "net.sourceforge.fuge.common.audit.Security" );
            security = ( Security ) ci.unmarshal( securityXML, security, performer );

            Set<Contact> cs = new HashSet<Contact>();
            for ( FuGECommonAuditSecurityType.Owners owner : securityXML.getOwners() ) {
                cs.add( ( Contact ) entityService.getIdentifiable( owner.getContactRef() ) );
            }
            security.setOwners( cs );

            Set<SecurityAccess> accesses = new HashSet<SecurityAccess>();
            for ( FuGECommonAuditSecurityAccessType accessXML : securityXML.getSecurityAccess() ) {
                SecurityAccess access = ( SecurityAccess ) entityService.createDescribable(
                        "net.sourceforge.fuge.common.audit.SecurityAccess" );
                access = ( SecurityAccess ) cd.unmarshal( accessXML, access, performer );

                if ( accessXML.getAccessRight() != null ) {
                    FuGECommonAuditSecurityAccessType.AccessRight accessRightXML = accessXML.getAccessRight();
                    // we will have a problem here if the ontologycollection hasn't been loaded yet.
                    // todo: temp solution is to simply not load these access rights if there is no ontology collection.
                    try {
                        access.setAccessRight( ( OntologyIndividual ) entityService.getIdentifiable( accessRightXML.getOntologyTermRef() ) );
                    } catch ( EntityServiceException e ) {
                        System.err.println(
                                "No Ontology Individual found: NOT LOADING THIS SECURITY ACCESS RIGHT! = " +
                                        accessXML.getAccessRight().getOntologyTermRef() );
                    }
                }
                if ( accessXML.getSecurityGroupRef() != null ) {
                    access.setAccessGroup( ( SecurityGroup ) entityService.getIdentifiable( accessXML.getSecurityGroupRef() ) );
                }
                DatabaseObjectHelper.save( "net.sourceforge.fuge.common.audit.SecurityAccess", access, performer );
                accesses.add( access );
            }
            security.setSecurityRights( accesses );

            // load fuge object into database
            DatabaseObjectHelper.save( "net.sourceforge.fuge.common.audit.Security", security, performer );
            securities.add( security );
        }

        auditColl.setSecurityCollection( securities );

        return auditColl;
    }

    private Person unmarshalPerson( FuGECommonAuditPersonType personXML, Person person, Person performer )
            throws EntityServiceException {

        person = ( Person ) unmarshallContact( personXML, person, performer );

        // set person traits
        String temp;
        if ( ( temp = personXML.getFirstName() ) != null )
            person.setFirstName( temp );
        if ( ( temp = personXML.getLastName() ) != null )
            person.setLastName( temp );
        if ( ( temp = personXML.getMidInitials() ) != null )
            person.setMidInitials( temp );

        Set<Organization> affiliations = new HashSet<Organization>();
        for ( FuGECommonAuditPersonType.Affiliations affiliationXML : personXML.getAffiliations() ) {
            affiliations.add( ( Organization ) entityService.getIdentifiable( affiliationXML.getOrganizationRef() ) );
        }
        person.setAffiliations( affiliations );

        return person;
    }

    public FuGECollectionAuditCollectionType marshal(
            FuGECollectionAuditCollectionType auditCollXML, AuditCollection auditColl )
            throws EntityServiceException {

        // set describable information
        auditCollXML = ( FuGECollectionAuditCollectionType ) cd.marshal( auditCollXML, auditColl );

        // Contacts

        ObjectFactory factory = new ObjectFactory();
        for ( Contact contact : auditColl.getAllContacts() ) {

            // Discover if Person or Organization
            if ( contact instanceof Organization ) {
                Organization organization = ( Organization ) contact;

                // create jaxb object
                FuGECommonAuditOrganizationType organizationXML = new FuGECommonAuditOrganizationType();

                // set organization traits
                organizationXML = marshalOrganization( organizationXML, organization );

                // add jaxb object into collection of objects
                auditCollXML.getContact().add( factory.createOrganization( organizationXML ) );

            } else if ( contact instanceof Person ) {
                Person person = ( Person ) contact;

                // create jaxb object
                FuGECommonAuditPersonType personXML = new FuGECommonAuditPersonType();

                // set jaxb object
                personXML = marshalPerson( personXML, person );

                // add jaxb object into collection of objects
                auditCollXML.getContact().add( factory.createPerson( personXML ) );
            }
        }
        // set the security and security group
        for ( SecurityGroup sg : auditColl.getSecurityGroups() ) {
            FuGECommonAuditSecurityGroupType sgXML = new FuGECommonAuditSecurityGroupType();
            sgXML = ( FuGECommonAuditSecurityGroupType ) ci.marshal( sgXML, sg );

//            sg = ( SecurityGroup ) entityService.greedyGet( sg );
            for ( Contact contact : sg.getMembers() ) {
                FuGECommonAuditSecurityGroupType.Members memXML = new FuGECommonAuditSecurityGroupType.Members();
                memXML.setContactRef( contact.getIdentifier() );
                sgXML.getMembers().add( memXML );
            }
            auditCollXML.getSecurityGroup().add( sgXML );
        }

        // fixme should owner really be a collection??
        for ( Security security : auditColl.getSecurityCollection() ) {
            FuGECommonAuditSecurityType securityXML = new FuGECommonAuditSecurityType();
            securityXML = ( FuGECommonAuditSecurityType ) ci.marshal( securityXML, security );

//            security = ( Security ) entityService.greedyGet( security );
            for ( Contact c : security.getOwners() ) {
                FuGECommonAuditSecurityType.Owners ownerXML = new FuGECommonAuditSecurityType.Owners();
                ownerXML.setContactRef( c.getIdentifier() );
                securityXML.getOwners().add( ownerXML );
            }

            for ( SecurityAccess sa : security.getSecurityRights() ) {
                FuGECommonAuditSecurityAccessType accessXML = new FuGECommonAuditSecurityAccessType();
                accessXML = ( FuGECommonAuditSecurityAccessType ) cd.marshal( accessXML, sa );

                if ( sa.getAccessRight() != null ) {
                    FuGECommonAuditSecurityAccessType.AccessRight accessRightXML = new FuGECommonAuditSecurityAccessType.AccessRight();
                    accessRightXML.setOntologyTermRef( sa.getAccessRight().getIdentifier() );
                    accessXML.setAccessRight( accessRightXML );
                }
                if ( sa.getAccessGroup() != null ) {
//                    sa = ( SecurityAccess ) entityService.greedyGet( sa );
                    accessXML.setSecurityGroupRef( sa.getAccessGroup().getIdentifier() );
                }
                securityXML.getSecurityAccess().add( accessXML );
            }
            auditCollXML.getSecurity().add( securityXML );
        }

        return auditCollXML;
    }

    public FuGECollectionAuditCollectionType generateRandomXML( FuGECollectionAuditCollectionType auditCollXML ) {

        // set describable information
        auditCollXML = ( FuGECollectionAuditCollectionType ) cd.generateRandomXML( auditCollXML );

        // Contacts
        String firstOrg = null;
        ObjectFactory factory = new ObjectFactory();
        for ( int i = 0; i < NUMBER_ELEMENTS; i++ ) {
            // create jaxb object
            FuGECommonAuditOrganizationType organizationXML = new FuGECommonAuditOrganizationType();

            // set jaxb object
            organizationXML = ( FuGECommonAuditOrganizationType ) generateRandomContactXML( organizationXML );

            // set organization traits - only set a parent if i > 0.

            if ( i > 0 ) {
                FuGECommonAuditOrganizationType.Parent parentOrganizationXML = new FuGECommonAuditOrganizationType.Parent();
                parentOrganizationXML.setOrganizationRef( firstOrg );
                organizationXML.setParent( parentOrganizationXML );
            } else {
                firstOrg = organizationXML.getIdentifier();
            }

            // add jaxb object into collection of objects
            auditCollXML.getContact().add( factory.createOrganization( organizationXML ) );

            // create jaxb object
            FuGECommonAuditPersonType personXML = new FuGECommonAuditPersonType();

            personXML = ( FuGECommonAuditPersonType ) generateRandomContactXML( personXML );

            // set jaxb object
            personXML = generateRandomPersonXML( personXML, organizationXML );

            // add jaxb object into collection of objects
            auditCollXML.getContact().add( factory.createPerson( personXML ) );
        }

        // set the security and security group
        for ( int i = 0; i < NUMBER_ELEMENTS; i++ ) {
            FuGECommonAuditSecurityGroupType sgXML = new FuGECommonAuditSecurityGroupType();
            sgXML = ( FuGECommonAuditSecurityGroupType ) ci.generateRandomXML( sgXML );

            for ( int ii = 0; ii < NUMBER_ELEMENTS; ii++ ) {
                FuGECommonAuditSecurityGroupType.Members memXML = new FuGECommonAuditSecurityGroupType.Members();
                memXML.setContactRef( auditCollXML.getContact().get( ii ).getValue().getIdentifier() );
                sgXML.getMembers().add( memXML );
            }
            auditCollXML.getSecurityGroup().add( sgXML );
        }

        return auditCollXML;
    }

    // generates together with links to the ontologycollection.
    public FuGECollectionFuGEType generateRandomXMLwithLinksOut( FuGECollectionFuGEType fuGEType ) {

        // create jaxb object
        FuGECollectionAuditCollectionType auditCollXML = generateRandomXML( new FuGECollectionAuditCollectionType() );

        // fixme should owner really be a collection??
        for ( int i = 0; i < NUMBER_ELEMENTS; i++ ) {
            FuGECommonAuditSecurityType securityXML = new FuGECommonAuditSecurityType();
            securityXML = ( FuGECommonAuditSecurityType ) ci.generateRandomXML( securityXML );

            for ( int ii = 0; ii < NUMBER_ELEMENTS; ii++ ) {
                FuGECommonAuditSecurityType.Owners ownerXML = new FuGECommonAuditSecurityType.Owners();
                ownerXML.setContactRef( auditCollXML.getContact().get( ii ).getValue().getIdentifier() );
                securityXML.getOwners().add( ownerXML );
            }

            for ( int ii = 0; ii < NUMBER_ELEMENTS; ii++ ) {
                FuGECommonAuditSecurityAccessType accessXML = new FuGECommonAuditSecurityAccessType();
                accessXML = ( FuGECommonAuditSecurityAccessType ) cd.generateRandomXML( accessXML );

                FuGECommonAuditSecurityAccessType.AccessRight accessRightXML = new FuGECommonAuditSecurityAccessType.AccessRight();
                if ( fuGEType.getOntologyCollection() == null ) {
                    OntologyCollectionMappingHelper coc = new OntologyCollectionMappingHelper();
                    fuGEType.setOntologyCollection( coc.generateRandomXML( new FuGECollectionOntologyCollectionType() ) );
                }
                accessRightXML.setOntologyTermRef(
                        fuGEType.getOntologyCollection().getOntologyTerm().get( ii ).getValue().getIdentifier() );
                accessXML.setAccessRight( accessRightXML );

                accessXML.setSecurityGroupRef( auditCollXML.getSecurityGroup().get( ii ).getIdentifier() );
                securityXML.getSecurityAccess().add( accessXML );
            }
            auditCollXML.getSecurity().add( securityXML );
        }

        fuGEType.setAuditCollection( auditCollXML );
        return fuGEType;
    }

    private FuGECommonAuditContactType generateRandomContactXML( FuGECommonAuditContactType contactXML ) {
        // set all identifiable traits in the jaxb object
        contactXML = ( FuGECommonAuditContactType ) ci.generateRandomXML( contactXML );

        // set all non-identifiable contact traits
        contactXML.setAddress( String.valueOf( Math.random() ) );
        contactXML.setEmail( String.valueOf( Math.random() ) );
        contactXML.setFax( String.valueOf( Math.random() ) );
        contactXML.setPhone( String.valueOf( Math.random() ) );
        contactXML.setTollFreePhone( String.valueOf( Math.random() ) );

        return contactXML;
    }

    private FuGECommonAuditPersonType generateRandomPersonXML(
            FuGECommonAuditPersonType personXML,
            FuGECommonAuditOrganizationType organizationXML ) {

        // set person traits
        personXML.setFirstName( String.valueOf( Math.random() ) );
        personXML.setLastName( String.valueOf( Math.random() ) );
        personXML.setMidInitials( String.valueOf( Math.random() ) );

        for ( int i = 0; i < NUMBER_ELEMENTS; i++ ) {
            FuGECommonAuditPersonType.Affiliations affiliationXML = new FuGECommonAuditPersonType.Affiliations();

            affiliationXML.setOrganizationRef( organizationXML.getIdentifier() );
            personXML.getAffiliations().add( affiliationXML );
        }
        return personXML;
    }

    public FuGECommonAuditPersonType marshalPerson( FuGECommonAuditPersonType personXML,
                                                    Person person ) {

        personXML = ( FuGECommonAuditPersonType ) marshalContact( personXML, person );

        // set person traits
        String temp;
        if ( ( temp = person.getFirstName() ) != null )
            personXML.setFirstName( temp );
        if ( ( temp = person.getLastName() ) != null )
            personXML.setLastName( temp );
        if ( ( temp = person.getMidInitials() ) != null )
            personXML.setMidInitials( temp );

        Collection<Organization> affiliations;
        if ( !( affiliations = person.getAffiliations() ).isEmpty() ) {
            for ( Organization affiliation : affiliations ) {
                FuGECommonAuditPersonType.Affiliations affiliationXML = new FuGECommonAuditPersonType.Affiliations();

                affiliationXML.setOrganizationRef( affiliation.getIdentifier() );
                personXML.getAffiliations().add( affiliationXML );
            }
        }
        return personXML;
    }

    public Organization unmarshalOrganization( FuGECommonAuditOrganizationType organizationXML,
                                               Organization organization, Person performer ) throws EntityServiceException {

        organization = ( Organization ) unmarshallContact( organizationXML, organization, performer );

        // set organization traits, if present
        if ( organizationXML.getParent() != null ) {
            Organization parent = ( Organization ) entityService.getIdentifiable( organizationXML.getParent().getOrganizationRef() );
            if ( parent == null ) {
                throw new EntityServiceException( "Organziation " + organizationXML.getIdentifier() +
                        " has a Parent Organization (" + organizationXML.getParent().getOrganizationRef() + ") NOT YET in the database!" );
            } else {
                organization.setParent( parent );
            }
        }
        return organization;
    }

    public FuGECommonAuditOrganizationType marshalOrganization( FuGECommonAuditOrganizationType organizationXML,
                                                                Organization organization ) {

        organizationXML = ( FuGECommonAuditOrganizationType ) marshalContact( organizationXML, organization );

        if ( organization.getParent() != null ) {
            FuGECommonAuditOrganizationType.Parent parentOrganizationXML = new FuGECommonAuditOrganizationType.Parent();
            parentOrganizationXML.setOrganizationRef( organization.getParent().getIdentifier() );
            organizationXML.setParent( parentOrganizationXML );
        }
        return organizationXML;
    }

    private Contact unmarshallContact( FuGECommonAuditContactType contactXML,
                                       Contact contact, Person performer ) throws EntityServiceException {
        // set all identifiable traits in the fuge object
        contact = ( Contact ) ci.unmarshal( contactXML, contact, performer );

        String temp;
        // set all non-identifiable contact traits
        if ( ( temp = contactXML.getAddress() ) != null )
            contact.setAddress( temp );
        if ( ( temp = contactXML.getEmail() ) != null )
            contact.setEmail( temp );
        if ( ( temp = contactXML.getFax() ) != null )
            contact.setFax( temp );
        if ( ( temp = contactXML.getPhone() ) != null )
            contact.setPhone( temp );
        if ( ( temp = contactXML.getTollFreePhone() ) != null )
            contact.setTollFreePhone( temp );

        return contact;
    }

    public FuGECommonAuditContactType marshalContact( FuGECommonAuditContactType contactXML,
                                                      Contact contact ) {
        // set all identifiable traits in the jaxb object
        contactXML = ( FuGECommonAuditContactType ) ci.marshal( contactXML, contact );

        String temp;
        // set all non-identifiable contact traits
        if ( ( temp = contact.getAddress() ) != null )
            contactXML.setAddress( temp );
        if ( ( temp = contact.getEmail() ) != null )
            contactXML.setEmail( temp );
        if ( ( temp = contact.getFax() ) != null )
            contactXML.setFax( temp );
        if ( ( temp = contact.getPhone() ) != null )
            contactXML.setPhone( temp );
        if ( ( temp = contact.getTollFreePhone() ) != null )
            contactXML.setTollFreePhone( temp );

        return contactXML;
    }
}
