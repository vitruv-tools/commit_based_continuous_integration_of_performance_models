package cipm.consistency.initialisers.jamopp;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.emftext.language.java.JavaPackage;
import org.emftext.language.java.commons.Commentable;

/**
 * A utility class that provides information about EObjects used by JaMoPP, as
 * well as methods to access their types. There are further methods, which map
 * their types to the initialisers implemented in sub-packages. <br>
 * <br>
 * This class is intended to be used in tests, which ensure that all necessary
 * initialisers are implemented and can be accessed.
 * 
 * @author Alp Torac Genc
 */
public class JaMoPPHelper {

	/**
	 * The suffix used in the concrete implementation of EObject classes in JaMoPP.
	 */
	public String getImplSuffix() {
		return "Impl";
	}

	/**
	 * @return Types of concrete implementations and interfaces of all Java-Model
	 *         elements.
	 */
	public Set<Class<?>> getAllPossibleTypes() {
		return this.getAllPossibleTypes(JavaPackage.eINSTANCE.getESubpackages());
	}

	/**
	 * Recursively discovers sub-packages of cPac (including cPac) for
	 * {@link EClassifier}s contained within, aggregates the types represented by
	 * the EClassifiers as a Set and returns the Set.
	 * 
	 * @param cPac The package, which is the start point of the discovery.
	 * @return All types represented by EClassifiers contained in cPac and its
	 *         sub-packages. Includes types of interfaces as well as concrete
	 *         implementation classes.
	 */
	public Set<Class<?>> getAllPossibleTypes(EPackage cPac) {
		var clss = cPac.getEClassifiers();
		var subPacs = cPac.getESubpackages();

		var foundClss = new HashSet<Class<?>>();

		if (clss != null) {
			for (var cls : clss) {
				foundClss.add(cls.getInstanceClass());

				/*
				 * Although cls is technically of type EClassifier, it also implements EClass
				 */
				if (cls instanceof EClass) {
					var castedCls = (EClass) cls;

					/*
					 * Add the concrete implementation class, if cls represents a concrete class
					 */
					if (!castedCls.isAbstract()) {
						foundClss.add(cPac.getEFactoryInstance().create(castedCls).getClass());
					}
				}
			}
		}

		if (subPacs != null) {
			foundClss.addAll(this.getAllPossibleTypes(subPacs));
		}

		return foundClss;
	}

	/**
	 * @return All types represented by {@link EClassifiers} contained in pacs and
	 *         their sub-packages. Includes types of interfaces as well as concrete
	 *         implementation classes.
	 * @see {@link #getAllPossibleTypes(EPackage)}}
	 */
	public Set<Class<?>> getAllPossibleTypes(Collection<EPackage> pacs) {
		var foundClss = new HashSet<Class<?>>();

		for (var pac : pacs) {
			foundClss.addAll(this.getAllPossibleTypes(pac));
		}

		return foundClss;
	}

	/**
	 * Used to determine which EObject implementors should have an initialiser. <br>
	 * <br>
	 * Here, such implementors (initialiser candidates) implement
	 * {@link Commentable} and their names do not end with {@link #implSuffix}.
	 * 
	 * @return The classes from {@link #getAllPossibleTypes()}, which should have a
	 *         corresponding initialiser interface.
	 */
	public Collection<Class<?>> getAllInitialiserCandidates() {
		var fullHierarchy = getAllPossibleTypes();

		var intfcs = fullHierarchy.stream().filter((c) -> Commentable.class.isAssignableFrom(c))
				.filter((c) -> !c.getSimpleName().endsWith(this.getImplSuffix())).toArray(Class<?>[]::new);

		return List.of(intfcs);
	}

	/**
	 * @return The EObject types from {@link #getAllPossibleTypes()}, which should
	 *         have a corresponding concrete initialiser that can instantiate the
	 *         said type.
	 */
	public Collection<Class<?>> getAllConcreteInitialiserCandidates() {
		var fullHierarchy = getAllPossibleTypes();

		var intfcs = fullHierarchy.stream().filter((c) -> Commentable.class.isAssignableFrom(c))
				.filter((c) -> fullHierarchy.stream()
						.anyMatch((c2) -> c2.getSimpleName().equals(c.getSimpleName() + this.getImplSuffix())))
				.toArray(Class<?>[]::new);

		return List.of(intfcs);
	}
}
