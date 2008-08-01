package net.sourceforge.symba.mapping.hibernatejaxb2.helper;

import net.sourceforge.fuge.common.ontology.OntologyTerm;
import net.sourceforge.fuge.common.measurement.ComplexValue;
import net.sourceforge.fuge.common.audit.Person;
import net.sourceforge.fuge.service.EntityServiceException;
import net.sourceforge.fuge.util.generatedJAXB2.FuGECollectionFuGEType;
import net.sourceforge.fuge.util.generatedJAXB2.FuGECommonMeasurementComplexValueType;

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
public class ComplexValueMappingHelper implements MappingHelper<ComplexValue, FuGECommonMeasurementComplexValueType> {

    public ComplexValue unmarshal( FuGECommonMeasurementComplexValueType valueXML,
                                   ComplexValue value, Person performer ) throws EntityServiceException {

        // set any ComplexValue-specific traits

        // default value as an ontology term
        if ( valueXML.getValue() != null ) {
            value.setValue( ( OntologyTerm ) entityService.getIdentifiable( valueXML.getValue().getOntologyTermRef() ) );
        }


        return value;
    }

    public FuGECommonMeasurementComplexValueType marshal( FuGECommonMeasurementComplexValueType valueXML,
                                                         ComplexValue value ) {

        // set any ComplexValue-specific traits
        if ( value.getValue() != null ) {
            FuGECommonMeasurementComplexValueType.Value defaultValueXML = new FuGECommonMeasurementComplexValueType.Value();
            defaultValueXML.setOntologyTermRef( value.getValue().getIdentifier() );
            valueXML.setValue( defaultValueXML );
        }

        return valueXML;
    }

    public FuGECommonMeasurementComplexValueType generateRandomXMLWithLinksOut( FuGECommonMeasurementComplexValueType valueXML,
                                                                               FuGECollectionFuGEType frXML ) {
        if ( frXML.getOntologyCollection() != null ) {
            FuGECommonMeasurementComplexValueType.Value defaultValueXML = new FuGECommonMeasurementComplexValueType.Value();
            defaultValueXML.setOntologyTermRef(
                    frXML.getOntologyCollection().getOntologyTerm().get( 0 ).getValue().getIdentifier() );
            valueXML.setValue( defaultValueXML );
        }
        return valueXML;
    }
}
