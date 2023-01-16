#!/usr/bin/env bash
# enable jobcontrol
set -m
# exit on error
set -e

# Initialize submodules
git submodule update --init

# Prepare bundles from the CIPM-Pipeline
sh ./scripts/internal/build-cipm-pipeline.sh

# Start server for the update sites of JaMoPP and SoMoX
pushd scripts/update-site-server
fuser -k 8081/tcp || echo No previous server started
mvn spring-boot:run &
popd

# Build JaMoPP
pushd Palladio-Supporting-EclipseJavaDevelopmentTools
mvn clean verify -Dmaven.test.skip=true
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
