:: Clean sub-modules.
cd Vitruv
git stash --include-untracked
cd ..
git submodule deinit -f --all

:: Clean all ignored files.
git clean -X -d -f
