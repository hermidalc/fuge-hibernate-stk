package net.sourceforge.symba.mapping.hibernatejaxb2.helper;

import net.sourceforge.fuge.service.EntityService;
import net.sourceforge.fuge.service.EntityServiceException;
import net.sourceforge.fuge.ServiceLocator;
import net.sourceforge.fuge.common.audit.Person;

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
 * The interface for all Helpers, providing basic methods they must implement, which then can be used when retrieving
 * the objects via web services, as well as generally within SyMBA.
 * <p/>
 * P stands for the FuGE POJO object made by AndroMDA
 * <p/>
 * J stands for the FuGE JAXB object made by JAXB2
 * <p/>
 * $LastChangedDate$
 * $LastChangedRevision$
 * $Author$
 * $HeadURL: $
 */

public interface MappingHelper<P, J> {

    static final EntityService entityService = ServiceLocator.instance().getEntityService();

    /**
     * Marshal the contents of the POJO object into the JAXB object
     *
     * @param j the FuGE JAXB object to be filled
     * @param p the FuGE POJO to parse
     * @return p p with the contents of j put into it
     * @throws net.sourceforge.fuge.service.EntityServiceException
     *          if there is a problem with the connection to
     *          the database
     */
    J marshal( J j, P p ) throws EntityServiceException;

    /**
     * Unmarshal the contents of the JAXB object into the POJO object.
     * <p/>
     * A deliberate design decision has been made here that the hibernate object p will not be updated
     * in the database within this method. This allows any final changes to be made to an object once the unmarshal
     * code has been run, before loading into the database. However, any child objects that need to be loaded into
     * the database in order for successful filling of the parent object p WILL be loaded into the database within
     * this method.
     *
     * You may optionally pass a performer, who will be marked within the audit trail as the person who is associated
     * with loading of all of the child FuGE objects that must be loaded into the database, as described in the
     * previous paragraph. If the performer is null, then the audit information will be added but without a person
     * attached to it. 
     *
     * @param j the FuGE JAXB object to parse
     * @param p the FuGE POJO to be filled
     * @param performer the Person object to associate with the audit trail
     * @return p p with the contents of j put into it
     * @throws net.sourceforge.fuge.service.EntityServiceException
     *          if there is a problem with the connection to
     *          the database
     */
    P unmarshal( J j, P p, Person performer ) throws EntityServiceException;
}
