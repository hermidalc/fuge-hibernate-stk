package net.sourceforge.symba.mapping.hibernatejaxb2.helper;

import net.sourceforge.fuge.bio.data.Data;
import net.sourceforge.fuge.bio.material.Material;
import net.sourceforge.fuge.bio.material.GenericMaterialMeasurement;
import net.sourceforge.fuge.common.protocol.GenericProtocolApplication;
import net.sourceforge.fuge.common.protocol.Protocol;
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
public class GenericProtocolApplicationMappingHelper implements MappingHelper<GenericProtocolApplication, FuGECommonProtocolGenericProtocolApplicationType> {

    // @todo GenMatMeas has not been implemented, and there seems to be a problem somewhere in uml OR xsd such that the inputmaterials aren't properly working
    public GenericProtocolApplication unmarshal(
            FuGECommonProtocolGenericProtocolApplicationType genericProtocolApplicationXML,
            GenericProtocolApplication genericProtocolApplication, Person performer ) throws EntityServiceException {

        // set any GenericProtocolApplication-specific traits

        genericProtocolApplication.setProtocol(
                ( Protocol ) entityService.getIdentifiable(
                        genericProtocolApplicationXML.getProtocolRef() ) );

        // input data
        Set<Data> datas = new HashSet<Data>();
        for ( FuGECommonProtocolGenericProtocolApplicationType.InputData gidXML : genericProtocolApplicationXML
                .getInputData() ) {
            datas.add( ( Data ) entityService.getIdentifiable( gidXML.getDataRef() ) );
        }
        genericProtocolApplication.setInputData( datas );

        // output data
        Set<Data> outdatas = new HashSet<Data>();
        for ( FuGECommonProtocolGenericProtocolApplicationType.OutputData godXML : genericProtocolApplicationXML
                .getOutputData() ) {
            outdatas.add( ( Data ) entityService.getIdentifiable( godXML.getDataRef() ) );
        }
        genericProtocolApplication.setOutputData( outdatas );

        // input complete material
        Set<Material> icmaterials = new HashSet<Material>();
        for ( FuGECommonProtocolGenericProtocolApplicationType.InputCompleteMaterials gicmXML : genericProtocolApplicationXML
                .getInputCompleteMaterials() ) {
            icmaterials.add( ( Material ) entityService.getIdentifiable( gicmXML.getMaterialRef() ) );
        }
        genericProtocolApplication.setInputCompleteMaterials( icmaterials );

        // input material (generic material measurement)
        Set<GenericMaterialMeasurement> gmms = new HashSet<GenericMaterialMeasurement>();
        DescribableMappingHelper dmh = new DescribableMappingHelper();
        for ( FuGEBioMaterialGenericMaterialMeasurementType gmmXML : genericProtocolApplicationXML
                .getGenericMaterialMeasurement() ) {
            GenericMaterialMeasurement gmm = ( GenericMaterialMeasurement ) entityService
                    .createDescribable( "net.sourceforge.fuge.bio.material.GenericMaterialMeasurement" );
            gmm = ( GenericMaterialMeasurement ) dmh.unmarshal( gmmXML, gmm, performer );
            gmm.setMeasuredMaterial( ( Material ) entityService.getIdentifiable( gmmXML.getMaterialRef() ) );
            if ( gmmXML.getMeasurement() != null ) {
                MeasurementMappingHelper mmh = new MeasurementMappingHelper();
                gmm.setMeasurement( mmh.unmarshal( gmmXML.getMeasurement().getValue(), null, performer ) );
            }
            DatabaseObjectHelper.save( "net.sourceforge.fuge.bio.material.GenericMaterialMeasurement", gmm, performer );
            gmms.add( gmm );
        }
        genericProtocolApplication.setInputMaterials( gmms );

        // output material
        Set<Material> materials = new HashSet<Material>();
        for ( FuGECommonProtocolGenericProtocolApplicationType.OutputMaterials gomXML : genericProtocolApplicationXML
                .getOutputMaterials() ) {
            materials.add( ( Material ) entityService.getIdentifiable( gomXML.getMaterialRef() ) );
        }
        genericProtocolApplication.setOutputMaterials( materials );

        return genericProtocolApplication;
    }

    // @todo GenMatMeas has not been implemented
    public FuGECommonProtocolGenericProtocolApplicationType marshal(
            FuGECommonProtocolGenericProtocolApplicationType genericProtocolApplicationXML,
            GenericProtocolApplication genericProtocolApplication ) throws EntityServiceException {

        // protocol ref
        genericProtocolApplicationXML.setProtocolRef( genericProtocolApplication.getProtocol().getIdentifier() );

        // input data
        for ( Data data : genericProtocolApplication.getInputData() ) {
            FuGECommonProtocolGenericProtocolApplicationType.InputData idXML = new FuGECommonProtocolGenericProtocolApplicationType.InputData();
            idXML.setDataRef( data.getIdentifier() );
            genericProtocolApplicationXML.getInputData().add( idXML );
        }

        // output data
        for ( Data data : genericProtocolApplication.getOutputData() ) {
            FuGECommonProtocolGenericProtocolApplicationType.OutputData godXML = new FuGECommonProtocolGenericProtocolApplicationType.OutputData();
            godXML.setDataRef( data.getIdentifier() );
            genericProtocolApplicationXML.getOutputData().add( godXML );
        }

        // input complete material
        for ( Material material : genericProtocolApplication.getInputCompleteMaterials() ) {
            FuGECommonProtocolGenericProtocolApplicationType.InputCompleteMaterials gicmXML = new FuGECommonProtocolGenericProtocolApplicationType.InputCompleteMaterials();
            gicmXML.setMaterialRef( material.getIdentifier() );
            genericProtocolApplicationXML.getInputCompleteMaterials().add( gicmXML );
        }

        // input material (generic material measurement)
        DescribableMappingHelper dmh = new DescribableMappingHelper();
        MeasurementMappingHelper mmh = new MeasurementMappingHelper();
        for ( GenericMaterialMeasurement gmm : genericProtocolApplication.getInputMaterials() ) {
            FuGEBioMaterialGenericMaterialMeasurementType gmmXML = new FuGEBioMaterialGenericMaterialMeasurementType();
            gmmXML = ( FuGEBioMaterialGenericMaterialMeasurementType ) dmh.marshal( gmmXML, gmm );
            gmmXML.setMaterialRef( gmm.getMeasuredMaterial().getIdentifier() );
            if ( gmm.getMeasurement() != null ) {
                FuGECommonMeasurementMeasurementType measurementXML = mmh.marshal( null, gmm.getMeasurement() );
                if ( measurementXML instanceof FuGECommonMeasurementAtomicValueType ) {
                    gmmXML.setMeasurement( ( new ObjectFactory() ).createAtomicValue(
                            ( FuGECommonMeasurementAtomicValueType ) measurementXML ) );
                } else if ( measurementXML instanceof FuGECommonMeasurementBooleanValueType ) {
                    gmmXML.setMeasurement( ( new ObjectFactory() ).createBooleanValue(
                            ( FuGECommonMeasurementBooleanValueType ) measurementXML ) );
                } else if ( measurementXML instanceof FuGECommonMeasurementComplexValueType ) {
                    gmmXML.setMeasurement( ( new ObjectFactory() ).createComplexValue(
                            ( FuGECommonMeasurementComplexValueType ) measurementXML ) );
                } else if ( measurementXML instanceof FuGECommonMeasurementRangeType ) {
                    gmmXML.setMeasurement(
                            ( new ObjectFactory() ).createRange( ( FuGECommonMeasurementRangeType ) measurementXML ) );
                }
            }
            genericProtocolApplicationXML.getGenericMaterialMeasurement().add( gmmXML );
        }

        // output material
        for ( Material material : genericProtocolApplication.getOutputMaterials() ) {
            FuGECommonProtocolGenericProtocolApplicationType.OutputMaterials godXML = new FuGECommonProtocolGenericProtocolApplicationType.OutputMaterials();
            godXML.setMaterialRef( material.getIdentifier() );
            genericProtocolApplicationXML.getOutputMaterials().add( godXML );
        }

        return genericProtocolApplicationXML;
    }
}
