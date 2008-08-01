package net.sourceforge.symba.mapping.hibernatejaxb2.helper;

import net.sourceforge.fuge.common.measurement.*;
import net.sourceforge.fuge.common.ontology.OntologyTerm;
import net.sourceforge.fuge.common.audit.Person;
import net.sourceforge.fuge.service.EntityServiceException;
import net.sourceforge.fuge.util.generatedJAXB2.*;
import net.sourceforge.symba.mapping.hibernatejaxb2.DatabaseObjectHelper;

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
public class MeasurementMappingHelper implements MappingHelper<Measurement, FuGECommonMeasurementMeasurementType> {
    private final DescribableMappingHelper cd;
    private final AtomicValueMappingHelper cav;
    private final BooleanValueMappingHelper cbv;
    private final ComplexValueMappingHelper ccv;
    private final RangeMappingHelper cr;

    public MeasurementMappingHelper() {
        this.cd = new DescribableMappingHelper();
        this.cav = new AtomicValueMappingHelper();
        this.cbv = new BooleanValueMappingHelper();
        this.ccv = new ComplexValueMappingHelper();
        this.cr = new RangeMappingHelper();
    }

    /**
     * This method works differently from the other unmarshalling methods: as we don't know, going into this method,
     * which implementation of Measurement is being unmarshalled, we cannot create/load the object in the database
     * until we get to this method. Therefore, database changes on the main object are done in this method.
     *
     * @param measurementXML the JAXB2 object to parse
     * @param measurement    isn't used in this method currently - just keeping in line with its parent class
     * @param performer      the person to assign to the modification/creation of this object
     * @return a brand-new object of the Measurement type, loaded into the database and filled with information
     *         from measurementXML
     * @throws EntityServiceException
     */
    public Measurement unmarshal( FuGECommonMeasurementMeasurementType measurementXML, Measurement measurement, Person performer )
            throws EntityServiceException {

        if ( measurementXML instanceof FuGECommonMeasurementAtomicValueType ) {
            AtomicValue value = ( AtomicValue ) entityService.createDescribable( "net.sourceforge.fuge.common.measurement.AtomicValue" );

            value = ( AtomicValue ) unmarshalDefaultValue( measurementXML, value, performer );

            // get atomic value attributes
            value = cav.unmarshal( ( FuGECommonMeasurementAtomicValueType ) measurementXML, value, performer );
            DatabaseObjectHelper.save( "net.sourceforge.fuge.common.measurement.AtomicValue", value, performer );
            return value;
        } else if ( measurementXML instanceof FuGECommonMeasurementBooleanValueType ) {
            BooleanValue value = ( BooleanValue ) entityService.createDescribable( "net.sourceforge.fuge.common.measurement.BooleanValue" );

            value = ( BooleanValue ) unmarshalDefaultValue( measurementXML, value, performer );

            // get boolean value attributes
            value = cbv.unmarshal( ( FuGECommonMeasurementBooleanValueType ) measurementXML, value, performer );
            DatabaseObjectHelper.save( "net.sourceforge.fuge.common.measurement.BooleanValue", value, performer );
            return value;
        } else if ( measurementXML instanceof FuGECommonMeasurementComplexValueType ) {
            ComplexValue value = ( ComplexValue ) entityService.createDescribable( "net.sourceforge.fuge.common.measurement.ComplexValue" );

            value = ( ComplexValue ) unmarshalDefaultValue( measurementXML, value, performer );

            // get complex value attributes
            value = ccv.unmarshal( ( FuGECommonMeasurementComplexValueType ) measurementXML, value, performer );
            DatabaseObjectHelper.save( "net.sourceforge.fuge.common.measurement.ComplexValue", value, performer );
            return value;
        } else if ( measurementXML instanceof FuGECommonMeasurementRangeType ) {
            Range value = ( Range ) entityService.createDescribable( "net.sourceforge.fuge.common.measurement.Range" );

            value = ( Range ) unmarshalDefaultValue( measurementXML, value, performer );

            // get range attributes
            value = cr.unmarshal( ( FuGECommonMeasurementRangeType ) measurementXML, value, performer );
            DatabaseObjectHelper.save( "net.sourceforge.fuge.common.measurement.Range", value, performer );
            return value;
        }
        return null; // shouldn't get here as there is currently only these types of Default Values allowed.
    }

    private Measurement unmarshalDefaultValue( FuGECommonMeasurementMeasurementType measurementXML, Measurement measurement, Person performer ) {

        // get default value attributes
        measurement = ( Measurement ) cd.unmarshal( measurementXML, measurement, performer );

        if ( measurementXML.getDataType() != null ) {
            measurement.setDataType( ( OntologyTerm ) entityService.getIdentifiable( measurementXML.getDataType().getOntologyTermRef() ) );
        }
        if ( measurementXML.getUnit() != null ) {
            measurement.setUnit( ( OntologyTerm ) entityService.getIdentifiable( measurementXML.getUnit().getOntologyTermRef() ) );
        }

        return measurement;
    }

    private FuGECommonMeasurementMeasurementType marshalDefaultValue( FuGECommonMeasurementMeasurementType measurementXML, Measurement measurement ) {

        // get default value attributes
        measurementXML = ( FuGECommonMeasurementMeasurementType ) cd.marshal( measurementXML, measurement );

        if ( measurement.getDataType() != null ) {
            FuGECommonMeasurementMeasurementType.DataType dt = new FuGECommonMeasurementMeasurementType.DataType();
            dt.setOntologyTermRef( measurement.getDataType().getIdentifier() );
            measurementXML.setDataType( dt );
        }

        if ( measurement.getUnit() != null ) {
            FuGECommonMeasurementMeasurementType.Unit unit = new FuGECommonMeasurementMeasurementType.Unit();
            unit.setOntologyTermRef( measurement.getUnit().getIdentifier() );
            measurementXML.setUnit( unit );
        }

        return measurementXML;
    }

    // measurementXML currently unused.
    public FuGECommonMeasurementMeasurementType marshal( FuGECommonMeasurementMeasurementType measurementXML, Measurement measurement ) {

        if ( measurement instanceof AtomicValue ) {
            FuGECommonMeasurementAtomicValueType valueXML = new FuGECommonMeasurementAtomicValueType();

            valueXML = ( FuGECommonMeasurementAtomicValueType ) marshalDefaultValue( valueXML, measurement );

            // get atomic value attributes
            valueXML = cav.marshal( valueXML, ( AtomicValue ) measurement );
            return ( valueXML );
        } else if ( measurement instanceof BooleanValue ) {
            FuGECommonMeasurementBooleanValueType valueXML = new FuGECommonMeasurementBooleanValueType();

            valueXML = ( FuGECommonMeasurementBooleanValueType ) marshalDefaultValue( valueXML, measurement );

            // get boolean value attributes
            valueXML = cbv.marshal( valueXML, ( BooleanValue ) measurement );
            return ( valueXML );
        } else if ( measurement instanceof ComplexValue ) {
            FuGECommonMeasurementComplexValueType valueXML = new FuGECommonMeasurementComplexValueType();

            valueXML = ( FuGECommonMeasurementComplexValueType ) marshalDefaultValue( valueXML, measurement );

            // get complex value attributes
            valueXML = ccv.marshal( valueXML, ( ComplexValue ) measurement );
            return ( valueXML );
        } else if ( measurement instanceof Range ) {
            FuGECommonMeasurementRangeType valueXML = new FuGECommonMeasurementRangeType();

            valueXML = ( FuGECommonMeasurementRangeType ) marshalDefaultValue( valueXML, measurement );

            // get complex value attributes
            valueXML = cr.marshal( valueXML, ( Range ) measurement );
            return ( valueXML );
        }
        return null; // shouldn't get here as there is currently only these types of Default Values allowed.
    }
}
