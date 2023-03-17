package cipm.consistency.commitintegration.lang.lua.changeresolution;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.compare.match.IMatchEngine;
import org.eclipse.emf.compare.match.eobject.EqualityHelperExtensionProviderDescriptorRegistryImpl;
import org.eclipse.emf.compare.match.eobject.WeightProviderDescriptorRegistryImpl;
import org.eclipse.emf.compare.match.impl.MatchEngineFactoryImpl;
import org.eclipse.emf.compare.scope.IComparisonScope;
import org.eclipse.emf.compare.utils.UseIdentifiers;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;

public class ImMatchEngineFactory extends MatchEngineFactoryImpl {
    
    public ImMatchEngineFactory() {
        super(UseIdentifiers.ONLY, WeightProviderDescriptorRegistryImpl.createStandaloneInstance(),
                EqualityHelperExtensionProviderDescriptorRegistryImpl.createStandaloneInstance());
    }

    private boolean containsIm(Notifier not) {
        if (not instanceof XMIResourceImpl xmi) {
            return xmi.getURI()
                .lastSegment()
                .endsWith(".imm");
        }
        return false;
    }

    @Override
    public boolean isMatchEngineFactoryFor(IComparisonScope scope) {
        return containsIm(scope.getLeft()) && containsIm(scope.getRight());
    }

    @Override
    public IMatchEngine getMatchEngine() {
        
        // TODO Auto-generated method stub
        return super.getMatchEngine();
    }

}
