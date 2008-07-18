package net.sourceforge.symba.mapping.hibernatejaxb2;

import net.sourceforge.symba.mapping.hibernatejaxb2.xml.XMLUnmarshaler;
import net.sourceforge.fuge.common.audit.Person;
import net.sourceforge.fuge.service.EntityService;
import net.sourceforge.fuge.ServiceLocator;

import java.util.Date;

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

public class UnmarshalXML {
    public static void main( String[] args ) throws Exception {
        if ( args.length != 2 )
            throw new java.lang.Exception( "You must provide 2 arguments in this order: schema-file input-xml-file" );

        XMLUnmarshaler unmarshalTest = new XMLUnmarshaler( args[0], args[1] );

        Person performer = ( Person ) DatabaseObjectHelper.getOrCreate( null, "Example Person " + new Date(), "net.sourceforge.fuge.common.audit.Person" );

        EntityService entityService = ServiceLocator.instance().getEntityService();

        entityService.save( "net.sourceforge.fuge.common.audit.Person", performer, null );

        unmarshalTest.Jaxb2ToFuGE( performer );
    }
}
