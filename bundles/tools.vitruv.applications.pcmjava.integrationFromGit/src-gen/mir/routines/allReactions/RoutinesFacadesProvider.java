package mir.routines.allReactions;

import tools.vitruv.extensions.dslsruntime.reactions.AbstractRepairRoutinesFacade;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractRoutinesFacadesProvider;
import tools.vitruv.extensions.dslsruntime.reactions.RoutinesFacadeExecutionState;
import tools.vitruv.extensions.dslsruntime.reactions.structure.ReactionsImportPath;

@SuppressWarnings("all")
public class RoutinesFacadesProvider extends AbstractRoutinesFacadesProvider {
  public AbstractRepairRoutinesFacade createRoutinesFacade(final ReactionsImportPath reactionsImportPath, final RoutinesFacadeExecutionState sharedExecutionState) {
    switch(reactionsImportPath.getPathString()) {
    	case "allReactions": {
    		return new mir.routines.allReactions.RoutinesFacade(this, reactionsImportPath, sharedExecutionState);
    	}
    	case "allReactions.packageAndClassifiers": {
    		return new mir.routines.packageAndClassifiers.RoutinesFacade(this, reactionsImportPath, sharedExecutionState);
    	}
    	case "allReactions.classifierBody": {
    		return new mir.routines.classifierBody.RoutinesFacade(this, reactionsImportPath, sharedExecutionState);
    	}
    	case "allReactions.methodBody": {
    		return new mir.routines.methodBody.RoutinesFacade(this, reactionsImportPath, sharedExecutionState);
    	}
    	default: {
    		throw new IllegalArgumentException("Unexpected import path: " + reactionsImportPath.getPathString());
    	}
    }
  }
}
