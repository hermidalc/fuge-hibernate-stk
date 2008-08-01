package net.sourceforge.symba.mapping.hibernatejaxb2.helper;


import net.sourceforge.fuge.common.protocol.*;
import net.sourceforge.fuge.common.measurement.Measurement;
import net.sourceforge.fuge.common.audit.Person;
import net.sourceforge.fuge.service.EntityServiceException;
import net.sourceforge.fuge.util.generatedJAXB2.*;

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
public class ParameterMappingHelper implements MappingHelper<Parameter, FuGECommonProtocolParameterType> {
    private final IdentifiableMappingHelper ci;
    private final GenericParameterMappingHelper cgp;

    public ParameterMappingHelper() {
        this.ci = new IdentifiableMappingHelper();
        this.cgp = new GenericParameterMappingHelper();
    }

    /**
     * Does not perform any update to the database of the Provider object (though child objects are updated).
     *
     * @param parameterXML the jaxb object to parse
     * @param parameter    the hibernate object to fill
     * @param performer    the person who will get put in the audit trail
     * @return the newly-filled hibernate object
     * @throws EntityServiceException if there is a problem with the connection to the database
     */
    public Parameter unmarshal( FuGECommonProtocolParameterType parameterXML, Parameter parameter, Person performer )
            throws EntityServiceException {

        if ( parameterXML instanceof FuGECommonProtocolGenericParameterType ) {

            GenericParameter genericParameter = ( GenericParameter ) parameter;

            // get parameter attributes
            genericParameter = ( GenericParameter ) ci.unmarshal( parameterXML, genericParameter, performer );

            // isInputParam
            genericParameter.setIsInputParam( parameterXML.isIsInputParam() );

            // measurement
            MeasurementMappingHelper measurementMappingHelper = new MeasurementMappingHelper();
            Measurement measurement = measurementMappingHelper.unmarshal( parameterXML.getMeasurement().getValue(), null, performer );
            genericParameter.setDefaultValue( measurement );

            // get generic parameter attributes
            genericParameter = cgp.unmarshal(
                    ( FuGECommonProtocolGenericParameterType ) parameterXML, genericParameter, performer );

            return genericParameter;
        }
        return null; // shouldn't get here as there is currently only one type of Parameter allowed.
    }

    public FuGECommonProtocolParameterType marshal( FuGECommonProtocolParameterType parameterXML, Parameter parameter ) {

        if ( parameter instanceof GenericParameter ) {
            FuGECommonProtocolGenericParameterType genericParameterXML = ( FuGECommonProtocolGenericParameterType ) parameterXML;

            // get parameter attributes
            genericParameterXML = ( FuGECommonProtocolGenericParameterType ) ci.marshal(
                    genericParameterXML, parameter );

            // isInputParam
            genericParameterXML.setIsInputParam( parameter.getIsInputParam() );

            // measurement
            MeasurementMappingHelper measurementMappingHelper = new MeasurementMappingHelper();
            FuGECommonMeasurementMeasurementType measurementXML = measurementMappingHelper.marshal( null, parameter.getDefaultValue() );
            // todo make this next bit more elegant
            if ( measurementXML instanceof FuGECommonMeasurementAtomicValueType ) {
                genericParameterXML.setMeasurement( ( new ObjectFactory() ).createAtomicValue( ( FuGECommonMeasurementAtomicValueType ) measurementXML ) );
            } else if ( measurementXML instanceof FuGECommonMeasurementBooleanValueType ) {
                genericParameterXML.setMeasurement( ( new ObjectFactory() ).createBooleanValue( ( FuGECommonMeasurementBooleanValueType ) measurementXML ) );
            } else if ( measurementXML instanceof FuGECommonMeasurementComplexValueType ) {
                genericParameterXML.setMeasurement( ( new ObjectFactory() ).createComplexValue( ( FuGECommonMeasurementComplexValueType ) measurementXML ) );
            } else if ( measurementXML instanceof FuGECommonMeasurementRangeType ) {
                genericParameterXML.setMeasurement( ( new ObjectFactory() ).createRange( ( FuGECommonMeasurementRangeType ) measurementXML ) );
            }

            // get generic parameter attributes
            genericParameterXML = cgp.marshal( genericParameterXML, ( GenericParameter ) parameter );

            return genericParameterXML;
        }
        return null; // shouldn't get here as there is currently only one type of Parameter allowed.
    }
}
