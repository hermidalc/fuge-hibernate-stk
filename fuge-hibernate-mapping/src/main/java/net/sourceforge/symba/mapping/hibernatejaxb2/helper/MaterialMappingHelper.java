package net.sourceforge.symba.mapping.hibernatejaxb2.helper;


import net.sourceforge.fuge.bio.material.GenericMaterial;
import net.sourceforge.fuge.bio.material.Material;
import net.sourceforge.fuge.common.audit.ContactRole;
import net.sourceforge.fuge.common.audit.Person;
import net.sourceforge.fuge.common.ontology.OntologyTerm;
import net.sourceforge.fuge.service.EntityServiceException;
import net.sourceforge.fuge.util.generatedJAXB2.FuGEBioMaterialGenericMaterialType;
import net.sourceforge.fuge.util.generatedJAXB2.FuGEBioMaterialMaterialType;
import net.sourceforge.fuge.util.generatedJAXB2.FuGECollectionFuGEType;
import net.sourceforge.fuge.util.generatedJAXB2.FuGECommonAuditContactRoleType;

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
public class MaterialMappingHelper implements MappingHelper<Material, FuGEBioMaterialMaterialType> {
    private final int NUMBER_ELEMENTS = 2;
    private final IdentifiableMappingHelper ci;
    private final GenericMaterialMappingHelper cgm;
    private final ContactRoleMappingHelper ccr;

    public MaterialMappingHelper() {
        this.ci = new IdentifiableMappingHelper();
        this.cgm = new GenericMaterialMappingHelper();
        this.ccr = new ContactRoleMappingHelper();
    }

    /**
     * Does not perform any update to the database of the Data object (though child objects may be updated).
     *
     * @param materialXML the jaxb object to parse
     * @param material    the hibernate object to fill
     * @param performer   the person to add to the audit information
     * @return the newly-filled hibernate object
     */
    public Material unmarshal( FuGEBioMaterialMaterialType materialXML, Material material, Person performer ) {

        // determine what sort of material it is
        if ( materialXML instanceof FuGEBioMaterialGenericMaterialType ) {

            GenericMaterial gmaterial = ( GenericMaterial ) material;

            // set the material attributes.
            gmaterial = ( GenericMaterial ) unmarshalMaterialSpecific( materialXML, gmaterial, performer );

            // set the generic material attributes
            gmaterial = cgm.unmarshal( ( FuGEBioMaterialGenericMaterialType ) materialXML, gmaterial, performer );

            return gmaterial;
        }
        return null; // shouldn't get here as there is currently only one type of Material allowed.
    }

    private Material unmarshalMaterialSpecific( FuGEBioMaterialMaterialType materialXML,
                                                Material material, Person performer )
            throws EntityServiceException {

        material = ( Material ) ci.unmarshal( materialXML, material, performer );

        // contacts
        Set<ContactRole> contactRoles = new HashSet<ContactRole>();
        for ( FuGECommonAuditContactRoleType contactRoleXML : materialXML.getContactRole() ) {
            ContactRole cr = ccr.unmarshal( contactRoleXML, ( ContactRole ) entityService.createDescribable( "net.sourceforge.fuge.common.audit.ContactRole" ), performer );
            contactRoles.add( ( ContactRole ) entityService.save( "net.sourceforge.fuge.common.audit.ContactRole", cr, performer ) );
        }
        material.setContacts( contactRoles );

        // material type
        if ( materialXML.getMaterialType() != null ) {
            material.setMaterialType( ( OntologyTerm ) entityService.getIdentifiable( materialXML.getMaterialType().getOntologyTermRef() ) );
        }

        // characteristics
        Set<OntologyTerm> characteristics = new HashSet<OntologyTerm>();
        for ( FuGEBioMaterialMaterialType.Characteristics characteristicXML : materialXML.getCharacteristics() ) {
            characteristics.add( ( OntologyTerm ) entityService.getIdentifiable( characteristicXML.getOntologyTermRef() ) );
        }
        material.setCharacteristics( characteristics );

        // quality control statistics
        Set<OntologyTerm> qcs = new HashSet<OntologyTerm>();
        for ( net.sourceforge.fuge.util.generatedJAXB2.FuGEBioMaterialMaterialType.QualityControlStatistics qcXML : materialXML.getQualityControlStatistics() ) {
            qcs.add( ( OntologyTerm ) entityService.getIdentifiable( qcXML.getOntologyTermRef() ) );
        }
        material.setQualityControlStatistics( qcs );

        return material;
    }

    public FuGEBioMaterialMaterialType marshal( FuGEBioMaterialMaterialType materialXML, Material material )
            throws EntityServiceException {

        // determine what sort of material it is
        if ( material instanceof GenericMaterial ) {

            // create fuge object
            FuGEBioMaterialGenericMaterialType genericMaterialXML = ( FuGEBioMaterialGenericMaterialType ) materialXML;

            // set the material attributes
            genericMaterialXML = ( FuGEBioMaterialGenericMaterialType ) marshalMaterialSpecific(
                    genericMaterialXML, material );

            // set the generic material attributes
            genericMaterialXML = cgm.marshal( genericMaterialXML, ( GenericMaterial ) material );

            return genericMaterialXML;
        }
        return null;  // shouldn't get here as there is currently only one type of Material allowed.
    }

    public FuGEBioMaterialMaterialType generateRandomXML( FuGEBioMaterialMaterialType materialXML ) {
        return generateRandomXML( materialXML, null );
    }

    public FuGEBioMaterialMaterialType generateRandomXML( FuGEBioMaterialMaterialType materialXML,
                                                          FuGEBioMaterialGenericMaterialType genXML ) {

        materialXML = ( FuGEBioMaterialMaterialType ) ci.generateRandomXML( materialXML );

        // set the generic material attributes
        if ( genXML == null ) {
            materialXML = cgm.generateRandomXML( ( FuGEBioMaterialGenericMaterialType ) materialXML );
        } else {
            materialXML = cgm.generateRandomXMLWithComponents( ( FuGEBioMaterialGenericMaterialType ) materialXML, genXML );
        }
        return materialXML;
    }

    // this can only be run if you can pass it an entire fuge object
    public FuGEBioMaterialMaterialType generateRandomXMLWithLinksOut( FuGECollectionFuGEType frXML,
                                                                      FuGEBioMaterialGenericMaterialType genXML ) {

        // create fuge object
        FuGEBioMaterialGenericMaterialType genericMaterialXML = ( FuGEBioMaterialGenericMaterialType ) generateRandomXML( new FuGEBioMaterialGenericMaterialType(), genXML );

        // set the material attributes
        genericMaterialXML = ( FuGEBioMaterialGenericMaterialType ) generateRandomSpecificXML(
                genericMaterialXML, frXML );

        return genericMaterialXML;
    }

    // This should be run at a time where the ontology collection and audit collection have already been run.
    private FuGEBioMaterialMaterialType generateRandomSpecificXML( FuGEBioMaterialMaterialType materialXML,
                                                                   FuGECollectionFuGEType frXML ) {

        for ( int i = 0; i < NUMBER_ELEMENTS; i++ ) {
            materialXML.getContactRole().add( ccr.generateRandomXMLwithLinksOut( frXML ) );
        }

        if ( frXML.getOntologyCollection() != null ) {
            FuGEBioMaterialMaterialType.MaterialType materialTypeXML = new FuGEBioMaterialMaterialType.MaterialType();
            materialTypeXML.setOntologyTermRef(
                    frXML.getOntologyCollection().getOntologyTerm().get( 0 ).getValue().getIdentifier() );
            materialXML.setMaterialType( materialTypeXML );

            for ( int i = 0; i < NUMBER_ELEMENTS; i++ ) {
                FuGEBioMaterialMaterialType.Characteristics characteristicXML = new FuGEBioMaterialMaterialType.Characteristics();
                characteristicXML.setOntologyTermRef(
                        frXML.getOntologyCollection().getOntologyTerm().get( i ).getValue().getIdentifier() );
                materialXML.getCharacteristics().add( characteristicXML );
            }

            for ( int i = 0; i < NUMBER_ELEMENTS; i++ ) {
                FuGEBioMaterialMaterialType.QualityControlStatistics qcsXML = new FuGEBioMaterialMaterialType.QualityControlStatistics();
                qcsXML.setOntologyTermRef(
                        frXML.getOntologyCollection().getOntologyTerm().get( i ).getValue().getIdentifier() );
                materialXML.getQualityControlStatistics().add( qcsXML );
            }
        }
        return materialXML;
    }

    private FuGEBioMaterialMaterialType marshalMaterialSpecific( FuGEBioMaterialMaterialType materialXML,
                                                                 Material material )
            throws EntityServiceException {

        materialXML = ( FuGEBioMaterialMaterialType ) ci.marshal( materialXML, material );

        for ( ContactRole contactRole : material.getContacts() ) {
            materialXML.getContactRole().add( ccr.marshal( new FuGECommonAuditContactRoleType(), contactRole ) );
        }

        if ( material.getMaterialType() != null ) {
            FuGEBioMaterialMaterialType.MaterialType materialTypeXML = new FuGEBioMaterialMaterialType.MaterialType();
            materialTypeXML.setOntologyTermRef( material.getMaterialType().getIdentifier() );
            materialXML.setMaterialType( materialTypeXML );
        }

//        System.out.println( "Number of characteristics: " + material.getCharacteristics().size() );
        for ( OntologyTerm characteristic : material.getCharacteristics() ) {

            FuGEBioMaterialMaterialType.Characteristics characteristicXML = new FuGEBioMaterialMaterialType.Characteristics();
            characteristicXML.setOntologyTermRef( characteristic.getIdentifier() );
            materialXML.getCharacteristics().add( characteristicXML );
        }

        for ( OntologyTerm qcs : material.getQualityControlStatistics() ) {

            FuGEBioMaterialMaterialType.QualityControlStatistics qcsXML = new FuGEBioMaterialMaterialType.QualityControlStatistics();
            qcsXML.setOntologyTermRef( qcs.getIdentifier() );
            materialXML.getQualityControlStatistics().add( qcsXML );
        }
        return materialXML;
    }

}
