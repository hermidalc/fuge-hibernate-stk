package net.sourceforge.symba.mapping.hibernatejaxb2.helper;


import net.sourceforge.fuge.bio.material.GenericMaterial;
import net.sourceforge.fuge.bio.material.Material;
import net.sourceforge.fuge.collection.MaterialCollection;
import net.sourceforge.fuge.service.EntityServiceException;
import net.sourceforge.fuge.util.generatedJAXB2.*;
import net.sourceforge.fuge.common.audit.Person;
import net.sourceforge.symba.mapping.hibernatejaxb2.DatabaseObjectHelper;

import javax.xml.bind.JAXBElement;
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
public class MaterialCollectionMappingHelper implements MappingHelper<MaterialCollection, FuGECollectionMaterialCollectionType> {
    private final int NUMBER_ELEMENTS = 2;
    private final DescribableMappingHelper cd;
    private final MaterialMappingHelper cm;

    public MaterialCollectionMappingHelper() {
        this.cd = (new IdentifiableMappingHelper()).getCisbanDescribableHelper();
        this.cm = new MaterialMappingHelper();
    }

    public MaterialCollection unmarshal(
            FuGECollectionMaterialCollectionType matCollXML, MaterialCollection matColl, Person performer )
            throws EntityServiceException {

        // set describable information
        matColl = ( MaterialCollection ) cd.unmarshal( matCollXML, matColl, performer );
        matColl = unmarshalCollectionContents( matCollXML, matColl, performer );

        // load the fuge object into the database
        entityService.save( "net.sourceforge.fuge.collection.MaterialCollection", matColl, performer );

        return matColl;
    }

    public MaterialCollection unmarshalCollectionContents( FuGECollectionMaterialCollectionType matCollXML,
                                                           MaterialCollection matColl, Person performer )
            throws EntityServiceException {
        // Create collection of material for addition to the material collection
        Set<Material> materials = new HashSet<Material>();

        // set up the converter

        for ( JAXBElement<? extends FuGEBioMaterialMaterialType> materialElementXML : matCollXML.getMaterial() ) {
            FuGEBioMaterialMaterialType materialXML = materialElementXML.getValue();
            // set fuge object
            if ( materialXML instanceof FuGEBioMaterialGenericMaterialType ) {
                // Retrieve from the database or create a new local instance.
                GenericMaterial gmaterial = ( GenericMaterial ) DatabaseObjectHelper.getOrCreate(
                        materialXML.getIdentifier(),
                        materialXML.getName(),
                        "net.sourceforge.fuge.bio.material.GenericMaterial" );
                gmaterial = (GenericMaterial) cm.unmarshal( materialXML, gmaterial, performer );
                entityService.save( "net.sourceforge.fuge.bio.material.GenericMaterial", gmaterial, performer);

                materials.add( gmaterial );
            }
        }

        // Add the set of generic materials to the material collection
        matColl.setMaterials( materials );

        return matColl;
    }

    public FuGECollectionMaterialCollectionType marshal(
            FuGECollectionMaterialCollectionType matCollXML, MaterialCollection matColl )
            throws EntityServiceException {

        // set describable information
        matCollXML = ( FuGECollectionMaterialCollectionType ) cd.marshal( matCollXML, matColl );

        // set up the converter and the factory
        ObjectFactory factory = new ObjectFactory();

        for ( Material material : matColl.getMaterials() ) {
//            System.out.println(
//                    "Number of characteristics before marshaling of material: " +
//                            material.getCharacteristics().size() );

            if ( material instanceof GenericMaterial ) {
                // set jaxb object
                matCollXML.getMaterial().add(
                        factory.createGenericMaterial(
                                ( FuGEBioMaterialGenericMaterialType ) cm.marshal( new FuGEBioMaterialGenericMaterialType(), material ) ) );
            }
        }
        return matCollXML;
    }

    public FuGECollectionMaterialCollectionType generateRandomXML( FuGECollectionMaterialCollectionType matCollXML ) {

        // set describable information
        matCollXML = ( FuGECollectionMaterialCollectionType ) cd.generateRandomXML( matCollXML );

        return matCollXML;
    }

    public FuGECollectionFuGEType generateRandomXMLWithLinksOut( FuGECollectionFuGEType frXML ) {
        // create the jaxb material collection object
        FuGECollectionMaterialCollectionType matCollXML = generateRandomXML( new FuGECollectionMaterialCollectionType() );

        // set up the converter and the factory
        ObjectFactory factory = new ObjectFactory();

        for ( int i = 0; i < NUMBER_ELEMENTS; i++ ) {
            // set jaxb object
            if ( i > 0 ) {
                matCollXML.getMaterial().add(
                        factory.createGenericMaterial(
                                ( FuGEBioMaterialGenericMaterialType ) cm.generateRandomXMLWithLinksOut(
                                        frXML, ( FuGEBioMaterialGenericMaterialType ) matCollXML.getMaterial()
                                        .get( 0 )
                                        .getValue() ) ) );
            } else {
                matCollXML.getMaterial().add(
                        factory.createGenericMaterial(
                                ( FuGEBioMaterialGenericMaterialType ) cm.generateRandomXMLWithLinksOut(
                                        frXML, null ) ) );
            }
        }

        frXML.setMaterialCollection( matCollXML );
        return frXML;
    }
}
