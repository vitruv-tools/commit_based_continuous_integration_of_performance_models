# Commit-Based Continuous Integration of Performance Models

This repository provides the prototypical implementation for the change extraction, change propagation, incremental model update, and adaptive instrumentation of the CIPM approach.

# Setup
**Note: a new setup method is currently in development. Therefore, some configurations are not supported yet or can cause issues.**

The project needs Java 17 and two instances of the Eclipse Modeling Tools 2022-09 setup as follows:

For the initial setup after cloning the repository, a setup script is provided (`scripts/setup.bat`). Currently, only Windows is supported. The script must be executed from the top-level directory of the repository (and not within the `scripts` directory). As the script uses Maven to build dependent projects, the `JAVA_HOME` environment variable must be set pointing to a JDK 11 (also the top-level JDK directory, not the `bin` folder so that Maven can find the Java executable in `%JAVA_HOME%\bin\java.exe`).

## First Instance

The first instance contains plugins and dependencies which need to be loaded in the second instance later on.

1. Please add the following update site and install the according components:
  - Lombok (from [update site](https://projectlombok.org/p2))

1. Import the following projects:
  - `commit-based-cipm/releng-dev/cipm.consistency.targetplatform`
  - `commit-based-cipm/fi/*` except the domain projects
  - `luaXtext/org.xtext.lua.*`

1. Open the target platform, activate it and then reload it.

1. Make sure that the `Setting Preferences > Xtend > Compiler > General > Source Compatibility level ..`  is set to Java 11.

1. Trigger modelcode generation for the genmodels (Open the genmodel file and click `Generator > Generate Model Code`):
  - org.splevo.diffing/model/splevodiff.genmodel
  - org.splevo.jamopp.diffing/model/jamoppdiff.genmodel

1. Generate the Lua xtext code by running `org.xtext.lua/src/org.xtext.lua/GenerateLua.mwe2`

1. If some projects show errors run `Project > Clean > Clean all projects`

1. Start the "SecondInstance" launch configuration and proceed to the next section

## Second Instance

Import all the following projects into the first instance:
 - All projects located in the `commit-based-cipm/bundles/si` folder

<!-- The project requires Java 13 and an Eclipse Modeling Tools 2021-09 instance with the installation of Xtext (from the Marketplace), PCM 5.0 (from [update site](https://updatesite.palladio-simulator.com/palladio-build-updatesite/releases/5.0.0)), Lombok (from [update site](https://projectlombok.org/p2)), Checkstyle (from [update site](https://checkstyle.org/eclipse-cs-update-site)), SoMoX (from [update site](https://updatesite.palladio-simulator.com/palladio-reverseengineering-somox-jamopp/nightly/)), JaMoPP (from [update site](https://updatesite.palladio-simulator.com/palladio-supporting-eclipsejavadevelopmenttools/nightly/)) and SDQ Commons 2.0 (from [update site](http://kit-sdq.github.io/updatesite/release/commons/2.0.0/)).

Currently, further required plugins are contained within the Git submodules. After the submodules have been initialized, Vitruv needs to be setup according to its documentation, in the CIPM-Pipeline directory `cipm.consistency.bridge.eclipse/cipm.consistency.base.shared/dep-generator`, the command `gradlew bundle copyBundles` needs to be executed, and the following plugins need to be imported into Eclipse:

* All from `/CIPM-Pipeline/cipm.consistency.bridge.eclipse`, `/Vitruv/bundles`, and `/Palladio-Build-Coding-Conventions`
* From `/Vitruv-Domains-ComponentBasedSystems/bundles`:
    * tools.vitruv.domains.java
    * tools.vitruv.domains.pcm
    * tools.vitruv.domains.uml
* From `/Vitruv-Applications-ComponentBasedSystems/bundles`:
    * tools.vitruv.applications.pcmjava.util
    * tools.vitruv.applications.util.temporary
* From this repository (`/changed-based-adaptive-instrumentation` and `/Vitruv-Applications-PCMJavaAdditionals`):
    * cipm.consistency.commitintegration.diff.util
    * cipm.consistency.commitintegration.settings
    * cipm.consistency.domains.java
    * cipm.consistency.tools.evaluation.data
    * org.splevo.commons
    * org.splevo.diffing
    * org.splevo.extraction
    * org.splevo.jamopp.diffing
    * org.splevo.jamopp.extraction
    * org.splevo.jamopp.util

A new Eclipse instance with all imported plugins has to be started. Afterwards, the remaining plugins of this repository (again `/changed-based-adaptive-instrumentation` and `/Vitruv-Applications-PCMJavaAdditionals`) need to be imported.

To setup the correct Checkstyle configuration, the files `org.splevo.releng.codeconventions/splevo-checkstyle-rules.xml` from SPLevo as `SPLevo Style` and `org.palladiosimulator.codeconventions/palladio-checkstyle-rules.xml` from Palladio-Build-CodingConventions as `Palladio Coding Conventions` shall be imported. In addition, the `Palladio Coding Conventions` need to be set as the default Checkstyle configuration. -->
