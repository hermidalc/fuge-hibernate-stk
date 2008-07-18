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
 */
package net.sourceforge.symba.mapping.hibernatejaxb2.xml;

import org.testng.annotations.Test;
import org.xml.sax.SAXException;
import net.sourceforge.fuge.util.RandomXmlGenerator;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.net.URISyntaxException;
import java.util.Scanner;
import java.util.SortedSet;
import java.util.TreeSet;


public class XmlDbRoundtripTest {

    @Test( groups = { "hibernate" } )
    public void testRoundtripWithDatabase() throws URISyntaxException, JAXBException, SAXException, IOException {

        // todo this file should be put in a suitable user-specified directory
        String inputTestFile = "testInputRoundtripWithDatabase.xml";
        String outputTestFile = "testOutputRoundtripWithDatabase.xml";
        String inputComparedFile = "testInputRoundtripWithDatabaseCompared.xml";
        String outputComparedFile = "testOutputRoundtripWithDatabaseCompared.xml";
        RandomXmlGenerator.generate( inputTestFile, 1 );

        XMLUnmarshaler unTest = new XMLUnmarshaler( inputTestFile );
        String fugeIdentifier = unTest.Jaxb2ToFuGE( null );

        XMLMarshaler marshalTest = new XMLMarshaler( null );
        marshalTest.FuGEToJaxb2( fugeIdentifier, outputTestFile );

        // Read in the outputted file, removing known bugs
        SortedSet<String> output = parseOutKnownExceptions( outputTestFile );

        // Read in the inputted file
        // You must sort it, otherwise slight changes in ordering will be perceived as differences in the file
        SortedSet<String> input = parseOutKnownExceptions( inputTestFile );

        // print out the modified versions of the files
        PrintWriter outputCompared = new PrintWriter( new BufferedWriter( new FileWriter( outputComparedFile ) ) );
        outputCompared.print( output );
        outputCompared.close();
        PrintWriter inputCompared = new PrintWriter( new BufferedWriter( new FileWriter( inputComparedFile ) ) );
        inputCompared.print( input );
        inputCompared.close();

        // compare the two files
        assert ( input.toString().equals( output.toString() ) ) : "Error: the input and output files are not identical. Please check.";

    }

    /**
     * You must sort it, otherwise slight changes in ordering will be perceived as differences in the file
     * @param filename the file to read into the SortedSet
     * @return the filled SortedSet (implemented internally as a TreeSet)
     * @throws java.io.FileNotFoundException if the file could not be opened
     */
    private SortedSet<String> parseOutKnownExceptions( String filename ) throws FileNotFoundException {

        SortedSet<String> sortedSet = new TreeSet<String>();
        Scanner scanner = new Scanner( new FileInputStream( filename ) );

        while ( scanner.hasNextLine() ) {
            String current = scanner.nextLine();
            // If any of the following are found, remove them from the comparison:
            //   + The FuGE element often has its attributes rearranged in the version extracted from the database
            if ( !current.startsWith( "<FuGE" ) &&
                    !current.contains( "<_accessRight" ) &&
                    !current.contains( "<_equipment GenericEquipment_ref" ) ) {
                sortedSet.add( current + System.getProperty( "line.separator" ) );
            }
        }

        scanner.close();

        return sortedSet;
    }
}
