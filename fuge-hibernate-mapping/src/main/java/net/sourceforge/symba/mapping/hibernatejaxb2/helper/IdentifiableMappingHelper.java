package net.sourceforge.symba.mapping.hibernatejaxb2.helper;

import net.sourceforge.fuge.common.Identifiable;
import net.sourceforge.fuge.common.audit.Person;
import net.sourceforge.fuge.common.references.BibliographicReference;
import net.sourceforge.fuge.common.references.Database;
import net.sourceforge.fuge.common.references.DatabaseReference;
import net.sourceforge.fuge.service.EntityServiceException;
import net.sourceforge.fuge.util.generatedJAXB2.FuGECommonIdentifiableType;
import net.sourceforge.fuge.util.generatedJAXB2.FuGECommonReferencesDatabaseReferenceType;
import net.sourceforge.symba.mapping.hibernatejaxb2.DatabaseObjectHelper;

import java.io.PrintStream;
import java.io.PrintWriter;
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
public class IdentifiableMappingHelper implements MappingHelper<Identifiable, FuGECommonIdentifiableType> {
    private final DescribableMappingHelper cd;

    public IdentifiableMappingHelper() {
        this.cd = new DescribableMappingHelper();
    }

    public DescribableMappingHelper getCisbanDescribableHelper() {
        return cd;
    }

    public Identifiable unmarshal( FuGECommonIdentifiableType identifiableXML, Identifiable identifiable, Person performer )
            throws EntityServiceException {

        identifiable = ( Identifiable ) cd.unmarshal( identifiableXML, identifiable, performer );
        identifiable.setIdentifier( identifiableXML.getIdentifier() );
        if ( identifiableXML.getName() != null )
            identifiable.setName( identifiableXML.getName() );

        Set<DatabaseReference> databaseEntries = new HashSet<DatabaseReference>();
        for ( FuGECommonReferencesDatabaseReferenceType DatabaseReferenceXML : identifiableXML.getDatabaseReference() ) {
            DatabaseReference DatabaseReference = ( DatabaseReference ) entityService
                    .createDescribable( "net.sourceforge.fuge.common.references.DatabaseReference" );
            DatabaseReference = ( DatabaseReference ) cd.unmarshal( DatabaseReferenceXML, DatabaseReference, performer );
            if ( DatabaseReferenceXML.getAccession() != null )
                DatabaseReference.setAccession( DatabaseReferenceXML.getAccession() );
            if ( DatabaseReferenceXML.getAccessionVersion() != null )
                DatabaseReference.setAccessionVersion( DatabaseReferenceXML.getAccessionVersion() );
            if ( DatabaseReferenceXML.getDatabaseRef() != null ) {
                DatabaseReference
                        .setDatabase( ( Database ) entityService.getIdentifiable( DatabaseReferenceXML.getDatabaseRef() ) );
            }
            DatabaseObjectHelper.save( "net.sourceforge.fuge.common.references.DatabaseReference", DatabaseReference, performer );
            databaseEntries.add( DatabaseReference );
        }

        identifiable.setDatabaseReferences( databaseEntries );

        Set<BibliographicReference> brRefs = new HashSet<BibliographicReference>();
        for ( FuGECommonIdentifiableType.BibliographicReferences brRefXML : identifiableXML
                .getBibliographicReferences() ) {
            brRefs.add(
                    ( BibliographicReference ) entityService.getIdentifiable( brRefXML.getBibliographicReferenceRef() ) );
        }

        identifiable.setBibliographicReferences( brRefs );

        return identifiable;
    }

    public FuGECommonIdentifiableType marshal( FuGECommonIdentifiableType identifiableXML,
                                                 Identifiable identifiable ) {

        identifiableXML = ( FuGECommonIdentifiableType ) cd.marshal( identifiableXML, identifiable );
        identifiableXML.setIdentifier( identifiable.getIdentifier() );

        if ( identifiable.getName() != null )
            identifiableXML.setName( identifiable.getName() );

        for ( DatabaseReference DatabaseReference : identifiable.getDatabaseReferences() ) {
            FuGECommonReferencesDatabaseReferenceType DatabaseReferenceXML = new FuGECommonReferencesDatabaseReferenceType();
            DatabaseReferenceXML = ( FuGECommonReferencesDatabaseReferenceType ) cd
                    .marshal( DatabaseReferenceXML, DatabaseReference );
            if ( DatabaseReference.getAccession() != null )
                DatabaseReferenceXML.setAccession( DatabaseReference.getAccession() );
            if ( DatabaseReference.getAccessionVersion() != null )
                DatabaseReferenceXML.setAccessionVersion( DatabaseReference.getAccessionVersion() );
            if ( DatabaseReference.getDatabase() != null )
                DatabaseReferenceXML.setDatabaseRef( DatabaseReference.getDatabase().getIdentifier() );
            identifiableXML.getDatabaseReference().add( DatabaseReferenceXML );
        }

        for ( BibliographicReference brRef : identifiable.getBibliographicReferences() ) {
            FuGECommonIdentifiableType.BibliographicReferences brRefXML = new FuGECommonIdentifiableType.BibliographicReferences();
            brRefXML.setBibliographicReferenceRef( brRef.getIdentifier() );
            identifiableXML.getBibliographicReferences().add( brRefXML );
        }
        return identifiableXML;
    }

    public void prettyPrint( String comment, Identifiable identifiable, PrintStream printStream ) {
        if ( identifiable.getName() != null ) {
            printStream.println( comment + identifiable.getName() + " (name)" );
        }
    }

    public void prettyHtml( String comment, Identifiable identifiable, PrintWriter printWriter ) {
        if ( comment != null ) {
            printWriter.print( comment );
        }
        // print the name of the object, if present
        if ( identifiable.getName() != null ) {
            printWriter.println( identifiable.getName() );
        }
        printWriter.flush();
    }
}