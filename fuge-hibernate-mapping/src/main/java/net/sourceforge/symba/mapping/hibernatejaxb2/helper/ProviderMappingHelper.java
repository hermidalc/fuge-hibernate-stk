package net.sourceforge.symba.mapping.hibernatejaxb2.helper;


import net.sourceforge.fuge.collection.Provider;
import net.sourceforge.fuge.common.audit.ContactRole;
import net.sourceforge.fuge.common.audit.Person;
import net.sourceforge.fuge.common.protocol.Software;
import net.sourceforge.fuge.service.EntityServiceException;
import net.sourceforge.fuge.util.generatedJAXB2.FuGECollectionProviderType;
import net.sourceforge.fuge.util.generatedJAXB2.FuGECommonAuditContactRoleType;
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
public class ProviderMappingHelper implements MappingHelper<Provider, FuGECollectionProviderType> {
    private final IdentifiableMappingHelper ci;
    private final ContactRoleMappingHelper ccr;

    public ProviderMappingHelper() {
        this.ci = new IdentifiableMappingHelper();
        this.ccr = new ContactRoleMappingHelper();
    }

    /**
     * Does not perform any update to the database of the Provider object (though child objects may be updated).
     *
     * @param providerXML the jaxb object to parse
     * @param provider    the hibernate object to fill
     * @param performer the person to put in the audit details
     * @return the newly-filled hibernate object
     * @throws EntityServiceException if there is a problem with the connection to the database
     */
    public Provider unmarshal( FuGECollectionProviderType providerXML, Provider provider, Person performer )
            throws EntityServiceException {

        provider = ( Provider ) ci.unmarshal( providerXML, provider, performer );

        // contact
        ContactRole contactRole = ( ContactRole ) entityService.createDescribable( "net.sourceforge.fuge.common.audit.ContactRole" );
        contactRole = ccr.unmarshal( providerXML.getContactRole(), contactRole, performer );
        DatabaseObjectHelper.save( "net.sourceforge.fuge.common.audit.ContactRole", contactRole, performer );
        provider.setProvider( contactRole );

        // provider type
        if ( providerXML.getSoftwareRef() != null ) {
            provider.setProducingSoftware( ( Software ) entityService.getIdentifiable( providerXML.getSoftwareRef() ) );
        }

        return provider;
    }

    public FuGECollectionProviderType marshal( FuGECollectionProviderType providerXML, Provider provider )
            throws EntityServiceException {

        providerXML = ( FuGECollectionProviderType ) ci.marshal( providerXML, provider );

        providerXML.setContactRole( ccr.marshal( new FuGECommonAuditContactRoleType(), provider.getProvider() ) );

        if ( provider.getProducingSoftware() != null ) {
            providerXML.setSoftwareRef( provider.getProducingSoftware().getIdentifier() );
        }

        return providerXML;
    }
}
