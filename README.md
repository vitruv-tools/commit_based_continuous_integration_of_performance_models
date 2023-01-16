# Commit-Based Continuous Integration of Performance Models

**The code review summary of this project can be found [here](./CODE_REVIEW.md)**

This repository provides the prototypical implementation for the change extraction, change propagation, incremental model update, and adaptive instrumentation of the CIPM approach.

# Setup
**Note: a new setup method is currently in development. Therefore, some configurations are not supported yet or can cause issues.**

1. Install Java JDK 11 and 17

1. Install Maven
    - The `JAVA_HOME` environment variable must be set pointing to a JDK 11 (so that Maven can find the Java executable in `%JAVA_HOME%\bin\java.exe`).

1. Clone this Repository

1. For the initial setup after cloning the repository, setup scripts for Linux and Windows are provided.
    - Execute `./scripts/setup.sh` (Linux) or `.\scripts\setup.bat` (Windows) from the top-level of this repository.

Further the project needs two instances of the Eclipse Modeling Tools 2022-09 setup as follows:

## First Eclipse Instance

The first instance contains plugins and dependencies which need to be loaded in the second instance later on.

1. Please add the following update site and install the according components:
    - Lombok (from its [update site](https://projectlombok.org/p2))
    - CheckStyle (from its [update site](https://checkstyle.org/eclipse-cs-update-site))

1. Import the following projects:
    - `commit-based-cipm/releng-dev/cipm.consistency.targetplatform`
    - `commit-based-cipm/fi/*` except the domain projects
    - `luaXtext/org.xtext.lua.*`

1. Open the target platform, activate it and then reload it.

1. Make sure that the Setting ` Preferences > Xtend > Compiler > General > Source Compatibility level ..`  is set to Java 11.

1. Trigger modelcode generation for the genmodels (Open the genmodel file and click `Generator > Generate Model Code`):
    - org.splevo.diffing/model/splevodiff.genmodel
    - org.splevo.jamopp.diffing/model/jamoppdiff.genmodel

1. Generate the Lua xtext code by running `org.xtext.lua/src/org.xtext.lua/GenerateLua.mwe2`

1. If some projects show errors run `Project > Clean > Clean all projects`

1. Start the `SecondInstance` launch configuration and proceed to the next section

## Second Eclipse Instance

1. Import all the following projects into the first instance:
   - All projects located in the `commit-based-cipm/bundles/si` folder

1. Run the `CodeReviewEntryPoint` launch configuration. The configuration executes a unit test that executes an integration.
