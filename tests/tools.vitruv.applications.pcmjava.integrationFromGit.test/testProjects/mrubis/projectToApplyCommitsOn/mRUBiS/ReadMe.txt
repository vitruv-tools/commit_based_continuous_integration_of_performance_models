
The Modular Rice University Bidding System (mRUBiS) has been developed with
Enterprise Java Beans 3 (EJB 3.0) technology as part of Java EE 5 for the 
GlassFish Application Server 2.1.1. The server is available for download here:

	https://glassfish.java.net/download-archive.html


================================================================================
Building, Deploying, and Running mRUBiS
---------------------------------------

This extracted folder 'mRUBiS' is an Eclipse project folder that can be imported
into Eclipse as a Java project. The structure of the project is as following:

Each component of mRUBiS has its individual folder with subfolders such as 'res'
and 'src'. The folders 'res' contain resources, especially configuration files 
such as deployment descriptors. The folders 'src' contain Java source code. An
architectural view of mRUBiS is provided by models located in the folder 'doc'.

________________________________________________________________________________
  // Authentication Service
- authservice
	|_ res
	|_ src
  // Bid and Buy Service
- bidandbuyservice
	|_ res
	|_ src
  // Objects used by all Services 
  // (business objects are used by the service interfaces)
- businessobjects
	|_ res
	|_ src
  // Client Sample Application
- client
	|_ src
  // Interfaces of the Services 
  // (interfaces uses the business objects)
- contracts
	|_ res
	|_ src
  // SQL code for the database and test data
- database
  // Documentation
- doc
  // Database Entities
- entities
	|_ res
	|_ src
  // Inventory Service
- inventorymgmt
	|_ res
	|_ src
  // Item Management Service
- itemmgmt
	|_ res
	|_ src
  // Persistence Service
- persistenceservice
	|_ res
	|_ src
  // Query Service
- queryservice
	|_ res
	|_ src
  // Reputation Service
- reputationservice
	|_ res
	|_ src
  // User Management Service
- usermgmt
	|_ res
	|_ src
  // ANT script and properties
- build.properties
- build.xml
  // This file
- ReadMe.txt
________________________________________________________________________________


To build and deploy mRUBiS, a script for Apache Ant---tested under Linux---is 
provided (build.xml). To use this script, a property of the script has to be 
adjusted in the properties file (build.properties). Set the 'glassfish.home' 
property to the fully qualified name of the 'glassfish' folder where the 
application server has been installed, for instance:

	glassfish.home=/home/thomas/server/glassfish

When using the Ant script, this property is needed to resolve dependencies to 
libraries of the Java Enterprise Edition that are contained in the folder 
'${glassfish.home}/lib'. 

Moreover, to enable Eclipse to compile and test the mRUBiS project, you have to 
manually adjust the libraries of the project's Java Build Path, such that 
Eclipse finds the two libraries 'javaee.jar' and 'appserv-rt.jar' located in the
'${glassfish.home}/lib' folder. 

Having set the property and resolved the dependencies to libraries, the Ant 
script can be used (either from the Terminal or within Eclipse). 

In the following, the relevant commands are outlined when using the Terminal. 
Navigate to the mRUBiS project folder that directly contains the build.xml file
to execute any of the following commands.


- ant start-server
    Starts the GlassFish application server 
	(including the database server as part of GlassFish)

- ant stop-server
	Stops the GlassFish application server 
	(including the database server as part of GlassFish)

- ant setup-database
	Creates the mRUBiS database and its schema, and inserts test data into the 
	database

- ant reset-database
	Resets the data in the database to its original state 
	(probably needed between multiple tests)

- ant cleanup-database
	Destroys the database (and thus all data contained in the database) 

- ant build
	Compiles and packages mRUBiS to EJB modules that are located in the 
	subfolder 'dist' and that can be deployed to GlassFish

- ant clean
	Cleans up the artifacts created by 'ant build' 

- ant deploy
	Deploys all EJB modules of mRUBiS to GlassFish

- ant undeploy
	Undeploys all EJB modules of mRUBiS from GlassFish

	EJB modules can also be manually (un)deployed to GlassFish through the 
	Administration Console (http://localhost:4848 if GlassFish runs on 
	localhost) 

Having deployed mRUBiS, the marketplace is now running.

To test the mRUBiS installation, a test client is provided as part of the mRUBiS
project. In the project's subfolder 'client/src' the main class
'de.hpi.sam.rubis.client.main.ClientSession' can be executed within Eclipse to 
send request to the mRUBiS application deployed in GlassFish.

================================================================================
Contact information:

Thomas Vogel
System Analysis and Modeling Group
Hasso Plattner Institute for Software Systems Engineering
University of Potsdam, Germany

www:  http://www.hpi.uni-potsdam.de/giese/staff/thomas_vogel.html
	  http://www.hpi.uni-potsdam.de/giese/mdelab/?page_id=942

================================================================================

