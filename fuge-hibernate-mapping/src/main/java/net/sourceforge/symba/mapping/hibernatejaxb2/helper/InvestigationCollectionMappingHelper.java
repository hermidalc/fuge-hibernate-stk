package net.sourceforge.symba.mapping.hibernatejaxb2.helper;


import net.sourceforge.fuge.bio.data.DataPartition;
import net.sourceforge.fuge.bio.investigation.Factor;
import net.sourceforge.fuge.bio.investigation.FactorValue;
import net.sourceforge.fuge.bio.investigation.Investigation;
import net.sourceforge.fuge.collection.InvestigationCollection;
import net.sourceforge.fuge.common.ontology.OntologyTerm;
import net.sourceforge.fuge.common.measurement.Measurement;
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
public class InvestigationCollectionMappingHelper implements MappingHelper<InvestigationCollection, FuGECollectionInvestigationCollectionType> {
    private final int NUMBER_ELEMENTS = 2;
    private final IdentifiableMappingHelper ci;
    private final DescribableMappingHelper cd;

    public InvestigationCollectionMappingHelper() {
        this.ci = new IdentifiableMappingHelper();
        this.cd = ci.getCisbanDescribableHelper();
    }

    // todo investigation incomplete
    public InvestigationCollection unmarshal(
            FuGECollectionInvestigationCollectionType investigationCollectionXML, InvestigationCollection investigationCollection, Person performer )
            throws EntityServiceException {

        investigationCollection = ( InvestigationCollection ) cd.unmarshal(
                investigationCollectionXML, investigationCollection, performer );

        Set<Factor> factors = new HashSet<Factor>();
        for ( FuGEBioInvestigationFactorType factorXML : investigationCollectionXML.getFactor() ) {

            // Retrieve from the database or create a new local instance.
            Factor factor = ( Factor ) DatabaseObjectHelper.getOrCreate(
                    factorXML.getIdentifier(),
                    factorXML.getName(),
                    "net.sourceforge.fuge.bio.investigation.Factor" );

            factor = ( Factor ) ci.unmarshal( factorXML, factor, performer );

            // set the non-identifiable traits

            factor.setFactorType( ( OntologyTerm ) entityService.getIdentifiable( factorXML.getFactorType().getOntologyTermRef() ) );

            Set<FactorValue> factorValues = new HashSet<FactorValue>();
            MeasurementMappingHelper mmh = new MeasurementMappingHelper();
            for ( FuGEBioInvestigationFactorValueType factorValueXML : factorXML.getFactorValue() ) {
                FactorValue factorValue = ( FactorValue ) entityService.createDescribable(
                        "net.sourceforge.fuge.bio.investigation.FactorValue" );
                factorValue = ( FactorValue ) cd.unmarshal( factorValueXML, factorValue, performer );                               
                Measurement measurement = mmh.unmarshal( factorValueXML.getMeasurement().getValue(), null, performer );

                factorValue.setValue( measurement );

                Set<DataPartition> dataPartitions = new HashSet<DataPartition>();
                for ( FuGEBioInvestigationFactorValueType.DataPartitions dataPartitionXML : factorValueXML.getDataPartitions() ) {
                    // todo - if not here, where does the dta partition sit?
                    DataPartition dataPartition = ( DataPartition ) entityService.getIdentifiable( dataPartitionXML.getDataPartitionRef() );
                    // todo: what happened to the partitioned data?
//                    dataPartition.setPartitionedData( ( Data ) entityService.getIdentifiable( dataPartitionXML.getDataPartitionRef() ) );
                    // todo not sure how to set the dimension element here. can't get it from dataPartitionXML
                    dataPartitions.add( dataPartition );
                }
                factorValue.setDataPartitions( dataPartitions );
                entityService.save( "net.sourceforge.fuge.bio.investigation.FactorValue", factorValue, performer );
                factorValues.add( factorValue );
            }
            factor.setFactorValues( factorValues );

            // load fuge object into database
            entityService.save( "net.sourceforge.fuge.bio.investigation.Factor", factor, performer );
            factors.add( factor );
        }
        investigationCollection.setFactorCollection( factors );

        Set<Investigation> investigations = new HashSet<Investigation>();
        for ( FuGEBioInvestigationInvestigationType investigationXML : investigationCollectionXML.getInvestigation() ) {
            Investigation investigation = ( Investigation ) DatabaseObjectHelper.getOrCreate(
                    investigationXML.getIdentifier(),
                    investigationXML.getName(),
                    "net.sourceforge.fuge.bio.investigation.Investigation" );
            investigation = ( Investigation ) ci.unmarshal( investigationXML, investigation, performer );
            // load fuge object into database
            entityService.save( "net.sourceforge.fuge.bio.investigation.Investigation", investigation, performer );
            investigations.add( investigation );
        }
        investigationCollection.setInvestigations( investigations );

        entityService.save( "net.sourceforge.fuge.collection.InvestigationCollection", investigationCollection, performer );
        return investigationCollection;
    }

    // todo investigation incomplete
    public FuGECollectionInvestigationCollectionType marshal( FuGECollectionInvestigationCollectionType investigationCollectionXML, InvestigationCollection investigationCollection )
            throws EntityServiceException {

        // set describable information
        investigationCollectionXML = ( FuGECollectionInvestigationCollectionType ) cd.marshal(
                investigationCollectionXML, investigationCollection );

        for ( Factor factor : investigationCollection.getFactorCollection() ) {
            FuGEBioInvestigationFactorType factorXML = new FuGEBioInvestigationFactorType();

            factorXML = ( FuGEBioInvestigationFactorType ) ci.marshal( factorXML, factor );

            // set the non-identifiable traits

            FuGEBioInvestigationFactorType.FactorType categoryValueXML = new FuGEBioInvestigationFactorType.FactorType();
            categoryValueXML.setOntologyTermRef( factor.getFactorType().getIdentifier() );
            factorXML.setFactorType( categoryValueXML );

            MeasurementMappingHelper measurementMappingHelper = new MeasurementMappingHelper();
            ObjectFactory factory = new ObjectFactory();
            for ( FactorValue factorValue : factor.getFactorValues() ) {
                FuGEBioInvestigationFactorValueType factorValueXML = new FuGEBioInvestigationFactorValueType();

                factorValueXML = ( FuGEBioInvestigationFactorValueType ) cd.marshal(
                        factorValueXML, factorValue );
                // first argument isn't used, so can pass null
                FuGECommonMeasurementMeasurementType measurementXML = measurementMappingHelper.marshal( null, factorValue.getValue() );
                // todo make this next bit more elegant
                if ( measurementXML instanceof FuGECommonMeasurementAtomicValueType ) {
                    factorValueXML.setMeasurement( ( new ObjectFactory() ).createAtomicValue( ( FuGECommonMeasurementAtomicValueType ) measurementXML ) );
                } else if ( measurementXML instanceof FuGECommonMeasurementBooleanValueType ) {
                    factorValueXML.setMeasurement( ( new ObjectFactory() ).createBooleanValue( ( FuGECommonMeasurementBooleanValueType ) measurementXML ) );
                } else if ( measurementXML instanceof FuGECommonMeasurementComplexValueType ) {
                    factorValueXML.setMeasurement( ( new ObjectFactory() ).createComplexValue( ( FuGECommonMeasurementComplexValueType ) measurementXML ) );
                } else if ( measurementXML instanceof FuGECommonMeasurementRangeType ) {
                    factorValueXML.setMeasurement( ( new ObjectFactory() ).createRange( ( FuGECommonMeasurementRangeType ) measurementXML ) );
                }

//                for ( DataPartition dataPartition : factorValue.getDataPartitions() ) {
//                    FuGEBioInvestigationFactorValueType.DataPartitions dataPartitionXML = new FuGEBioInvestigationFactorValueType.DataPartitions();
//                    dataPartitionXML.setDataPartitionRef( dataPartition.getPartitionedData().getIdentifier() );
                // todo not sure how to set the dimension element here. can't get it from dataPartitionXML
//                    factorValueXML.getDataPartitions().add( dataPartitionXML );
//                }
                factorXML.getFactorValue().add( factorValueXML );
            }

            investigationCollectionXML.getFactor().add( factorXML );
        }

        for ( Investigation investigation : investigationCollection.getInvestigations() ) {
            FuGEBioInvestigationInvestigationType investigationXML = new FuGEBioInvestigationInvestigationType();

            investigationXML = ( FuGEBioInvestigationInvestigationType ) ci.marshal(
                    investigationXML, investigation );
            investigationCollectionXML.getInvestigation().add( investigationXML );
        }

        return investigationCollectionXML;
    }

    // does not add factors, as they need to be passed a fuge object. See generateRandomXMLWithLinksOut for that code
    public FuGECollectionInvestigationCollectionType generateRandomXML( FuGECollectionInvestigationCollectionType investigationCollectionXML ) {

        investigationCollectionXML = ( FuGECollectionInvestigationCollectionType ) cd.generateRandomXML(
                investigationCollectionXML );

        for ( int i = 0; i < NUMBER_ELEMENTS; i++ ) {
            FuGEBioInvestigationInvestigationType investigationXML = new FuGEBioInvestigationInvestigationType();

            investigationXML = ( FuGEBioInvestigationInvestigationType ) ci.generateRandomXML( investigationXML );
            investigationCollectionXML.getInvestigation().add( investigationXML );
        }
        return investigationCollectionXML;
    }

    // todo investigation incomplete
    public FuGECollectionFuGEType generateRandomXMLWithLinksOut( FuGECollectionFuGEType fuGEType ) {
        FuGECollectionInvestigationCollectionType investigationCollectionXML = generateRandomXML( new FuGECollectionInvestigationCollectionType() );

        for ( int i = 0; i < NUMBER_ELEMENTS; i++ ) {
            FuGEBioInvestigationFactorType factorXML = new FuGEBioInvestigationFactorType();

            factorXML = ( FuGEBioInvestigationFactorType ) ci.generateRandomXML( factorXML );

            // set the non-identifiable traits

            if ( fuGEType.getOntologyCollection() != null ) {
                FuGEBioInvestigationFactorType.FactorType categoryValueXML = new FuGEBioInvestigationFactorType.FactorType();
                categoryValueXML.setOntologyTermRef(
                        fuGEType.getOntologyCollection().getOntologyTerm().get( i ).getValue().getIdentifier() );
                factorXML.setFactorType( categoryValueXML );
            }

            MeasurementMappingHelper measurementMappingHelper = new MeasurementMappingHelper();
            ObjectFactory factory = new ObjectFactory();
            for ( int ii = 0; ii < NUMBER_ELEMENTS; ii++ ) {
                FuGEBioInvestigationFactorValueType factorValueXML = new FuGEBioInvestigationFactorValueType();
                factorValueXML = ( FuGEBioInvestigationFactorValueType ) cd.generateRandomXML( factorValueXML );
                if ( fuGEType.getOntologyCollection() != null ) {
                    FuGECommonMeasurementBooleanValueType valueXML = ( FuGECommonMeasurementBooleanValueType )
                            measurementMappingHelper.generateRandomXMLWithLinksOut( new FuGECommonMeasurementBooleanValueType(), fuGEType );
                    factorValueXML.setMeasurement( factory.createBooleanValue( valueXML ) );
                }

// todo still not sure where datapartitions fit in, so won't make them for now.
//                for ( int iii = 0; iii < NUMBER_ELEMENTS; iii++ ) {
//                    FuGEBioInvestigationFactorValueType.DataPartitions dataPartitionXML = new FuGEBioInvestigationFactorValueType.DataPartitions();
//                    if ( fuGEType.getDataCollection() != null ) {
//                        dataPartitionXML.setDataPartitionRef( fuGEType.getDataCollection().getData().get( iii ).getValue().getIdentifier() );
//                        // todo not sure how to set the dimension element here. can't get it from dataPartitionXML
//                        factorValueXML.getDataPartitions().add( dataPartitionXML );
//                    }
//                }
                factorXML.getFactorValue().add( factorValueXML );
            }
            investigationCollectionXML.getFactor().add( factorXML );
        }

        fuGEType.setInvestigationCollection( investigationCollectionXML );
        return fuGEType;
    }
}
