# Commit-Based Continuous Integration of Performance Models

This repository provides the prototypical implementation for the change extraction, change propagation, incremental model update, and adaptive instrumentation of the CIPM approach.

# Setup

**Note: a new setup method is currently in development. Therefore, some configurations are not supported yet or can cause issues.**

The project requires Java 11 and an Eclipse Modeling Tools 2022-09 instance with the installation of Xtext (from the Marketplace), Lombok (from [its update site](https://projectlombok.org/p2)), and Checkstyle (optional, from [update site](https://checkstyle.org/eclipse-cs-update-site)).

For the initial setup after cloning the repository, a setup script is provided (`scripts/setup.bat`). Currently, only Windows is supported. The script must be executed from the top-level directory of the repository (and not within the `scripts` directory). As the script uses Maven to build dependent projects, the `JAVA_HOME` environment variable must be set pointing to a JDK 11 (also the top-level JDK directory, not the `bin` folder so that Maven can find the Java executable in `%JAVA_HOME%\bin\java.exe`).

After executing the setup script, all bundles from `commit-based-cipm/bundles/fi` and `commit-based-cipm/releng-dev` can be imported into the Eclipse instance. The `releng-dev` directory contains the bundle `cipm.consistency.targetplatform` with `cipm.consistency.targetplatform.target` file. Within Eclipse, open this file and click on `Set as active target platform`. Wait until the target platform is set, loaded, and the plugins are compiled. It is possible that the target platform needs to be reloaded. In `org.splevo.diffing` and `org.splevo.jamopp.diffing`, open the `model/*.genmodel` files with the genmodel editor and generate only the model code.

The project requires a second running Eclipse instance. After all plugins in the first instance has been setup, start the provided `SecondInstance` run configuration. It should start the second instance. In the second instance, import all bundles from `commit-based-cipm/bundles/si`. In `cipm.consistency.vsum.test`, the test cases should be executable.

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

# Executing the Pipeline with TeaStore or TEAMMATES

The manual to execute the pipeline with TeaStore or TEAMMATES can be found [here](change-based-adaptive-instrumentation/cipm.consistency.vsum.test). Reference PCM repository models for these executions can be found [here](data).
