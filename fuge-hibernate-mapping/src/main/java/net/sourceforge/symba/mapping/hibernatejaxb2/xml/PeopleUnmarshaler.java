package net.sourceforge.symba.mapping.hibernatejaxb2.xml;


import net.sourceforge.fuge.collection.AuditCollection;
import net.sourceforge.fuge.service.EntityService;
import net.sourceforge.fuge.service.EntityServiceException;
import net.sourceforge.fuge.util.generatedJAXB2.FuGECollectionAuditCollectionType;
import net.sourceforge.fuge.ServiceLocator;
import net.sourceforge.fuge.common.audit.Person;
import net.sourceforge.symba.mapping.hibernatejaxb2.helper.AuditCollectionMappingHelper;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
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

public class PeopleUnmarshaler {
    private final String XMLFilename;
    private final EntityService entityService;

    public PeopleUnmarshaler( String inputXML ) {
        ServiceLocator serviceLocator = ServiceLocator.instance();
        this.entityService = serviceLocator.getEntityService();
        this.XMLFilename = inputXML;
    }

    public void Jaxb2ToFuGE( Person performer ) throws JAXBException, FileNotFoundException, EntityServiceException, URISyntaxException {

        // create a JAXBContext capable of handling classes generated into
        // the FuGE.util.generatedJAXB2 package
        JAXBContext jc = JAXBContext.newInstance( "net.sourceforge.fuge.util.generatedJAXB2" );

        // create an Unmarshaller
        Unmarshaller u = jc.createUnmarshaller();

        // unmarshal JUST what is normally available within an AuditCollection
        JAXBElement<?> genericTopLevelElement = ( JAXBElement<?> ) u.unmarshal( new FileInputStream( XMLFilename ) );

        // Get the AuditCollection root object. REMEMBER that we will not be loading the Collection, JUST its contents.
        FuGECollectionAuditCollectionType collectionType = ( FuGECollectionAuditCollectionType ) genericTopLevelElement
                .getValue();

        // get and store all information in the database
        AuditCollectionMappingHelper cac = new AuditCollectionMappingHelper();

        // unmarshall the jaxb object without loading the collection into the database
        AuditCollection auditCollection = ( AuditCollection ) entityService.createDescribable( "net.sourceforge.fuge.collection.AuditCollection" );
        cac.unmarshalCollectionContents( collectionType, auditCollection, performer );
    }
}
