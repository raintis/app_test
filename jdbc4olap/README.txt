jdbc4olap build steps.
----------------------
1. Get the source from the subversion bank:

svn co https://jdbc4olap.svn.sourceforge.net/svnroot/jdbc4olap/trunk/jdbc4olap

2. (Optional) Edit the file: jdbc4olap/test.properties with valid userID and password.

3. Make sure maven (v2.0.9 or newer recommended) is on your path ($MAVEN_HOME/bin/mvn.sh or .bat).
Move to jdbc4olap dir (created above) and run:

mvn clean package

This will compile, run unit tests, and build the driver jar. Unit tests that require an olap/xmla server will fail if test.properties is incorrect or the server is not accessible.
In order to build the jars w/out running unit tests, run:

mvn clean package -DskipTests

Maven creates all it's output in the 'target' directory.

Some other useful maven commands:
mvn generate-sources        - create sql grammar source files
mvn compile                 - create sql grammar, and compile production classes
mvn compiler:testCompile    - compile unit test classes
mvn test                    - create sql grammar, compile all classes, run unit tests
mvn package                 - create sql grammar, compile all classes, run unit tests, build jars
mvn site                    - create site documentation
mvn scm:update              - fetch the latest source from the bank

NOTE: If you get a compile error like:
        [INFO] Compilation failure

C:\path with spaces\jdbc4olap\src\org\jdbc4olap\jdbc\OlapStatement.java:[71,0] org.jdbc4olap.jdbc.OlapStatement is not a
bstract and does not override abstract method isPoolable() in java.sql.Statement

        then you are probably building using a jdk newer than 1.5.
        In order to build using jdk1.6 or newer, you need to add a command line system property pointing to the jdk1.5 javac.
        This is required to ensure the JDBC v3.0 api (which ships with 1.5) is used during compilation.
        For example:
        mvn clean package -Djdk_1_5_home=/usr/java/jdk1.5.0_07
        or:
        mvn clean package -Djdk_1_5_home="C:\Program Files\Java\jdk1.5.0_07"


Eclipse setup for Maven
-------------------
There are several ways of making Maven work in Eclipse, including using m2eclipse (http://m2eclipse.codehaus.org/)
and running "mvn eclipse:eclipse" which generates a fresh .classpath file.

IDEA ItelliJ (http://www.jetbrains.com/) can use the pom.xml as a project definition.
First, you should build the project via the command line using: mvn package
Then remove the following two source dirs from the IDEA project structure config.
This avoids bogus error warnings about "duplicate classes" in the generated grammer source files, etc.:

jjtree
target/generated-sources/jjtree

keep the final generated source tree:
target/generated-source/javacc


SQuirrel SQL setup for manual testing.
-------------------
Link or copy the library dependency jar files below into the SQuirrel SQL lib dir (SQuirrel SQL Client/lib).
These jars will be available after running the maven build in: target/jdbc4olap-<version>-dist

activation-1.0.2.jar (optional?)
commons-codec-1.3.jar
saaj-api-1.3.jar
saaj-impl-1.3.jar

Then launch SQuirrel SQL, and setup the jdbc4olap driver with:

Example URL: jdbc:jdbc4olap:http://server:port/sap/bw/xml/soap/xmla?sap-client=number
Extra Class Path tab: include jdbc4olap-<version>.jar  (located in target or target/jdbc4olap-<version>-dist directory)
Classname: org.jdbc4olap.jdbc.OlapDriver

The driver currently loads the WHOLE shebang (and catalog selection is not implemented), so it can take while for things to load.
You can increase the debug info on system.out by setting the driver property: isDriverDebug = true (via SQuirrel SQL driver props dialog).

See http://www.squirrelsql.org/ for more details about SQuirrel SQL.

Runtime Dependencies
-------------------
For a list of jars required at runtime, see the "Dependencies" report (online or by generating the site locally by executing: mvn site, 
then open: /target/index.html, ProjectInformation -> Dependencies).
NOTE: In addition to the jdbc4olap.jar itself, you will need all "compile" and "runtime" jars are the classpath.
