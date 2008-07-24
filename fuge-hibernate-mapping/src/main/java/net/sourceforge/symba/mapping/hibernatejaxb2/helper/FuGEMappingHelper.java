package net.sourceforge.symba.mapping.hibernatejaxb2.helper;


import net.sourceforge.fuge.bio.investigation.Investigation;
import net.sourceforge.fuge.collection.*;
import net.sourceforge.fuge.common.description.Description;
import net.sourceforge.fuge.common.audit.Person;
import net.sourceforge.fuge.service.EntityServiceException;
import net.sourceforge.fuge.util.generatedJAXB2.*;
import net.sourceforge.symba.mapping.hibernatejaxb2.DatabaseObjectHelper;

import java.io.PrintWriter;
import java.util.Collection;

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
public class FuGEMappingHelper implements MappingHelper<FuGE, FuGECollectionFuGEType> {
    private final IdentifiableMappingHelper ci;
    private final AuditCollectionMappingHelper cac;
    private final OntologyCollectionMappingHelper coc;
    private final ReferenceableCollectionMappingHelper crc;
    private final MaterialCollectionMappingHelper cmc;
    private final ProviderMappingHelper cpr;
    private final DataCollectionMappingHelper cdc;
    private final ProtocolCollectionMappingHelper cpc;
    private final InvestigationCollectionMappingHelper cinv;

    public FuGEMappingHelper() {
        this.ci = new IdentifiableMappingHelper();
        this.cac = new AuditCollectionMappingHelper();
        this.coc = new OntologyCollectionMappingHelper();
        this.crc = new ReferenceableCollectionMappingHelper();
        this.cmc = new MaterialCollectionMappingHelper();
        this.cpr = new ProviderMappingHelper();
        this.cdc = new DataCollectionMappingHelper();
        this.cpc = new ProtocolCollectionMappingHelper();
        this.cinv = new InvestigationCollectionMappingHelper();
    }

    public FuGE unmarshal( FuGECollectionFuGEType frXML,
                           FuGE fr, Person performer ) throws EntityServiceException {

        // Must be done first: AuditCollection, OntologyCollection. After that must come ReferenceableCollection.

        // get and store in the database all AuditCollection information
        // unmarshall the jaxb object into a fuge object, then set the fuge object within the top level fuge root object
        fr.setAuditCollection( cac.unmarshal( frXML.getAuditCollection(), ( AuditCollection ) entityService.createDescribable(
                "net.sourceforge.fuge.collection.AuditCollection" ), performer ) );

        // get and store in the database all OntologyCollection information
        // unmarshall the jaxb object into a fuge object, then set the fuge object within the top level fuge root object
        fr.setOntologyCollection( coc.unmarshal( frXML.getOntologyCollection(), ( OntologyCollection ) entityService.createDescribable(
                "net.sourceforge.fuge.collection.OntologyCollection" ), performer ) );

        // get and store in the database all ReferenceableCollection information
        if ( frXML.getReferenceableCollection() != null ) {
            // unmarshall the jaxb object into a fuge object, then set the fuge object within the top level fuge root object
            fr.setReferenceableCollection( crc.unmarshal( frXML.getReferenceableCollection(),
                    ( ReferenceableCollection ) entityService.createDescribable( "net.sourceforge.fuge.collection.ReferenceableCollection" ), performer ) );
        }

        // set all identifiable traits in the fuge object
        fr = ( FuGE ) ci.unmarshal( frXML, fr, performer );

        // Get all MaterialCollection information
        if ( frXML.getMaterialCollection() != null ) {
            // unmarshall the jaxb object into a fuge object, then set the fuge object within the top level fuge root object
            fr.setMaterialCollection( cmc.unmarshal( frXML.getMaterialCollection(),
                    ( MaterialCollection ) entityService.createDescribable( "net.sourceforge.fuge.collection.MaterialCollection" ), performer ) );
        }

        // Get all DataCollection information
        if ( frXML.getDataCollection() != null ) {
            // unmarshall the jaxb object into a fuge object, then set the fuge object within the top level fuge root object
            fr.setDataCollection( cdc.unmarshal( frXML.getDataCollection(),
                    ( DataCollection ) entityService.createDescribable( "net.sourceforge.fuge.collection.DataCollection" ), performer ) );
        }

        // Get all ProtocolCollection information
        if ( frXML.getProtocolCollection() != null ) {
            // unmarshall the jaxb object into a fuge object, then set the fuge object within the top level fuge root object
            fr.setProtocolCollection( cpc.unmarshal( frXML.getProtocolCollection(),
                    ( ProtocolCollection ) entityService.createDescribable( "net.sourceforge.fuge.collection.ProtocolCollection" ), performer ) );
        }

        // Get a Provider, if present
        if ( frXML.getProvider() != null ) {
            // unmarshall the jaxb object into a fuge object, then set the fuge object within the top level fuge root object
            Provider provider = cpr.unmarshal( frXML.getProvider(), ( Provider ) DatabaseObjectHelper.getOrCreate(
                    frXML.getProvider().getIdentifier(),
                    frXML.getProvider().getName(),
                    "net.sourceforge.fuge.collection.Provider" ), performer );
            DatabaseObjectHelper.save( "net.sourceforge.fuge.collection.Provider", provider, performer );
            fr.setProvider( provider );

        }

        // Get an Investigation, if present
        if ( frXML.getInvestigationCollection() != null ) {
            // unmarshall the jaxb object into a fuge object, then set the fuge object within the top level fuge root object
            fr.setInvestigationCollection(
                    cinv.unmarshal( frXML.getInvestigationCollection(),
                            ( InvestigationCollection ) entityService.createDescribable( "net.sourceforge.fuge.collection.InvestigationCollection" ), performer ) );
        }

        return fr;
    }

    public FuGECollectionFuGEType marshal( FuGECollectionFuGEType frXML,
                                           FuGE fr ) throws EntityServiceException {
        // get all identifiable traits from the fuge object
        frXML = ( FuGECollectionFuGEType ) ci.marshal( frXML, fr );

        // get from the database all AuditCollection information
        if ( fr.getAuditCollection() != null ) {
            // marshall the fuge object into a jaxb object
            frXML.setAuditCollection( cac.marshal( new FuGECollectionAuditCollectionType(), fr.getAuditCollection() ) );
        }

        // get from the database all OntologyCollection information
        if ( fr.getOntologyCollection() != null ) {
            // marshall the fuge object into a jaxb object
            frXML.setOntologyCollection( coc.marshal( new FuGECollectionOntologyCollectionType(), fr.getOntologyCollection() ) );
        }

        // get from the database all ReferenceableCollection information
        if ( fr.getReferenceableCollection() != null ) {
            // marshall the fuge object into a jaxb object
            frXML.setReferenceableCollection( crc.marshal( new FuGECollectionReferenceableCollectionType(), fr.getReferenceableCollection() ) );
        }

        // Get all MaterialCollection information
        if ( fr.getMaterialCollection() != null ) {
            // marshall the fuge object into a jaxb object
            frXML.setMaterialCollection( cmc.marshal( new FuGECollectionMaterialCollectionType(), fr.getMaterialCollection() ) );
        }

        // Get all Provider information
        if ( fr.getProvider() != null ) {
            // marshall the fuge object into a jaxb object
            frXML.setProvider( cpr.marshal( new FuGECollectionProviderType(), fr.getProvider() ) );
        }

        // Get all data collection information
        if ( fr.getDataCollection() != null ) {
            // marshall the fuge object into a jaxb object
            frXML.setDataCollection( cdc.marshal( new FuGECollectionDataCollectionType(), fr.getDataCollection() ) );
        }

        // Get all protocol collection information
        if ( fr.getProtocolCollection() != null ) {
            // marshall the fuge object into a jaxb object
            frXML.setProtocolCollection( cpc.marshal( new FuGECollectionProtocolCollectionType(), fr.getProtocolCollection() ) );
        }
        // Get an Investigation, if present
        if ( fr.getInvestigationCollection() != null ) {
            // unmarshall the jaxb object into a fuge object, then set the fuge object within the top level fuge root object
            frXML.setInvestigationCollection( cinv.marshal( new FuGECollectionInvestigationCollectionType(), fr.getInvestigationCollection() ) );
        }
        return frXML;
    }

    public FuGECollectionFuGEType generateRandomXML() {
        return generateRandomXML( new FuGECollectionFuGEType() );
    }

    public FuGECollectionFuGEType generateRandomXML( FuGECollectionFuGEType frXML ) {

        // generate identifiable traits
        frXML = ( FuGECollectionFuGEType ) ci.generateRandomXML( frXML );

        // generate AuditCollection information
        if ( frXML.getAuditCollection() == null ) {
            frXML = cac.generateRandomXMLwithLinksOut( frXML );
        }

        // generate OntologyCollection information
        if ( frXML.getOntologyCollection() == null ) {
            frXML.setOntologyCollection( coc.generateRandomXML( new FuGECollectionOntologyCollectionType() ) );
        }

        // generate ReferenceableCollection information
        if ( frXML.getReferenceableCollection() == null ) {
            frXML = crc.generateRandomXMLwithLinksOut( frXML );
        }

        // Get all MaterialCollection information
        if ( frXML.getMaterialCollection() == null ) {
            frXML = cmc.generateRandomXMLWithLinksOut( frXML );
        }

        // Get all data collection information - MUST BE DONE before Protocol and after Material
        if ( frXML.getDataCollection() == null ) {
            frXML = cdc.generateRandomXMLWithLinksOut( frXML );
        }

        // Get all protocol collection information
        if ( frXML.getProtocolCollection() == null ) {
            // marshall the fuge object into a jaxb object
            frXML = cpc.generateRandomXML( frXML );
        }

        // Get all Provider information
        if ( frXML.getProvider() == null ) {
            // marshall the fuge object into a jaxb object
            frXML = cpr.generateRandomXMLWithLinksOut( frXML );
        }

        // Get an Investigation, if present
        if ( frXML.getInvestigationCollection() == null ) {
            // unmarshall the jaxb object into a fuge object, then set the fuge object within the top level fuge root object
            frXML = cinv.generateRandomXMLWithLinksOut( frXML );
        }

        return frXML;
    }

    public void prettyHtml( FuGE fuge, PrintWriter printStream ) throws EntityServiceException {

        printStream.println( "<h3>" );
        ci.prettyHtml( "Experiment Name: ", fuge, printStream );
        printStream.println( "</h3>" );

        // Now print out the initial creator of the FuGE object
        if ( fuge.getProvider().getProvider().getContact().getName() != null &&
                fuge.getProvider().getProvider().getContact().getName().length() > 0 ) {
            String provider;
            provider = fuge.getProvider().getProvider().getContact().getName();

            printStream.println( "<h4>" );
            printStream.println( "Provider of the Experiment: " + provider );
            printStream.println( "</h4>" );
        }


        if ( fuge.getInvestigationCollection() != null ) {
            Collection<Investigation> collection = fuge.getInvestigationCollection().getInvestigations();
            if ( !collection.isEmpty() ) {
                for ( Investigation inv : collection ) {
                    for ( Description de : inv.getDescriptions() ) {
                        printStream.println( "<p>" );
                        printStream.println( de.getText() );
                        printStream.println( "</p>" );
                    }
                }
            }
        }
        printStream.println( "<table border=\"1\">" );
        cpc.prettyHtml( fuge.getProtocolCollection(), printStream );
        printStream.println( "</table>" );
        printStream.flush();
    }
}
