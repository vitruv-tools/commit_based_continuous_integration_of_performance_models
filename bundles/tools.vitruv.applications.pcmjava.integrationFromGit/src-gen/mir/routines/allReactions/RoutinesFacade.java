package mir.routines.allReactions;

import org.eclipse.emf.ecore.EObject;
import org.emftext.language.java.classifiers.Classifier;
import org.emftext.language.java.classifiers.ConcreteClassifier;
import org.emftext.language.java.classifiers.Interface;
import org.emftext.language.java.containers.CompilationUnit;
import org.emftext.language.java.members.ClassMethod;
import org.emftext.language.java.members.Field;
import org.emftext.language.java.members.InterfaceMethod;
import org.emftext.language.java.members.Member;
import org.emftext.language.java.members.Method;
import org.emftext.language.java.parameters.OrdinaryParameter;
import org.emftext.language.java.parameters.Parameter;
import org.emftext.language.java.parameters.Parametrizable;
import org.emftext.language.java.types.TypeReference;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.core.entity.InterfaceProvidingRequiringEntity;
import org.palladiosimulator.pcm.repository.BasicComponent;
import org.palladiosimulator.pcm.repository.DataType;
import org.palladiosimulator.pcm.repository.OperationInterface;
import org.palladiosimulator.pcm.repository.OperationRequiredRole;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.repository.RepositoryComponent;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractRepairRoutinesFacade;
import tools.vitruv.extensions.dslsruntime.reactions.RoutinesFacadeExecutionState;
import tools.vitruv.extensions.dslsruntime.reactions.RoutinesFacadesProvider;
import tools.vitruv.extensions.dslsruntime.reactions.structure.ReactionsImportPath;

@SuppressWarnings("all")
public class RoutinesFacade extends AbstractRepairRoutinesFacade {
  public RoutinesFacade(final RoutinesFacadesProvider routinesFacadesProvider, final ReactionsImportPath reactionsImportPath, final RoutinesFacadeExecutionState executionState) {
    super(routinesFacadesProvider, reactionsImportPath, executionState);
  }
  
  public boolean createPCMSignature(final InterfaceMethod interfaceMethod) {
    mir.routines.packageAndClassifiers.RoutinesFacade _routinesFacade = this._getRoutinesFacadesProvider().getRoutinesFacade(this._getReactionsImportPath().append(ReactionsImportPath.fromPathString("packageAndClassifiers")));
    return _routinesFacade.createPCMSignature(interfaceMethod);
  }
  
  public boolean createSeffFromImplementingInterfaces(final ClassMethod classMethod, final org.emftext.language.java.classifiers.Class javaClass) {
    mir.routines.packageAndClassifiers.RoutinesFacade _routinesFacade = this._getRoutinesFacadesProvider().getRoutinesFacade(this._getReactionsImportPath().append(ReactionsImportPath.fromPathString("packageAndClassifiers")));
    return _routinesFacade.createSeffFromImplementingInterfaces(classMethod, javaClass);
  }
  
  public boolean createSeffFromImplementingInterface(final ClassMethod classMethod, final org.emftext.language.java.classifiers.Class javaClass, final Interface javaInterface) {
    mir.routines.packageAndClassifiers.RoutinesFacade _routinesFacade = this._getRoutinesFacadesProvider().getRoutinesFacade(this._getReactionsImportPath().append(ReactionsImportPath.fromPathString("packageAndClassifiers")));
    return _routinesFacade.createSeffFromImplementingInterface(classMethod, javaClass, javaInterface);
  }
  
  public boolean createSEFF(final Method javaMethod, final org.emftext.language.java.classifiers.Class javaClass, final ClassMethod classMethod) {
    mir.routines.packageAndClassifiers.RoutinesFacade _routinesFacade = this._getRoutinesFacadesProvider().getRoutinesFacade(this._getReactionsImportPath().append(ReactionsImportPath.fromPathString("packageAndClassifiers")));
    return _routinesFacade.createSEFF(javaMethod, javaClass, classMethod);
  }
  
  public boolean removedInterfaceMethodEvent(final InterfaceMethod interfaceMethod) {
    mir.routines.packageAndClassifiers.RoutinesFacade _routinesFacade = this._getRoutinesFacadesProvider().getRoutinesFacade(this._getReactionsImportPath().append(ReactionsImportPath.fromPathString("packageAndClassifiers")));
    return _routinesFacade.removedInterfaceMethodEvent(interfaceMethod);
  }
  
  public boolean removedClassMethodEvent(final ClassMethod classMethod) {
    mir.routines.packageAndClassifiers.RoutinesFacade _routinesFacade = this._getRoutinesFacadesProvider().getRoutinesFacade(this._getReactionsImportPath().append(ReactionsImportPath.fromPathString("packageAndClassifiers")));
    return _routinesFacade.removedClassMethodEvent(classMethod);
  }
  
  public boolean classMapping(final org.emftext.language.java.classifiers.Class javaClass, final CompilationUnit compilationUnit, final org.emftext.language.java.containers.Package javaPackage) {
    mir.routines.packageAndClassifiers.RoutinesFacade _routinesFacade = this._getRoutinesFacadesProvider().getRoutinesFacade(this._getReactionsImportPath().append(ReactionsImportPath.fromPathString("packageAndClassifiers")));
    return _routinesFacade.classMapping(javaClass, compilationUnit, javaPackage);
  }
  
  public boolean checkSystemAndComponent(final org.emftext.language.java.containers.Package javaPackage, final org.emftext.language.java.classifiers.Class javaClass) {
    mir.routines.packageAndClassifiers.RoutinesFacade _routinesFacade = this._getRoutinesFacadesProvider().getRoutinesFacade(this._getReactionsImportPath().append(ReactionsImportPath.fromPathString("packageAndClassifiers")));
    return _routinesFacade.checkSystemAndComponent(javaPackage, javaClass);
  }
  
  public boolean createElement(final org.emftext.language.java.classifiers.Class javaClass, final org.emftext.language.java.containers.Package javaPackage, final CompilationUnit compilationUnit) {
    mir.routines.packageAndClassifiers.RoutinesFacade _routinesFacade = this._getRoutinesFacadesProvider().getRoutinesFacade(this._getReactionsImportPath().append(ReactionsImportPath.fromPathString("packageAndClassifiers")));
    return _routinesFacade.createElement(javaClass, javaPackage, compilationUnit);
  }
  
  public boolean createArchitecturalElement(final org.emftext.language.java.containers.Package javaPackage, final String name, final String rootPackageName) {
    mir.routines.packageAndClassifiers.RoutinesFacade _routinesFacade = this._getRoutinesFacadesProvider().getRoutinesFacade(this._getReactionsImportPath().append(ReactionsImportPath.fromPathString("packageAndClassifiers")));
    return _routinesFacade.createArchitecturalElement(javaPackage, name, rootPackageName);
  }
  
  public boolean createOrFindPCMInterface(final Interface javaInterface, final CompilationUnit compilationUnit) {
    mir.routines.packageAndClassifiers.RoutinesFacade _routinesFacade = this._getRoutinesFacadesProvider().getRoutinesFacade(this._getReactionsImportPath().append(ReactionsImportPath.fromPathString("packageAndClassifiers")));
    return _routinesFacade.createOrFindPCMInterface(javaInterface, compilationUnit);
  }
  
  public boolean createNonContractsInterface(final Interface javaInterface, final CompilationUnit compilationUnit, final org.emftext.language.java.containers.Package javaPackage) {
    mir.routines.packageAndClassifiers.RoutinesFacade _routinesFacade = this._getRoutinesFacadesProvider().getRoutinesFacade(this._getReactionsImportPath().append(ReactionsImportPath.fromPathString("packageAndClassifiers")));
    return _routinesFacade.createNonContractsInterface(javaInterface, compilationUnit, javaPackage);
  }
  
  public boolean createInterface(final Interface javaInterface, final CompilationUnit compilationUnit, final org.emftext.language.java.containers.Package javaPackage) {
    mir.routines.packageAndClassifiers.RoutinesFacade _routinesFacade = this._getRoutinesFacadesProvider().getRoutinesFacade(this._getReactionsImportPath().append(ReactionsImportPath.fromPathString("packageAndClassifiers")));
    return _routinesFacade.createInterface(javaInterface, compilationUnit, javaPackage);
  }
  
  public boolean addInterfaceCorrespondence(final OperationInterface pcmInterface, final Interface javaInterface, final CompilationUnit compilationUnit) {
    mir.routines.packageAndClassifiers.RoutinesFacade _routinesFacade = this._getRoutinesFacadesProvider().getRoutinesFacade(this._getReactionsImportPath().append(ReactionsImportPath.fromPathString("packageAndClassifiers")));
    return _routinesFacade.addInterfaceCorrespondence(pcmInterface, javaInterface, compilationUnit);
  }
  
  public boolean updateRepositoryInterfaces(final OperationInterface pcmInterface, final Repository pcmRepository) {
    mir.routines.packageAndClassifiers.RoutinesFacade _routinesFacade = this._getRoutinesFacadesProvider().getRoutinesFacade(this._getReactionsImportPath().append(ReactionsImportPath.fromPathString("packageAndClassifiers")));
    return _routinesFacade.updateRepositoryInterfaces(pcmInterface, pcmRepository);
  }
  
  public boolean removedClassEvent(final org.emftext.language.java.classifiers.Class clazz) {
    mir.routines.packageAndClassifiers.RoutinesFacade _routinesFacade = this._getRoutinesFacadesProvider().getRoutinesFacade(this._getReactionsImportPath().append(ReactionsImportPath.fromPathString("packageAndClassifiers")));
    return _routinesFacade.removedClassEvent(clazz);
  }
  
  public boolean removedInterfaceEvent(final Interface interfaze) {
    mir.routines.packageAndClassifiers.RoutinesFacade _routinesFacade = this._getRoutinesFacadesProvider().getRoutinesFacade(this._getReactionsImportPath().append(ReactionsImportPath.fromPathString("packageAndClassifiers")));
    return _routinesFacade.removedInterfaceEvent(interfaze);
  }
  
  public boolean createPackageEClassCorrespondence(final org.emftext.language.java.containers.Package jPackage) {
    mir.routines.packageAndClassifiers.RoutinesFacade _routinesFacade = this._getRoutinesFacadesProvider().getRoutinesFacade(this._getReactionsImportPath().append(ReactionsImportPath.fromPathString("packageAndClassifiers")));
    return _routinesFacade.createPackageEClassCorrespondence(jPackage);
  }
  
  public boolean createOrFindArchitecturalElement(final org.emftext.language.java.containers.Package javaPackage, final String containerName) {
    mir.routines.packageAndClassifiers.RoutinesFacade _routinesFacade = this._getRoutinesFacadesProvider().getRoutinesFacade(this._getReactionsImportPath().append(ReactionsImportPath.fromPathString("packageAndClassifiers")));
    return _routinesFacade.createOrFindArchitecturalElement(javaPackage, containerName);
  }
  
  public boolean createOrFindArchitecturalElementInPackage(final org.emftext.language.java.containers.Package javaPackage, final org.emftext.language.java.containers.Package containingPackage, final String rootPackageName) {
    mir.routines.packageAndClassifiers.RoutinesFacade _routinesFacade = this._getRoutinesFacadesProvider().getRoutinesFacade(this._getReactionsImportPath().append(ReactionsImportPath.fromPathString("packageAndClassifiers")));
    return _routinesFacade.createOrFindArchitecturalElementInPackage(javaPackage, containingPackage, rootPackageName);
  }
  
  public boolean createOrFindRepository(final org.emftext.language.java.containers.Package javaPackage, final String packageName, final String newTag) {
    mir.routines.packageAndClassifiers.RoutinesFacade _routinesFacade = this._getRoutinesFacadesProvider().getRoutinesFacade(this._getReactionsImportPath().append(ReactionsImportPath.fromPathString("packageAndClassifiers")));
    return _routinesFacade.createOrFindRepository(javaPackage, packageName, newTag);
  }
  
  public boolean ensureFirstCaseUpperCaseRepositoryNaming(final Repository pcmRepository, final org.emftext.language.java.containers.Package javaPackage) {
    mir.routines.packageAndClassifiers.RoutinesFacade _routinesFacade = this._getRoutinesFacadesProvider().getRoutinesFacade(this._getReactionsImportPath().append(ReactionsImportPath.fromPathString("packageAndClassifiers")));
    return _routinesFacade.ensureFirstCaseUpperCaseRepositoryNaming(pcmRepository, javaPackage);
  }
  
  public boolean addRepositoryCorrespondence(final Repository pcmRepository, final org.emftext.language.java.containers.Package javaPackage, final String newTag) {
    mir.routines.packageAndClassifiers.RoutinesFacade _routinesFacade = this._getRoutinesFacadesProvider().getRoutinesFacade(this._getReactionsImportPath().append(ReactionsImportPath.fromPathString("packageAndClassifiers")));
    return _routinesFacade.addRepositoryCorrespondence(pcmRepository, javaPackage, newTag);
  }
  
  public boolean createRepository(final org.emftext.language.java.containers.Package javaPackage, final String packageName, final String newTag) {
    mir.routines.packageAndClassifiers.RoutinesFacade _routinesFacade = this._getRoutinesFacadesProvider().getRoutinesFacade(this._getReactionsImportPath().append(ReactionsImportPath.fromPathString("packageAndClassifiers")));
    return _routinesFacade.createRepository(javaPackage, packageName, newTag);
  }
  
  public boolean removedPackageRoutine(final org.emftext.language.java.containers.Package javaPackage) {
    mir.routines.packageAndClassifiers.RoutinesFacade _routinesFacade = this._getRoutinesFacadesProvider().getRoutinesFacade(this._getReactionsImportPath().append(ReactionsImportPath.fromPathString("packageAndClassifiers")));
    return _routinesFacade.removedPackageRoutine(javaPackage);
  }
  
  public boolean deleteCorrespondenceBetweenJavaPackageAndMetaElement(final org.emftext.language.java.containers.Package javaPackage, final org.emftext.language.java.containers.Package metaElement) {
    mir.routines.packageAndClassifiers.RoutinesFacade _routinesFacade = this._getRoutinesFacadesProvider().getRoutinesFacade(this._getReactionsImportPath().append(ReactionsImportPath.fromPathString("packageAndClassifiers")));
    return _routinesFacade.deleteCorrespondenceBetweenJavaPackageAndMetaElement(javaPackage, metaElement);
  }
  
  public boolean deleteMetaElementForPackage(final org.emftext.language.java.containers.Package packageCorrespondingToMetaElement) {
    mir.routines.packageAndClassifiers.RoutinesFacade _routinesFacade = this._getRoutinesFacadesProvider().getRoutinesFacade(this._getReactionsImportPath().append(ReactionsImportPath.fromPathString("packageAndClassifiers")));
    return _routinesFacade.deleteMetaElementForPackage(packageCorrespondingToMetaElement);
  }
  
  public boolean createOperationProvidedRole(final TypeReference typeReference) {
    mir.routines.packageAndClassifiers.RoutinesFacade _routinesFacade = this._getRoutinesFacadesProvider().getRoutinesFacade(this._getReactionsImportPath().append(ReactionsImportPath.fromPathString("packageAndClassifiers")));
    return _routinesFacade.createOperationProvidedRole(typeReference);
  }
  
  public boolean createOperationProvidedRoleFromTypeReference(final Classifier classifierInterface, final org.emftext.language.java.classifiers.Class javaClass, final TypeReference reference) {
    mir.routines.packageAndClassifiers.RoutinesFacade _routinesFacade = this._getRoutinesFacadesProvider().getRoutinesFacade(this._getReactionsImportPath().append(ReactionsImportPath.fromPathString("packageAndClassifiers")));
    return _routinesFacade.createOperationProvidedRoleFromTypeReference(classifierInterface, javaClass, reference);
  }
  
  public boolean removeOperationProvidedRole(final TypeReference typeReference) {
    mir.routines.packageAndClassifiers.RoutinesFacade _routinesFacade = this._getRoutinesFacadesProvider().getRoutinesFacade(this._getReactionsImportPath().append(ReactionsImportPath.fromPathString("packageAndClassifiers")));
    return _routinesFacade.removeOperationProvidedRole(typeReference);
  }
  
  public boolean createOrFindSystem(final org.emftext.language.java.containers.Package javaPackage, final String name) {
    mir.routines.packageAndClassifiers.RoutinesFacade _routinesFacade = this._getRoutinesFacadesProvider().getRoutinesFacade(this._getReactionsImportPath().append(ReactionsImportPath.fromPathString("packageAndClassifiers")));
    return _routinesFacade.createOrFindSystem(javaPackage, name);
  }
  
  public boolean addSystemCorrespondence(final org.palladiosimulator.pcm.system.System pcmSystem, final org.emftext.language.java.containers.Package javaPackage) {
    mir.routines.packageAndClassifiers.RoutinesFacade _routinesFacade = this._getRoutinesFacadesProvider().getRoutinesFacade(this._getReactionsImportPath().append(ReactionsImportPath.fromPathString("packageAndClassifiers")));
    return _routinesFacade.addSystemCorrespondence(pcmSystem, javaPackage);
  }
  
  public boolean createSystem(final org.emftext.language.java.containers.Package javaPackage, final String name) {
    mir.routines.packageAndClassifiers.RoutinesFacade _routinesFacade = this._getRoutinesFacadesProvider().getRoutinesFacade(this._getReactionsImportPath().append(ReactionsImportPath.fromPathString("packageAndClassifiers")));
    return _routinesFacade.createSystem(javaPackage, name);
  }
  
  public boolean createBasicComponent(final org.emftext.language.java.containers.Package javaPackage, final String name, final String rootPackageName) {
    mir.routines.packageAndClassifiers.RoutinesFacade _routinesFacade = this._getRoutinesFacadesProvider().getRoutinesFacade(this._getReactionsImportPath().append(ReactionsImportPath.fromPathString("packageAndClassifiers")));
    return _routinesFacade.createBasicComponent(javaPackage, name, rootPackageName);
  }
  
  public boolean createCompositeComponent(final org.emftext.language.java.containers.Package javaPackage, final String name, final String rootPackageName) {
    mir.routines.packageAndClassifiers.RoutinesFacade _routinesFacade = this._getRoutinesFacadesProvider().getRoutinesFacade(this._getReactionsImportPath().append(ReactionsImportPath.fromPathString("packageAndClassifiers")));
    return _routinesFacade.createCompositeComponent(javaPackage, name, rootPackageName);
  }
  
  public boolean addCorrespondenceAndUpdateRepository(final RepositoryComponent pcmComponent, final org.emftext.language.java.containers.Package javaPackage, final Repository pcmRepository) {
    mir.routines.packageAndClassifiers.RoutinesFacade _routinesFacade = this._getRoutinesFacadesProvider().getRoutinesFacade(this._getReactionsImportPath().append(ReactionsImportPath.fromPathString("packageAndClassifiers")));
    return _routinesFacade.addCorrespondenceAndUpdateRepository(pcmComponent, javaPackage, pcmRepository);
  }
  
  public boolean addCorrespondenceBetweenPcmComponentAndJavaPackage(final RepositoryComponent pcmComponent, final org.emftext.language.java.containers.Package javaPackage) {
    mir.routines.packageAndClassifiers.RoutinesFacade _routinesFacade = this._getRoutinesFacadesProvider().getRoutinesFacade(this._getReactionsImportPath().append(ReactionsImportPath.fromPathString("packageAndClassifiers")));
    return _routinesFacade.addCorrespondenceBetweenPcmComponentAndJavaPackage(pcmComponent, javaPackage);
  }
  
  public boolean addCorrespondenceBetweenPcmComponentOrSystemAndJavaCompilationUnit(final InterfaceProvidingRequiringEntity pcmComponentOrSystem, final CompilationUnit compilationUnit) {
    mir.routines.packageAndClassifiers.RoutinesFacade _routinesFacade = this._getRoutinesFacadesProvider().getRoutinesFacade(this._getReactionsImportPath().append(ReactionsImportPath.fromPathString("packageAndClassifiers")));
    return _routinesFacade.addCorrespondenceBetweenPcmComponentOrSystemAndJavaCompilationUnit(pcmComponentOrSystem, compilationUnit);
  }
  
  public boolean renameRepository(final org.emftext.language.java.containers.Package javaPackage) {
    mir.routines.packageAndClassifiers.RoutinesFacade _routinesFacade = this._getRoutinesFacadesProvider().getRoutinesFacade(this._getReactionsImportPath().append(ReactionsImportPath.fromPathString("packageAndClassifiers")));
    return _routinesFacade.renameRepository(javaPackage);
  }
  
  public boolean renameSystem(final org.emftext.language.java.containers.Package javaPackage) {
    mir.routines.packageAndClassifiers.RoutinesFacade _routinesFacade = this._getRoutinesFacadesProvider().getRoutinesFacade(this._getReactionsImportPath().append(ReactionsImportPath.fromPathString("packageAndClassifiers")));
    return _routinesFacade.renameSystem(javaPackage);
  }
  
  public boolean renameComponent(final org.emftext.language.java.containers.Package javaPackage) {
    mir.routines.packageAndClassifiers.RoutinesFacade _routinesFacade = this._getRoutinesFacadesProvider().getRoutinesFacade(this._getReactionsImportPath().append(ReactionsImportPath.fromPathString("packageAndClassifiers")));
    return _routinesFacade.renameComponent(javaPackage);
  }
  
  public boolean renameInterface(final Interface javaInterface) {
    mir.routines.packageAndClassifiers.RoutinesFacade _routinesFacade = this._getRoutinesFacadesProvider().getRoutinesFacade(this._getReactionsImportPath().append(ReactionsImportPath.fromPathString("packageAndClassifiers")));
    return _routinesFacade.renameInterface(javaInterface);
  }
  
  public boolean renameComponentFromClass(final org.emftext.language.java.classifiers.Class javaClass) {
    mir.routines.packageAndClassifiers.RoutinesFacade _routinesFacade = this._getRoutinesFacadesProvider().getRoutinesFacade(this._getReactionsImportPath().append(ReactionsImportPath.fromPathString("packageAndClassifiers")));
    return _routinesFacade.renameComponentFromClass(javaClass);
  }
  
  public boolean renameDataTypeFromClass(final org.emftext.language.java.classifiers.Class javaClass) {
    mir.routines.packageAndClassifiers.RoutinesFacade _routinesFacade = this._getRoutinesFacadesProvider().getRoutinesFacade(this._getReactionsImportPath().append(ReactionsImportPath.fromPathString("packageAndClassifiers")));
    return _routinesFacade.renameDataTypeFromClass(javaClass);
  }
  
  public boolean createDataType(final org.emftext.language.java.classifiers.Class javaClass, final CompilationUnit compilationUnit) {
    mir.routines.packageAndClassifiers.RoutinesFacade _routinesFacade = this._getRoutinesFacadesProvider().getRoutinesFacade(this._getReactionsImportPath().append(ReactionsImportPath.fromPathString("packageAndClassifiers")));
    return _routinesFacade.createDataType(javaClass, compilationUnit);
  }
  
  public boolean createOrFindCompositeDataType(final org.emftext.language.java.classifiers.Class javaClass, final CompilationUnit compilationUnit) {
    mir.routines.packageAndClassifiers.RoutinesFacade _routinesFacade = this._getRoutinesFacadesProvider().getRoutinesFacade(this._getReactionsImportPath().append(ReactionsImportPath.fromPathString("packageAndClassifiers")));
    return _routinesFacade.createOrFindCompositeDataType(javaClass, compilationUnit);
  }
  
  public boolean createCompositeDataType(final org.emftext.language.java.classifiers.Class javaClass, final CompilationUnit compilationUnit) {
    mir.routines.packageAndClassifiers.RoutinesFacade _routinesFacade = this._getRoutinesFacadesProvider().getRoutinesFacade(this._getReactionsImportPath().append(ReactionsImportPath.fromPathString("packageAndClassifiers")));
    return _routinesFacade.createCompositeDataType(javaClass, compilationUnit);
  }
  
  public boolean createOrFindCollectionDataType(final org.emftext.language.java.classifiers.Class javaClass, final CompilationUnit compilationUnit) {
    mir.routines.packageAndClassifiers.RoutinesFacade _routinesFacade = this._getRoutinesFacadesProvider().getRoutinesFacade(this._getReactionsImportPath().append(ReactionsImportPath.fromPathString("packageAndClassifiers")));
    return _routinesFacade.createOrFindCollectionDataType(javaClass, compilationUnit);
  }
  
  public boolean createCollectionDataType(final org.emftext.language.java.classifiers.Class javaClass, final CompilationUnit compilationUnit) {
    mir.routines.packageAndClassifiers.RoutinesFacade _routinesFacade = this._getRoutinesFacadesProvider().getRoutinesFacade(this._getReactionsImportPath().append(ReactionsImportPath.fromPathString("packageAndClassifiers")));
    return _routinesFacade.createCollectionDataType(javaClass, compilationUnit);
  }
  
  public boolean addDataTypeCorrespondence(final org.emftext.language.java.classifiers.Class javaClass, final CompilationUnit compilationUnit, final DataType dataType) {
    mir.routines.packageAndClassifiers.RoutinesFacade _routinesFacade = this._getRoutinesFacadesProvider().getRoutinesFacade(this._getReactionsImportPath().append(ReactionsImportPath.fromPathString("packageAndClassifiers")));
    return _routinesFacade.addDataTypeCorrespondence(javaClass, compilationUnit, dataType);
  }
  
  public boolean addDataTypeInRepository(final DataType pcmDataType) {
    mir.routines.packageAndClassifiers.RoutinesFacade _routinesFacade = this._getRoutinesFacadesProvider().getRoutinesFacade(this._getReactionsImportPath().append(ReactionsImportPath.fromPathString("packageAndClassifiers")));
    return _routinesFacade.addDataTypeInRepository(pcmDataType);
  }
  
  public boolean createJavaSubPackages(final org.emftext.language.java.containers.Package javaPackage) {
    mir.routines.packageAndClassifiers.RoutinesFacade _routinesFacade = this._getRoutinesFacadesProvider().getRoutinesFacade(this._getReactionsImportPath().append(ReactionsImportPath.fromPathString("packageAndClassifiers")));
    return _routinesFacade.createJavaSubPackages(javaPackage);
  }
  
  public boolean createJavaPackage(final EObject sourceElementMappedToPackage, final org.emftext.language.java.containers.Package parentPackage, final String packageName, final String newTag) {
    mir.routines.packageAndClassifiers.RoutinesFacade _routinesFacade = this._getRoutinesFacadesProvider().getRoutinesFacade(this._getReactionsImportPath().append(ReactionsImportPath.fromPathString("packageAndClassifiers")));
    return _routinesFacade.createJavaPackage(sourceElementMappedToPackage, parentPackage, packageName, newTag);
  }
  
  public boolean renameMember(final Member javaMember) {
    mir.routines.classifierBody.RoutinesFacade _routinesFacade = this._getRoutinesFacadesProvider().getRoutinesFacade(this._getReactionsImportPath().append(ReactionsImportPath.fromPathString("classifierBody")));
    return _routinesFacade.renameMember(javaMember);
  }
  
  public boolean createParameter(final OrdinaryParameter javaParameter, final Parametrizable javaMethod) {
    mir.routines.classifierBody.RoutinesFacade _routinesFacade = this._getRoutinesFacadesProvider().getRoutinesFacade(this._getReactionsImportPath().append(ReactionsImportPath.fromPathString("classifierBody")));
    return _routinesFacade.createParameter(javaParameter, javaMethod);
  }
  
  public boolean deleteParameter(final OrdinaryParameter javaParameter) {
    mir.routines.classifierBody.RoutinesFacade _routinesFacade = this._getRoutinesFacadesProvider().getRoutinesFacade(this._getReactionsImportPath().append(ReactionsImportPath.fromPathString("classifierBody")));
    return _routinesFacade.deleteParameter(javaParameter);
  }
  
  public boolean changeParameterName(final String newName, final Parameter javaParameter) {
    mir.routines.classifierBody.RoutinesFacade _routinesFacade = this._getRoutinesFacadesProvider().getRoutinesFacade(this._getReactionsImportPath().append(ReactionsImportPath.fromPathString("classifierBody")));
    return _routinesFacade.changeParameterName(newName, javaParameter);
  }
  
  public boolean createInnerDeclaration(final ConcreteClassifier classifier, final Field javaField) {
    mir.routines.classifierBody.RoutinesFacade _routinesFacade = this._getRoutinesFacadesProvider().getRoutinesFacade(this._getReactionsImportPath().append(ReactionsImportPath.fromPathString("classifierBody")));
    return _routinesFacade.createInnerDeclaration(classifier, javaField);
  }
  
  public boolean createOrFindAssemblyContext(final ConcreteClassifier classifier, final Field javaField) {
    mir.routines.classifierBody.RoutinesFacade _routinesFacade = this._getRoutinesFacadesProvider().getRoutinesFacade(this._getReactionsImportPath().append(ReactionsImportPath.fromPathString("classifierBody")));
    return _routinesFacade.createOrFindAssemblyContext(classifier, javaField);
  }
  
  public boolean createAssemblyContext(final ConcreteClassifier classifier, final Field javaField) {
    mir.routines.classifierBody.RoutinesFacade _routinesFacade = this._getRoutinesFacadesProvider().getRoutinesFacade(this._getReactionsImportPath().append(ReactionsImportPath.fromPathString("classifierBody")));
    return _routinesFacade.createAssemblyContext(classifier, javaField);
  }
  
  public boolean addAssemblyContextCorrespondence(final AssemblyContext assemblyContext, final Field javaField) {
    mir.routines.classifierBody.RoutinesFacade _routinesFacade = this._getRoutinesFacadesProvider().getRoutinesFacade(this._getReactionsImportPath().append(ReactionsImportPath.fromPathString("classifierBody")));
    return _routinesFacade.addAssemblyContextCorrespondence(assemblyContext, javaField);
  }
  
  public boolean fieldCreatedCorrespondingToOperationInterface(final Classifier classifier, final Field javaField) {
    mir.routines.classifierBody.RoutinesFacade _routinesFacade = this._getRoutinesFacadesProvider().getRoutinesFacade(this._getReactionsImportPath().append(ReactionsImportPath.fromPathString("classifierBody")));
    return _routinesFacade.fieldCreatedCorrespondingToOperationInterface(classifier, javaField);
  }
  
  public boolean fieldCreatedCorrespondingToRepositoryComponent(final Classifier classifier, final Field javaField) {
    mir.routines.classifierBody.RoutinesFacade _routinesFacade = this._getRoutinesFacadesProvider().getRoutinesFacade(this._getReactionsImportPath().append(ReactionsImportPath.fromPathString("classifierBody")));
    return _routinesFacade.fieldCreatedCorrespondingToRepositoryComponent(classifier, javaField);
  }
  
  public boolean createOperationRequiredRoleCorrespondingToField(final Field javaField, final OperationInterface operationInterface, final RepositoryComponent repoComponent) {
    mir.routines.classifierBody.RoutinesFacade _routinesFacade = this._getRoutinesFacadesProvider().getRoutinesFacade(this._getReactionsImportPath().append(ReactionsImportPath.fromPathString("classifierBody")));
    return _routinesFacade.createOperationRequiredRoleCorrespondingToField(javaField, operationInterface, repoComponent);
  }
  
  public boolean changeInnerDeclarationType(final TypeReference typeReference, final Field javaField) {
    mir.routines.classifierBody.RoutinesFacade _routinesFacade = this._getRoutinesFacadesProvider().getRoutinesFacade(this._getReactionsImportPath().append(ReactionsImportPath.fromPathString("classifierBody")));
    return _routinesFacade.changeInnerDeclarationType(typeReference, javaField);
  }
  
  public boolean changedFieldTypeEvent(final Field field, final TypeReference oldType, final TypeReference newType) {
    mir.routines.classifierBody.RoutinesFacade _routinesFacade = this._getRoutinesFacadesProvider().getRoutinesFacade(this._getReactionsImportPath().append(ReactionsImportPath.fromPathString("classifierBody")));
    return _routinesFacade.changedFieldTypeEvent(field, oldType, newType);
  }
  
  public boolean createRequiredRole(final BasicComponent basicComponent, final OperationInterface opInterface, final Field field) {
    mir.routines.classifierBody.RoutinesFacade _routinesFacade = this._getRoutinesFacadesProvider().getRoutinesFacade(this._getReactionsImportPath().append(ReactionsImportPath.fromPathString("classifierBody")));
    return _routinesFacade.createRequiredRole(basicComponent, opInterface, field);
  }
  
  public boolean removeRequiredRoleAndCorrespondence(final OperationRequiredRole orr, final Field field) {
    mir.routines.classifierBody.RoutinesFacade _routinesFacade = this._getRoutinesFacadesProvider().getRoutinesFacade(this._getReactionsImportPath().append(ReactionsImportPath.fromPathString("classifierBody")));
    return _routinesFacade.removeRequiredRoleAndCorrespondence(orr, field);
  }
  
  public boolean removedFieldEvent(final Field field) {
    mir.routines.classifierBody.RoutinesFacade _routinesFacade = this._getRoutinesFacadesProvider().getRoutinesFacade(this._getReactionsImportPath().append(ReactionsImportPath.fromPathString("classifierBody")));
    return _routinesFacade.removedFieldEvent(field);
  }
  
  public boolean changeReturnType(final Method javaMethod, final TypeReference typeReference) {
    mir.routines.classifierBody.RoutinesFacade _routinesFacade = this._getRoutinesFacadesProvider().getRoutinesFacade(this._getReactionsImportPath().append(ReactionsImportPath.fromPathString("classifierBody")));
    return _routinesFacade.changeReturnType(javaMethod, typeReference);
  }
}
