MAVEN PROFILES AND TESTING WITHIN THE FuGE HIBERNATE STK

By default, the build profile used in this STK is the "local" profile. Within this profile and the
production profile ("-Denv=prod"), tests are disabled. This is because the tests change the database, and by default,
we do not want to risk changes to what might be your production database.

However, if you wish to perform tests, you should explicitly use either the "val" (for "validation") or the "dev"
(for "development") profile. This can be done by adding "-Denv=val" (or "-Denv=dev", as appropriate) to
the maven command you wish to run, e.g.

mvn install -Denv=val

This will then use your test database settings.

TESTING FOR THE FIRST TIME, OR AFTER A DATABASE RE-BUILD

The first time you run your test, make sure that your test database exists. First, ensure that you have created
your test database. Within psql, if  your test database is "testfuge", and your user is "fuge", then you should
use the following command:

create database testfuge owner fuge;

If your database is empty, either because you just created it or because you have had to drop the tables due to
a change to the UML, you will need to fill it before any tests in the "hibernate" group can be run without failing.
This means you'll need to run the "mvn install" command without tests first, then call the tests explicitly.
For an example using the val profile run the following from the top-level directory (the "mvn clean" command is
optional):

mvn clean
mvn install -Denv=val -DskipTests
mvn -f fuge-hibernate-core/pom.xml andromdapp:schema -Denv=val -Dtasks=create
mvn test -Denv=val

RUNNING JAVA MAIN METHODS WITHIN MAVEN

You can call Java classes with main() methods using the maven "exec:java" command. However, some users have experienced
problems with this command. Note that you can still run any of these programs in the normal manner. Using the maven
exec:java command will ensure that you are using the same classpath as maven uses when building. Some example
commands follow (these should be run from within the fuge-hibernate-mapping subdirectory):

1) Load a FuGE-ML file into the database:
mvn -e exec:java -Dexec.mainClass="net.sourceforge.symba.mapping.hibernatejaxb2.UnmarshalXML" -Dexec.args="/path/to/xmlSchema.xsd fugeml.xml"

2) Perform a Roundtrip for a given XML file:
mvn -e exec:java -Dexec.mainClass="net.sourceforge.symba.mapping.hibernatejaxb2.XMLRoundtrip" -Dexec.args="path/to/xmlSchema.xsd fugeml-in.xml fugeml-out.xml"

3) Marshal FuGE Objects from the database into FuGE-ML
mvn -e exec:java -Dexec.mainClass="net.sourceforge.symba.mapping.hibernatejaxb2.MarshalXML" -Dexec.args="path/to/xmlSchema.xsd identifier fugeml-out.xml"
