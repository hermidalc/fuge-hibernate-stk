package net.sourceforge.symba.mapping.hibernatejaxb2.helper;

import net.sourceforge.fuge.common.audit.ContactRole;
import net.sourceforge.fuge.common.audit.Person;
import net.sourceforge.fuge.common.ontology.OntologyTerm;
import net.sourceforge.fuge.common.protocol.Parameterizable;
import net.sourceforge.fuge.service.EntityServiceException;
import net.sourceforge.fuge.util.generatedJAXB2.FuGECommonAuditContactRoleType;
import net.sourceforge.fuge.util.generatedJAXB2.FuGECommonProtocolParameterizableType;
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
public class ParameterizableMappingHelper implements MappingHelper<Parameterizable, FuGECommonProtocolParameterizableType> {
    private final ContactRoleMappingHelper ccr;

    public ParameterizableMappingHelper() {
        this.ccr = new ContactRoleMappingHelper();
    }

    public Parameterizable unmarshal( FuGECommonProtocolParameterizableType parameterizableXML,
                                      Parameterizable parameterizable, Person performer )
            throws EntityServiceException {

        // contacts
        Set<ContactRole> contactRoles = new HashSet<ContactRole>();

        for ( FuGECommonAuditContactRoleType contactRoleXML : parameterizableXML.getContactRole() ) {
            ContactRole cr = ccr.unmarshal( contactRoleXML, ( ContactRole ) entityService.createDescribable( "net.sourceforge.fuge.common.audit.ContactRole" ), performer );
            contactRoles.add( ( ContactRole ) DatabaseObjectHelper.save( "net.sourceforge.fuge.common.audit.ContactRole", cr, performer ) );
        }
        parameterizable.setProvider( contactRoles );

        // parameterizable types
        if ( parameterizableXML.getTypes() != null ) {
            Set<OntologyTerm> pts = new HashSet<OntologyTerm>();
            for ( FuGECommonProtocolParameterizableType.Types parameterizableTypesXML : parameterizableXML.getTypes() ) {
                pts.add( ( OntologyTerm ) entityService.getIdentifiable( parameterizableTypesXML.getOntologyTermRef() ) );
            }
            parameterizable.setTypes( pts );
        }

        return parameterizable;
    }

    public FuGECommonProtocolParameterizableType marshal(
            FuGECommonProtocolParameterizableType parameterizableXML, Parameterizable parameterizable )
            throws EntityServiceException {

        // get any lazily loaded objects
//        parameterizable = ( Parameterizable ) entityService.greedyGet( parameterizable );

        for ( ContactRole contactRole : parameterizable.getProvider() ) {
            parameterizableXML.getContactRole().add( ccr.marshal( new FuGECommonAuditContactRoleType(), contactRole ) );
        }

        for ( OntologyTerm ontologyTerm : parameterizable.getTypes() ) {

            FuGECommonProtocolParameterizableType.Types parameterizableTypesXML = new FuGECommonProtocolParameterizableType.Types();
            parameterizableTypesXML.setOntologyTermRef( ontologyTerm.getIdentifier() );
            parameterizableXML.getTypes().add( parameterizableTypesXML );
        }
        return parameterizableXML;
    }
}
