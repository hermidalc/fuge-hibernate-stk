package net.sourceforge.symba.mapping.hibernatejaxb2.helper;


import net.sourceforge.fuge.common.protocol.*;
import net.sourceforge.fuge.common.audit.Person;
import net.sourceforge.fuge.service.EntityServiceException;
import net.sourceforge.fuge.util.generatedJAXB2.FuGECollectionFuGEType;
import net.sourceforge.fuge.util.generatedJAXB2.FuGECommonProtocolGenericEquipmentType;
import net.sourceforge.fuge.util.generatedJAXB2.FuGECommonProtocolGenericParameterType;
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
public class GenericEquipmentMappingHelper implements MappingHelper<GenericEquipment, FuGECommonProtocolGenericEquipmentType> {
    private final int NUMBER_ELEMENTS = 2;
    private final ParameterMappingHelper cp;

    public GenericEquipmentMappingHelper() {
        this.cp = new ParameterMappingHelper();

    }

    public GenericEquipment unmarshal( FuGECommonProtocolGenericEquipmentType genericEquipmentXML,
                                       GenericEquipment genericEquipment, Person performer ) throws EntityServiceException {

        // set any GenericEquipment-specific traits
        Set<GenericParameter> genericParameters = new HashSet<GenericParameter>();
        for ( FuGECommonProtocolGenericParameterType genericParameterXML : genericEquipmentXML.getGenericParameter() ) {
            GenericParameter gp = ( GenericParameter ) cp.unmarshal( genericParameterXML, ( GenericParameter ) DatabaseObjectHelper.getOrCreate(
                    genericParameterXML.getIdentifier(),
                    genericParameterXML.getName(),
                    "net.sourceforge.fuge.common.protocol.GenericParameter" ), performer );
            entityService.save( "net.sourceforge.fuge.common.protocol.GenericParameter", gp, performer );
            genericParameters.add( gp );
        }
        if ( !genericParameters.isEmpty() )
            genericEquipment.setParameters( genericParameters );

        Set<GenericEquipment> genericEquipments = new HashSet<GenericEquipment>();
        for ( FuGECommonProtocolGenericEquipmentType.EquipmentParts referencedXML : genericEquipmentXML.getEquipmentParts() ) {
            genericEquipments.add( ( GenericEquipment ) entityService.getIdentifiable( referencedXML.getGenericEquipmentRef() ) );
        }
        if ( !genericEquipments.isEmpty() )
            genericEquipment.setEquipmentParts( genericEquipments );

        Set<GenericSoftware> genericSoftwares = new HashSet<GenericSoftware>();
        for ( FuGECommonProtocolGenericEquipmentType.Software referencedXML : genericEquipmentXML.getSoftware() ) {
            genericSoftwares.add( ( GenericSoftware ) entityService.getIdentifiable( referencedXML.getGenericSoftwareRef() ) );
        }
        if ( !genericSoftwares.isEmpty() )
            genericEquipment.setSoftware( genericSoftwares );


        return genericEquipment;
    }

    public FuGECommonProtocolGenericEquipmentType marshal(
            FuGECommonProtocolGenericEquipmentType genericEquipmentXML,
            GenericEquipment genericEquipment ) throws EntityServiceException {

        // get any lazily loaded objects
//        genericEquipment = ( GenericEquipment ) entityService.greedyGet( genericEquipment );

        // set any GenericEquipment-specific traits

        for ( GenericParameter parameter : genericEquipment.getParameters() ) {
            // set fuge object
            genericEquipmentXML.getGenericParameter()
                    .add( ( FuGECommonProtocolGenericParameterType ) cp.marshal( new FuGECommonProtocolGenericParameterType(), parameter ) );
        }
        for ( GenericEquipment equipment : genericEquipment.getEquipmentParts() ) {
            FuGECommonProtocolGenericEquipmentType.EquipmentParts parts = new FuGECommonProtocolGenericEquipmentType.EquipmentParts();
            parts.setGenericEquipmentRef( equipment.getIdentifier() );
            genericEquipmentXML.getEquipmentParts().add( parts );
        }
        for ( GenericSoftware software : genericEquipment.getSoftware() ) {
            FuGECommonProtocolGenericEquipmentType.Software softwareXML = new FuGECommonProtocolGenericEquipmentType.Software();
            softwareXML.setGenericSoftwareRef( software.getIdentifier() );
            genericEquipmentXML.getSoftware().add( softwareXML );
        }

        return genericEquipmentXML;
    }

    // there is currently no part of generating generic equipment that does not involve a fuge object, therefore
    // ensure you use generateRandomXMLWithLinksOut
    public FuGECommonProtocolGenericEquipmentType generateRandomXML( FuGECommonProtocolGenericEquipmentType genericEquipmentXML ) {
        return genericEquipmentXML;
    }

    public FuGECommonProtocolGenericEquipmentType generateRandomXMLWithLinksOut(
            FuGECommonProtocolGenericEquipmentType genericEquipmentXML,
            FuGECommonProtocolGenericEquipmentType partXML,
            FuGECollectionFuGEType frXML ) {

        genericEquipmentXML = generateRandomXML( genericEquipmentXML );

        for ( int i = 0; i < NUMBER_ELEMENTS; i++ ) {
            FuGECommonProtocolGenericParameterType parameterXML = new FuGECommonProtocolGenericParameterType();
            genericEquipmentXML.getGenericParameter()
                    .add( ( FuGECommonProtocolGenericParameterType ) cp.generateRandomXMLWithLinksOut( parameterXML, frXML ) );
        }

        if ( partXML != null ) {
            // parts list of one
            FuGECommonProtocolGenericEquipmentType.EquipmentParts parts = new FuGECommonProtocolGenericEquipmentType.EquipmentParts();
            parts.setGenericEquipmentRef( partXML.getIdentifier() );
            genericEquipmentXML.getEquipmentParts().add( parts );
        }

        if ( frXML.getProtocolCollection() != null ) {
            for ( int i = 0; i < NUMBER_ELEMENTS; i++ ) {
                FuGECommonProtocolGenericEquipmentType.Software softwareXML = new FuGECommonProtocolGenericEquipmentType.Software();
                softwareXML.setGenericSoftwareRef(
                        frXML.getProtocolCollection().getSoftware().get( i ).getValue().getIdentifier() );
                genericEquipmentXML.getSoftware().add( softwareXML );
            }
        }
        return genericEquipmentXML;
    }
}
