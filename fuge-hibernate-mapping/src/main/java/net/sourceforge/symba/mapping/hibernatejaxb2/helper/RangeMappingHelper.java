package net.sourceforge.symba.mapping.hibernatejaxb2.helper;

import net.sourceforge.fuge.common.measurement.Range;
import net.sourceforge.fuge.common.ontology.OntologyTerm;
import net.sourceforge.fuge.common.audit.Person;
import net.sourceforge.fuge.util.generatedJAXB2.FuGECommonMeasurementRangeType;
import net.sourceforge.fuge.service.EntityServiceException;

import java.util.Set;
import java.util.HashSet;

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
public class RangeMappingHelper implements MappingHelper<Range, FuGECommonMeasurementRangeType> {

    /**
     * Marshal the contents of the POJO object into the JAXB object
     *
     * @param rangeXML the FuGE JAXB object to be filled
     * @param range    the FuGE POJO to parse
     * @return filled version of JAXB object
     * @throws net.sourceforge.fuge.service.EntityServiceException
     *          if there is a problem with the connection to
     *          the database
     */
    public FuGECommonMeasurementRangeType marshal( FuGECommonMeasurementRangeType rangeXML, Range range ) throws
            EntityServiceException {

        if ( range.getLowerLimit() != null ) {
            rangeXML.setLowerLimit( range.getLowerLimit() );
        }
        if ( range.getUpperLimit() != null ) {
            rangeXML.setUpperLimit( range.getUpperLimit() );
        }

        if ( !range.getRangeDescriptors().isEmpty() ) {
            for ( OntologyTerm ontologyTerm : range.getRangeDescriptors() ) {
                FuGECommonMeasurementRangeType.RangeDescriptors rd =
                        new FuGECommonMeasurementRangeType.RangeDescriptors();
                rd.setOntologyTermRef( ontologyTerm.getIdentifier() );
                rangeXML.getRangeDescriptors().add( rd );
            }
        }

        return rangeXML;
    }

    /**
     * Unmarshal the contents of the JAXB object into the POJO object
     *
     * @param rangeXML  the FuGE JAXB object to parse
     * @param range     the FuGE POJO to be filled
     * @param performer the person to assign to the audit trail
     * @return filled version of POJO object
     * @throws net.sourceforge.fuge.service.EntityServiceException
     *          if there is a problem with the connection to
     *          the database
     */
    public Range unmarshal( FuGECommonMeasurementRangeType rangeXML, Range range, Person performer ) throws
            EntityServiceException {

        // set any Range-specific traits

        if ( rangeXML.getLowerLimit() != null ) {
            range.setLowerLimit( rangeXML.getLowerLimit() );
        }

        if ( rangeXML.getUpperLimit() != null ) {
            range.setUpperLimit( rangeXML.getUpperLimit() );
        }

        if ( !rangeXML.getRangeDescriptors().isEmpty() ) {
            Set<OntologyTerm> ots = new HashSet<OntologyTerm>();
            for ( FuGECommonMeasurementRangeType.RangeDescriptors rangeDescriptor : rangeXML.getRangeDescriptors() ) {
                ots.add( ( OntologyTerm ) entityService.getIdentifiable( rangeDescriptor.getOntologyTermRef() ) );
            }
            range.setRangeDescriptors( ots );
        }

        return range;
    }
}
