# change-based-adaptive-instrumentation

This repository provides the prototypical implementation for the change extraction, change propagation, incremental model update, and adaptive instrumentation of the CIPM approach.

# Setup

The project requires Java 13 and an Eclipse Modeling Tools 2021-03 instance with the installation of Xtext (from the Marketplace), PCM 5.0 (from [update site](https://updatesite.palladio-simulator.com/palladio-build-updatesite/releases/5.0.0)), Lombok (from [update site](https://projectlombok.org/p2)), Checkstyle (from [update site](https://checkstyle.org/eclipse-cs-update-site)), SoMoX (from [update site](https://updatesite.palladio-simulator.com/palladio-reverseengineering-somox-jamopp/nightly/)) and SDQ Commons 2.0 (from [update site](http://kit-sdq.github.io/updatesite/release/commons/2.0.0/)).

Currently, further required plugins are contained within the Git submodules. After the submodules have been initialized, Vitruv and Palladio-Supporting-EclipseJavaDevelopmentTools need to be setup according to their documentation, and the following plugins need to be imported into Eclipse:

* From the CIPM-Pipeline:
    * cipm.consistency.base.shared
    * cipm.consistency.domains.im
    * cipm.consistency.domains.pcm
    * cipm.consistency.models.instrumentation
* All from Vitruv and Palladio-Supporting-EclipseJavaDevelopmentTools
* From Vitruv-Domains-ComponentBasedSystems:
    * tools.vitruv.domains.java
    * tools.vitruv.domains.pcm
    * tools.vitruv.domains.uml
* From Vitruv-Applications-ComponentBasedSystems:
    * tools.vitruv.applications.pcmjava.util
    * tools.vitruv.applications.util.temporary
* From Palladio-ReverseEngineering-SoMoX-JaMoPP:
    * org.somox.core
    * org.somox.filter
    * org.somox.kdmhelper
    * org.somox.sourcecodedecorator
    * org.somox.util
    * org.somox.gast2seff
* From this repository:
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

A new Eclipse instance with all imported plugins has to be started. Afterwards, the remaining plugins of this repository need to be imported.
# Remark
The submodules will be deleted later when the created pull requests are merged.
