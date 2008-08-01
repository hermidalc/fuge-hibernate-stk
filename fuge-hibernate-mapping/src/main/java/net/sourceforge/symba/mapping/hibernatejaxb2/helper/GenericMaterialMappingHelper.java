package net.sourceforge.symba.mapping.hibernatejaxb2.helper;

import net.sourceforge.fuge.bio.material.GenericMaterial;
import net.sourceforge.fuge.service.EntityServiceException;
import net.sourceforge.fuge.util.generatedJAXB2.FuGEBioMaterialGenericMaterialType;
import net.sourceforge.fuge.common.audit.Person;

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
public class GenericMaterialMappingHelper implements MappingHelper<GenericMaterial, FuGEBioMaterialGenericMaterialType> {

    // @todo assumes all Collections (ReferenceableCollection, AuditCollection etc) are already extant in the database
    public GenericMaterial unmarshal( FuGEBioMaterialGenericMaterialType genericMaterialXML,
                                      GenericMaterial genericMaterial, Person performer ) throws EntityServiceException {

        // set only GenericMaterial-specific traits

        if ( !genericMaterialXML.getComponents().isEmpty() ) {
            // Components. These elements are references to GenericMaterial.
            Set<GenericMaterial> components = new HashSet<GenericMaterial>();
            for ( FuGEBioMaterialGenericMaterialType.Components componentXML : genericMaterialXML.getComponents() ) {
                components.add( ( GenericMaterial ) entityService.getIdentifiable( componentXML.getGenericMaterialRef() ) );
            }

            genericMaterial.setComponents( components );
        }

        return genericMaterial;
    }

    public FuGEBioMaterialGenericMaterialType marshal(
            FuGEBioMaterialGenericMaterialType genericMaterialXML, GenericMaterial genericMaterial )
             {

        // set only GenericMaterial-specific traits

        // Components. These elements are references to GenericMaterial.
        for ( GenericMaterial component : genericMaterial.getComponents() ) {

            FuGEBioMaterialGenericMaterialType.Components componentsXML = new FuGEBioMaterialGenericMaterialType.Components();
            componentsXML.setGenericMaterialRef( component.getIdentifier() );
            genericMaterialXML.getComponents().add( componentsXML );
        }

        return genericMaterialXML;
    }
}
