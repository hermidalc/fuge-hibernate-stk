package net.sourceforge.symba.mapping.hibernatejaxb2.helper;


import net.sourceforge.fuge.collection.FuGE;
import net.sourceforge.fuge.collection.ProtocolCollection;
import net.sourceforge.fuge.common.protocol.*;
import net.sourceforge.fuge.common.audit.Person;
import net.sourceforge.fuge.service.EntityServiceException;
import net.sourceforge.fuge.util.generatedJAXB2.*;
import net.sourceforge.symba.mapping.hibernatejaxb2.DatabaseObjectHelper;

import javax.xml.bind.JAXBElement;
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
public class ProtocolCollectionMappingHelper implements MappingHelper<ProtocolCollection, FuGECollectionProtocolCollectionType> {
    private final int NUMBER_ELEMENTS = 2;
    private final DescribableMappingHelper cd;
    private final ProtocolMappingHelper cpr;
    private final EquipmentMappingHelper ceq;
    private final SoftwareMappingHelper csw;
    private final ProtocolApplicationMappingHelper capp;

    public ProtocolCollectionMappingHelper() {
        this.cd = new DescribableMappingHelper();
        this.cpr = new ProtocolMappingHelper();
        this.ceq = new EquipmentMappingHelper();
        this.csw = new SoftwareMappingHelper();
        this.capp = new ProtocolApplicationMappingHelper();

    }

    public ProtocolCollection unmarshal(
            FuGECollectionProtocolCollectionType protocolCollectionXML, ProtocolCollection protocolCollection, Person performer )
            throws EntityServiceException {

        // set describable information
        protocolCollection = ( ProtocolCollection ) cd
                .unmarshal( protocolCollectionXML, protocolCollection, performer );
        protocolCollection = unmarshalCollectionContents( protocolCollectionXML, protocolCollection, performer );

        // load the fuge object into the database
        DatabaseObjectHelper.save( "net.sourceforge.fuge.collection.ProtocolCollection", protocolCollection, performer );

        return protocolCollection;
    }

    public ProtocolCollection unmarshalCollectionContents( FuGECollectionProtocolCollectionType protocolCollectionXML,
                                                           ProtocolCollection protocolCollection, Person performer )
            throws EntityServiceException {
        // There are references to equipment and software within Protocol, so those should be loaded first

        // equipment
        Set<Equipment> equipments = new HashSet<Equipment>();
        for ( JAXBElement<? extends FuGECommonProtocolEquipmentType> elementXML : protocolCollectionXML
                .getEquipment() ) {
            if ( elementXML.getValue() instanceof FuGECommonProtocolGenericEquipmentType ) {
                // set fuge object
                GenericEquipment genericEquipment = ( GenericEquipment ) ceq.unmarshal( elementXML.getValue(),
                        ( GenericEquipment ) DatabaseObjectHelper.getOrCreate(
                                elementXML.getValue().getIdentifier(),
                                elementXML.getValue().getName(),
                                "net.sourceforge.fuge.common.protocol.GenericEquipment" ), performer );
                DatabaseObjectHelper.save( "net.sourceforge.fuge.common.protocol.GenericEquipment", genericEquipment, performer );
                equipments.add( genericEquipment );
            }
        }
        protocolCollection.setAllEquipment( equipments );

        // software: there are references to equipment within software
        Set<Software> softwares = new HashSet<Software>();
        for ( JAXBElement<? extends FuGECommonProtocolSoftwareType> elementXML : protocolCollectionXML
                .getSoftware() ) {
            if ( elementXML.getValue() instanceof FuGECommonProtocolGenericSoftwareType ) {
                FuGECommonProtocolSoftwareType protocolSoftwareXML = elementXML.getValue();
                GenericSoftware gs = ( GenericSoftware ) csw.unmarshal( protocolSoftwareXML, ( GenericSoftware ) DatabaseObjectHelper.getOrCreate(
                        protocolSoftwareXML.getIdentifier(),
                        protocolSoftwareXML.getName(),
                        "net.sourceforge.fuge.common.protocol.GenericSoftware" ), performer );
                DatabaseObjectHelper.save( "net.sourceforge.fuge.common.protocol.GenericSoftware", gs, performer );
                softwares.add( gs );
            }
        }
        protocolCollection.setAllSoftwares( softwares );

        // protocol
        Set<Protocol> protocols = new HashSet<Protocol>();
        for ( JAXBElement<? extends FuGECommonProtocolProtocolType> protocolElementXML : protocolCollectionXML
                .getProtocol() ) {
            FuGECommonProtocolProtocolType protocolXML = protocolElementXML.getValue();
            if ( protocolXML instanceof FuGECommonProtocolGenericProtocolType ) {
                GenericProtocol genericProtocol = ( GenericProtocol ) cpr.unmarshal( protocolXML, ( Protocol ) DatabaseObjectHelper.getOrCreate(
                        protocolXML.getIdentifier(),
                        protocolXML.getName(),
                        "net.sourceforge.fuge.common.protocol.GenericProtocol" ), performer );
                DatabaseObjectHelper.save( "net.sourceforge.fuge.common.protocol.GenericProtocol", genericProtocol, performer );
                protocols.add( genericProtocol );
            }
        }
        protocolCollection.setProtocols( protocols );

        // protocol application
        Set<ProtocolApplication> protocolApplications = new HashSet<ProtocolApplication>();
        for ( JAXBElement<? extends FuGECommonProtocolProtocolApplicationType> elementXML : protocolCollectionXML
                .getProtocolApplication() ) {
            FuGECommonProtocolProtocolApplicationType protocolApplicationXML = elementXML.getValue();
            GenericProtocolApplication gpa = ( GenericProtocolApplication ) capp.unmarshal( protocolApplicationXML,
                    ( GenericProtocolApplication ) DatabaseObjectHelper.getOrCreate(
                            protocolApplicationXML.getIdentifier(),
                            protocolApplicationXML.getName(),
                            "net.sourceforge.fuge.common.protocol.GenericProtocolApplication" ), performer );
            DatabaseObjectHelper.save( "net.sourceforge.fuge.common.protocol.GenericProtocolApplication", gpa, performer );
            protocolApplications.add( gpa );
        }
        protocolCollection.setProtocolApplications( protocolApplications );

        return protocolCollection;
    }

    public FuGECollectionProtocolCollectionType marshal(
            FuGECollectionProtocolCollectionType protocolCollectionXML, ProtocolCollection protocolCollection )
            throws EntityServiceException {

        protocolCollectionXML = ( FuGECollectionProtocolCollectionType ) cd
                .marshal( protocolCollectionXML, protocolCollection );

        // equipment
        for ( Equipment equipment : protocolCollection.getAllEquipment() ) {
            FuGECommonProtocolEquipmentType equipmentXML = ceq.marshal( new FuGECommonProtocolGenericEquipmentType(), equipment );
            if ( equipment instanceof GenericEquipment ) {
                JAXBElement<? extends FuGECommonProtocolGenericEquipmentType> element = ( new ObjectFactory() )
                        .createGenericEquipment( ( FuGECommonProtocolGenericEquipmentType ) equipmentXML );
                protocolCollectionXML.getEquipment().add( element );
            }
        }

        // software
        for ( Software software : protocolCollection.getAllSoftwares() ) {
            // todo: there is an error in the way the "Softwares" and Protocols are retrieved from a ProtocolCollection. Retrieval from the DB again is the only way I have found so far to sort it out.
            // Otherwise, you'll find that the software variable will be of type,
            // e.g. net.sourceforge.fuge.common.protocol.SoftwareImpl$$EnhancerByCGLIB$$86ae582c
            // rather than
            //  net.sourceforge.fuge.common.protocol.GenericSoftwareImpl
            // and the "(software instanceof GenericSoftware) will be false.
            software = ( Software ) entityService.getIdentifiable( software.getId() );

            FuGECommonProtocolSoftwareType softwareXML = csw.marshal( new FuGECommonProtocolGenericSoftwareType(), software );
            if ( software instanceof GenericSoftware ) {
                JAXBElement<? extends FuGECommonProtocolGenericSoftwareType> element = ( new ObjectFactory() )
                        .createGenericSoftware( ( FuGECommonProtocolGenericSoftwareType ) softwareXML );
                protocolCollectionXML.getSoftware().add( element );
            }
        }

        // protocol
        for ( Protocol protocol : protocolCollection.getProtocols() ) {
            // todo: there is an error in the way the "Softwares" and Protocols are retrieved from a ProtocolCollection. Retrieval from the DB again is the only way I have found so far to sort it out.
            // Otherwise, you'll find that the protocol variable will be of type,
            // e.g. net.sourceforge.fuge.common.protocol.ProtocolImpl$$EnhancerByCGLIB$$608851dd
            // rather than
            //  net.sourceforge.fuge.common.protocol.GenericProtocolImpl
            // and the "(protocol instanceof GenericProtocol) will be false.
            protocol = ( Protocol ) entityService.getIdentifiable( protocol.getId() );
            FuGECommonProtocolProtocolType protocolXML = cpr.marshal( new FuGECommonProtocolGenericProtocolType(), protocol );
            if ( protocol instanceof GenericProtocol ) {
                JAXBElement<? extends FuGECommonProtocolGenericProtocolType> element = ( new ObjectFactory() )
                        .createGenericProtocol( ( FuGECommonProtocolGenericProtocolType ) protocolXML );
                protocolCollectionXML.getProtocol().add( element );
            }
        }

        // protocol application
        for ( ProtocolApplication protocolApplication : protocolCollection.getProtocolApplications() ) {
            FuGECommonProtocolProtocolApplicationType paXML = capp.marshal( new FuGECommonProtocolGenericProtocolApplicationType(), protocolApplication );
            if ( protocolApplication instanceof GenericProtocolApplication ) {
                JAXBElement<? extends FuGECommonProtocolGenericProtocolApplicationType> element = ( new ObjectFactory() )
                        .createGenericProtocolApplication(
                                ( FuGECommonProtocolGenericProtocolApplicationType ) paXML );
                protocolCollectionXML.getProtocolApplication().add( element );
            }
        }

        return protocolCollectionXML;
    }

    public FuGECollectionProtocolCollectionType generateRandomXML( FuGECollectionProtocolCollectionType protocolCollectionXML ) {

        protocolCollectionXML = ( FuGECollectionProtocolCollectionType ) cd
                .generateRandomXML( protocolCollectionXML );

        return protocolCollectionXML;
    }

    public FuGECollectionFuGEType generateRandomXML( FuGECollectionFuGEType frXML ) {
        FuGECollectionProtocolCollectionType protocolCollectionXML = generateRandomXML( new FuGECollectionProtocolCollectionType() );

        if ( protocolCollectionXML.getEquipment().isEmpty() ) {
            // equipment
            protocolCollectionXML = ceq.generateRandomXMLWithLinksOut( protocolCollectionXML, frXML );
        }

        if ( protocolCollectionXML.getSoftware().isEmpty() ) {
            // software
            for ( int i = 0; i < NUMBER_ELEMENTS; i++ ) {
                FuGECommonProtocolGenericSoftwareType genericSoftwareXML = new FuGECommonProtocolGenericSoftwareType();
                genericSoftwareXML = ( FuGECommonProtocolGenericSoftwareType ) csw.generateRandomXMLWithLinksOut( genericSoftwareXML, protocolCollectionXML, frXML );
                JAXBElement<? extends FuGECommonProtocolGenericSoftwareType> element = ( new ObjectFactory() )
                        .createGenericSoftware( genericSoftwareXML );
                protocolCollectionXML.getSoftware().add( element );
            }
        }

        if ( protocolCollectionXML.getProtocol().isEmpty() ) {
            // protocol
            for ( int i = 0; i < NUMBER_ELEMENTS; i++ ) {
                FuGECommonProtocolGenericProtocolType genericProtocolXML = new FuGECommonProtocolGenericProtocolType();
                genericProtocolXML = ( FuGECommonProtocolGenericProtocolType ) cpr
                        .generateRandomXML( genericProtocolXML, protocolCollectionXML, frXML );
                JAXBElement<? extends FuGECommonProtocolGenericProtocolType> element = ( new ObjectFactory() )
                        .createGenericProtocol( genericProtocolXML );
                protocolCollectionXML.getProtocol().add( element );
            }
        }

        if ( protocolCollectionXML.getProtocolApplication().isEmpty() ) {
            // protocol application
            for ( int i = 0; i < NUMBER_ELEMENTS; i++ ) {
                JAXBElement<? extends FuGECommonProtocolGenericProtocolApplicationType> element = ( new ObjectFactory() )
                        .createGenericProtocolApplication(
                                ( FuGECommonProtocolGenericProtocolApplicationType ) capp
                                        .generateRandomXMLWithLinksOut( i, protocolCollectionXML, frXML ) );
                protocolCollectionXML.getProtocolApplication().add( element );
            }
        }

        frXML.setProtocolCollection( protocolCollectionXML );
        return frXML;
    }

    // We go through all equipment referenced in the protocols in protocolSet, retrieving all present.
    // These will get added to the experiment. ALWAYS ignore Dummy Equipment, if present.
    public Set<Equipment> addRelevantEquipment( FuGE fuge,
                                                Set<Protocol> protocolSet ) throws EntityServiceException {

        Set<Equipment> equipmentSet;

        if ( fuge.getProtocolCollection() != null && fuge.getProtocolCollection().getAllEquipment() != null ) {
            equipmentSet = ( Set<Equipment> ) fuge.getProtocolCollection().getAllEquipment();
        } else {
            equipmentSet = new HashSet<Equipment>();
        }


        for ( Protocol obj : protocolSet ) {
            if ( obj instanceof GenericProtocol ) {
                GenericProtocol gp = ( GenericProtocol ) obj;
//                gp = ( GenericProtocol ) entityService.greedyGet( gp );
                for ( GenericEquipment genericEquipment : gp.getEquipment() ) {
                    if ( !genericEquipment.getName().contains( " Dummy" ) ) {
                        if ( !equipmentSet.contains( genericEquipment ) ) {
                            // the current equipment is not yet in the experiment. Add it.
                            equipmentSet.add( genericEquipment );
                        }
                    }
                }
            }
        }

        return equipmentSet;
    }

    // We go through all protocols in the database, retrieving all that are directly associated (via GenericAction)
    // with the top-level investigation id'ed in protocolIdentifier. These will get added to the experiment.
    // only works for generic protocols
    public Set<Protocol> addRelevantProtocols( FuGE fuge,
                                               String protocolIdentifier ) throws EntityServiceException {

        Protocol abstractProtocol = ( Protocol ) entityService.getIdentifiable( protocolIdentifier );

        Set<Protocol> protocolSet;
        if ( fuge.getProtocolCollection() != null ) {
            protocolSet = ( Set<Protocol> ) fuge.getProtocolCollection().getProtocols();
        } else {
            protocolSet = new HashSet<Protocol>();
        }

        if ( !( abstractProtocol instanceof GenericProtocol ) ) {
            return protocolSet;
        }

        GenericProtocol topLevelProtocol = ( GenericProtocol ) abstractProtocol;

        // Assume that, if there is already something in the protocol collection, it must at a minimum
        // already contain the top-level protocol
        if ( protocolSet.isEmpty() ) {
            protocolSet.add( topLevelProtocol );
        }

        protocolSet.addAll( getChildProtocols( topLevelProtocol, protocolSet ) );

        return protocolSet;
    }

    // Works similarly to addRelevantProtocols, but does not initialize the Set<Protocol> with values from the
    // top-level investigation.
    public Set<Protocol> getChildProtocols( GenericProtocol parentProtocol, Set<Protocol> alreadyAddedProtocols ) {

        if ( alreadyAddedProtocols == null ) {
            alreadyAddedProtocols = new HashSet<Protocol>();
        }

        for ( Action action : parentProtocol.getActions() ) {
            if ( action instanceof GenericAction ) {
                GenericAction genericAction = ( GenericAction ) action;
//            System.out.println( "Investigating genericAction.getName() = " + genericAction.getName() );
                // add the generic protocol referenced by the generic action.
                GenericProtocol genericProtocol = ( GenericProtocol ) genericAction.getChildProtocol();
                boolean matchFound = false;
                for ( Protocol protocol : alreadyAddedProtocols ) {
                    if ( protocol instanceof GenericProtocol ) {
                        GenericProtocol gpSearch = ( GenericProtocol ) protocol;
                        if ( gpSearch.getIdentifier().trim().equals( genericProtocol.getIdentifier().trim() ) ) {
                            matchFound = true;
//                        System.err.println( "Match Found" );
                            break;
                        }
                    }
                }
                if ( !matchFound ) {
                    // the current protocol is not yet in the experiment. Add it, and any actions that may be present
                    // within this protocol.
                    alreadyAddedProtocols.add( genericProtocol );
                    alreadyAddedProtocols.addAll( getChildProtocols( genericProtocol, alreadyAddedProtocols ) );
                }
            }
        }
        return alreadyAddedProtocols;
    }

    // We are NOT printing the collection itself, just the contents of the collection.
    // Just prints a small subset of information about the objects
    public void prettyPrint( ProtocolCollection protocolCollection, PrintStream printStream ) {
        for ( Protocol protocol : protocolCollection.getProtocols() ) {
            cpr.prettyPrint( protocol, printStream );
        }
    }

    public void prettyHtml( ProtocolCollection protocolCollection,
                            PrintWriter printStream ) throws EntityServiceException {

        if ( protocolCollection == null || protocolCollection.getProtocols() == null ) {
            printStream.println(
                    "Error: this experiment has no protocols. Please contact " +
                            "helpdesk@cisban.ac.uk" );
            return;
        }
        boolean afterFirst = false;
        for ( Protocol protocol : protocolCollection.getProtocols() ) {
            if ( protocol instanceof GenericProtocol ) {
                GenericProtocol temp = ( GenericProtocol ) protocol;
                // Drill down from each top-level protocol all the way into the data files
                if ( !temp.getName().contains( "Component" ) ) {
                    // print out any data files associated with the GPAs associated with this protocol.
                    if ( !afterFirst ) {
                        afterFirst = true;
                        printStream.println( "<tr>" );
                    }
                    cpr.prettyHtml( null, temp, protocolCollection.getProtocolApplications(), printStream );
                }
            }
        }
        printStream.flush();
    }
}
