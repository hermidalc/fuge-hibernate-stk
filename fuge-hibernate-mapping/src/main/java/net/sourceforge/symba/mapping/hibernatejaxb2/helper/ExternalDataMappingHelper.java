package net.sourceforge.symba.mapping.hibernatejaxb2.helper;

import net.sourceforge.fuge.bio.data.ExternalData;
import net.sourceforge.fuge.common.ontology.OntologyTerm;
import net.sourceforge.fuge.common.audit.Person;
import net.sourceforge.fuge.common.description.Uri;
import net.sourceforge.fuge.service.EntityServiceException;
import net.sourceforge.fuge.util.generatedJAXB2.FuGEBioDataExternalDataType;
import net.sourceforge.fuge.util.generatedJAXB2.FuGECollectionFuGEType;
import net.sourceforge.fuge.util.generatedJAXB2.FuGECommonDescriptionURIType;

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
public class ExternalDataMappingHelper implements MappingHelper<ExternalData, FuGEBioDataExternalDataType> {
    private final DescribableMappingHelper cd;

    public ExternalDataMappingHelper() {
        this.cd = new DescribableMappingHelper();
    }

    public ExternalData unmarshal( FuGEBioDataExternalDataType externalDataXML,
                                   ExternalData externalData, Person performer ) throws EntityServiceException {

        // set only ExternalData-specific traits

        // FileFormat
        if ( externalDataXML.getFileFormat() != null ) {
            externalData.setFileFormat( ( OntologyTerm ) entityService.getIdentifiable( externalDataXML.getFileFormat().getOntologyTermRef() ) );
        }

        // Location
        if ( externalDataXML.getLocation() != null ) {
            externalData.setLocation( externalDataXML.getLocation() );
        }

        // external format documentation
        if ( externalDataXML.getExternalFormatDocumentation() != null ) {
            // create jaxb object
            FuGECommonDescriptionURIType externalFormatURIXML = externalDataXML.getExternalFormatDocumentation().getURI();

            // create fuge object
            Uri externalFormatURI = ( Uri ) entityService.createDescribable( "net.sourceforge.fuge.common.description.Uri" );

            // set fuge object
            externalFormatURI = ( Uri ) cd.unmarshal( externalFormatURIXML, externalFormatURI, performer );
            externalFormatURI.setLocation( externalFormatURIXML.getLocation() );

            // load fuge object into database
            entityService.save( "net.sourceforge.fuge.common.description.Uri", externalFormatURI, performer );

            // load fuge object into describable
            externalData.setExternalFormatDocumentation( externalFormatURI );
        }

        return externalData;
    }

    public FuGEBioDataExternalDataType marshal( FuGEBioDataExternalDataType externalDataXML,
                                                ExternalData externalData ) throws EntityServiceException {

        // set any ExternalData-specific traits

        // FileFormat
        if ( externalData.getFileFormat() != null ) {
            FuGEBioDataExternalDataType.FileFormat fileformatXML = new FuGEBioDataExternalDataType.FileFormat();
            fileformatXML.setOntologyTermRef( externalData.getFileFormat().getIdentifier() );
            externalDataXML.setFileFormat( fileformatXML );
        }

        // Location
        if ( externalData.getLocation() != null ) {
            externalDataXML.setLocation( externalData.getLocation() );
        }

        // external format documentation
        if ( externalData.getExternalFormatDocumentation() != null ) {
            // create jaxb object
            FuGEBioDataExternalDataType.ExternalFormatDocumentation externalFormatDocXML = new FuGEBioDataExternalDataType.ExternalFormatDocumentation();
            FuGECommonDescriptionURIType uriXML = new FuGECommonDescriptionURIType();

            // set jaxb object
            uriXML = ( FuGECommonDescriptionURIType ) cd.marshal(
                    uriXML, externalData.getExternalFormatDocumentation() );
            uriXML.setLocation( externalData.getExternalFormatDocumentation().getLocation() );

            // load jaxb object into describableXML
            externalFormatDocXML.setURI( uriXML );
            externalDataXML.setExternalFormatDocumentation( externalFormatDocXML );
        }

        return externalDataXML;
    }

    public FuGEBioDataExternalDataType generateRandomXML( FuGEBioDataExternalDataType externalDataXML ) {

        // Location
        externalDataXML.setLocation( "http://some.random.url/" + String.valueOf( Math.random() ) );

        // external format documentation
        FuGEBioDataExternalDataType.ExternalFormatDocumentation efdXML = new FuGEBioDataExternalDataType.ExternalFormatDocumentation();
        FuGECommonDescriptionURIType uriXML = new FuGECommonDescriptionURIType();

        // set jaxb object
        uriXML = ( FuGECommonDescriptionURIType ) cd.generateRandomXML( uriXML );
        uriXML.setLocation( "http://some.sortof.string/" + String.valueOf( Math.random() ) );

        // load jaxb object into describableXML
        efdXML.setURI( uriXML );
        externalDataXML.setExternalFormatDocumentation( efdXML );

        return externalDataXML;
    }

    public FuGEBioDataExternalDataType generateRandomXMLWithLinksOut( FuGEBioDataExternalDataType externalDataXML,
                                                                      FuGECollectionFuGEType frXML ) {
        externalDataXML = generateRandomXML( externalDataXML );

        // FileFormat
        if ( frXML.getOntologyCollection() != null ) {
            FuGEBioDataExternalDataType.FileFormat fileformatXML = new FuGEBioDataExternalDataType.FileFormat();
            fileformatXML.setOntologyTermRef(
                    frXML.getOntologyCollection().getOntologyTerm().get( 0 ).getValue().getIdentifier() );
            externalDataXML.setFileFormat( fileformatXML );
        }

        return externalDataXML;
    }
}
