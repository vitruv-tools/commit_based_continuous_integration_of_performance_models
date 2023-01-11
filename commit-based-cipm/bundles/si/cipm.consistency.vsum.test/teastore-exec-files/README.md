# Files for the TeaStore

(All given files are relative to the directory in which this file is located.)

This directory contains files for executing the tests in the TeaStoreCITest.java file with the TeaStore. It requires Maven available on the path. By automatically building the TeaStore during the test, its dependencies are downloaded in the local Maven cache. If the dependencies shall not influence the default local Maven cache, the alternative settings (with the suffix "_with_local_mvn_repo") place the Maven dependencies in a local Maven cache in the target directory of the test ("../target/"). To enable these settings, the "settings_with_local_mvn_repo.properties" can be renamed to "settings.properties", or the path can be adjusted in the TeaStoreCITest.java file.

Additionally, this directory includes the "module-configuration.properties" and "external-call-target-pairs.json" files which store information for which the developer is asked during the test execution. To avoid the questions, the files need to be placed in the directory "../target/TeaStoreTest/java/".
