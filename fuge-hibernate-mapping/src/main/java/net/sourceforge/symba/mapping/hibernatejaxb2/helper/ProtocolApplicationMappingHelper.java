package net.sourceforge.symba.mapping.hibernatejaxb2.helper;


import net.sourceforge.fuge.bio.data.ExternalData;
import net.sourceforge.fuge.bio.data.Data;
import net.sourceforge.fuge.bio.material.Material;
import net.sourceforge.fuge.common.protocol.*;
import net.sourceforge.fuge.common.description.Description;
import net.sourceforge.fuge.common.audit.Person;
import net.sourceforge.fuge.service.EntityServiceException;
import net.sourceforge.fuge.util.generatedJAXB2.*;
import net.sourceforge.symba.mapping.hibernatejaxb2.DatabaseObjectHelper;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.DatatypeConfigurationException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;
import java.util.GregorianCalendar;

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
public class ProtocolApplicationMappingHelper implements
        MappingHelper<ProtocolApplication, FuGECommonProtocolProtocolApplicationType> {
    private final IdentifiableMappingHelper ci;
    private final ParameterizableApplicationMappingHelper cparapp;
    private final DescribableMappingHelper cd;
    private final GenericProtocolApplicationMappingHelper cgpa;

    public ProtocolApplicationMappingHelper() {
        this.ci = new IdentifiableMappingHelper();
        this.cd = ci.getCisbanDescribableHelper();
        this.cparapp = new ParameterizableApplicationMappingHelper();
        this.cgpa = new GenericProtocolApplicationMappingHelper();
    }

    // @todo Have not coded PartitionPair
    public ProtocolApplication unmarshal(
            FuGECommonProtocolProtocolApplicationType protocolApplicationXML,
            ProtocolApplication protocolApplication,
            Person performer )
            throws EntityServiceException {

        // determine what sort of protocol application it is
        if ( protocolApplicationXML instanceof FuGECommonProtocolGenericProtocolApplicationType ) {

            GenericProtocolApplication genericProtocolApplication = ( GenericProtocolApplication ) protocolApplication;

            // get protocol application attributes
            genericProtocolApplication = ( GenericProtocolApplication ) ci
                    .unmarshal( protocolApplicationXML, genericProtocolApplication, performer );
            genericProtocolApplication = ( GenericProtocolApplication ) cparapp
                    .unmarshal( protocolApplicationXML, genericProtocolApplication, performer );

            if ( protocolApplicationXML.getActivityDate() != null ) {
                genericProtocolApplication.setActivityDate( new java.sql.Timestamp(
                        protocolApplicationXML.getActivityDate().toGregorianCalendar().getTimeInMillis() ) );
            }

            Set<SoftwareApplication> set = new HashSet<SoftwareApplication>();
            for ( FuGECommonProtocolSoftwareApplicationType typeXML : protocolApplicationXML
                    .getSoftwareApplication() ) {
                SoftwareApplication application = ( SoftwareApplication ) DatabaseObjectHelper.getOrCreate(
                        typeXML.getIdentifier(),
                        typeXML.getName(), "net.sourceforge.fuge.common.protocol.SoftwareApplication" );
                application = ( SoftwareApplication ) ci.unmarshal( typeXML, application, performer );
                application
                        .setAppliedSoftware( ( Software ) entityService.getIdentifiable( typeXML.getSoftwareRef() ) );
                DatabaseObjectHelper
                        .save( "net.sourceforge.fuge.common.protocol.SoftwareApplication", application, performer );
                set.add( application );
            }
            genericProtocolApplication.setSoftwareApplications( set );

            Set<EquipmentApplication> set2 = new HashSet<EquipmentApplication>();
            for ( FuGECommonProtocolEquipmentApplicationType typeXML : protocolApplicationXML
                    .getEquipmentApplication() ) {
                EquipmentApplication application = ( EquipmentApplication ) DatabaseObjectHelper.getOrCreate(
                        typeXML.getIdentifier(),
                        typeXML.getName(), "net.sourceforge.fuge.common.protocol.EquipmentApplication" );
                application = ( EquipmentApplication ) ci.unmarshal( typeXML, application, performer );
                application
                        .setAppliedEquipment(
                                ( Equipment ) entityService.getIdentifiable( typeXML.getEquipmentRef() ) );

                if ( typeXML.getParameterValue() != null && typeXML.getParameterValue().size() > 0 ) {

                    application = ( EquipmentApplication ) cparapp.unmarshal(
                            typeXML, application, performer );
                }

                DatabaseObjectHelper
                        .save( "net.sourceforge.fuge.common.protocol.EquipmentApplication", application, performer );
                set2.add( application );
            }
            genericProtocolApplication.setEquipmentApplications( set2 );

            Set<ActionApplication> set3 = new HashSet<ActionApplication>();
            for ( FuGECommonProtocolActionApplicationType typeXML : protocolApplicationXML.getActionApplication() ) {
                ActionApplication application = ( ActionApplication ) DatabaseObjectHelper.getOrCreate(
                        typeXML.getIdentifier(), typeXML.getName(),
                        "net.sourceforge.fuge.common.protocol.ActionApplication" );
                application = ( ActionApplication ) ci.unmarshal( typeXML, application, performer );
                application.setAction( ( Action ) entityService.getIdentifiable( typeXML.getActionRef() ) );
                if ( typeXML.getProtocolApplicationRef() != null ) {
                    application.setChildProtocolApplication(
                            ( ProtocolApplication ) entityService
                                    .getIdentifiable( typeXML.getProtocolApplicationRef() ) );
                }

                if ( typeXML.getActionDeviation() != null ) {
                    Description description = ( Description ) entityService
                            .createDescribable( "net.sourceforge.fuge.common.description.Description" );
                    description = ( Description ) cd
                            .unmarshal( typeXML.getActionDeviation().getDescription(), description, performer );
                    description.setText( typeXML.getActionDeviation().getDescription().getText() );
                    DatabaseObjectHelper
                            .save( "net.sourceforge.fuge.common.description.Description", description, performer );
                    application.setActionDeviation( description );
                }
                DatabaseObjectHelper
                        .save( "net.sourceforge.fuge.common.protocol.ActionApplication", application, performer );
                set3.add( application );
            }
            genericProtocolApplication.setActionApplications( set3 );

            if ( protocolApplicationXML.getProtocolDeviation() != null ) {
                Description description = ( Description ) entityService
                        .createDescribable( "net.sourceforge.fuge.common.description.Description" );
                description = ( Description ) cd.unmarshal(
                        protocolApplicationXML.getProtocolDeviation().getDescription(), description, performer );
                description.setText( protocolApplicationXML.getProtocolDeviation().getDescription().getText() );
                DatabaseObjectHelper
                        .save( "net.sourceforge.fuge.common.description.Description", description, performer );
                genericProtocolApplication.setProtocolDeviation( description );
            }

            // get generic protocol application attributes
            genericProtocolApplication = cgpa.unmarshal(
                    ( FuGECommonProtocolGenericProtocolApplicationType ) protocolApplicationXML,
                    genericProtocolApplication, performer );

            return genericProtocolApplication;
        }

        return null; // shouldn't get here as there is currently only one type of ProtocolApplication allowed.
    }

    // @todo Have not coded PartitionPair
    public FuGECommonProtocolProtocolApplicationType marshal(
            FuGECommonProtocolProtocolApplicationType protocolApplicationXML, ProtocolApplication protocolApplication )
            throws EntityServiceException {

        // determine what sort of protocol application it is
        if ( protocolApplication instanceof GenericProtocolApplication ) {
            FuGECommonProtocolGenericProtocolApplicationType genericProtocolApplicationXML =
                    ( FuGECommonProtocolGenericProtocolApplicationType ) protocolApplicationXML;

            // get generic protocol application attributes now so that lazy loading is sorted
            genericProtocolApplicationXML = cgpa.marshal(
                    genericProtocolApplicationXML, ( GenericProtocolApplication ) protocolApplication );

            // get protocol application attributes
            genericProtocolApplicationXML = ( FuGECommonProtocolGenericProtocolApplicationType ) ci
                    .marshal( genericProtocolApplicationXML, protocolApplication );
            genericProtocolApplicationXML = ( FuGECommonProtocolGenericProtocolApplicationType ) cparapp
                    .marshal(
                            genericProtocolApplicationXML, ( GenericProtocolApplication ) protocolApplication );

            if ( protocolApplication.getActivityDate() != null ) {
                GregorianCalendar gc = new GregorianCalendar();
                gc.setTime( protocolApplication.getActivityDate() );
                try {
                    genericProtocolApplicationXML
                            .setActivityDate( DatatypeFactory.newInstance().newXMLGregorianCalendar( gc ) );
                } catch ( DatatypeConfigurationException e ) {
                    throw new EntityServiceException( "Error converting java.sql.Timestamp to XMLGregorianCalendar",
                            e );
                }
            }

            for ( SoftwareApplication application : protocolApplication.getSoftwareApplications() ) {
                FuGECommonProtocolSoftwareApplicationType typeXML = new FuGECommonProtocolSoftwareApplicationType();
                typeXML = ( FuGECommonProtocolSoftwareApplicationType ) ci.marshal(
                        typeXML, application );
                typeXML.setSoftwareRef( application.getAppliedSoftware().getIdentifier() );
                genericProtocolApplicationXML.getSoftwareApplication().add( typeXML );
            }

            for ( EquipmentApplication application : protocolApplication.getEquipmentApplications() ) {
                FuGECommonProtocolEquipmentApplicationType typeXML = new FuGECommonProtocolEquipmentApplicationType();
                typeXML = ( FuGECommonProtocolEquipmentApplicationType ) ci
                        .marshal( typeXML, application );
                typeXML.setEquipmentRef( application.getAppliedEquipment().getIdentifier() );
                if ( application.getParameterValues() != null && application.getParameterValues().size() > 0 ) {

                    typeXML = ( FuGECommonProtocolEquipmentApplicationType ) cparapp
                            .marshal(
                                    typeXML, application );
                }
                genericProtocolApplicationXML.getEquipmentApplication().add( typeXML );
            }
            for ( ActionApplication application : protocolApplication.getActionApplications() ) {
                FuGECommonProtocolActionApplicationType typeXML = new FuGECommonProtocolActionApplicationType();

                typeXML = ( FuGECommonProtocolActionApplicationType ) ci.marshal( typeXML, application );
                typeXML.setActionRef( application.getAction().getIdentifier() );

                if ( application.getChildProtocolApplication() != null ) {
                    typeXML.setProtocolApplicationRef( application.getChildProtocolApplication().getIdentifier() );
                }

                if ( application.getActionDeviation() != null ) {
                    FuGECommonDescriptionDescriptionType descXML = new FuGECommonDescriptionDescriptionType();
                    descXML = ( FuGECommonDescriptionDescriptionType ) cd
                            .marshal( descXML, application.getActionDeviation() );
                    descXML.setText( application.getActionDeviation().getText() );
                    FuGECommonProtocolActionApplicationType.ActionDeviation deviation =
                            new FuGECommonProtocolActionApplicationType.ActionDeviation();
                    deviation.setDescription( descXML );

                    typeXML.setActionDeviation( deviation );
                }

                genericProtocolApplicationXML.getActionApplication().add( typeXML );
            }

            if ( protocolApplication.getProtocolDeviation() != null ) {
                FuGECommonProtocolProtocolApplicationType.ProtocolDeviation pdXML =
                        new FuGECommonProtocolProtocolApplicationType.ProtocolDeviation();
                FuGECommonDescriptionDescriptionType descXML = new FuGECommonDescriptionDescriptionType();
                descXML = ( FuGECommonDescriptionDescriptionType ) cd
                        .marshal( descXML, protocolApplication.getProtocolDeviation() );
                descXML.setText( protocolApplication.getProtocolDeviation().getText() );
                pdXML.setDescription( descXML );
                genericProtocolApplicationXML.setProtocolDeviation( pdXML );
            }
            return genericProtocolApplicationXML;
        }
        return null; // shouldn't get here as there is currently only one type of ProtocolApplication allowed.
    }

    public void prettyHtml( ProtocolApplication pa, PrintWriter printStream ) throws EntityServiceException {

        GenericProtocolApplication gpa = ( GenericProtocolApplication ) pa;
        // get any lazily loaded objects
//        gpa = ( GenericProtocolApplication ) entityService.greedyGet( gpa );

        boolean tdPrinted = false;
        if ( gpa.getInputCompleteMaterials() != null && gpa.getInputCompleteMaterials().size() > 0 ) {
            printStream.println( "<td>" );
            tdPrinted = true;
            for ( Material material : gpa.getInputCompleteMaterials() ) {
                // will print the same info, regardless of the type of Material
//                material = ( Material ) entityService.greedyGet( material );
                ci.prettyHtml( null, material, printStream );
                // should we print the treatments?
                // then print the ontology terms
                printStream.println( "<br>" );
                if ( material.getMaterialType() != null ) {
                    printStream.println( material.getMaterialType().getTerm() );
                    if ( material.getMaterialType().getOntologySource() != null )
                        printStream.println( "(" + material.getMaterialType().getOntologySource().getName() + ")" );
                    printStream.println( "<br>" );
                }
                if ( material.getCharacteristics() != null ) {
                    OntologyCollectionMappingHelper helper = new OntologyCollectionMappingHelper();
                    helper.prettyHtml( material.getCharacteristics(), printStream );
                }
            }
        }
        if ( gpa.getOutputData() != null && gpa.getOutputData().size() > 0 ) {
            if ( !tdPrinted ) {
                printStream.println( "<td>" );
                tdPrinted = true;
            }
            for ( Data data : gpa.getOutputData() ) {
                if ( data instanceof ExternalData ) {
                    ExternalData externalData = ( ExternalData ) data;
                    ci.prettyHtml( null, externalData, printStream );
                    // then print out a form button
                    printStream.println(
                            "<form action=\"downloadSingleFile.jsp\"><input type=\"hidden\" name=\"identifier\" value=\"" +
                            externalData.getLocation() + "\"/>" +
                            "<input type=\"hidden\" name=\"friendly\" value=\"" +
                            externalData.getName() + "\"/>" +
                            "<input type=\"submit\" value=\"Download This File\"/></form>" );
                }
            }
        }
        if ( tdPrinted ) {
            printStream.println( "</td>" );
        }

    }
}
