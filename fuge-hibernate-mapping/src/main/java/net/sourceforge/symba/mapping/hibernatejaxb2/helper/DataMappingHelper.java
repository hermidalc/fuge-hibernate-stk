package net.sourceforge.symba.mapping.hibernatejaxb2.helper;


import net.sourceforge.fuge.bio.data.Data;
import net.sourceforge.fuge.bio.data.ExternalData;
import net.sourceforge.fuge.service.EntityServiceException;
import net.sourceforge.fuge.util.generatedJAXB2.FuGEBioDataDataType;
import net.sourceforge.fuge.util.generatedJAXB2.FuGEBioDataExternalDataType;
import net.sourceforge.fuge.common.audit.Person;

/**
 * Copyright Notice
 * <p/>
 * The MIT License
 * <p/>
 * Copyright (c) 2008 2007-8 Proteomics Standards Initiative / Microarray and Gene Expression Data Society
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * <p/>
 * Acknowledgements
 * The authors wish to thank the Proteomics Standards Initiative for
 * the provision of infrastructure and expertise in the form of the PSI
 * Document Process that has been used to formalise this document.
 * <p/>
 * $LastChangedDate$
 * $LastChangedRevision$
 * $Author$
 * $HeadURL: $
 */
public class DataMappingHelper implements MappingHelper<Data, FuGEBioDataDataType> {
    private final IdentifiableMappingHelper ci;
    private final ExternalDataMappingHelper ced;

    public DataMappingHelper() {
        this.ci = new IdentifiableMappingHelper();
        this.ced = new ExternalDataMappingHelper();
    }

    /**
     * todo Dimension not implemented
     * <p/>
     * Does not perform any update to the database of the Data object (though child objects may be updated).
     *
     * @param dataXML   the jaxb object to parse
     * @param data      the hibernate object to fill
     * @param performer the person to assign to the audit trail
     * @return the newly-filled hibernate object
     * @throws EntityServiceException if there is a problem with the connection to the database
     */
    public Data unmarshal( FuGEBioDataDataType dataXML, Data data, Person performer )
            throws EntityServiceException {

        // RawData will never be in the XML
        if ( dataXML instanceof FuGEBioDataExternalDataType ) {

            ExternalData externalData = ( ExternalData ) data;

            // set the data attributes.
            externalData = ( ExternalData ) ci.unmarshal( dataXML, externalData, performer );

            // set the external data attributes
            externalData = ced.unmarshal( ( FuGEBioDataExternalDataType ) dataXML, externalData, performer );

            return externalData;
        }
        return null; // shouldn't get here as there is currently only one type of Data coded - will get here if InternalData is used in the xml. 
    }

    // @todo Dimension and InternalData not implemented
    public FuGEBioDataDataType marshal( FuGEBioDataDataType dataXML, Data data )
            throws EntityServiceException {

        // RawData will never be in the XML
        if ( data instanceof ExternalData ) {
            // create fuge object
            FuGEBioDataExternalDataType externalDataXML = new FuGEBioDataExternalDataType();

            // set the data attributes
            externalDataXML = ( FuGEBioDataExternalDataType ) ci.marshal( externalDataXML, data );

            // set the externaldata attributes
            externalDataXML = ced.marshal( externalDataXML, ( ExternalData ) data );

            return externalDataXML;
        }
        return null; // shouldn't get here as there is currently only one type of Data coded - will get here if InternalData is used in the xml.
    }
}
