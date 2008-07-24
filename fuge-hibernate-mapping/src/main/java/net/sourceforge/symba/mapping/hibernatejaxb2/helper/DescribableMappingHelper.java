package net.sourceforge.symba.mapping.hibernatejaxb2.helper;

import net.sourceforge.fuge.common.audit.*;
import net.sourceforge.fuge.common.Describable;
import net.sourceforge.fuge.common.description.Description;
import net.sourceforge.fuge.common.description.NameValueType;
import net.sourceforge.fuge.common.description.Uri;
import net.sourceforge.fuge.common.ontology.OntologyTerm;
import net.sourceforge.fuge.service.EntityServiceException;
import net.sourceforge.fuge.util.generatedJAXB2.*;
import net.sourceforge.symba.mapping.hibernatejaxb2.DatabaseObjectHelper;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.DatatypeConfigurationException;
import java.util.*;

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
public class DescribableMappingHelper implements MappingHelper<Describable, FuGECommonDescribableType> {
    private final int NUMBER_ELEMENTS = 2;

    public DescribableMappingHelper() {
    }

    /**
     * Please note that this Describable object itself is NEITHER created NOR loaded into the database here,
     * as that would require a knowledge of what subclass of Describable this was. However, other objects that are
     * part of the Describable object are loaded.
     */
    public Describable unmarshal( FuGECommonDescribableType describableXML,
                                  Describable describable, Person performer ) throws EntityServiceException {

        // In jaxb, auditTrail is an object in its own right, while in fuge, it is simply
        // a collection of Audits. It is an optional element
        // get jaxb object of the auditTrail
        FuGECommonDescribableType.AuditTrail auditTrailXML = describableXML.getAuditTrail();
        if ( auditTrailXML != null ) {
            // create fuge object of audit trail if it is present in the xml
            Set<Audit> auditTrail = new HashSet<Audit>();

            // set fuge object by getting all audits

            // create jaxb object
            List<FuGECommonAuditAuditType> auditsXML = auditTrailXML.getAudit();
            for ( FuGECommonAuditAuditType auditXML : auditsXML ) {
                // create fuge object
                Audit audit = ( Audit ) entityService.createDescribable( "net.sourceforge.fuge.common.audit.Audit" );

                // set fuge object
                audit = ( Audit ) unmarshal( auditXML, audit, performer );
                // in addition to the standard describables, it also has date, action and contact ref, of which
                // the first two are required.

                audit.setDate( new java.sql.Timestamp( auditXML.getDate().toGregorianCalendar().getTimeInMillis() ) );
                // @todo options are hardcoded: is this really the only/best way?
                if ( auditXML.getAction().equals( "creation" ) )
                    audit.setAction( AuditAction.creation );
                if ( auditXML.getAction().equals( "deletion" ) )
                    audit.setAction( AuditAction.deletion );
                if ( auditXML.getAction().equals( "modification" ) )
                    audit.setAction( AuditAction.modification );

                if ( auditXML.getContactRef() != null )
                    audit.setPerformer( ( Contact ) entityService.getIdentifiable( auditXML.getContactRef() ) );

                // load fuge object in database
                DatabaseObjectHelper.save( "net.sourceforge.fuge.common.audit.Audit", audit, performer );

                // add to collection
                auditTrail.add( audit );
            }
            // load fuge object into describable
            describable.setAuditTrail( auditTrail );
        }

        // create jaxb object for 0 or 1 descriptions (optional), which contain 1 to many Description elements.
        FuGECommonDescribableType.Descriptions descriptionsElementXML = describableXML.getDescriptions();

        if ( descriptionsElementXML != null ) {
            // create jaxb object
            List<FuGECommonDescriptionDescriptionType> descriptionsXML = descriptionsElementXML.getDescription();

            // create fuge object
            Set<Description> descriptions = new HashSet<Description>();

            // set fuge object
            for ( FuGECommonDescriptionDescriptionType descriptionXML : descriptionsXML ) {
                Description description = ( Description ) entityService.createDescribable(
                        "net.sourceforge.fuge.common.description.Description" );
                description = ( Description ) unmarshal( descriptionXML, description, performer );
                description.setText( descriptionXML.getText() );
                DatabaseObjectHelper.save( "net.sourceforge.fuge.common.description.Description", description, performer );
                descriptions.add( description );
            }
            // load fuge object into describable
            describable.setDescriptions( descriptions );
        }

        // create jaxb object for any number of annotations (optional), which contains one required OntologyTerm_ref
        List<FuGECommonDescribableType.Annotations> annotationsXML = describableXML.getAnnotations();

        if ( !annotationsXML.isEmpty() ) {
            // create fuge object
            Set<OntologyTerm> annotations = new HashSet<OntologyTerm>();

            // set fuge object
            for ( FuGECommonDescribableType.Annotations annotationXML : annotationsXML ) {
                annotations.add( ( OntologyTerm ) entityService.getIdentifiable( annotationXML.getOntologyTermRef() ) );
            }
            // load fuge object into describable
            describable.setAnnotations( annotations );
        }

        // create jaxb object for 0 or 1 URI (optional), which contains exactly 1 URI - weird nesting in the XML
        if ( describableXML.getUri() != null ) {
            // create jaxb object
            FuGECommonDescriptionURIType uriXML = describableXML.getUri().getURI();

            // create fuge object
            Uri uri = ( Uri ) entityService.createDescribable( "net.sourceforge.fuge.common.description.Uri" );

            // the Uri itself is describable
            uri = ( Uri ) unmarshal( uriXML, uri, performer );
            uri.setLocation( uriXML.getLocation() );

            // load fuge object into database
            DatabaseObjectHelper.save( "net.sourceforge.fuge.common.description.Uri", uri, performer );

            // load fuge object into describable
            describable.setUri( uri );
        }

        // create jaxb object for 0 or 1 propertySets, which contain at least 1 NameValueType element
        FuGECommonDescribableType.PropertySets propertySetsXML = describableXML.getPropertySets();

        if ( propertySetsXML != null ) {
            // create jaxb object
            List<FuGECommonDescriptionNameValueTypeType> nameValueTypesXML = propertySetsXML.getNameValueType();

            // create fuge collection object
            Set<NameValueType> propertySets = new HashSet<NameValueType>();

            // set fuge collection object
            for ( FuGECommonDescriptionNameValueTypeType nameValueTypeXML : nameValueTypesXML ) {
                // create fuge object
                NameValueType nameValueType = ( NameValueType ) entityService.createDescribable(
                        "net.sourceforge.fuge.common.description.NameValueType" );

                // set fuge object
                nameValueType = ( NameValueType ) unmarshal( nameValueTypeXML, nameValueType, performer );
                nameValueType.setName( nameValueTypeXML.getName() );
                nameValueType.setType( nameValueTypeXML.getType() );
                nameValueType.setValue( nameValueTypeXML.getValue() );

                // load fuge object into database
                DatabaseObjectHelper.save( "net.sourceforge.fuge.common.description.NameValueType", nameValueType, performer );

                // load fuge object into collection
                propertySets.add( nameValueType );
            }

            // load fuge object into describable
            describable.setPropertySets( propertySets );
        }

        // load fuge object reference into describable
        if ( describableXML.getSecurityRef() != null ) {
            describable.setSecurity( ( Security ) entityService.getIdentifiable( describableXML.getSecurityRef() ) );
        }
        return describable;

    }

    public FuGECommonDescribableType marshal( FuGECommonDescribableType describableXML,
                                              Describable describable ) {
        // In jaxb, auditTrail is an object in its own right, while in fuge, it is simply
        // a collection of Audits. It is an optional element

        if ( describable.getAuditTrail() != null && !describable.getAuditTrail().isEmpty() ) {

            // create jaxb object
            FuGECommonDescribableType.AuditTrail auditsXML = new FuGECommonDescribableType.AuditTrail();

            for ( Audit audit : describable.getAuditTrail() ) {

                // create jaxb object
                FuGECommonAuditAuditType auditXML = new FuGECommonAuditAuditType();

                // set jaxb object
                auditXML = ( FuGECommonAuditAuditType ) marshal( auditXML, audit );

                // in addition to the standard describables, it also has date, action and contact ref, of which
                // the first two are required.
                GregorianCalendar gc = new GregorianCalendar();
                gc.setTime( audit.getDate() );
                try {
                    auditXML.setDate( DatatypeFactory.newInstance().newXMLGregorianCalendar( gc ) );
                } catch ( DatatypeConfigurationException e ) {
                    throw new EntityServiceException( "Error converting from java.sql.Timestamp to XMLGregorianCalendar", e );
                }

                // @todo options are hardcoded: is this really the only/best way?
                if ( audit.getAction() == AuditAction.creation )
                    auditXML.setAction( "creation" );
                if ( audit.getAction() == AuditAction.deletion )
                    auditXML.setAction( "deletion" );
                if ( audit.getAction() == AuditAction.modification )
                    auditXML.setAction( "modification" );
                if ( audit.getPerformer() != null )
                    auditXML.setContactRef( audit.getPerformer().getIdentifier() );

                // add to collection
                auditsXML.getAudit().add( auditXML );
            }
            // load jaxb object into describableXML
            describableXML.setAuditTrail( auditsXML );
        }

        // create fuge object for 0 or 1 descriptions (optional), which contain 1 to many Description elements.

        if ( !describable.getDescriptions().isEmpty() ) {
            // create jaxb objects
            FuGECommonDescribableType.Descriptions descriptionsXML = new FuGECommonDescribableType.Descriptions();

            // set jaxb object
            for ( Description description : describable.getDescriptions() ) {

                // create singular jaxb object
                FuGECommonDescriptionDescriptionType descriptionXML = new FuGECommonDescriptionDescriptionType();

                // set jaxb object
                descriptionXML = ( FuGECommonDescriptionDescriptionType ) marshal(
                        descriptionXML, description );
                descriptionXML.setText( description.getText() );

                // add to collection of objects
                descriptionsXML.getDescription().add( descriptionXML );
            }
            // load jaxb object into describableXML
            describableXML.setDescriptions( descriptionsXML );
        }

        // create fuge object for any number of annotations (optional), which contains one required OntologyTerm_ref
        Collection<OntologyTerm> annotations;
        if ( !( annotations = describable.getAnnotations() ).isEmpty() ) {

            // set jaxb object
            for ( OntologyTerm annotation : annotations ) {

                FuGECommonDescribableType.Annotations annotationXML = new FuGECommonDescribableType.Annotations();
                annotationXML.setOntologyTermRef( annotation.getIdentifier() );
                describableXML.getAnnotations().add( annotationXML );
            }
        }

        // create jaxb object for 0 or 1 Uri (optional), which contains exactly 1 Uri - weird nesting in the XML
        Uri uri = describable.getUri();

        if ( uri != null ) {
            // create jaxb objects
            FuGECommonDescribableType.Uri uriElementXML = new FuGECommonDescribableType.Uri();
            FuGECommonDescriptionURIType uriXML = new FuGECommonDescriptionURIType();

            // pull out describable elements
            uriXML = ( FuGECommonDescriptionURIType ) marshal( uriXML, uri );
            // set the actual location / uri
            uriXML.setLocation( uri.getLocation() );

            // load jaxb object into describableXML
            uriElementXML.setURI( uriXML );
            describableXML.setUri( uriElementXML );
        }

        // create fuge object for 0 or 1 propertySets, which contain at least 1 NameValueType element
        // create jaxb objects
        FuGECommonDescribableType.PropertySets propertySetsXML = new FuGECommonDescribableType.PropertySets();

        Collection<NameValueType> nameValueTypes;
        if ( !( nameValueTypes = describable.getPropertySets() ).isEmpty() ) {
            // set jaxb collection object
            for ( NameValueType nameValueType : nameValueTypes ) {

                // create singular jaxb object
                FuGECommonDescriptionNameValueTypeType nameValueTypeXML = new FuGECommonDescriptionNameValueTypeType();

                // set jaxb object
                nameValueTypeXML = ( FuGECommonDescriptionNameValueTypeType ) marshal(
                        nameValueTypeXML, nameValueType );
                nameValueTypeXML.setName( nameValueType.getName() );
                nameValueTypeXML.setType( nameValueType.getType() );
                nameValueTypeXML.setValue( nameValueType.getValue() );

                // load jaxb object into collection
                propertySetsXML.getNameValueType().add( nameValueTypeXML );
            }

            // load jaxb object into describable
            describableXML.setPropertySets( propertySetsXML );
        }

        // load jaxb secURIty object reference into describableXML
        if ( describable.getSecurity() != null ) {
            describableXML.setSecurityRef( describable.getSecurity().getIdentifier() );
        }
        return describableXML;
    }

    // specifically for generating random values for use in testing. Only FuGE objects will get the full
    // generated XML, as this prevents infinite recursion.
    public FuGECommonDescribableType generateRandomXML( FuGECommonDescribableType describableXML ) {

        // this ensures that if smaller objects (like DatabaseEntry) are being created, there is no unneccessary attempt
        //  to create sub-objects, and additionally there will be no infinite recursion

        // create jaxb object
        FuGECommonDescribableType.AuditTrail auditsXML = new FuGECommonDescribableType.AuditTrail();

        for ( int i = 0; i < NUMBER_ELEMENTS; i++ ) {
            // create jaxb object
            FuGECommonAuditAuditType auditXML = new FuGECommonAuditAuditType();

            // set jaxb object
            if ( describableXML instanceof FuGECollectionFuGEType )
                auditXML = ( FuGECommonAuditAuditType ) generateRandomXML( auditXML );

            // in addition to the standard describables, it also has date, action and contact ref, of which
            // the first two are required.
            try {
                auditXML.setDate( DatatypeFactory.newInstance().newXMLGregorianCalendar( new GregorianCalendar() ) );
            } catch ( DatatypeConfigurationException e ) {
                throw new EntityServiceException( "Error creating new date for random xml generation", e );
            }

            // @todo options are hardcoded: is this really the only/best way?
            auditXML.setAction( "creation" );
            if ( describableXML instanceof FuGECollectionFuGEType ) {
                FuGECollectionFuGEType fuGEType = ( FuGECollectionFuGEType ) describableXML;
                if ( fuGEType.getAuditCollection() == null ) {
                    AuditCollectionMappingHelper cac = new AuditCollectionMappingHelper();
                    fuGEType = cac.generateRandomXMLwithLinksOut( fuGEType );
                }
                auditXML.setContactRef(
                        fuGEType.getAuditCollection().getContact().get( i ).getValue().getIdentifier() );
                describableXML = fuGEType;
            }

            // add to collection
            auditsXML.getAudit().add( auditXML );
        }

        // load jaxb object into fuGEType
        describableXML.setAuditTrail( auditsXML );

        // create fuge object for 0 or 1 descriptions (optional), which contain 1 to many Description elements.

        // create jaxb objects
        FuGECommonDescribableType.Descriptions descriptionsXML = new FuGECommonDescribableType.Descriptions();

        // set jaxb object
        for ( int i = 0; i < NUMBER_ELEMENTS; i++ ) {

            // create singular jaxb object
            FuGECommonDescriptionDescriptionType descriptionXML = new FuGECommonDescriptionDescriptionType();

            // set jaxb object
            if ( describableXML instanceof FuGECollectionFuGEType )
                descriptionXML = ( FuGECommonDescriptionDescriptionType ) generateRandomXML( descriptionXML );
            descriptionXML.setText( String.valueOf( Math.random() ) );

            // add to collection of objects
            descriptionsXML.getDescription().add( descriptionXML );
        }
        // load jaxb object into fuGEType
        describableXML.setDescriptions( descriptionsXML );

        // create fuge object for any number of annotations (optional), which contains one required OntologyTerm_ref
        if ( describableXML instanceof FuGECollectionFuGEType ) {
            FuGECollectionFuGEType fuGEType = ( FuGECollectionFuGEType ) describableXML;
            for ( int i = 0; i < NUMBER_ELEMENTS; i++ ) {
                FuGECommonDescribableType.Annotations annotationXML = new FuGECommonDescribableType.Annotations();
                if ( fuGEType.getOntologyCollection() == null ) {
                    OntologyCollectionMappingHelper coc = new OntologyCollectionMappingHelper();
                    fuGEType.setOntologyCollection( coc.generateRandomXML( new FuGECollectionOntologyCollectionType() ) );
                }
                annotationXML.setOntologyTermRef(
                        fuGEType.getOntologyCollection().getOntologyTerm().get( i ).getValue().getIdentifier() );
                fuGEType.getAnnotations().add( annotationXML );
                describableXML = fuGEType;
            }
        }

        FuGECommonDescribableType.Uri uriElementXML = new FuGECommonDescribableType.Uri();
        FuGECommonDescriptionURIType uriXML = new FuGECommonDescriptionURIType();

        // add describable information to the Uri only if it is a fuge object to prevent lots of recursion
        // (URIs have URIs have URIs...)
        if ( describableXML instanceof FuGECollectionFuGEType )
            uriXML = ( FuGECommonDescriptionURIType ) generateRandomXML( uriXML );
        uriXML.setLocation( "http://some.random.url/" + String.valueOf( Math.random() ) );

        // load jaxb object into fuGEType
        uriElementXML.setURI( uriXML );
        describableXML.setUri( uriElementXML );

        // create fuge object for 0 or 1 propertySets, which contain at least 1 NameValueType element
        // create jaxb objects
        FuGECommonDescribableType.PropertySets propertySetsXML = new FuGECommonDescribableType.PropertySets();

        for ( int i = 0; i < NUMBER_ELEMENTS; i++ ) {
            // create singular jaxb object
            FuGECommonDescriptionNameValueTypeType nameValueTypeXML = new FuGECommonDescriptionNameValueTypeType();

            // set jaxb object
            if ( describableXML instanceof FuGECollectionFuGEType )
                nameValueTypeXML = ( FuGECommonDescriptionNameValueTypeType ) generateRandomXML( nameValueTypeXML );
            nameValueTypeXML.setName( String.valueOf( Math.random() ) );
            nameValueTypeXML.setType( String.valueOf( Math.random() ) );
            nameValueTypeXML.setValue( String.valueOf( Math.random() ) );

            // load jaxb object into collection
            propertySetsXML.getNameValueType().add( nameValueTypeXML );
        }

        // load jaxb object into describable
        describableXML.setPropertySets( propertySetsXML );

        // load jaxb secURIty object reference into fuGEType
        if ( describableXML instanceof FuGECollectionFuGEType ) {
            FuGECollectionFuGEType fuGEType = ( FuGECollectionFuGEType ) describableXML;
            if ( fuGEType.getAuditCollection() == null ) {
                AuditCollectionMappingHelper cac = new AuditCollectionMappingHelper();
                fuGEType = cac.generateRandomXMLwithLinksOut( fuGEType );
            }
            fuGEType.setSecurityRef( fuGEType.getAuditCollection().getSecurity().get( 0 ).getIdentifier() );
            describableXML = fuGEType;
        }
        return describableXML;
    }
}
