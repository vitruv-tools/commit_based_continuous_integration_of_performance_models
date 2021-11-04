# Files for the TeaStore

This directory contains files for executing the test with the TeaStore. It requires Maven available on the path. By building the TeaStore, its dependencies are downloaded in the local Maven cache. If the dependencies shall not influence the default local Maven cache, the alternative settings (with the suffix "_with_local_mvn_repo") place the Maven dependencies in a local Maven cache in the target directory of the test ("../target/").

Additionally, this directory includes the "module-configuration.properties" and "external-call-target-pairs.json" files which store information for which the developer is asked during the test execution. To avoid the questions, the files need to be placed in the directory "../target/TeaStoreTest/java/".
