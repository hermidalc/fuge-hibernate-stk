package net.sourceforge.symba.mapping.hibernatejaxb2.helper;


import net.sourceforge.fuge.collection.FuGE;
import net.sourceforge.fuge.collection.OntologyCollection;
import net.sourceforge.fuge.collection.ProtocolCollection;
import net.sourceforge.fuge.common.measurement.ComplexValue;
import net.sourceforge.fuge.common.ontology.*;
import net.sourceforge.fuge.common.protocol.*;
import net.sourceforge.fuge.common.audit.Person;
import net.sourceforge.fuge.service.EntityServiceException;
import net.sourceforge.fuge.util.generatedJAXB2.*;
import net.sourceforge.symba.mapping.hibernatejaxb2.DatabaseObjectHelper;

import javax.xml.bind.JAXBElement;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Collection;
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
public class OntologyCollectionMappingHelper implements MappingHelper<OntologyCollection, FuGECollectionOntologyCollectionType> {
    private final DescribableMappingHelper cd;
    private final IdentifiableMappingHelper ci;

    public OntologyCollectionMappingHelper() {
        this.ci = new IdentifiableMappingHelper();
        this.cd = ci.getCisbanDescribableHelper();
    }

    public OntologyCollection unmarshal( FuGECollectionOntologyCollectionType ontoCollXML, OntologyCollection ontoColl, Person performer )
            throws EntityServiceException {

        ontoColl = ( OntologyCollection ) cd.unmarshal( ontoCollXML, ontoColl, performer );

        ontoColl = unmarshalCollectionContents( ontoCollXML, ontoColl, performer );

        // set collection in database
        DatabaseObjectHelper.save( "net.sourceforge.fuge.collection.OntologyCollection", ontoColl, performer );

        return ontoColl;
    }

    public OntologyCollection unmarshalCollectionContents( FuGECollectionOntologyCollectionType ontoCollXML,
                                                           OntologyCollection ontoColl, Person performer ) throws EntityServiceException {
        // Ontology Sources
        Set<OntologySource> ontologySources = new HashSet<OntologySource>();
        for ( FuGECommonOntologyOntologySourceType ontoSourceXML : ontoCollXML.getOntologySource() ) {

            // Retrieve from the database or create a new local instance.
            OntologySource ontologySource = ( OntologySource ) DatabaseObjectHelper.getOrCreate(
                    ontoSourceXML.getIdentifier(),
                    ontoSourceXML.getName(),
                    "net.sourceforge.fuge.common.ontology.OntologySource" );

            ontologySource = ( OntologySource ) ci.unmarshal( ontoSourceXML, ontologySource, performer );

            ontologySource.setOntologyURI( ontoSourceXML.getOntologyURI() );

            DatabaseObjectHelper.save( "net.sourceforge.fuge.common.ontology.OntologySource", ontologySource, performer );
            ontologySources.add( ontologySource );
        }
        ontoColl.setSources( ontologySources );

        // Ontology Terms
        Set<OntologyTerm> ontologyTerms = new HashSet<OntologyTerm>();
        for ( JAXBElement<? extends FuGECommonOntologyOntologyTermType> ontologyTermElementXML : ontoCollXML.getOntologyTerm() ) {
            FuGECommonOntologyOntologyTermType ontologyTermXML = ontologyTermElementXML.getValue();
            if ( ontologyTermXML instanceof FuGECommonOntologyOntologyIndividualType ) {
                FuGECommonOntologyOntologyIndividualType ontologyIndividualXML = ( FuGECommonOntologyOntologyIndividualType ) ontologyTermXML;
                ontologyTerms.add( unmarshalOntologyIndividual( ontologyIndividualXML, performer ) );
            }
        }
        ontoColl.setOntologyTerms( ontologyTerms );
        return ontoColl;
    }

    public FuGECollectionOntologyCollectionType marshal( FuGECollectionOntologyCollectionType ontoCollXML, OntologyCollection ontoColl )
            throws EntityServiceException {

        ontoCollXML = ( FuGECollectionOntologyCollectionType ) cd.marshal( ontoCollXML, ontoColl );

        // set ontology terms
        ObjectFactory factory = new ObjectFactory();
        for ( OntologyTerm ontologyTerm : ontoColl.getOntologyTerms() ) {
            // todo: there is an error in the way the OntologyTerm are retrieved from an OntologyCollection. Retrieval from the DB again is the only way I have found so far to sort it out.
            // Otherwise, you'll find that the ontologyTerm variable will be of type,
            // e.g. net.sourceforge.fuge.common.ontology.OntologyTermImpl$$EnhancerByCGLIB$$7e8613ff
            // rather than
            //  net.sourceforge.fuge.common.protocol.OntologyIndividualImpl
            // and the (ontologyTerm instanceof OntologyIndividual) will be false.
            ontologyTerm = (OntologyTerm) entityService.getIdentifiable( ontologyTerm.getId() );

            if ( ontologyTerm instanceof OntologyIndividual )
                ontoCollXML.getOntologyTerm()
                        .add( factory.createOntologyIndividual( marshalOntologyIndividual( ( OntologyIndividual ) ontologyTerm ) ) );
        }

        // set ontology source
        for ( OntologySource ontoSource : ontoColl.getSources() ) {

            FuGECommonOntologyOntologySourceType ontoSourceXML = new FuGECommonOntologyOntologySourceType();
            ontoSourceXML = ( FuGECommonOntologyOntologySourceType ) ci.marshal(
                    ontoSourceXML, ontoSource );
            ontoSourceXML.setOntologyURI( ontoSource.getOntologyURI() );
            ontoCollXML.getOntologySource().add( ontoSourceXML );
        }

        return ontoCollXML;
    }

    private OntologyIndividual unmarshalOntologyIndividual(
            FuGECommonOntologyOntologyIndividualType ontologyIndividualXML, Person performer )
            throws EntityServiceException {

        // Retrieve from the database or create a new local instance.
        OntologyIndividual ontologyIndividual = ( OntologyIndividual ) DatabaseObjectHelper.getOrCreate(
                ontologyIndividualXML.getIdentifier(),
                ontologyIndividualXML.getName(),
                "net.sourceforge.fuge.common.ontology.OntologyIndividual" );

        // set the ontology term traits
        ontologyIndividual = ( OntologyIndividual ) unmarshalOntologyTerm( ontologyIndividualXML, ontologyIndividual, performer );

        // set the OntologyIndividual-specific traits
        Set<OntologyProperty> ontologyProperties = new HashSet<OntologyProperty>();
        for ( JAXBElement<? extends FuGECommonOntologyOntologyPropertyType> ontologyPropertyElementXML : ontologyIndividualXML
                .getOntologyProperty() ) {

            FuGECommonOntologyOntologyPropertyType ontologyPropertyXML = ontologyPropertyElementXML.getValue();

            if ( ontologyPropertyXML instanceof FuGECommonOntologyObjectPropertyType ) {

                FuGECommonOntologyObjectPropertyType objectPropertyXML = ( FuGECommonOntologyObjectPropertyType ) ontologyPropertyXML;

                // set object property

                // Retrieve from the database or create a new local instance.
                ObjectProperty objectProperty = ( ObjectProperty ) DatabaseObjectHelper.getOrCreate(
                        objectPropertyXML.getIdentifier(),
                        objectPropertyXML.getName(),
                        "net.sourceforge.fuge.common.ontology.ObjectProperty" );

                objectProperty = ( ObjectProperty ) unmarshalOntologyTerm( objectPropertyXML, objectProperty, performer );

                Set<OntologyIndividual> smallOis = new HashSet<OntologyIndividual>();
                for ( FuGECommonOntologyOntologyIndividualType smallOiXML : objectPropertyXML.getOntologyIndividual() )
                    smallOis.add( unmarshalOntologyIndividual( smallOiXML, performer ) );

                objectProperty.setContent( smallOis );
                objectProperty.setName( objectPropertyXML.getName() );
                DatabaseObjectHelper.save( "net.sourceforge.fuge.common.ontology.ObjectProperty", objectProperty, performer );

                ontologyProperties.add( objectProperty );

            } else if ( ontologyPropertyXML instanceof FuGECommonOntologyDataPropertyType ) {

                FuGECommonOntologyDataPropertyType dataPropertyXML = ( FuGECommonOntologyDataPropertyType ) ontologyPropertyXML;

                // set data property

                // Retrieve from the database or create a new local instance.
                DataProperty dataProperty = ( DataProperty ) DatabaseObjectHelper.getOrCreate(
                        dataPropertyXML.getIdentifier(),
                        dataPropertyXML.getName(),
                        "net.sourceforge.fuge.common.ontology.DataProperty" );

                dataProperty = ( DataProperty ) unmarshalOntologyTerm( dataPropertyXML, dataProperty, performer );
                dataProperty.setDataType( dataPropertyXML.getDataType() );
                dataProperty.setName( dataPropertyXML.getName() );
                DatabaseObjectHelper.save( "net.sourceforge.fuge.common.ontology.DataProperty", dataProperty, performer );
                ontologyProperties.add( dataProperty );
            }
        }
        ontologyIndividual.setProperties( ontologyProperties );
        ontologyIndividual.setName( ontologyIndividualXML.getName() );
        DatabaseObjectHelper.save( "net.sourceforge.fuge.common.ontology.OntologyIndividual", ontologyIndividual, performer );
        return ontologyIndividual;
    }

    private FuGECommonOntologyOntologyIndividualType marshalOntologyIndividual(
            OntologyIndividual ontologyIndividual ) {

        FuGECommonOntologyOntologyIndividualType ontologyIndividualXML = new FuGECommonOntologyOntologyIndividualType();
        ontologyIndividualXML = ( FuGECommonOntologyOntologyIndividualType ) marshalOntologyTerm(
                ontologyIndividualXML, ontologyIndividual );

        ObjectFactory factory = new ObjectFactory();

        for ( OntologyProperty ontologyProperty : ontologyIndividual.getProperties() ) {

            if ( ontologyProperty instanceof ObjectProperty ) {
                FuGECommonOntologyObjectPropertyType objectPropertyXML = new FuGECommonOntologyObjectPropertyType();
                ObjectProperty objectProperty = ( ObjectProperty ) ontologyProperty;
                objectPropertyXML = ( FuGECommonOntologyObjectPropertyType ) marshalOntologyTerm(
                        objectPropertyXML, objectProperty );
                for ( OntologyIndividual smallOi : objectProperty.getContent() ) {
                    objectPropertyXML.getOntologyIndividual().add( marshalOntologyIndividual( smallOi ) );
                }

                ontologyIndividualXML.getOntologyProperty().add( factory.createObjectProperty( objectPropertyXML ) );
            } else if ( ontologyProperty instanceof DataProperty ) {
                FuGECommonOntologyDataPropertyType dataPropertyXML = new FuGECommonOntologyDataPropertyType();
                DataProperty dataProperty = ( DataProperty ) ontologyProperty;
                dataPropertyXML = ( FuGECommonOntologyDataPropertyType ) marshalOntologyTerm(
                        dataPropertyXML, dataProperty );
                dataPropertyXML.setDataType( dataProperty.getDataType() );
                ontologyIndividualXML.getOntologyProperty().add( factory.createDataProperty( dataPropertyXML ) );
            }
        }
        return ontologyIndividualXML;
    }


    private OntologyTerm unmarshalOntologyTerm( FuGECommonOntologyOntologyTermType ontologyTermXML,
                                                OntologyTerm ontologyTerm, Person performer )
            throws EntityServiceException {
        ontologyTerm = ( OntologyTerm ) ci.unmarshal( ontologyTermXML, ontologyTerm, performer );
        ontologyTerm.setTerm( ontologyTermXML.getTerm() );
        ontologyTerm.setTermAccession( ontologyTermXML.getTermAccession() );
        if ( ontologyTermXML.getOntologySourceRef() != null ) {
            ontologyTerm.setOntologySource( ( OntologySource ) entityService.getIdentifiable( ontologyTermXML.getOntologySourceRef() ) );
        }
        return ontologyTerm;
    }

    private FuGECommonOntologyOntologyTermType marshalOntologyTerm(
            FuGECommonOntologyOntologyTermType ontologyTermXML, OntologyTerm ontologyTerm ) {

        ontologyTermXML = ( FuGECommonOntologyOntologyTermType ) ci.marshal(
                ontologyTermXML, ontologyTerm );

        if ( ontologyTerm.getTerm() != null ) ontologyTermXML.setTerm( ontologyTerm.getTerm() );
        if ( ontologyTerm.getTermAccession() != null )
            ontologyTermXML.setTermAccession( ontologyTerm.getTermAccession() );
        if ( ontologyTerm.getOntologySource() != null )
            ontologyTermXML.setOntologySourceRef( ontologyTerm.getOntologySource().getIdentifier() );

        return ontologyTermXML;
    }

    public FuGE addRelevantOntologyTerms( FuGE fuge, Person performer ) throws EntityServiceException {
        OntologyCollection ontologyCollection = ( OntologyCollection ) entityService.createDescribable(
                "net.sourceforge.fuge.collection.OntologyCollection" );

        // many protocols contain ontology terms, so we need to add any mentioned terms to the OntologyCollection

        // go through each of the protocols in the experiment. Currently, it is only in the Parameters of the
        // Actions of the protocols, and in the Parameters of the Protocols themselves where we look for ontology terms
        // start by making sure we won't lose any already-included ontology terms from the collection
        Set<OntologyTerm> ontologyTerms;
        if ( fuge.getOntologyCollection() != null ) {
            ontologyTerms = ( Set<OntologyTerm> ) fuge.getOntologyCollection().getOntologyTerms();
        } else {
            ontologyTerms = new HashSet<OntologyTerm>();
        }

        ontologyTerms.addAll( addOntologyTermsFromProtocols( fuge.getProtocolCollection(), ontologyTerms ) );
        ontologyTerms.addAll(
                addOntologyTermsFromEquipment(
                        ( Set<Equipment> ) fuge.getProtocolCollection().getAllEquipment(), ontologyTerms ) );

        ontologyCollection.setOntologyTerms( ontologyTerms );
        // don't do any checking with ontology sources - just assume that those already present are correct.
        // todo check for new ontology sources associated with the new ontology terms#
        if ( fuge.getOntologyCollection() != null && fuge.getOntologyCollection().getSources() != null ) {
            ontologyCollection.setSources( fuge.getOntologyCollection().getSources() );
        }
        // load the fuge object into the database
        DatabaseObjectHelper.save( "net.sourceforge.fuge.collection.OntologyCollection", ontologyCollection, performer );
        fuge.setOntologyCollection( ontologyCollection );

        return fuge;
    }

    private Set<OntologyTerm> addOntologyTermsFromEquipment( Set<Equipment> allEquipment,
                                                             Set<OntologyTerm> ontologyTerms )
            throws EntityServiceException {
        for ( Equipment equipment : allEquipment ) {
            if ( equipment instanceof GenericEquipment ) {
//                // checking for generic parameters
//                for ( Object gpObj : equipment.getParameters() ) {
//                    if ( gpObj instanceof GenericParameter ) {
//                        ontologyTerms.addAll(
//                                addOntologyTermsFromParameter(
//                                        ( GenericParameter ) gpObj, ontologyTerms ) );
//                    }
//                }
                // checking for ontology terms elsewhere in the GenericEquipment
                if ( equipment.getMake() != null ) {
                    ontologyTerms = addOntologyTermIfFound(
                            ontologyTerms, equipment.getMake().getIdentifier() );
                }
                if ( equipment.getModel() != null ) {
                    ontologyTerms = addOntologyTermIfFound(
                            ontologyTerms, equipment.getModel().getIdentifier() );
                }
                if ( equipment.getAnnotations() != null && !equipment.getAnnotations().isEmpty() ) {
                    for ( OntologyTerm ot : equipment.getAnnotations() ) {
                        ontologyTerms = addOntologyTermIfFound(
                                ontologyTerms, ot.getIdentifier() );
                    }
                }
            }
        }
        return ontologyTerms;
    }


    private Set<OntologyTerm> addOntologyTermsFromProtocols( ProtocolCollection protocolCollection,
                                                             Set<OntologyTerm> ontologyTerms )
            throws EntityServiceException {

        for ( Protocol protocol : protocolCollection.getProtocols() ) {
            if ( protocol instanceof GenericProtocol ) {
                // checking for generic parameters inside the protocol
                for ( GenericParameter genericParameter : ( ( GenericProtocol ) protocol ).getParameters() ) {
                    ontologyTerms.addAll( addOntologyTermsFromParameter( genericParameter, ontologyTerms ) );
                }
                // checking for generic parameters inside the protocol's generic action
                for ( Action action : ( ( GenericProtocol ) protocol ).getActions() ) {
                    if ( action instanceof GenericAction ) {
                        for ( GenericParameter genericParameter : ( ( GenericAction ) action ).getParameters() ) {
                            ontologyTerms.addAll( addOntologyTermsFromParameter( genericParameter, ontologyTerms ) );
                        }
                    }
                }
            }
        }
        return ontologyTerms;
    }

    private Set<OntologyTerm> addOntologyTermsFromParameter( GenericParameter genericParameter,
                                                             Set<OntologyTerm> ontologyTerms ) throws EntityServiceException {
        if ( genericParameter.getDefaultValue() != null ) {
            boolean found = false;
            for ( OntologyTerm ot : ontologyTerms ) {
                if ( ot.getIdentifier().equals( genericParameter.getDefaultValue().getUnit().getIdentifier() ) ) {
                    // this is already present - don't add
                    found = true;
                    break;
                }
            }
            if ( !found ) {
                ontologyTerms.add( ( OntologyTerm ) entityService.getIdentifiable(
                        genericParameter.getDefaultValue().getUnit().getIdentifier() ) );
            }
            found = false;
            for ( OntologyTerm ot : ontologyTerms ) {
                if ( ot.getIdentifier().equals( genericParameter.getDefaultValue().getDataType().getIdentifier() ) ) {
                    // this is already present - don't add
                    found = true;
                    break;
                }
            }
            if ( !found ) {
                ontologyTerms.add( ( OntologyTerm ) entityService.getIdentifiable(
                        genericParameter.getDefaultValue().getDataType().getIdentifier() ) );
            }
        }

        if ( genericParameter.getDefaultValue() instanceof ComplexValue ) {
            // complex values can have ontology terms as their value
            ComplexValue complexValue = ( ComplexValue ) genericParameter.getDefaultValue();
            ontologyTerms = addOntologyTermIfFound( ontologyTerms, complexValue.getValue().getIdentifier() );
        }
        return ontologyTerms;
    }

    private Set<OntologyTerm> addOntologyTermIfFound( Set<OntologyTerm> ontologyTerms,
                                                      String identifier ) throws EntityServiceException {
        // todo faster search algorithm
        boolean found = false;
        for ( OntologyTerm ot : ontologyTerms ) {
            if ( ot.getIdentifier().equals( identifier ) ) {
                // this is already present - don't add
                found = true;
                break;
            }
        }
        if ( !found ) {
//            System.err.println( "ADDING: " + identifier );
            ontologyTerms.add( ( OntologyTerm ) entityService.getIdentifiable( identifier ) );
        }
        return ontologyTerms;
    }

    // We are NOT printing the collection itself, just the contents of the collection.
    // Just prints a small subset of information about the objects
    public void prettyPrint( OntologyCollection ontologyCollection, PrintStream printStream ) {

        String aaa = "Ontology Source             : ";
        String bbb = "Ontology Source Reference   : ";
        String ccc = "Ontology Individual         : ";
        String ddd = "Ontology Individual's Source: ";
        String eee = "Ontology Individual's Term  : ";

        // print ontology sources.
        for ( OntologySource source : ontologyCollection.getSources() ) {
            ci.prettyPrint( aaa, source, printStream );
            if ( source.getOntologyURI() != null ) {
                printStream.println( bbb + source.getOntologyURI() );
            }
        }

        // print ontology terms.
        for ( OntologyTerm ontologyTerm : ontologyCollection.getOntologyTerms() ) {
            if ( ontologyTerm instanceof OntologyIndividual ) {
                OntologyIndividual oi = ( OntologyIndividual ) ontologyTerm;
                ci.prettyPrint( ccc, oi, printStream );
                printStream.println( eee + oi.getTerm() );
                if ( oi.getOntologySource() != null ) {
                    ci.prettyPrint( ddd, oi.getOntologySource(), printStream );
                    printStream.println( bbb + oi.getOntologySource().getOntologyURI() );
                }
            }
        }

        // todo ObjectProperty and DataProperty
    }

    public void prettyHtml( Collection ontologyTerms, PrintWriter printStream ) {
        for ( Object obj : ontologyTerms ) {
            if ( obj instanceof OntologyTerm ) {
                OntologyTerm ontologyTerm = ( OntologyTerm ) obj;
                printStream.println( ontologyTerm.getTerm() );
                if ( ontologyTerm.getOntologySource() != null ) {
                    printStream.println( " (" + ontologyTerm.getOntologySource().getName() + ")" );
                }
                printStream.println( "<br>" );
            }
        }

    }
}
