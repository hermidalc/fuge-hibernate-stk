package net.sourceforge.symba.mapping.hibernatejaxb2.helper;

import net.sourceforge.fuge.common.protocol.*;
import net.sourceforge.fuge.common.audit.Person;
import net.sourceforge.fuge.service.EntityServiceException;
import net.sourceforge.fuge.util.generatedJAXB2.*;
import net.sourceforge.symba.mapping.hibernatejaxb2.DatabaseObjectHelper;

import javax.xml.bind.JAXBElement;
import java.io.PrintStream;
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
public class GenericProtocolMappingHelper implements MappingHelper<GenericProtocol, FuGECommonProtocolGenericProtocolType> {
    private final int NUMBER_ELEMENTS = 2;
    private final ActionMappingHelper ca;
    private final ParameterMappingHelper cp;

    public GenericProtocolMappingHelper() {
        this.ca = new ActionMappingHelper();
        this.cp = new ParameterMappingHelper();
    }

    // @todo assumes all Collections (ReferenceableCollection, AuditCollection etc) are already extant in the database
    public GenericProtocol unmarshal( FuGECommonProtocolGenericProtocolType genericProtocolXML,
                                      GenericProtocol genericProtocol, Person performer ) throws EntityServiceException {

        // set any GenericProtocol-specific traits

        // we only parse GenericActions
        Set<Action> actions = new HashSet<Action>();
        for ( JAXBElement<? extends FuGECommonProtocolActionType> genericActionXML : genericProtocolXML.getAction() ) {
            if ( genericActionXML.getValue() instanceof FuGECommonProtocolGenericActionType ) {
                GenericAction genericAction = ( GenericAction ) ca.unmarshal( genericActionXML.getValue(),
                        ( Action ) DatabaseObjectHelper.getOrCreate(
                                genericActionXML.getValue().getIdentifier(),
                                genericActionXML.getValue().getName(),
                                "net.sourceforge.fuge.common.protocol.GenericAction" ), performer );
                DatabaseObjectHelper.save( "net.sourceforge.fuge.common.protocol.GenericAction", genericAction, performer);
                actions.add( genericAction );
            }
        }
        genericProtocol.setActions( actions );
        // generic parameter
        Set<GenericParameter> genericParameters = new HashSet<GenericParameter>();
        for ( FuGECommonProtocolGenericParameterType genericParameterXML : genericProtocolXML
                .getGenericParameter() ) {
            GenericParameter gp = ( GenericParameter ) cp.unmarshal( genericParameterXML, ( GenericParameter ) DatabaseObjectHelper.getOrCreate(
                    genericParameterXML.getIdentifier(),
                    genericParameterXML.getName(),
                    "net.sourceforge.fuge.common.protocol.GenericParameter" ), performer );
            DatabaseObjectHelper.save( "net.sourceforge.fuge.common.protocol.GenericParameter", gp, performer );
            genericParameters.add( gp );
        }
        genericProtocol.setParameters( genericParameters );

        // genPrtcltoequip
        Set<GenericEquipment> genericEquipments = new HashSet<GenericEquipment>();
        for ( FuGECommonProtocolGenericProtocolType.Equipment genPrtclToEquipXML : genericProtocolXML.getEquipment() ) {
            genericEquipments.add(
                    ( GenericEquipment ) entityService.getIdentifiable( genPrtclToEquipXML.getGenericEquipmentRef() ) );
        }
        genericProtocol.setEquipment( genericEquipments );

        // generic software
        Set<GenericSoftware> genericSoftwares = new HashSet<GenericSoftware>();
        for ( FuGECommonProtocolGenericProtocolType.Software genSoftwareXML : genericProtocolXML.getSoftware() ) {
            genericSoftwares
                    .add( ( GenericSoftware ) entityService.getIdentifiable( genSoftwareXML.getGenericSoftwareRef() ) );
        }
        genericProtocol.setSoftware( genericSoftwares );

        return genericProtocol;
    }

    public FuGECommonProtocolGenericProtocolType marshal(
            FuGECommonProtocolGenericProtocolType genericProtocolXML,
            GenericProtocol genericProtocol ) throws EntityServiceException {

        // set any GenericProtocol-specific traits

        ObjectFactory factory = new ObjectFactory();
        // can only have generic actions.
        for ( Action action : genericProtocol.getActions() ) {
            if ( action instanceof GenericAction ) {
                GenericAction genericAction = ( GenericAction ) action;
                genericProtocolXML.getAction()
                        .add( factory.createGenericAction(
                                ( FuGECommonProtocolGenericActionType ) ca.marshal( new FuGECommonProtocolGenericActionType(), genericAction ) ) );
            }
        }

        // can only have generic parameters
        for ( GenericParameter genericParameter : genericProtocol.getParameters() ) {
            genericProtocolXML.getGenericParameter()
                    .add( ( FuGECommonProtocolGenericParameterType ) cp.marshal( new FuGECommonProtocolGenericParameterType(), genericParameter ) );
        }

        // protocol to equipment
        for ( GenericEquipment genericEquipment : genericProtocol.getEquipment() ) {
            FuGECommonProtocolGenericProtocolType.Equipment equip = new FuGECommonProtocolGenericProtocolType.Equipment();
            equip.setGenericEquipmentRef( genericEquipment.getIdentifier() );
            genericProtocolXML.getEquipment().add( equip );
        }
        // software
        for ( GenericSoftware genericSoftware : genericProtocol.getSoftware() ) {
            FuGECommonProtocolGenericProtocolType.Software genSoftware = new FuGECommonProtocolGenericProtocolType.Software();
            genSoftware.setGenericSoftwareRef( genericSoftware.getIdentifier() );
            genericProtocolXML.getSoftware().add( genSoftware );
        }

        return genericProtocolXML;
    }

    // the only way to make random xml is via generateRandomXMLWithLinks, as all parts require input from elsewhere in the fuge document
    public FuGECommonProtocolGenericProtocolType generateRandomXML( FuGECommonProtocolGenericProtocolType genericProtocolXML ) {
        return genericProtocolXML;
    }

    public FuGECommonProtocolGenericProtocolType generateRandomXML(
            FuGECommonProtocolGenericProtocolType genericProtocolXML,
            FuGECollectionProtocolCollectionType protocolCollectionXML,
            FuGECollectionFuGEType frXML ) {

        genericProtocolXML = generateRandomXML( genericProtocolXML );

        ObjectFactory factory = new ObjectFactory();

        // can only have generic actions.
        for ( int i = 0; i < NUMBER_ELEMENTS; i++ ) {
            genericProtocolXML.getAction().add(
                    factory.createGenericAction( ( FuGECommonProtocolGenericActionType ) ca.generateRandomXMLWithLinksOut(
                            new FuGECommonProtocolGenericActionType(), i, protocolCollectionXML, frXML ) ) );
        }
        // can only have generic parameters
        for ( int i = 0; i < NUMBER_ELEMENTS; i++ ) {
            genericProtocolXML.getGenericParameter().add(
                    ( FuGECommonProtocolGenericParameterType ) cp
                            .generateRandomXMLWithLinksOut( new FuGECommonProtocolGenericParameterType(), frXML ) );
        }

        if ( protocolCollectionXML != null ) {
            // protocol to equipment
            for ( int i = 0; i < NUMBER_ELEMENTS; i++ ) {
                FuGECommonProtocolGenericProtocolType.Equipment equip = new FuGECommonProtocolGenericProtocolType.Equipment();
                equip.setGenericEquipmentRef(
                        protocolCollectionXML.getEquipment().get( i ).getValue().getIdentifier() );
                genericProtocolXML.getEquipment().add( equip );
            }
            // software
            for ( int i = 0; i < NUMBER_ELEMENTS; i++ ) {
                FuGECommonProtocolGenericProtocolType.Software genSoftware = new FuGECommonProtocolGenericProtocolType.Software();
                genSoftware.setGenericSoftwareRef(
                        protocolCollectionXML.getSoftware().get( i ).getValue().getIdentifier() );
                genericProtocolXML.getSoftware().add( genSoftware );
            }
        }
        return genericProtocolXML;
    }

    // We are NOT printing the collection itself, just the contents of the collection.
    // Just prints a small subset of information about the objects
    public void prettyPrint( GenericProtocol genericProtocol, PrintStream printStream ) {
        prettyPrint( null, genericProtocol, printStream );
    }

    public void prettyPrint( String prepend, GenericProtocol genericProtocol, PrintStream printStream ) {
        for ( Action action : genericProtocol.getActions() ) {
            ca.prettyPrint( prepend, action, printStream );
        }
    }
}
