package net.sourceforge.symba.mapping.hibernatejaxb2.helper;


import net.sourceforge.fuge.collection.ReferenceableCollection;
import net.sourceforge.fuge.common.audit.ContactRole;
import net.sourceforge.fuge.common.audit.Person;
import net.sourceforge.fuge.common.references.BibliographicReference;
import net.sourceforge.fuge.common.references.Database;
import net.sourceforge.fuge.service.EntityServiceException;
import net.sourceforge.fuge.util.generatedJAXB2.*;
import net.sourceforge.symba.mapping.hibernatejaxb2.DatabaseObjectHelper;

import java.util.HashSet;
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
public class ReferenceableCollectionMappingHelper implements MappingHelper<ReferenceableCollection, FuGECollectionReferenceableCollectionType> {
    private final int NUMBER_ELEMENTS = 2;
    private final DescribableMappingHelper cd;
    private final IdentifiableMappingHelper ci;
    private final ContactRoleMappingHelper ccr;

    public ReferenceableCollectionMappingHelper() {
        this.ci = new IdentifiableMappingHelper();
        this.cd = ci.getCisbanDescribableHelper();
        this.ccr = new ContactRoleMappingHelper();
    }

    public ReferenceableCollection unmarshal(
            FuGECollectionReferenceableCollectionType refCollXML, ReferenceableCollection refColl, Person performer )
            throws EntityServiceException {

        refColl = ( ReferenceableCollection ) cd.unmarshal( refCollXML, refColl, performer );

        Set<BibliographicReference> bibRefs = new HashSet<BibliographicReference>();
        for ( FuGECommonReferencesBibliographicReferenceType bibRefXML : refCollXML.getBibliographicReference() ) {

            // Retrieve from the database or create a new local instance.
            BibliographicReference bibRef = ( BibliographicReference ) DatabaseObjectHelper.getOrCreate(
                    bibRefXML.getIdentifier(),
                    bibRefXML.getName(),
                    "net.sourceforge.fuge.common.references.BibliographicReference" );

            bibRef = ( BibliographicReference ) ci.unmarshal( bibRefXML, bibRef, performer );

            // set the non-identifiable traits
            if ( bibRefXML.getAuthors() != null )
                bibRef.setAuthors( bibRefXML.getAuthors() );
            if ( bibRefXML.getEditor() != null )
                bibRef.setEditor( bibRefXML.getEditor() );
            if ( bibRefXML.getIssue() != null )
                bibRef.setIssue( bibRefXML.getIssue() );
            if ( bibRefXML.getPages() != null )
                bibRef.setPages( bibRefXML.getPages() );
            if ( bibRefXML.getPublication() != null )
                bibRef.setPublication( bibRefXML.getPublication() );
            if ( bibRefXML.getPublisher() != null )
                bibRef.setPublisher( bibRefXML.getPublisher() );
            if ( bibRefXML.getTitle() != null )
                bibRef.setTitle( bibRefXML.getTitle() );
            if ( bibRefXML.getVolume() != null )
                bibRef.setVolume( bibRefXML.getVolume() );
            if ( bibRefXML.getYear() != null )
                bibRef.setYear( bibRefXML.getYear() );

            DatabaseObjectHelper.save( "net.sourceforge.fuge.common.references.BibliographicReference", bibRef, performer );
            bibRefs.add( bibRef );
        }

        refColl.setAllBibliographicReferences( bibRefs );

        Set<Database> dbs = new HashSet<Database>();
        for ( FuGECommonReferencesDatabaseType dbXML : refCollXML.getDatabase() ) {

            // Retrieve from the database or create a new local instance.
            Database db = ( Database ) DatabaseObjectHelper.getOrCreate(
                    dbXML.getIdentifier(),
                    dbXML.getName(),
                    "net.sourceforge.fuge.common.references.Database" );

            db = ( Database ) ci.unmarshal( dbXML, db, performer );

            // set non-identifiable traits
            if ( dbXML.getURI() != null ) {
                db.setURI( dbXML.getURI() );
            }
            if ( dbXML.getVersion() != null ) {
                db.setVersion( dbXML.getVersion() );
            }

            Set<ContactRole> contactRoles = new HashSet<ContactRole>();
            for ( FuGECommonAuditContactRoleType contactRoleXML : dbXML.getContactRole() ) {
                ContactRole cr = ccr.unmarshal( contactRoleXML, ( ContactRole ) entityService.createDescribable( "net.sourceforge.fuge.common.audit.ContactRole" ), performer );
                cr = ( ContactRole ) DatabaseObjectHelper.save( "net.sourceforge.fuge.common.audit.ContactRole", cr, performer );
                contactRoles.add( cr );
            }

            db.setDatabaseContact( contactRoles );
            DatabaseObjectHelper.save( "net.sourceforge.fuge.common.references.Database", db, performer );
            dbs.add( db );
        }

        refColl.setAllDatabases( dbs );

        DatabaseObjectHelper.save( "net.sourceforge.fuge.collection.ReferenceableCollection", refColl, performer );

        return refColl;
    }

    public FuGECollectionReferenceableCollectionType marshal( FuGECollectionReferenceableCollectionType refCollXML,
                                                              ReferenceableCollection refColl )
            throws EntityServiceException {


        refCollXML = ( FuGECollectionReferenceableCollectionType ) cd.marshal( refCollXML, refColl );

        for ( BibliographicReference bibRef : refColl.getAllBibliographicReferences() ) {

            FuGECommonReferencesBibliographicReferenceType bibRefXML = new FuGECommonReferencesBibliographicReferenceType();
            bibRefXML = ( FuGECommonReferencesBibliographicReferenceType ) ci.marshal(
                    bibRefXML, bibRef );

            if ( bibRef.getAuthors() != null )
                bibRefXML.setAuthors( bibRef.getAuthors() );
            if ( bibRef.getEditor() != null )
                bibRefXML.setEditor( bibRef.getEditor() );
            if ( bibRef.getIssue() != null )
                bibRefXML.setIssue( bibRef.getIssue() );
            if ( bibRef.getPages() != null )
                bibRefXML.setPages( bibRef.getPages() );
            if ( bibRef.getPublication() != null )
                bibRefXML.setPublication( bibRef.getPublication() );
            if ( bibRef.getPublisher() != null )
                bibRefXML.setPublisher( bibRef.getPublisher() );
            if ( bibRef.getTitle() != null )
                bibRefXML.setTitle( bibRef.getTitle() );
            if ( bibRef.getVolume() != null )
                bibRefXML.setVolume( bibRef.getVolume() );
            if ( bibRef.getYear() != 0 )
                bibRefXML.setYear( bibRef.getYear() ); // int will not be null, though it may be zero. Assume we won't print if it's zero.

            refCollXML.getBibliographicReference().add( bibRefXML );
        }

        for ( Database db : refColl.getAllDatabases() ) {

            FuGECommonReferencesDatabaseType dbXML = new FuGECommonReferencesDatabaseType();

            dbXML = ( FuGECommonReferencesDatabaseType ) ci.marshal( dbXML, db );
            if ( db.getURI() != null ) {
                dbXML.setURI( db.getURI() );
            }
            if ( db.getVersion() != null ) {
                dbXML.setVersion( db.getVersion() );
            }

            for ( ContactRole contactRole : db.getDatabaseContact() ) {
                dbXML.getContactRole().add( ccr.marshal( new FuGECommonAuditContactRoleType(), contactRole ) );
            }
            refCollXML.getDatabase().add( dbXML );
        }

        return refCollXML;
    }

    public FuGECollectionReferenceableCollectionType generateRandomXML( FuGECollectionReferenceableCollectionType refCollXML ) {

        refCollXML = ( FuGECollectionReferenceableCollectionType ) cd.generateRandomXML( refCollXML );

        for ( int i = 0; i < NUMBER_ELEMENTS; i++ ) {
            FuGECommonReferencesBibliographicReferenceType bibRefXML = new FuGECommonReferencesBibliographicReferenceType();
            bibRefXML = ( FuGECommonReferencesBibliographicReferenceType ) ci.generateRandomXML( bibRefXML );

            bibRefXML.setAuthors( String.valueOf( Math.random() ) );
            bibRefXML.setEditor( String.valueOf( Math.random() ) );
            bibRefXML.setIssue( String.valueOf( Math.random() ) );
            bibRefXML.setPages( String.valueOf( Math.random() ) );
            bibRefXML.setPublication( String.valueOf( Math.random() ) );
            bibRefXML.setPublisher( String.valueOf( Math.random() ) );
            bibRefXML.setTitle( String.valueOf( Math.random() ) );
            bibRefXML.setVolume( String.valueOf( Math.random() ) );
            bibRefXML.setYear(
                    ( int ) ( Math.random() *
                            10 ) ); // int will not be null, though it may be zero. Assume we won't print if it's zero.

            refCollXML.getBibliographicReference().add( bibRefXML );
        }

        return refCollXML;
    }

    public FuGECollectionFuGEType generateRandomXMLwithLinksOut( FuGECollectionFuGEType fuGEType ) {

        AuditCollectionMappingHelper cac = new AuditCollectionMappingHelper();
        OntologyCollectionMappingHelper coc = new OntologyCollectionMappingHelper();

        FuGECollectionReferenceableCollectionType refCollXML = generateRandomXML( new FuGECollectionReferenceableCollectionType() );

        for ( int i = 0; i < NUMBER_ELEMENTS; i++ ) {
            FuGECommonReferencesDatabaseType dbXML = new FuGECommonReferencesDatabaseType();

            dbXML = ( FuGECommonReferencesDatabaseType ) ci.generateRandomXML( dbXML );
            dbXML.setURI( String.valueOf( Math.random() ) );
            dbXML.setVersion( String.valueOf( Math.random() ) );
            for ( int ii = 0; ii < NUMBER_ELEMENTS; ii++ ) {
                if ( fuGEType.getAuditCollection() == null ) {
                    fuGEType = cac.generateRandomXMLwithLinksOut( fuGEType );
                }
                if ( fuGEType.getOntologyCollection() == null ) {
                    fuGEType.setOntologyCollection( coc.generateRandomXML( new FuGECollectionOntologyCollectionType() ) );
                }
                dbXML.getContactRole().add( ccr.generateRandomXMLwithLinksOut( fuGEType ) );
            }
            refCollXML.getDatabase().add( dbXML );
        }
        fuGEType.setReferenceableCollection( refCollXML );

        return fuGEType;
    }
}
