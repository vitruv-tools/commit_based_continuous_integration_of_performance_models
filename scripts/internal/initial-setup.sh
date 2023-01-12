#!/usr/bin/env bash
# enable jobcontrol
set -m
# exit on error
set -e

# Initialize submodules
git submodule update --init

# Prepare bundles from the CIPM-Pipeline
pushd CIPM-Pipeline/cipm.consistency.bridge.eclipse/cipm.consistency.base.shared/dep-generator
sh ./gradlew bundle copyBundles
popd
cp -r CIPM-Pipeline/cipm.consistency.bridge.eclipse/* commit-based-cipm/bundles/fi

# Start server for the update sites of JaMoPP and SoMoX
pushd scripts/update-site-server
sh ../../mvnw spring-boot:run &
popd

# Build JaMoPP
pushd Palladio-Supporting-EclipseJavaDevelopmentTools
sh mvnw clean verify -Dmaven.test.skip=true
popd

# Check that the server for the update site runs
curl --retry 10 --retry-connrefused --retry-delay 1 http://localhost:8081/administration/status
if [ $? != 0 ]; then exit 1; fi

# Build SoMoX
pushd Palladio-ReverseEngineering-SoMoX-JaMoPP
git restore .
git apply --ignore-whitespace ../scripts/internal/SoMoX-Local-Update-Site.patch
# cp -r ../.mvn/wrapper .mvn/
mvn clean verify -Dmaven.test.skip=true
popd
