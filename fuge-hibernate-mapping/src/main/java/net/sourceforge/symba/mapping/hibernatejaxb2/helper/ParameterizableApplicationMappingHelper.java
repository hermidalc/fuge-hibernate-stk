package net.sourceforge.symba.mapping.hibernatejaxb2.helper;

import net.sourceforge.fuge.common.protocol.*;
import net.sourceforge.fuge.common.audit.Person;
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
public class ParameterizableApplicationMappingHelper implements MappingHelper<ParameterizableApplication, FuGECommonProtocolParameterizableApplicationType> {
    private final DescribableMappingHelper cd;
    private final IdentifiableMappingHelper ci;

    public ParameterizableApplicationMappingHelper() {
        cd = new DescribableMappingHelper();
        ci = new IdentifiableMappingHelper();
    }

    public ParameterizableApplication unmarshal( FuGECommonProtocolParameterizableApplicationType parameterizableApplicationXML,
                                                 ParameterizableApplication parameterizableApplication, Person performer )
            throws EntityServiceException {

        parameterizableApplication = ( ParameterizableApplication ) ci.unmarshal( parameterizableApplicationXML, parameterizableApplication, performer );

        Set<ParameterValue> parameterValues = new HashSet<ParameterValue>();
        MeasurementMappingHelper measurementMappingHelper = new MeasurementMappingHelper();
        for ( FuGECommonProtocolParameterValueType parameterValueXML : parameterizableApplicationXML.getParameterValue() ) {

            ParameterValue pv = ( ParameterValue ) entityService.createDescribable(
                    "net.sourceforge.fuge.common.protocol.ParameterValue" );
            pv = ( ParameterValue ) cd.unmarshal( parameterValueXML, pv, performer );
            pv.setParameter( ( Parameter ) entityService.getIdentifiable( parameterValueXML.getParameterRef() ) );

            pv.setValue( measurementMappingHelper.unmarshal( parameterValueXML.getMeasurement().getValue(), null, performer ) );

            DatabaseObjectHelper.save( "net.sourceforge.fuge.common.protocol.ParameterValue", pv, performer );
            parameterValues.add( pv );

        }

        parameterizableApplication.setParameterValues( parameterValues );

        return parameterizableApplication;
    }

    public FuGECommonProtocolParameterizableApplicationType marshal(
            FuGECommonProtocolParameterizableApplicationType parameterizableApplicationXML,
            ParameterizableApplication parameterizableApplication ) {

        parameterizableApplicationXML = ( FuGECommonProtocolParameterizableApplicationType ) ci.marshal( parameterizableApplicationXML, parameterizableApplication );

        MeasurementMappingHelper measurementMappingHelper = new MeasurementMappingHelper();

        for ( ParameterValue pvalue : parameterizableApplication.getParameterValues() ) {

            FuGECommonProtocolParameterValueType pvXML = new FuGECommonProtocolParameterValueType();
            pvXML = ( FuGECommonProtocolParameterValueType ) cd.marshal( pvXML, pvalue );

            pvXML.setParameterRef( pvalue.getParameter().getIdentifier() );
            FuGECommonMeasurementMeasurementType measurementXML = measurementMappingHelper.marshal( null, pvalue.getValue() );

            if ( measurementXML instanceof FuGECommonMeasurementAtomicValueType ) {
                pvXML.setMeasurement( ( new ObjectFactory() ).createAtomicValue( ( FuGECommonMeasurementAtomicValueType ) measurementXML ) );
            } else if ( measurementXML instanceof FuGECommonMeasurementBooleanValueType ) {
                pvXML.setMeasurement( ( new ObjectFactory() ).createBooleanValue( ( FuGECommonMeasurementBooleanValueType ) measurementXML ) );
            } else if ( measurementXML instanceof FuGECommonMeasurementComplexValueType ) {
                pvXML.setMeasurement( ( new ObjectFactory() ).createComplexValue( ( FuGECommonMeasurementComplexValueType ) measurementXML ) );
            } else if ( measurementXML instanceof FuGECommonMeasurementRangeType ) {
                pvXML.setMeasurement( ( new ObjectFactory() ).createRange( ( FuGECommonMeasurementRangeType ) measurementXML ) );
            }
            parameterizableApplicationXML.getParameterValue().add( pvXML );
        }

        return parameterizableApplicationXML;
    }

}
