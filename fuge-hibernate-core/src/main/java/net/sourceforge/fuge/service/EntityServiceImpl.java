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
/**
 * This is only generated once! It will never be overwritten.
 * You can (and have to!) safely modify it by hand.
 */
package net.sourceforge.fuge.service;

import net.sourceforge.fuge.common.audit.Person;
import net.sourceforge.fuge.common.audit.Audit;
import net.sourceforge.fuge.common.audit.AuditAction;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * @see net.sourceforge.fuge.service.EntityService
 */
public class EntityServiceImpl
        extends net.sourceforge.fuge.service.EntityServiceBase {

    public static boolean addDbAuditTrail;

    public EntityServiceImpl() {

        // get the value for whether or not the audit trail entry for loading into the database should be
        // performed. The only time it shouldn't be performed (against the developer's wishes) is for
        // unit-testing the round-trip.
        ResourceBundle rb = ResourceBundle.getBundle( "fuge-hibernate" );
        addDbAuditTrail = rb.getString( "net.sourceforge.fuge.addDbAuditTrail" ) != null &&
                rb.getString( "net.sourceforge.fuge.addDbAuditTrail" ).equals( "true" );
    }

    /**
     * @see net.sourceforge.fuge.service.EntityService#getIdentifiable(java.lang.Long)
     */
    protected net.sourceforge.fuge.common.Identifiable handleGetIdentifiable( java.lang.Long id )
            throws java.lang.Exception {
        return ( net.sourceforge.fuge.common.Identifiable ) getIdentifiableDao().load(
                net.sourceforge.fuge.common.IdentifiableDao.TRANSFORM_NONE, id );
    }

    /**
     * @see net.sourceforge.fuge.service.EntityService#getDescribable(java.lang.Long)
     */
    protected net.sourceforge.fuge.common.Describable handleGetDescribable( java.lang.Long id )
            throws java.lang.Exception {
        return ( net.sourceforge.fuge.common.Describable ) getDescribableDao().load(
                net.sourceforge.fuge.common.DescribableDao.TRANSFORM_NONE, id );
    }

    /**
     * @see net.sourceforge.fuge.service.EntityService#getIdentifiable(java.lang.String)
     */
    protected net.sourceforge.fuge.common.Identifiable handleGetIdentifiable( java.lang.String identifier )
            throws java.lang.Exception {
        return getIdentifiableDao().get( identifier );
    }

    /**
     * @see net.sourceforge.fuge.service.EntityService#createDescribable(java.lang.String)
     */
    protected net.sourceforge.fuge.common.Describable handleCreateDescribable( java.lang.String fullClassName )
            throws java.lang.Exception {
        Method newInstanceMethod = Class.forName( fullClassName + "$Factory" ).getMethod( "newInstance" );
        return ( net.sourceforge.fuge.common.Describable ) newInstanceMethod.invoke( null );
    }

    /**
     * @see net.sourceforge.fuge.service.EntityService#createIdentifiable(java.lang.String, java.lang.String, java.lang.String)
     */
    protected net.sourceforge.fuge.common.Identifiable handleCreateIdentifiable( java.lang.String identifier, java.lang.String name, java.lang.String fullyQualifiedClassName )
            throws java.lang.Exception {
        net.sourceforge.fuge.common.Identifiable createdIdentifiable;

        try {

            Method newInstanceMethod = Class.forName( fullyQualifiedClassName + "$Factory" ).getMethod( "newInstance" );
            Object temporaryObject = newInstanceMethod.invoke( null );

            // return immediately, not providing the object, if the newly-created class is not Identifiable
            if ( !( temporaryObject instanceof net.sourceforge.fuge.common.Identifiable ) ) {
                System.err.println( "Created object is not of type Identifiable: " + fullyQualifiedClassName );
                return null;
            }

            createdIdentifiable = ( net.sourceforge.fuge.common.Identifiable ) temporaryObject;

            // Set the identifier
            Method setIdentifierMethod = Class.forName( fullyQualifiedClassName )
                    .getMethod( "setIdentifier", Class.forName( "java.lang.String" ) );
            setIdentifierMethod.invoke( createdIdentifiable, identifier );

            // set the name
            Method setNameMethod = Class.forName( fullyQualifiedClassName )
                    .getMethod( "setName", Class.forName( "java.lang.String" ) );
            setNameMethod.invoke( createdIdentifiable, name );

        } catch ( NoSuchMethodException e ) {
            e.printStackTrace();
            return null;
        } catch ( ClassNotFoundException e ) {
            e.printStackTrace();
            return null;
        } catch ( InvocationTargetException e ) {
            e.printStackTrace();
            return null;
        } catch ( IllegalAccessException e ) {
            e.printStackTrace();
            return null;
        }

        return createdIdentifiable;
    }

    /**
     * @see net.sourceforge.fuge.service.EntityService#save(java.lang.String, net.sourceforge.fuge.common.Describable)
     */
    protected net.sourceforge.fuge.common.Describable handleSave( java.lang.String fullyQualifiedClassName, net.sourceforge.fuge.common.Describable describable )
            throws java.lang.Exception {
        if ( describable.getId() == null ) {
            return create( fullyQualifiedClassName, describable );
        } else {
            return update( fullyQualifiedClassName, describable );
        }
    }

    /**
     * @see net.sourceforge.fuge.service.EntityService#save(java.lang.String, net.sourceforge.fuge.common.Describable, boolean)
     */
    protected net.sourceforge.fuge.common.Describable handleSave( java.lang.String fullyQualifiedClassName, net.sourceforge.fuge.common.Describable describable, boolean addAudit )
            throws java.lang.Exception {

        if ( addAudit ) {
            return save( fullyQualifiedClassName, describable, null );
        } else {
            return save( fullyQualifiedClassName, describable );
        }
    }

    /**
     * @see net.sourceforge.fuge.service.EntityService#save(java.lang.String, net.sourceforge.fuge.common.Describable, net.sourceforge.fuge.common.audit.Person)
     */
    protected net.sourceforge.fuge.common.Describable handleSave( java.lang.String fullyQualifiedClassName, net.sourceforge.fuge.common.Describable describable, net.sourceforge.fuge.common.audit.Person performer )
            throws java.lang.Exception {

        if ( describable.getId() == null ) {
            return create( fullyQualifiedClassName, describable, performer );
        } else {
            return update( fullyQualifiedClassName, describable, performer );
        }
    }

    /**
     * @see net.sourceforge.fuge.service.EntityService#create(java.lang.String, net.sourceforge.fuge.common.Describable)
     */
    protected net.sourceforge.fuge.common.Describable handleCreate( java.lang.String fullyQualifiedClassName, net.sourceforge.fuge.common.Describable describable )
            throws java.lang.Exception {

        // use the class for the object to save as the class to retrieve the update method from
        Class specificClass = Class.forName( fullyQualifiedClassName );
        Method createMethod = Class.forName( fullyQualifiedClassName + "Dao" ).getMethod( "create", specificClass );

        // get just the class name, without the package name
        String entityType = fullyQualifiedClassName.substring( fullyQualifiedClassName.lastIndexOf( "." ) + 1 );
        // use that class name to get the method to retrieve the Spring Data Access Object
        Method daoMethod = this.getClass().getSuperclass().getDeclaredMethod( "get" + entityType + "Dao" );
        // use the method to retrieve the Data Access Object
        Object daoObj = daoMethod.invoke( this );

        try {
            // Create the object in the database via the update method retrieved earlier and the dao object created earlier
            createMethod.invoke( daoObj, specificClass.cast( describable ) );
        }
        catch ( InvocationTargetException ex ) {
            throw new EntityServiceException( "Error invoking the create method on " + fullyQualifiedClassName, ex.getTargetException() );
        }

        return describable;
    }

    /**
     * @see net.sourceforge.fuge.service.EntityService#create(java.lang.String, net.sourceforge.fuge.common.Describable, boolean)
     */
    protected net.sourceforge.fuge.common.Describable handleCreate( java.lang.String fullyQualifiedClassName, net.sourceforge.fuge.common.Describable describable, boolean addAuditInfo )
            throws java.lang.Exception {

        if ( addAuditInfo ) {
            return create( fullyQualifiedClassName, describable, null );
        } else {
            return create( fullyQualifiedClassName, describable );
        }
    }

    /**
     * @see net.sourceforge.fuge.service.EntityService#create(java.lang.String, net.sourceforge.fuge.common.Describable, net.sourceforge.fuge.common.audit.Person)
     */
    protected net.sourceforge.fuge.common.Describable handleCreate( java.lang.String fullyQualifiedClassName, net.sourceforge.fuge.common.Describable describable, net.sourceforge.fuge.common.audit.Person performer )
            throws java.lang.Exception {

        if ( !( describable instanceof Audit ) ) {
            // Add the audit information
            describable.setAuditTrail( getNewAuditTrail( ( Set<Audit> ) describable.getAuditTrail(), performer, AuditAction.creation ) );
        }

        return create( fullyQualifiedClassName, describable );
    }

    /**
     * @see net.sourceforge.fuge.service.EntityService#update(java.lang.String, net.sourceforge.fuge.common.Describable)
     */
    protected net.sourceforge.fuge.common.Describable handleUpdate( java.lang.String fullyQualifiedClassName, net.sourceforge.fuge.common.Describable describable )
            throws java.lang.Exception {

        // use the class for the object to save as the class to retrieve the update method from
        Class specificClass = Class.forName( fullyQualifiedClassName );
        Method updateMethod = Class.forName( fullyQualifiedClassName + "Dao" ).getMethod( "update", specificClass );

        // get just the class name, without the package name
        String entityType = fullyQualifiedClassName.substring( fullyQualifiedClassName.lastIndexOf( "." ) + 1 );
        // use that class name to get the method to retrieve the Spring Data Access Object
        Method daoMethod = this.getClass().getSuperclass().getDeclaredMethod( "get" + entityType + "Dao" );
        // use the method to retrieve the Data Access Object
        Object daoObj = daoMethod.invoke( this );

        try {
            // Create the object in the database via the update method retrieved earlier and the dao object created earlier
            updateMethod.invoke( daoObj, specificClass.cast( describable ) );
        }
        catch ( InvocationTargetException ex ) {
            throw new EntityServiceException( "Error invoking the update method on " + fullyQualifiedClassName, ex.getTargetException() );
        }

        return describable;
    }

    /**
     * @see net.sourceforge.fuge.service.EntityService#update(java.lang.String, net.sourceforge.fuge.common.Describable, boolean)
     */
    protected net.sourceforge.fuge.common.Describable handleUpdate( java.lang.String fullyQualifiedClassName, net.sourceforge.fuge.common.Describable describable, boolean addAuditInfo )
            throws java.lang.Exception {

        if ( addAuditInfo ) {
            return update( fullyQualifiedClassName, describable, null );
        } else {
            return update( fullyQualifiedClassName, describable );
        }
    }

    /**
     * @see net.sourceforge.fuge.service.EntityService#update(java.lang.String, net.sourceforge.fuge.common.Describable, net.sourceforge.fuge.common.audit.Person)
     */
    protected net.sourceforge.fuge.common.Describable handleUpdate( java.lang.String fullyQualifiedClassName, net.sourceforge.fuge.common.Describable describable, net.sourceforge.fuge.common.audit.Person performer )
            throws java.lang.Exception {

        if ( !( describable instanceof Audit ) ) {
            // Add the audit information
            describable.setAuditTrail( getNewAuditTrail( ( Set<Audit> ) describable.getAuditTrail(), performer, AuditAction.modification ) );
        }

        return update( fullyQualifiedClassName, describable );
    }

    /**
     * Prepare and store an audit message in the database. If person is not null, it MUST ALREADY be loaded into
     * the database when you assign it to the Audit instance.
     *
     * @param existingAuditTrail the audit trail to add the new audit information to
     * @param performer          the person to assign to this audit instance, may be null
     * @param auditAction        whether it is a creation or update of an object. @return the updated set of audit changes
     * @return the audit trail with the added audit information
     * @throws EntityServiceException if there is a problem connecting to the database
     */
    private Set<Audit> getNewAuditTrail( Set<Audit> existingAuditTrail, Person performer, AuditAction auditAction ) throws EntityServiceException {

        if ( addDbAuditTrail ) {

            Audit auditInstance = ( Audit ) createDescribable( "net.sourceforge.fuge.common.audit.Audit" );
            auditInstance.setAction( auditAction );
            // todo not sure if this is the right conversion method for new Date() to java.sql.timestamp
            auditInstance.setDate( new java.sql.Timestamp( ( new Date() ).getTime() ) );
            if ( performer != null ) {
                auditInstance.setPerformer( performer );
            }

            save( "net.sourceforge.fuge.common.audit.Audit", auditInstance, null );
            existingAuditTrail.add( auditInstance );
        }
        
        return existingAuditTrail;
    }

}