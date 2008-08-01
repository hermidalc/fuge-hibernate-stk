package net.sourceforge.symba.mapping.hibernatejaxb2.helper;


import net.sourceforge.fuge.common.protocol.GenericSoftware;
import net.sourceforge.fuge.common.protocol.Software;
import net.sourceforge.fuge.common.audit.Person;
import net.sourceforge.fuge.service.EntityServiceException;
import net.sourceforge.fuge.util.generatedJAXB2.FuGECommonProtocolGenericSoftwareType;
import net.sourceforge.fuge.util.generatedJAXB2.FuGECommonProtocolSoftwareType;

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
public class SoftwareMappingHelper implements MappingHelper<Software, FuGECommonProtocolSoftwareType> {
    private final ParameterizableMappingHelper cparam;
    private final GenericSoftwareMappingHelper cgs;
    private final IdentifiableMappingHelper ci;

    public SoftwareMappingHelper() {
        this.ci = new IdentifiableMappingHelper();
        this.cparam = new ParameterizableMappingHelper();
        this.cgs = new GenericSoftwareMappingHelper();
    }

    /**
     * Does not perform any update to the database of the Data object (though child objects may be updated).
     *
     * @param softwareXML the jaxb object to parse
     * @param software    the hibernate object to fill
     * @param performer   the person to assign to the audit trail
     * @return the newly-filled hibernate object
     * @throws EntityServiceException if there is a problem with the connection to the database
     */
    public Software unmarshal( FuGECommonProtocolSoftwareType softwareXML, Software software, Person performer )
            throws EntityServiceException {

        // determine what sort of software it is
        if ( softwareXML instanceof FuGECommonProtocolGenericSoftwareType ) {

            GenericSoftware genericSoftware = ( GenericSoftware ) software;

            // get software attributes
            genericSoftware = ( GenericSoftware ) ci.unmarshal( softwareXML, genericSoftware, performer );
            genericSoftware = ( GenericSoftware ) cparam.unmarshal( softwareXML, genericSoftware, performer );

            if ( softwareXML.getVersion() != null ) {
                genericSoftware.setVersion( softwareXML.getVersion() );
            }

            // get generic software attributes
            genericSoftware = cgs.unmarshal(
                    ( FuGECommonProtocolGenericSoftwareType ) softwareXML, genericSoftware, performer );


            return genericSoftware;
        }
        return null; // shouldn't get here as there is currently only one type of Software allowed.
    }

    public FuGECommonProtocolSoftwareType marshal( FuGECommonProtocolSoftwareType softwareXML, Software software )
            throws EntityServiceException {

        // determine what sort of software it is
        if ( software instanceof GenericSoftware ) {

            // create fuge object
            FuGECommonProtocolGenericSoftwareType genericSoftwareXML = new FuGECommonProtocolGenericSoftwareType();

            // get generic software attributes - doing it first will greedy get all appropriate objects before continuing.
            genericSoftwareXML = cgs.marshal( genericSoftwareXML, ( GenericSoftware ) software );

            // get software attributes
            genericSoftwareXML = ( FuGECommonProtocolGenericSoftwareType ) ci.marshal(
                    genericSoftwareXML, software );
            genericSoftwareXML = ( FuGECommonProtocolGenericSoftwareType ) cparam.marshal(
                    genericSoftwareXML, software );

            if ( software.getVersion() != null ) {
                genericSoftwareXML.setVersion( software.getVersion() );
            }

            return genericSoftwareXML;
        }
        return null; // shouldn't get here as there is currently only one type of Software allowed.
    }
}
