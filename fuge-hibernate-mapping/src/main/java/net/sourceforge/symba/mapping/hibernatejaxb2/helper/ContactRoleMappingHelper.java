package net.sourceforge.symba.mapping.hibernatejaxb2.helper;

import net.sourceforge.fuge.common.audit.Contact;
import net.sourceforge.fuge.common.audit.ContactRole;
import net.sourceforge.fuge.common.audit.Person;
import net.sourceforge.fuge.common.ontology.OntologyIndividual;
import net.sourceforge.fuge.service.EntityServiceException;
import net.sourceforge.fuge.util.generatedJAXB2.FuGECommonAuditContactRoleType;

/**
 * Copyright Notice
 * <p/>
 * The MIT License
 * <p/>
 * Copyright (c) 2008 2007-8 Proteomics Standards Initiative / Microarray and Gene Expression Data Society
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * <p/>
 * Acknowledgements
 * The authors wish to thank the Proteomics Standards Initiative for
 * the provision of infrastructure and expertise in the form of the PSI
 * Document Process that has been used to formalise this document.
 * <p/>
 * $LastChangedDate$
 * $LastChangedRevision$
 * $Author$
 * $HeadURL: $
 */
public class ContactRoleMappingHelper implements MappingHelper<ContactRole, FuGECommonAuditContactRoleType> {

    private final DescribableMappingHelper cd;

    public ContactRoleMappingHelper() {
        this.cd = new DescribableMappingHelper();
    }

    /**
     * Does not perform any update to the database of the ContactRole object
     *
     * @param contactRoleXML the jaxb object to parse
     * @param contactRole    the hibernate object to fill
     * @param performer      the person to assign to the audit trail
     * @return the newly-filled hibernate object
     * @throws EntityServiceException if there is a problem with the connection to the database
     */
    public ContactRole unmarshal(
            FuGECommonAuditContactRoleType contactRoleXML, ContactRole contactRole, Person performer )
            throws EntityServiceException {

        contactRole = ( ContactRole ) cd.unmarshal( contactRoleXML, contactRole, performer );

        contactRole.setContact( ( Contact ) entityService.getIdentifiable( contactRoleXML.getContactRef() ) );

        contactRole.setRole(
                ( OntologyIndividual ) entityService.getIdentifiable( contactRoleXML.getRole().getOntologyTermRef() ) );

        return contactRole;
    }

    public FuGECommonAuditContactRoleType marshal( FuGECommonAuditContactRoleType contactRoleXML,
                                                   ContactRole contactRole )
            throws EntityServiceException {

        contactRoleXML = ( FuGECommonAuditContactRoleType ) cd.marshal( contactRoleXML, contactRole );

        contactRoleXML.setContactRef( contactRole.getContact().getIdentifier() );

        FuGECommonAuditContactRoleType.Role roleXML = new FuGECommonAuditContactRoleType.Role();
        roleXML.setOntologyTermRef( contactRole.getRole().getIdentifier() );
        contactRoleXML.setRole( roleXML );

        return contactRoleXML;
    }
}
