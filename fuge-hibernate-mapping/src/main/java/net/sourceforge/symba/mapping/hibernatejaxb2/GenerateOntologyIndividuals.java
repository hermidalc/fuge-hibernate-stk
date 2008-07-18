package net.sourceforge.symba.mapping.hibernatejaxb2;

import net.sourceforge.fuge.util.generatedJAXB2.*;
import net.sourceforge.fuge.util.identification.FuGEIdentifierFactory;
import net.sourceforge.fuge.util.identification.FuGEIdentifier;
import org.xml.sax.SAXException;

import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.*;

/**
 * This is a very plain-jane way of generating OntologyCollection xml elements from a very basic list of
 * ontology individuals. Just provide an input file like the following:
 * accession1::name1
 * accession2::name2
 * [...]
 * accessionN::nameN
 *
 * And you'll get out some FuGE-ML to load into your database. The ontologySource URI and the URN namespace are
 * hard-coded right now, so you will need to modify that to suit your needs.
 *
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

public class GenerateOntologyIndividuals {
    private final String schemaFilename, inputListFilename, XMLFilename, ontoSourceName;

    private GenerateOntologyIndividuals( String sf, String ilf, String xf, String ontologySourceName ) {
        this.schemaFilename = sf;
        this.inputListFilename = ilf;
        this.XMLFilename = xf;
        this.ontoSourceName = ontologySourceName;
    }

    public static void main( String[] args ) throws Exception {
        if ( args.length != 4 )
            throw new java.lang.Exception(
                    "You must provide 4 arguments in this order: schema-file input-list output-xml-file OntologySourceName" );

        GenerateOntologyIndividuals xml = new GenerateOntologyIndividuals( args[0], args[1], args[2], args[3] );
        xml.generate();
    }

    private void generate() {
        OutputStream os = null;
        BufferedReader br;
        try {
            br = new BufferedReader( new InputStreamReader( new FileInputStream( inputListFilename ) ) );

            // create a JAXBContext capable of handling classes generated into
            // the fugeOM.util.generatedJAXB2 package
            JAXBContext jc = JAXBContext.newInstance( "fugeOM.util.generatedJAXB2" );

            // create a Marshaller
            Marshaller m = jc.createMarshaller();
            m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );

            // set the correct schema
            SchemaFactory sf = SchemaFactory.newInstance( W3C_XML_SCHEMA_NS_URI );
            Schema schema = sf.newSchema( new File( schemaFilename ) );
            m.setSchema( schema );

            // create a jaxb root object
            System.err.println( "Starting generation..." );

            FuGECollectionOntologyCollectionType ontoCollXML = new FuGECollectionOntologyCollectionType();

            // first make the ontology source object
            FuGEIdentifier idMaker = FuGEIdentifierFactory.createFuGEIdentifier( "some.domain.name", null );

            FuGECommonOntologyOntologySourceType sourceType = new FuGECommonOntologyOntologySourceType();
            sourceType.setIdentifier( idMaker.create( "net.sourceforge.fuge.common.ontology.OntologySource" ) );
            sourceType.setName( ontoSourceName );
            sourceType.setOntologyURI( "http://www.cisban.ac.uk" );
            ontoCollXML.getOntologySource().add( sourceType );

            String readIn;
            ObjectFactory factory = new ObjectFactory();
            while ( ( readIn = br.readLine() ) != null ) {
//                System.out.println( "Read in " + readIn + "|" );
                String line = readIn.trim();
                String[] columns = line.split( "::" );
                if ( columns.length == 2 ) {
                    FuGECommonOntologyOntologyIndividualType individualType = new FuGECommonOntologyOntologyIndividualType();
                    individualType.setIdentifier( idMaker.create( "net.sourceforge.fuge.common.ontology.OntologyIndividual" ) );
                    individualType.setTermAccession( columns[0] );
                    individualType.setTerm( columns[1] );
                    individualType.setName( columns[1] );
                    individualType.setOntologySourceRef( sourceType.getIdentifier() );
//                FugeOMCommonDescribableType.PropertySets ps = individualType.getPropertySets();
                    ontoCollXML.getOntologyTerm().add( factory.createOntologyIndividual( individualType ) );
                }
            }

            os = new FileOutputStream( XMLFilename );

            @SuppressWarnings( "unchecked" )
            JAXBElement element = new JAXBElement(new QName( "http://fuge.org/core", "OntologyCollection" ), FuGECollectionOntologyCollectionType.class, ontoCollXML );
            m.marshal( element, os );

        } catch ( JAXBException je ) {
            System.err.println( "JAXB Exception:" );
            try {
                os.flush();
                System.err.println( "Output buffer flushed." );
            } catch ( IOException e ) {
                System.err.println( "Internal IO Exception when flushing buffer" );
                e.printStackTrace();
            } catch ( NullPointerException e ) {
                System.err.println( "Null Pointer Exception when flushing buffer" );
                e.printStackTrace();
            }
            je.printStackTrace();
        } catch ( FileNotFoundException e ) {
            e.printStackTrace();
        } catch ( SAXException e ) {
            e.printStackTrace();
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }

}
