# Code Review: Continuous Integration of Performance Models for Lua-Based IIoT Applications

The Continuous Integration of Performance Models (CIPM) approach aims to models the performance of software during its development.
The PCM is used as an architectural performance model. Further, vitruv is used to keep the different models (Code Model, PCM and Instrumentation Model) consistent.
The goal of this thesis is to evaluate the existing approach using Lua applications from the industry (SICK AG).
Further details can be found in the [proposal](./documentation/proposal_final.pdf).

## Changes to the existing prototypical implementation

The previous prototypical implementation targeted Java Applications and hence used JaMoPP as a code model.
Further in used version 2 of Vitruv.

In order to achieve the goals of this thesis the following changes were made to the previous implementation
    - A code model for Lua was implemented using Xtext (the code model sources can be found in `./luaXtext/`)
		- The implementation was (partially) updated to use Vitruv 3
		- The previous implementation written with only supporting Java in mind. Instead of just changing the previous implemtation to support our new Lua use case, the architecture of the was modified to make it more generic. An overview over the new architecture can be found	[here](./documentation/figures/cipm_architecture_new.pdf).
		    - Interfaces were introduced for language specific functionality.
				- Components were restructured to make them generic regarding the code model
				- These changes are contained in the `./commit-based-cipm/bundles/si/*` projects. Therefore these projects would be the most interesting in terms of the code review.




