package net.sourceforge.symba.mapping.hibernatejaxb2.helper;

import net.sourceforge.fuge.common.protocol.Action;
import net.sourceforge.fuge.common.protocol.GenericAction;
import net.sourceforge.fuge.common.audit.Person;
import net.sourceforge.fuge.service.EntityServiceException;
import net.sourceforge.fuge.util.generatedJAXB2.FuGECommonProtocolActionType;
import net.sourceforge.fuge.util.generatedJAXB2.FuGECommonProtocolGenericActionType;

import java.io.PrintStream;

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
public class ActionMappingHelper implements MappingHelper<Action, FuGECommonProtocolActionType> {
    private final IdentifiableMappingHelper ci;
    private final GenericActionMappingHelper cga;

    public ActionMappingHelper() {
        this.ci = new IdentifiableMappingHelper();
        this.cga = new GenericActionMappingHelper();
    }

    /**
     * Does not perform any update to the database of the Provider object (though child objects may be updated).
     *
     * @param actionXML the jaxb object to parse
     * @param action    the hibernate object to fill
     * @param performer the person to assign to the creation/modification of the object
     * @return the newly-filled hibernate object
     * @throws EntityServiceException if there is a problem with the connection to the database
     */
    public Action unmarshal( FuGECommonProtocolActionType actionXML, Action action, Person performer )
            throws EntityServiceException {

        // determine what sort of action it is
        if ( actionXML instanceof FuGECommonProtocolGenericActionType ) {

            GenericAction genericAction = ( GenericAction ) action;

            // get action attributes
            genericAction = ( GenericAction ) ci.unmarshal( actionXML, genericAction, performer );

            // action ordinal
            if ( actionXML.getActionOrdinal() != null )
                genericAction.setActionOrdinal( actionXML.getActionOrdinal() );

            // get generic action attributes
            genericAction = cga
                    .unmarshal( ( FuGECommonProtocolGenericActionType ) actionXML, genericAction, performer );

            return genericAction;
        }
        return null; // shouldn't get here as there is currently only one type of Action allowed.
    }

    // This will always retrieve EXACTLY the version asked for. If you additionally wish to have the latest version,
    // you must additionally run getLatestVersion. This is so the LSID Authority can use these methods to get
    // exactly the objects asked for.
    public FuGECommonProtocolActionType marshal( FuGECommonProtocolActionType actionXML, Action action )
            throws EntityServiceException {

        // determine what sort of action it is
        if ( action instanceof GenericAction ) {
            // create fuge object
            FuGECommonProtocolGenericActionType genericActionXML = ( FuGECommonProtocolGenericActionType ) actionXML;

            // get action attributes
            genericActionXML = ( FuGECommonProtocolGenericActionType ) ci
                    .marshal( genericActionXML, action );

            // action ordinal
            genericActionXML.setActionOrdinal( action.getActionOrdinal() );

            // get generic action attributes
            genericActionXML = cga.marshal( genericActionXML, ( GenericAction ) action );

            return genericActionXML;
        }
        return null; // shouldn't get here as there is currently only one type of Action allowed.
    }

    // We are NOT printing the collection itself, just the contents of the collection.
    // Just prints a small subset of information about the objects
    public void prettyPrint( Action action, PrintStream printStream ) {
        prettyPrint( null, action, printStream );
    }

    public void prettyPrint( String prepend, Action action, PrintStream printStream ) {
        String aaa = "-----Action: ";
        if ( prepend != null ) {
            aaa = prepend + aaa;
        }
        ci.prettyPrint( aaa, action, printStream );
        cga.prettyPrint( prepend, ( GenericAction ) action, printStream );
    }
}
