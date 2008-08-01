package net.sourceforge.symba.mapping.hibernatejaxb2.helper;


import net.sourceforge.fuge.common.protocol.*;
import net.sourceforge.fuge.common.audit.Person;
import net.sourceforge.fuge.service.EntityServiceException;
import net.sourceforge.fuge.util.generatedJAXB2.FuGECommonProtocolGenericParameterType;
import net.sourceforge.fuge.util.generatedJAXB2.FuGECommonProtocolGenericSoftwareType;
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
public class GenericSoftwareMappingHelper implements MappingHelper<GenericSoftware, FuGECommonProtocolGenericSoftwareType> {
    private final ParameterMappingHelper cp;

    public GenericSoftwareMappingHelper() {
        this.cp = new ParameterMappingHelper();
    }

    public GenericSoftware unmarshal( FuGECommonProtocolGenericSoftwareType genericSoftwareXML,
                                      GenericSoftware genericSoftware, Person performer ) throws EntityServiceException {

        Set<GenericEquipment> genericEquipments = new HashSet<GenericEquipment>();

        Set<GenericParameter> genericParameters = new HashSet<GenericParameter>();
        // set any GenericSoftware-specific traits
        for ( FuGECommonProtocolGenericParameterType genericParameterXML : genericSoftwareXML.getGenericParameter() ) {
            GenericParameter gp = ( GenericParameter ) cp.unmarshal( genericParameterXML, ( GenericParameter ) DatabaseObjectHelper.getOrCreate(
                    genericParameterXML.getIdentifier(),
                    genericParameterXML.getName(),
                    "net.sourceforge.fuge.common.protocol.GenericParameter" ), performer );
            DatabaseObjectHelper.save( "net.sourceforge.fuge.common.protocol.GenericParameter", gp, performer );
            genericParameters.add( gp );
        }

        if ( !genericParameters.isEmpty() )
            genericSoftware.setParameters( genericParameters );

        for ( FuGECommonProtocolGenericSoftwareType.Equipment referencedXML : genericSoftwareXML.getEquipment() ) {
            genericEquipments.add( ( GenericEquipment ) entityService.getIdentifiable( referencedXML.getGenericEquipmentRef() ) );
        }
        if ( !genericEquipments.isEmpty() ) {
            genericSoftware.setEquipment( genericEquipments );
        }

        return genericSoftware;
    }

    public FuGECommonProtocolGenericSoftwareType marshal(
            FuGECommonProtocolGenericSoftwareType genericSoftwareXML,
            GenericSoftware genericSoftware ) throws EntityServiceException {

        // set any GenericSoftware-specific traits

        // you can only have a GenericParameter here
        for ( GenericParameter parameter : genericSoftware.getParameters() ) {
            // set fuge object
            genericSoftwareXML.getGenericParameter()
                    .add( ( FuGECommonProtocolGenericParameterType ) cp.marshal( new FuGECommonProtocolGenericParameterType(), parameter ) );
        }

        for ( GenericEquipment equipment : genericSoftware.getEquipment() ) {
            System.err.println( "retrieving generic equipment" );
            FuGECommonProtocolGenericSoftwareType.Equipment referencedEquipmentXML = new FuGECommonProtocolGenericSoftwareType.Equipment();
            referencedEquipmentXML.setGenericEquipmentRef( equipment.getIdentifier() );
            genericSoftwareXML.getEquipment().add( referencedEquipmentXML );
        }
        return genericSoftwareXML;
    }
}
