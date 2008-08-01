package net.sourceforge.symba.mapping.hibernatejaxb2.helper;


import net.sourceforge.fuge.common.protocol.GenericAction;
import net.sourceforge.fuge.common.protocol.GenericParameter;
import net.sourceforge.fuge.common.protocol.Protocol;
import net.sourceforge.fuge.common.ontology.OntologyTerm;
import net.sourceforge.fuge.common.audit.Person;
import net.sourceforge.fuge.service.EntityServiceException;
import net.sourceforge.fuge.util.generatedJAXB2.FuGECommonProtocolGenericActionType;
import net.sourceforge.fuge.util.generatedJAXB2.FuGECommonProtocolGenericParameterType;
import net.sourceforge.symba.mapping.hibernatejaxb2.DatabaseObjectHelper;

import java.io.PrintStream;
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
public class GenericActionMappingHelper implements MappingHelper<GenericAction, FuGECommonProtocolGenericActionType> {
    private final ParameterMappingHelper cp;

    public GenericActionMappingHelper() {
        this.cp = new ParameterMappingHelper();
    }

    public GenericAction unmarshal( FuGECommonProtocolGenericActionType genericActionXML,
                                    GenericAction genericAction, Person performer )
            throws EntityServiceException {

        // set any GenericAction-specific traits

        // action term
        if ( genericActionXML.getActionTerm() != null ) {
            genericAction.setActionTerm(
                    ( OntologyTerm ) entityService
                            .getIdentifiable( genericActionXML.getActionTerm().getOntologyTermRef() ) );
        }

        // action text
        if ( genericActionXML.getActionText() != null ) {
            genericAction.setActionText( genericActionXML.getActionText() );
        }

        // protocol ref
        if ( genericActionXML.getProtocolRef() != null ) {
            genericAction
                    .setChildProtocol( ( Protocol ) entityService.getIdentifiable( genericActionXML.getProtocolRef() ) );
        }

        // you can only have a GenericParameter here
        Set<GenericParameter> genericParameters = new HashSet<GenericParameter>();
        for ( FuGECommonProtocolGenericParameterType parameterXML : genericActionXML.getGenericParameter() ) {
            // set fuge object
            GenericParameter gp = ( GenericParameter ) cp.unmarshal( parameterXML, ( GenericParameter ) DatabaseObjectHelper.getOrCreate(
                    parameterXML.getIdentifier(),
                    parameterXML.getName(),
                    "net.sourceforge.fuge.common.protocol.GenericParameter" ), performer );
            DatabaseObjectHelper.save( "net.sourceforge.fuge.common.protocol.GenericParameter", gp, performer );
            genericParameters.add( gp );
        }
        genericAction.setParameters( genericParameters );

        return genericAction;
    }

    public FuGECommonProtocolGenericActionType marshal(
            FuGECommonProtocolGenericActionType genericActionXML,
            GenericAction genericAction ) throws EntityServiceException {

        // set any GenericAction-specific traits

        // action term
        if ( genericAction.getActionTerm() != null ) {
            FuGECommonProtocolGenericActionType.ActionTerm aterm = new FuGECommonProtocolGenericActionType.ActionTerm();
            aterm.setOntologyTermRef( genericAction.getActionTerm().getIdentifier() );
            genericActionXML.setActionTerm( aterm );
        }

        // action text
        if ( genericAction.getActionText() != null ) {
            genericActionXML.setActionText( genericAction.getActionText() );
        }

        // protocol ref
        if ( genericAction.getChildProtocol() != null ) {
            genericActionXML.setProtocolRef( genericAction.getChildProtocol().getIdentifier() );
        }

        // you can only have a GenericParameter here
        for ( GenericParameter parameter : genericAction.getParameters() ) {
            // set fuge object
            genericActionXML.getGenericParameter()
                    .add( ( FuGECommonProtocolGenericParameterType ) cp.marshal( new FuGECommonProtocolGenericParameterType(), parameter ) );
        }

        return genericActionXML;
    }

    // We are NOT printing the collection itself, just the contents of the collection.
    // Just prints a small subset of information about the objects
    public void prettyPrint( GenericAction genericAction, PrintStream printStream ) {
        prettyPrint( null, genericAction, printStream );
    }

    public void prettyPrint( String prepend, GenericAction genericAction, PrintStream printStream ) {
        String bbb = "------Generic Action Text: ";
        String ccc = "    Referenced ";

        if ( prepend != null ) {
            bbb = prepend + bbb;
            ccc = prepend + ccc;
        }

        ProtocolMappingHelper cpr = new ProtocolMappingHelper();

        if ( genericAction.getChildProtocol() != null ) {
            cpr.prettyPrint( ccc, genericAction.getChildProtocol(), printStream );
        }
        if ( genericAction.getActionText() != null ) {
            printStream.println( bbb + genericAction.getActionText() );
        }
    }
}
