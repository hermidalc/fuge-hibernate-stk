package net.sourceforge.symba.mapping.hibernatejaxb2.xml;


import net.sourceforge.fuge.collection.FuGE;
import net.sourceforge.symba.mapping.hibernatejaxb2.helper.FuGEMappingHelper;
import net.sourceforge.symba.mapping.hibernatejaxb2.DatabaseObjectHelper;
import net.sourceforge.fuge.service.EntityServiceException;
import net.sourceforge.fuge.service.EntityService;
import net.sourceforge.fuge.util.generatedJAXB2.FuGECollectionFuGEType;
import net.sourceforge.fuge.ServiceLocator;
import net.sourceforge.fuge.common.audit.Person;
import org.xml.sax.SAXException;

import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;

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

// NOTE: this class only works with a single FuGE element in an xml file: not multiple entries in the same file

public class XMLUnmarshaler {
    private final String schemaFilename, XMLFilename;
    private final EntityService entityService;

    public XMLUnmarshaler( String sf, String xf ) {
        ServiceLocator serviceLocator = ServiceLocator.instance();
        this.schemaFilename = sf;
        this.XMLFilename = xf;
        this.entityService = serviceLocator.getEntityService();
    }

    /**
     * Use this constructor if, for some reason, you don't have access to the XSD. However, doing this
     * is dangerous as you won't see if there are validation errors.
     *
     * @param xf the input XML file
     */
    public XMLUnmarshaler( String xf ) {
        ServiceLocator serviceLocator = ServiceLocator.instance();
        this.schemaFilename = null;
        this.XMLFilename = xf;
        this.entityService = serviceLocator.getEntityService();
    }

    public String Jaxb2ToFuGE( Person performer ) throws JAXBException, SAXException, EntityServiceException, URISyntaxException, FileNotFoundException {

        // create a JAXBContext capable of handling classes generated into
        // the fugeOM.util.generatedJAXB2 package
        JAXBContext jc = JAXBContext.newInstance( "net.sourceforge.fuge.util.generatedJAXB2" );

        // create an Unmarshaller
        Unmarshaller u = jc.createUnmarshaller();

        if ( schemaFilename != null ) {
            // Sort out validation settings
            SchemaFactory sf = SchemaFactory.newInstance( W3C_XML_SCHEMA_NS_URI );
            Schema schema = sf.newSchema( new File( schemaFilename ) );
            // set a schema for validation.
            u.setSchema( schema );
        }

        // unmarshal a fugeOM.util.generatedJAXB2 instance document into a tree of Java content
        // objects composed of classes from the fugeOM.util.generatedJAXB2 package.
        JAXBElement<?> genericTopLevelElement = ( JAXBElement<?> ) u.unmarshal( new FileInputStream( XMLFilename ) );

        // Get the jaxb root object
        FuGECollectionFuGEType frXML = ( FuGECollectionFuGEType ) genericTopLevelElement.getValue();

        // Before doing any unmarshaling, check to see if this object is in the database.
        FuGE fr;
        // Retrieve from the database or create a new local instance.
        fr = ( FuGE ) DatabaseObjectHelper.getOrCreate(
                frXML.getIdentifier(),
                frXML.getName(),
                "net.sourceforge.fuge.collection.FuGE" );

        FuGEMappingHelper cf = new FuGEMappingHelper();
        fr = cf.unmarshal( frXML, fr, performer );

        // Load the entire fuge entry into the database
        entityService.save( "net.sourceforge.fuge.collection.FuGE", fr, performer );
        return fr.getIdentifier();
    }
}
