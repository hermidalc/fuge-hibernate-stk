package net.sourceforge.symba.mapping.hibernatejaxb2.helper;


import net.sourceforge.fuge.bio.data.Data;
import net.sourceforge.fuge.bio.data.ExternalData;
import net.sourceforge.fuge.bio.data.InternalData;
import net.sourceforge.fuge.collection.DataCollection;
import net.sourceforge.fuge.service.EntityServiceException;
import net.sourceforge.fuge.util.generatedJAXB2.*;
import net.sourceforge.fuge.common.audit.Person;
import net.sourceforge.symba.mapping.hibernatejaxb2.DatabaseObjectHelper;

import javax.xml.bind.JAXBElement;
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
public class DataCollectionMappingHelper implements MappingHelper<DataCollection, FuGECollectionDataCollectionType> {
    private final int NUMBER_ELEMENTS = 2;
    private final DescribableMappingHelper cd;
    private final DataMappingHelper cdat;

    public DataCollectionMappingHelper() {
        this.cd = ( new IdentifiableMappingHelper() ).getCisbanDescribableHelper();
        this.cdat = new DataMappingHelper();

    }

    // @todo add implementation for InternalData, the other child of the abstract Data element. Also, HigherLevelAnalysis and Dimension are not implemented
    public DataCollection unmarshal(
            FuGECollectionDataCollectionType datCollXML, DataCollection datColl, Person performer )
            throws EntityServiceException {

        // set describable information
        datColl = ( DataCollection ) cd.unmarshal( datCollXML, datColl, performer );

        datColl = unmarshalCollectionContents( datCollXML, datColl, performer );

        // load the fuge object into the database
        DatabaseObjectHelper.save( "net.sourceforge.fuge.collection.DataCollection", datColl, performer );

        return datColl;
    }

    public DataCollection unmarshalCollectionContents( FuGECollectionDataCollectionType datCollXML,
                                                       DataCollection datColl, Person performer )
            throws EntityServiceException {
        // Create collection of data for addition to the data collection
        Set<Data> datas = new HashSet<Data>();

        for ( JAXBElement<? extends FuGEBioDataDataType> DataElementXML : datCollXML.getData() ) {
            FuGEBioDataDataType dataXML = DataElementXML.getValue();

            // As RawData objects do not appear in the XML, there is no need to code it here.
            ExternalData externalData = ( ExternalData ) DatabaseObjectHelper.getOrCreate(
                    dataXML.getIdentifier(),
                    dataXML.getName(),
                    "net.sourceforge.fuge.bio.data.ExternalData" );
            externalData = ( ExternalData ) cdat.unmarshal( dataXML, externalData, performer );
            DatabaseObjectHelper.save( "net.sourceforge.fuge.bio.data.ExternalData", externalData, null );
            datas.add( externalData );

        }

        // Add the set of generic datas to the Data collection
        datColl.setAllData( datas );

        return datColl;
    }

    // @todo add implementation for InternalData, the other child of the abstract Data element. Also, HigherLevelAnalysis and Dimension are not implemented
    public FuGECollectionDataCollectionType marshal(
            FuGECollectionDataCollectionType datCollXML, DataCollection datColl )
            throws EntityServiceException {

        // set describable information
        datCollXML = ( FuGECollectionDataCollectionType ) cd.marshal( datCollXML, datColl );

        // set up the factory
        ObjectFactory factory = new ObjectFactory();

        for ( Data data : datColl.getAllData() ) {

            // As RawData objects do not appear in the XML, there is no need to code it here.
            if ( data instanceof ExternalData ) {
                datCollXML.getData()
                        .add( factory.createExternalData( ( FuGEBioDataExternalDataType ) cdat.marshal( new FuGEBioDataExternalDataType(), data ) ) );
            } else if ( data instanceof InternalData ) {
                datCollXML.getData()
                        .add( factory.createInternalData( ( FuGEBioDataInternalDataType ) cdat.marshal( new FuGEBioDataGenericInternalDataType(), data ) ) );
            }
        }
        return datCollXML;
    }

    public FuGECollectionDataCollectionType generateRandomXML( FuGECollectionDataCollectionType datCollXML ) {
        // set describable information
        datCollXML = ( FuGECollectionDataCollectionType ) cd.generateRandomXML( datCollXML );

        return datCollXML;
    }

    // todo deal with InternalData
    public FuGECollectionFuGEType generateRandomXMLWithLinksOut( FuGECollectionFuGEType frXML ) {

        // create the jaxb Data collection object
        FuGECollectionDataCollectionType datCollXML = generateRandomXML( new FuGECollectionDataCollectionType() );

        // set up the factory
        ObjectFactory factory = new ObjectFactory();

        for ( int i = 0; i < NUMBER_ELEMENTS; i++ ) {

            // As RawData objects do not appear in the XML, there is no need to code it here.
            datCollXML.getData().add(
                    factory.createExternalData(
                            ( FuGEBioDataExternalDataType ) cdat.generateRandomXMLWithLinksOut(
                                    new FuGEBioDataExternalDataType(), frXML ) ) );
        }

        frXML.setDataCollection( datCollXML );
        return frXML;
    }
}
