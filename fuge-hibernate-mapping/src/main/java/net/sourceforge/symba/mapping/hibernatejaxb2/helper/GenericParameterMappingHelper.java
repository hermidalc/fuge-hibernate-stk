package net.sourceforge.symba.mapping.hibernatejaxb2.helper;

import net.sourceforge.fuge.common.protocol.GenericParameter;
import net.sourceforge.fuge.common.ontology.OntologyTerm;
import net.sourceforge.fuge.common.audit.Person;
import net.sourceforge.fuge.service.EntityServiceException;
import net.sourceforge.fuge.util.generatedJAXB2.FuGECommonProtocolGenericParameterType;

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
public class GenericParameterMappingHelper implements MappingHelper<GenericParameter, FuGECommonProtocolGenericParameterType> {

    // fixme: Assumes XML is Master version: that EVERY object that can be loaded (i.e. isn't a reference only) is newer than the database
    public GenericParameter unmarshal( FuGECommonProtocolGenericParameterType genericParameterXML,
                                       GenericParameter genericParameter, Person performer ) throws EntityServiceException {

        // set any GenericParameter-specific traits
        if ( genericParameterXML.getParameterType() != null ) {
            genericParameter.setParameterType( ( OntologyTerm ) entityService.getIdentifiable( genericParameterXML.getParameterType().getOntologyTermRef() ) );
        }

        return genericParameter;
    }

    // This will always retrieve EXACTLY the version asked for. If you additionally wish to have the latest version,
    // you must additionally run getLatestVersion. This is so the LSID Authority can use these methods to get
    // exactly the objects asked for.
    public FuGECommonProtocolGenericParameterType marshal(
            FuGECommonProtocolGenericParameterType genericParameterXML,
            GenericParameter genericParameter ) {

        // set any GenericParameter-specific traits
        if ( genericParameter.getParameterType() != null ) {
            FuGECommonProtocolGenericParameterType.ParameterType ptXML = new FuGECommonProtocolGenericParameterType.ParameterType();
            ptXML.setOntologyTermRef( genericParameter.getParameterType().getIdentifier() );
            genericParameterXML.setParameterType( ptXML );
        }

        return genericParameterXML;
    }
}
