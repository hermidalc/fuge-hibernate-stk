package net.sourceforge.symba.mapping.hibernatejaxb2;

import net.sourceforge.fuge.common.audit.Person;
import net.sourceforge.fuge.common.Identifiable;
import net.sourceforge.fuge.common.Describable;
import net.sourceforge.fuge.service.EntityService;
import net.sourceforge.fuge.service.EntityServiceException;
import net.sourceforge.fuge.util.identification.FuGEIdentifier;
import net.sourceforge.fuge.util.identification.FuGEIdentifierFactory;
import net.sourceforge.fuge.ServiceLocator;

/**
 * contains some convienience methods to help access to the database services.
 * <p/>
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

public class DatabaseObjectHelper {
    private static final String DEFAULT_DOMAIN_NAME = "net.sourceforge.fuge";

    // it is OK for the EntityService to be static, as the ServiceLocator instance used to get the EntityService
    // is itself static.
    private static final EntityService ENTITY_SERVICE = ServiceLocator.instance().getEntityService();

    // we are happy to use the same instance of FuGEIdentifier across all DatabaseObjectHelpers.
    private static FuGEIdentifier ID_MAKER = FuGEIdentifierFactory.createFuGEIdentifier( DEFAULT_DOMAIN_NAME, null );


    public static void setDomainName( String domainName ) {
        ID_MAKER = FuGEIdentifierFactory.createFuGEIdentifier( domainName, null );
    }

    /**
     * Check to see if the identifier is already in the database. If it is, return the associated object. If it isn't,
     * then run createIdentifiable with the provided identifier and return the new object. If no identifier
     * was provided, then create a fresh identifier and return the new object.
     * <p/>
     * This does NOT create any object in the database.
     *
     * @param identifier              The identifier to search for. If null, then the object is assumed to be new, and a
     *                                new identifier will be created for it. If not null, the identifier will be
     *                                searched for, and if not present in the database then will be used in the creation
     *                                of a new object.
     * @param name                    The name to associate with the object, if it is new. If already extant, this
     *                                value will be ignored.
     * @param fullyQualifiedClassName The class to use when creating the object, if it is new. If already extant, this value will be ignored.
     * @return the retrieved or newly-created Identifiable. Cast it to the correct class type.
     */
    public static Identifiable getOrCreate( String identifier,
                                            String name,
                                            String fullyQualifiedClassName ) {
        if ( identifier != null && identifier.length() > 0 ) {
            Identifiable identifiable = ENTITY_SERVICE.getIdentifiable( identifier );
            if ( identifiable == null ) {
                // No identifiable found - create a new one based on the provided identifier.
                if ( name != null && name.length() > 0 ) {
                    return ENTITY_SERVICE.createIdentifiable( identifier, name, fullyQualifiedClassName );
                } else {
                    return ENTITY_SERVICE.createIdentifiable( identifier, fullyQualifiedClassName );
                }
            } else {
                return identifiable;
            }
        } else {
            // if we get here then the identifier is empty - create a new one
            if ( name != null && name.length() > 0 ) {
                return ENTITY_SERVICE.createIdentifiable(
                        ID_MAKER.create( fullyQualifiedClassName ), name, fullyQualifiedClassName );
            } else {
                return ENTITY_SERVICE.createIdentifiable(
                        ID_MAKER.create( fullyQualifiedClassName ), fullyQualifiedClassName );
            }
        }
    }

    /**
     * Should only be used if you want to re-assign an identifier to an already existing Identifiable object
     * and then load that object into the database, as it does NOT create a new object but assumes a pre-existing one.
     * <p/>
     * The person must already be loaded in the database, if used. However, the method will deal properly
     * with null values in the person argument, therefore if you don't have person information, just pass
     * a null value for that argument and the audit information will be created without it.
     *
     * @param fullyQualifiedClassName its fully-qualified class name
     * @param identifiable            the object whose identifier is about to be re-assigned
     * @param person                  the person to attribute the change to. Set to null if you don't want audit information.
     * @return the newly-loaded object
     * @throws EntityServiceException if there is a problme loading the object into the database
     */
    public static Identifiable assignAndSave( String fullyQualifiedClassName, Identifiable identifiable,
                                              Person person ) {

        // set the new identifier
        identifiable.setIdentifier( ID_MAKER.create( fullyQualifiedClassName ) );

        // make sure to remove the old database ID
        identifiable.setId( null );

        return ( Identifiable ) ENTITY_SERVICE.save( fullyQualifiedClassName, identifiable, person );
    }

    /**
     * Placeholder to allow developers to ensure that some logic is performed before running save from the EntityService.
     * All Version 1 STK code within this project uses this in favor of the EntityService save method.
     *
     * @param fullyQualifiedClassName its fully-qualified class name
     * @param describable             the object to be saved
     * @param person                  the person to attribute the change to. Set to null if you don't want audit information. @return the newly-loaded object
     * @return the saved object, with any additional values filled in, e.g. id
     * @throws EntityServiceException if there is a problme loading the object into the database
     */
    public static Describable save( String fullyQualifiedClassName, Describable describable,
                                    Person person ) {

        // put your core logic here.

        return ENTITY_SERVICE.save( fullyQualifiedClassName, describable, person );
    }

}
