package net.sourceforge.symba.mapping.hibernatejaxb2.helper;


import net.sourceforge.fuge.common.ontology.OntologyTerm;
import net.sourceforge.fuge.common.protocol.*;
import net.sourceforge.fuge.common.audit.Person;
import net.sourceforge.fuge.service.EntityServiceException;
import net.sourceforge.fuge.util.generatedJAXB2.FuGECommonProtocolGenericProtocolType;
import net.sourceforge.fuge.util.generatedJAXB2.FuGECommonProtocolProtocolType;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.*;

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
public class ProtocolMappingHelper implements MappingHelper<Protocol, FuGECommonProtocolProtocolType> {
    private final IdentifiableMappingHelper ci;
    private final ParameterizableMappingHelper cparam;
    private final GenericProtocolMappingHelper cgp;

    public ProtocolMappingHelper() {
        this.ci = new IdentifiableMappingHelper();
        this.cparam = new ParameterizableMappingHelper();
        this.cgp = new GenericProtocolMappingHelper();
    }

    public Protocol unmarshal( FuGECommonProtocolProtocolType protocolXML, Protocol protocol, Person performer )
            throws EntityServiceException {

        // determine what sort of protocol it is
        if ( protocolXML instanceof FuGECommonProtocolGenericProtocolType ) {

            GenericProtocol genericProtocol = ( GenericProtocol ) protocol;

            // get protocol attributes
            genericProtocol = ( GenericProtocol ) ci.unmarshal( protocolXML, genericProtocol, performer );
            genericProtocol = ( GenericProtocol ) cparam.unmarshal( protocolXML, genericProtocol, performer );

            // input types
            if ( protocolXML.getInputTypes() != null ) {
                Set<OntologyTerm> ontologyTerms = new HashSet<OntologyTerm>();
                for ( FuGECommonProtocolProtocolType.InputTypes inputTypesXML : protocolXML.getInputTypes() ) {
                    ontologyTerms
                            .add( ( OntologyTerm ) entityService.getIdentifiable( inputTypesXML.getOntologyTermRef() ) );
                }
                genericProtocol.setInputTypes( ontologyTerms );
            }

            // output types
            if ( protocolXML.getOutputTypes() != null ) {
                Set<OntologyTerm> ontologyTerms = new HashSet<OntologyTerm>();
                for ( FuGECommonProtocolProtocolType.OutputTypes outputTypesXML : protocolXML.getOutputTypes() ) {
                    ontologyTerms
                            .add( ( OntologyTerm ) entityService.getIdentifiable( outputTypesXML.getOntologyTermRef() ) );
                }
                genericProtocol.setOutputTypes( ontologyTerms );
            }

            // get generic protocol attributes
            genericProtocol = cgp.unmarshal(
                    ( FuGECommonProtocolGenericProtocolType ) protocolXML, genericProtocol, performer );

            return genericProtocol;
        }
        return null;  // shouldn't get here as there is currently only one type of Protocol allowed.
    }

    public FuGECommonProtocolProtocolType marshal( FuGECommonProtocolProtocolType protocolXML, Protocol protocol )
            throws EntityServiceException {

        // get any lazily loaded objects
//        protocol = ( Protocol ) entityService.greedyGet( protocol );

        // determine what sort of protocol it is
        if ( protocol instanceof GenericProtocol ) {

            // create fuge object
            FuGECommonProtocolGenericProtocolType genericProtocolXML = ( FuGECommonProtocolGenericProtocolType ) protocolXML;

            // sort out lazy loading
            genericProtocolXML = cgp.marshal( genericProtocolXML, ( GenericProtocol ) protocol );

            // get protocol attributes
            genericProtocolXML = ( FuGECommonProtocolGenericProtocolType ) ci
                    .marshal( genericProtocolXML, protocol );

            genericProtocolXML = ( FuGECommonProtocolGenericProtocolType ) cparam
                    .marshal( genericProtocolXML, protocol );

            // input types
            for ( OntologyTerm ontologyTerm : protocol.getInputTypes() ) {

                FuGECommonProtocolProtocolType.InputTypes inputTypesXML = new FuGECommonProtocolProtocolType.InputTypes();
                inputTypesXML.setOntologyTermRef( ontologyTerm.getIdentifier() );
                genericProtocolXML.getInputTypes().add( inputTypesXML );
            }

            // output types
            for ( OntologyTerm ontologyTerm : protocol.getOutputTypes() ) {

                FuGECommonProtocolProtocolType.OutputTypes outputTypesXML = new FuGECommonProtocolProtocolType.OutputTypes();
                outputTypesXML.setOntologyTermRef( ontologyTerm.getIdentifier() );
                genericProtocolXML.getOutputTypes().add( outputTypesXML );
            }
            return genericProtocolXML;
        }
        return null;  // shouldn't get here as there is currently only one type of Protocol allowed.
    }

    // We are NOT printing the collection itself, just the contents of the collection.
    // Just prints a small subset of information about the objects
    public void prettyPrint( Protocol protocol, PrintStream printStream ) {
        prettyPrint( null, protocol, printStream );
    }

    public void prettyPrint( String prepend, Protocol protocol, PrintStream printStream ) {
        String aaa = "---Protocol: ";
        if ( prepend != null ) {
            aaa = prepend + aaa;
        }

        ci.prettyPrint( aaa, protocol, printStream );
        if ( protocol instanceof GenericProtocol ) {
            cgp.prettyPrint( prepend, ( GenericProtocol ) protocol, printStream );
        }
    }

    public void prettyHtml( GenericAction parentGA,
                            Protocol protocol,
                            Collection<ProtocolApplication> restrictedPAs,
                            PrintWriter printStream ) throws EntityServiceException {

        GenericProtocol gp;
        if ( protocol instanceof GenericProtocol ) {
            gp = ( GenericProtocol ) protocol;
        } else {
            return;
        }

        // Now print out all direct children of the complete protocol, and keep drilling down
        // until you have no more actions. At that stage, search the protocol applications for
        // a match: this is where your data will be.

        // this is so the current protocol has the right height. We have to look through
        // the entire tree to get the right number
        int size = countActions( gp );
        // todo fix 3-level display. currently hard-coded for top-level microarray only
        if ( size == 21 ) {
            size = 19;
        }

        if ( size > 0 ) {
            printStream.println( "<td rowspan=\"" + size + "\">" );
            if ( parentGA != null ) {
                ci.prettyHtml( null, parentGA, printStream );
            } else {
                ci.prettyHtml( null, gp, printStream );
            }
            printStream.println( "</td>" );

            Set<Action> aSet = ( Set<Action> ) gp.getActions();
            for ( int count = 1; count <= aSet.size(); count++ ) {
                for ( Action action : aSet ) {
                    if ( action instanceof GenericAction ) {
                        GenericAction ga = ( GenericAction ) action;
                        if ( count == ga.getActionOrdinal() ) {
                            // Find all GPAs associated with this GA from the list of restricted GPAs
                            prettyHtml( ga, ga.getChildProtocol(), getMatchedGPAs( ga, restrictedPAs ), printStream );
                        }
                    }
                }
            }
        } else {
            printStream.println( "<td>" );
            ci.prettyHtml( null, parentGA, printStream );
            printStream.println( "</td>" );

            ProtocolApplicationMappingHelper capp = new ProtocolApplicationMappingHelper();
            // If there are no actions in the protocol, then its matching GPA will be the one that holds the data
            if ( restrictedPAs != null && !restrictedPAs.isEmpty() ) {
                // for all matching GPAs...
                for ( ProtocolApplication pa : restrictedPAs ) {
                    // ...if there are no action applications....
                    if ( pa.getActionApplications() == null || pa.getActionApplications().isEmpty() ) {
                        // ...print the data
                        capp.prettyHtml( pa, printStream );
                    }
                }
            }
            printStream.println( "</tr>" );
            printStream.println( "<tr>" );
        }
        printStream.flush();
    }

    private List<ProtocolApplication> getMatchedGPAs( GenericAction ga, Collection<ProtocolApplication> apps ) {
        List<ProtocolApplication> matchedGPAs = new ArrayList<ProtocolApplication>();
        for ( ProtocolApplication currentPA : apps ) {
            if ( apps instanceof GenericProtocolApplication ) {
                GenericProtocolApplication currentGPA = ( GenericProtocolApplication ) currentPA;
                for ( ActionApplication currentAA : currentGPA.getActionApplications() ) {
                    if ( currentAA.getAction().getIdentifier().equals( ga.getIdentifier() ) ) {
                        matchedGPAs.add( currentAA.getChildProtocolApplication() );
                    }
                }
            }
        }
        return matchedGPAs;
    }

    private int countActions( GenericProtocol gp ) {
        // this is so the current protocol has the right height. We have to look through
        // the entire tree to get the right number

        int addition = 0;
        for ( Action action : gp.getActions() ) {
            // do the same thing for the protocol reference for each generic action
            addition++; // for the current action.
            GenericAction ga = ( GenericAction ) action;
            addition += countActions( ( GenericProtocol ) ga.getChildProtocol() );
        }
        return addition;
    }

}
