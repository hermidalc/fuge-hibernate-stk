package net.sourceforge.symba.mapping.hibernatejaxb2.helper;


import net.sourceforge.fuge.common.protocol.Equipment;
import net.sourceforge.fuge.common.protocol.GenericEquipment;
import net.sourceforge.fuge.common.ontology.OntologyTerm;
import net.sourceforge.fuge.common.audit.Person;
import net.sourceforge.fuge.service.EntityServiceException;
import net.sourceforge.fuge.util.generatedJAXB2.*;

import javax.xml.bind.JAXBElement;

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
public class EquipmentMappingHelper implements MappingHelper<Equipment, FuGECommonProtocolEquipmentType> {
    private final int NUMBER_ELEMENTS = 2;
    private final IdentifiableMappingHelper ci;
    private final ParameterizableMappingHelper cparam;
    private final GenericEquipmentMappingHelper cgeq;

    public EquipmentMappingHelper() {
        this.ci = new IdentifiableMappingHelper();
        this.cparam = new ParameterizableMappingHelper();
        this.cgeq = new GenericEquipmentMappingHelper();
    }

    /**
     * Does not perform any update to the database of the Data object (though child objects may be updated).
     *
     * @param equipmentXML the jaxb object to parse
     * @param equipment    the hibernate object to fill
     * @param performer the person to assign to the creation/modification of the object
     * @return the newly-filled hibernate object
     * @throws EntityServiceException if there is a problem with the connection to the database
     */
    public Equipment unmarshal( FuGECommonProtocolEquipmentType equipmentXML, Equipment equipment, Person performer )
            throws EntityServiceException {

        // determine what sort of equipment it is
        if ( equipmentXML instanceof FuGECommonProtocolGenericEquipmentType ) {
            // create or retrieve fuge object

            GenericEquipment genericEquipment = ( GenericEquipment ) equipment;

            // get generic equipment attributes - doing this first here runs the lazy loading before getting to equipment attributes
            genericEquipment = cgeq.unmarshal(
                    ( FuGECommonProtocolGenericEquipmentType ) equipmentXML, genericEquipment, performer );

            genericEquipment = ( GenericEquipment ) ci.unmarshal( equipmentXML, genericEquipment, performer );

            // get equipment attributes
            genericEquipment = ( GenericEquipment ) cparam.unmarshal( equipmentXML, genericEquipment, performer );

            if ( equipmentXML.getMake() != null ) {
                genericEquipment.setMake( ( OntologyTerm ) entityService.getIdentifiable( equipmentXML.getMake().getOntologyTermRef() ) );
            }
            if ( equipmentXML.getModel() != null ) {
                genericEquipment.setModel( ( OntologyTerm ) entityService.getIdentifiable( equipmentXML.getModel().getOntologyTermRef() ) );
            }

            return genericEquipment;
        }
        System.err.println( "Error processing XML Equipment class: Should be of type \"FuGECommonProtocolGenericEquipmentType\" and isn't" );
        return null;  // shouldn't get here as there is currently only one type of Equipment allowed.
    }

    public FuGECommonProtocolEquipmentType marshal( FuGECommonProtocolEquipmentType equipmentXML, Equipment equipment )
            throws EntityServiceException {

        if ( equipment instanceof GenericEquipment ) {

            // create fuge object
            FuGECommonProtocolGenericEquipmentType genericEquipmentXML = ( FuGECommonProtocolGenericEquipmentType ) equipmentXML;

            // get equipment attributes
            genericEquipmentXML = ( FuGECommonProtocolGenericEquipmentType ) ci.marshal(
                    genericEquipmentXML, equipment );

            genericEquipmentXML = ( FuGECommonProtocolGenericEquipmentType ) cparam.marshal(
                    genericEquipmentXML, equipment );

            if ( equipment.getMake() != null ) {
                FuGECommonProtocolEquipmentType.Make make = new FuGECommonProtocolEquipmentType.Make();
                make.setOntologyTermRef( equipment.getMake().getIdentifier() );
                genericEquipmentXML.setMake( make );
            }
            if ( equipment.getModel() != null ) {
                FuGECommonProtocolEquipmentType.Model model = new FuGECommonProtocolEquipmentType.Model();
                model.setOntologyTermRef( equipment.getModel().getIdentifier() );
                genericEquipmentXML.setModel( model );
            }

            // get generic equipment attributes
            genericEquipmentXML = cgeq.marshal( genericEquipmentXML, ( GenericEquipment ) equipment );

            return genericEquipmentXML;
        }
        return null;  // shouldn't get here as there is currently only one type of Equipment allowed.
    }

    // you will not get a complete equipment with this method - just the very basics. Instead, use generateRandomXMLWithLinksOut
    public FuGECommonProtocolEquipmentType generateRandomXML( FuGECommonProtocolEquipmentType genEquipmentXML ) {

        // get equipment attributes
        genEquipmentXML = ( FuGECommonProtocolGenericEquipmentType ) ci.generateRandomXML( genEquipmentXML );

        return genEquipmentXML;
    }

    // this method is different from the others in that it will generate ALL equipment
    // in one go, rather than just one piece of equipment. This is because software may not
    // have been made yet, and so this method needs protocolCollection changeable so that it can add
    // software if necessary.
    public FuGECollectionProtocolCollectionType generateRandomXMLWithLinksOut(
            FuGECollectionProtocolCollectionType protocolCollectionXML,
            FuGECollectionFuGEType frXML ) {

        for ( int i = 0; i < NUMBER_ELEMENTS; i++ ) {
            FuGECommonProtocolGenericEquipmentType genEquipmentXML = ( FuGECommonProtocolGenericEquipmentType ) generateRandomXML( new FuGECommonProtocolGenericEquipmentType() );

            genEquipmentXML = ( FuGECommonProtocolGenericEquipmentType ) cparam.generateRandomXMLWithLinksOut(
                    genEquipmentXML, frXML );

            if ( frXML.getOntologyCollection() != null ) {
                FuGECommonProtocolEquipmentType.Make make = new FuGECommonProtocolEquipmentType.Make();
                make.setOntologyTermRef(
                        frXML.getOntologyCollection().getOntologyTerm().get( 0 ).getValue().getIdentifier() );
                genEquipmentXML.setMake( make );

                FuGECommonProtocolEquipmentType.Model model = new FuGECommonProtocolEquipmentType.Model();
                model.setOntologyTermRef(
                        frXML.getOntologyCollection().getOntologyTerm().get( 0 ).getValue().getIdentifier() );
                genEquipmentXML.setModel( model );
            }

            // software required for generic equipment attributes
            if ( protocolCollectionXML.getSoftware() == null ) {
                SoftwareMappingHelper csw = new SoftwareMappingHelper();
                for ( int ii = 0; ii < NUMBER_ELEMENTS; ii++ ) {
                    FuGECommonProtocolGenericSoftwareType genericSoftwareXML = new FuGECommonProtocolGenericSoftwareType();
                    genericSoftwareXML = ( FuGECommonProtocolGenericSoftwareType ) csw.generateRandomXMLWithLinksOut( genericSoftwareXML, protocolCollectionXML, frXML );
                    JAXBElement<? extends FuGECommonProtocolGenericSoftwareType> element = ( new ObjectFactory() ).createGenericSoftware(
                            genericSoftwareXML );
                    protocolCollectionXML.getSoftware().add( element );
                }
            }
            // get generic equipment attributes
            if ( i > 0 ) {
                genEquipmentXML = cgeq.generateRandomXMLWithLinksOut(
                        genEquipmentXML,
                        ( FuGECommonProtocolGenericEquipmentType ) protocolCollectionXML.getEquipment()
                                .get( 0 )
                                .getValue(),
                        frXML );
            } else {
                genEquipmentXML = cgeq.generateRandomXMLWithLinksOut( genEquipmentXML, null, frXML );
            }

            JAXBElement<? extends FuGECommonProtocolGenericEquipmentType> element = ( new ObjectFactory() ).createGenericEquipment(
                    genEquipmentXML );
            protocolCollectionXML.getEquipment().add( element );
        }
        return protocolCollectionXML;

    }
}
