/*******************************************************************************
 * Copyright (c) 2014
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Benjamin Klatt
 *    Daniel Kojic
 *******************************************************************************/
package org.splevo.jamopp.refactoring.refactory.ifelse.tests;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.PatternLayout;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.compare.Comparison;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.emftext.language.java.commons.Commentable;
import org.emftext.language.java.members.ClassMethod;
import org.emftext.language.java.references.MethodCall;
import org.emftext.language.java.references.StringReference;
import org.emftext.language.java.statements.Block;
import org.emftext.language.java.statements.Condition;
import org.emftext.language.java.statements.Statement;
import org.emftext.refactoring.interpreter.internal.RefactoringInterpreter;
import org.emftext.refactoring.registry.refactoringspecification.exceptions.RefSpecAlreadyRegisteredException;
import org.emftext.refactoring.registry.rolemapping.exceptions.RoleMappingAlreadyRegistered;
import org.emftext.refactoring.registry.rolemodel.exceptions.RoleModelAlreadyRegisteredException;
import org.junit.BeforeClass;
import org.junit.Test;
import org.splevo.jamopp.diffing.JaMoPPDiffer;
import org.splevo.jamopp.extraction.JaMoPPSoftwareModelExtractor;
import org.splevo.jamopp.refactoring.refactory.ifelse.IfElseRefactoring;
import org.splevo.jamopp.refactoring.refactory.tests.util.RefactoryTestUtil;
import org.splevo.jamopp.refactoring.refactory.util.RefactoryUtil;
import org.splevo.jamopp.vpm.builder.JaMoPPVPMBuilder;
import org.splevo.jamopp.vpm.software.JaMoPPSoftwareElement;
import org.splevo.vpm.refinement.Refinement;
import org.splevo.vpm.refinement.RefinementFactory;
import org.splevo.vpm.refinement.RefinementType;
import org.splevo.vpm.refinement.VPMRefinementService;
import org.splevo.vpm.software.SoftwareElement;
import org.splevo.vpm.variability.VariationPoint;
import org.splevo.vpm.variability.VariationPointModel;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Test the if else refactoring implementation.
 */
public class IfElseRefactoringTest {

    /**
     * Prepare the test. Initializes a log4j logging environment and registers the refactorings.
     * 
     * @throws FileNotFoundException
     *             Failed to load the required models.
     * @throws RoleModelAlreadyRegisteredException
     *             Role model already registered.
     * @throws RefSpecAlreadyRegisteredException
     *             Refactoring specification already registered.
     * @throws RoleMappingAlreadyRegistered
     *             Role mapping already registered.
     */
    @BeforeClass
    public static void setUp() throws FileNotFoundException, RoleModelAlreadyRegisteredException,
            RefSpecAlreadyRegisteredException, RoleMappingAlreadyRegistered {
        BasicConfigurator.resetConfiguration();
        BasicConfigurator.configure(new ConsoleAppender(new PatternLayout("%m%n")));

        RefactoryUtil.initialize();

        // following prevents a class not def exception
        @SuppressWarnings("unused")
        Class<RefactoringInterpreter> i1 = RefactoringInterpreter.class;

        String baseDirectory = 
                "../org.splevo.jamopp.refactoring.refactory.ifelse/src/org/splevo/jamopp/refactoring/refactory/ifelse/";
        File rolesText = new File(baseDirectory + "IfElse.rolestext");
        File refspec = new File(baseDirectory + "IfElse.refspec");
        File roleMapping = new File(baseDirectory + "IfElse.rolemapping");

        RefactoryTestUtil.registerRoleModel(rolesText);
        RefactoryTestUtil.registerRefSpec(refspec);
        RefactoryTestUtil.registerRoleMapping(roleMapping);
    }

    /**
     * Test to build a vpm model from a diffing result.
     * 
     * <strong>Test Input</strong><br>
     * Two classes with a differing import (BigInteger vs. BigDecimal)
     * 
     * <strong>Test Result</strong><br>
     * The compilation unit of the leading variant (the location of the variation point) must
     * contain two imports (BigInteger and BigDecimal)
     * 
     * @throws Exception
     *             An unexpected failure during the test execution.
     */
    @Test
    public void test() throws Exception {

        String basePath = "../org.splevo.refactoring.tests/testcode/VariantInTheMiddle/";

        VariationPointModel vpm = initializeVariationPointModel(basePath);
        assertThat("Wrong number of vpm groups", vpm.getVariationPointGroups().size(), is(2));

        // diffing statements are expected to be separate add and deletes, so merge
        // them.
        Refinement refinement = RefinementFactory.eINSTANCE.createRefinement();
        refinement.setType(RefinementType.MERGE);
        refinement.getVariationPoints().add(vpm.getVariationPointGroups().get(0).getVariationPoints().get(0));
        refinement.getVariationPoints().add(vpm.getVariationPointGroups().get(1).getVariationPoints().get(0));
        VPMRefinementService refinementService = new VPMRefinementService();
        refinementService.applyRefinements(Lists.newArrayList(refinement), vpm);
        assertThat("Wrong number of refined vpm groups", vpm.getVariationPointGroups().size(), is(1));

        VariationPoint variationPoint = vpm.getVariationPointGroups().get(0).getVariationPoints().get(0);

        // store statements before refactoring for later verification
        EList<Statement> statementsBeforeRefactoring = ((ClassMethod) ((JaMoPPSoftwareElement) variationPoint
                .getLocation()).getJamoppElement()).getStatements();
        Statement firstStatementBeforeRefactoring = statementsBeforeRefactoring.get(0);
        Statement thirdStatementBeforeRefactoring = statementsBeforeRefactoring.get(2);

        IfElseRefactoring refactoring = new IfElseRefactoring();
        refactoring.refactor(variationPoint);

        SoftwareElement locationElement = variationPoint.getLocation();
        if (locationElement instanceof JaMoPPSoftwareElement) {
            JaMoPPSoftwareElement jamoppLocationElement = (JaMoPPSoftwareElement) locationElement;
            Commentable jamoppElement = jamoppLocationElement.getJamoppElement();
            assertThat("Location is not a Method", jamoppElement, instanceOf(ClassMethod.class));
            ClassMethod cm = (ClassMethod) jamoppElement;

            Statement firstStatement = cm.getStatements().get(0);
            Statement conditionStatement = cm.getStatements().get(1);
            Statement lastStatement = cm.getStatements().get(2);

            assertThat("First statement should be unchanged.", firstStatement,
                    is(equalTo(firstStatementBeforeRefactoring)));
            assertThat("Last statement should be unchanged.", lastStatement,
                    is(equalTo(thirdStatementBeforeRefactoring)));

            assertThat("Second statement should be the condition.", conditionStatement, instanceOf(Condition.class));
            Condition ifElse = (Condition) conditionStatement;
            assertThat(ifElse.getCondition(), is(notNullValue()));
            assertThat(ifElse.getStatement(), is(notNullValue()));
            assertThat(ifElse.getElseStatement(), is(notNullValue()));

            // verify condition
            assertThat(ifElse.getCondition(), instanceOf(StringReference.class));
            assertThat(((StringReference) ifElse.getCondition()).getNext(), instanceOf(MethodCall.class));

            // verify ifblock
            assertThat(ifElse.getStatement(), instanceOf(Block.class));
            Block ifBlock = (Block) ifElse.getStatement();
            assertThat("If-block should have 1 statement.", ifBlock.getStatements().size(), is(equalTo(1)));
            Statement statementOfFirstVariant = (Statement) ((JaMoPPSoftwareElement) variationPoint.getVariants()
                    .get(0).getImplementingElements().get(0)).getJamoppElement();
            assertThat("If-block contains wrong statement.", ifBlock.getStatements().get(0),
                    is(equalTo(statementOfFirstVariant)));

            // verify elseblock
            assertThat(ifElse.getElseStatement(), instanceOf(Condition.class));
            Condition elseCondition = (Condition) ifElse.getElseStatement();
            assertThat(elseCondition.getStatement(), instanceOf(Block.class));
            assertThat(((Block) elseCondition.getStatement()).getStatements().size(), is(equalTo(1)));
            Statement statementOfSecondVariant = (Statement) ((JaMoPPSoftwareElement) variationPoint.getVariants()
                    .get(1).getImplementingElements().get(0)).getJamoppElement();
            assertThat("Else-block contains wrong statement.", ((Block) elseCondition.getStatement()).getStatements()
                    .get(0), is(equalTo(statementOfSecondVariant)));
        } else {
            fail("Unexpected Variation Point Location");
        }
    }
    
    /**
     * Initialize the variation point model to refactor. Extract, Diff and init VPM.
     *
     * @param basePath
     *            The base path of the code to load (must contain subdirectories a and b)
     * @return The initialized VPM based on the source code differences.
     * @throws Exception
     *             Failed to initialize the model.
     */
    public static VariationPointModel initializeVariationPointModel(String basePath) throws Exception {
        JaMoPPSoftwareModelExtractor extractor = new JaMoPPSoftwareModelExtractor();
        List<String> urisA = Lists.newArrayList(new File(basePath + "a").getAbsolutePath());
        List<String> urisB = Lists.newArrayList(new File(basePath + "b").getAbsolutePath());
        NullProgressMonitor monitor = new NullProgressMonitor();
        ResourceSet setA = extractor.extractSoftwareModel(urisA, monitor, null);
        ResourceSet setB = extractor.extractSoftwareModel(urisB, monitor, null);

        String ignorePackages = buildIgnorePackages();

        Map<String, String> diffOptions = Maps.newLinkedHashMap();
        diffOptions.put(JaMoPPDiffer.OPTION_JAVA_IGNORE_PACKAGES, ignorePackages);

        JaMoPPDiffer differ = new JaMoPPDiffer();
        Comparison comparison = differ.doDiff(setA, setB, diffOptions);

        JaMoPPVPMBuilder builder = new JaMoPPVPMBuilder();
        VariationPointModel vpm = builder.buildVPM(comparison, "leading", "integration");
        return vpm;
    }

    /**
     * Build the configuration string for the packages to ignore.
     *
     * @return The regular expressions for the packages to ignore.
     */
    private static String buildIgnorePackages() {
        StringBuilder sb = new StringBuilder();
        sb.append("java.*");
        sb.append(System.getProperty("line.separator"));
        String ignorePackages = sb.toString();
        return ignorePackages;
    }

}
