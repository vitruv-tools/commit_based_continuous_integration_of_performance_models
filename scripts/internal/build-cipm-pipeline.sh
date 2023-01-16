#!/usr/bin/env sh
#

# build the CIPM-Pipeline submodule that is currently checked out
pushd CIPM-Pipeline/cipm.consistency.bridge.eclipse/cipm.consistency.base.shared/dep-generator
sh ./gradlew bundle copyBundles
popd
cp -rf CIPM-Pipeline/cipm.consistency.bridge.eclipse/* commit-based-cipm/bundles/fi
