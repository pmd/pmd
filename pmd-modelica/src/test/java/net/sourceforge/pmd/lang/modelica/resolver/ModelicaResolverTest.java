/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica.resolver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.modelica.ModelicaParsingHelper;
import net.sourceforge.pmd.lang.modelica.ast.ASTExtendsClause;
import net.sourceforge.pmd.lang.modelica.ast.ASTStoredDefinition;
import net.sourceforge.pmd.lang.modelica.ast.ModelicaClassSpecifierNode;
import net.sourceforge.pmd.lang.modelica.ast.ModelicaNode;
import net.sourceforge.pmd.lang.modelica.ast.ModelicaParserVisitorAdapter;

class ModelicaResolverTest {

    private final ModelicaParsingHelper modelica = ModelicaParsingHelper.DEFAULT;

    private static class NodeFinder extends ModelicaParserVisitorAdapter {
        private ModelicaNode result;
        private Class<?> nodeClass;
        private String nodeName;

        NodeFinder(Class<?> nodeClass, String nodeName) {
            this.nodeClass = nodeClass;
            this.nodeName = nodeName;
        }

        @Override
        public Object visitModelicaNode(ModelicaNode node, Object data) {
            if (nodeClass.isInstance(node) && node.getImage().equals(nodeName)) {
                assertNull(result);
                result = node;
            }
            return super.visitModelicaNode(node, data);
        }

        ModelicaNode getResult() {
            return result;
        }
    }

    private ModelicaNode findNodeByClassAndImage(ASTStoredDefinition ast, Class<?> clazz, String image) {
        NodeFinder vis = new NodeFinder(clazz, image);
        ast.acceptVisitor(vis, null);
        return vis.getResult();
    }

    private void ensureCounts(ResolutionResult result, int best, int hidden) {
        assertFalse(result.wasTimedOut());
        assertEquals(best, result.getBestCandidates().size());
        assertEquals(hidden, result.getHiddenCandidates().size());
    }

    private ResolutionResult<ResolvableEntity> resolveIn(int best, int hidden, ResolutionState state, SubcomponentResolver resolver, boolean absolute, String[] names) {
        ResolutionResult<ResolvableEntity> result = resolver.safeResolveComponent(ResolvableEntity.class, state, CompositeName.create(absolute, names));
        ensureCounts(result, best, hidden);
        return result;
    }

    private ResolutionResult<ResolvableEntity> resolveIn(int best, int hidden, ResolutionState state, ModelicaScope resolver, boolean absolute, String[] names) {
        ResolutionResult<ResolvableEntity> result = resolver.safeResolveLexically(ResolvableEntity.class, state, CompositeName.create(absolute, names));
        ensureCounts(result, best, hidden);
        return result;
    }

    private ResolutionResult<ResolvableEntity> testResolvedTypeCount(int best, int hidden, SubcomponentResolver scope, boolean absolute, String... names) {
        return resolveIn(best, hidden, ResolutionState.forType(), scope, absolute, names);
    }

    private ResolutionResult<ResolvableEntity> testResolvedTypeCount(int best, int hidden, ModelicaScope scope, boolean absolute, String... names) {
        return resolveIn(best, hidden, ResolutionState.forType(), scope, absolute, names);
    }

    private ResolutionResult<ResolvableEntity> testResolvedComponentCount(int best, int hidden, ModelicaScope scope, boolean absolute, String... names) {
        return resolveIn(best, hidden, ResolutionState.forComponentReference(), scope, absolute, names);
    }

    private ResolutionResult<ResolvableEntity> testLexicallyResolvedComponents(int best, int hidden, ModelicaClassScope scope, boolean absolute, String... names) {
        ResolutionState state = ResolutionState.forComponentReference();
        ResolutionResult<ResolvableEntity> result = scope.safeResolveLexically(ResolvableEntity.class, state, CompositeName.create(absolute, names));
        ensureCounts(result, best, hidden);
        return result;
    }

    @Test
    void verySimpleScopeTest() {
        String contents =
              "model TestPackage"
            + "  Real x;"
            + "end TestPackage;";

        ASTStoredDefinition ast = modelica.parse(contents);
        assertNotNull(ast);

        assertTrue(ast.getMostSpecificScope() instanceof ModelicaSourceFileScope);
        ModelicaSourceFileScope scope = (ModelicaSourceFileScope) ast.getMostSpecificScope();

        assertTrue(scope.getParent() instanceof RootScope);
        assertNull(scope.getParent().getParent());
    }

    @Test
    void simpleScopeTest() {
        String contents =
              "package TestPackage"
            + "  connector TestConnector"
            + "  end TestConnector;"
            + "  model TestModel"
            + "    model TestSubmodel"
            + "    end TestSubmodel;"
            + "  end TestModel;"
            + "  Real x;"
            + "end TestPackage;";

        ASTStoredDefinition ast = modelica.parse(contents);
        ModelicaSourceFileScope sourceFileScope = (ModelicaSourceFileScope) ast.getMostSpecificScope();

        assertEquals(1, sourceFileScope.getContainedDeclarations().size());

        ModelicaNode testSubmodel = findNodeByClassAndImage(ast, ModelicaClassSpecifierNode.class, "TestSubmodel");
        assertNotNull(testSubmodel);
        assertEquals(
                "#ROOT#FILE#Class:TestPackage#Class:TestModel#Class:TestSubmodel",
                ((AbstractModelicaScope) testSubmodel.getMostSpecificScope()).getNestingRepresentation()
        );

        ModelicaScope testPackage = testSubmodel.getMostSpecificScope().getParent().getParent();
        assertTrue(testPackage instanceof ModelicaClassScope);
        assertEquals("TestPackage", ((ModelicaClassScope) testPackage).getClassDeclaration().getSimpleTypeName());
        assertEquals(3, testPackage.getContainedDeclarations().size());
    }

    @Test
    void extendsScopeTest() {
        String contents =
                  "package Test"
                + "  model A"
                + "    extends B;"
                + "  end A;"
                + "  model B"
                + "  end B;"
                + "end Test;";

        ASTStoredDefinition ast = modelica.parse(contents);

        List<ASTExtendsClause> extendsClauses = ast.findDescendantsOfType(ASTExtendsClause.class);
        assertEquals(1, extendsClauses.size());
        ASTExtendsClause extendsB = extendsClauses.get(0);
        assertEquals("#ROOT#FILE#Class:Test#Class:A", ((AbstractModelicaScope) extendsB.getMostSpecificScope()).getNestingRepresentation());
    }

    @Test
    void absoluteResolutionTest() {
        String contents =
              "package TestPackage"
            + "  model TestModel"
            + "    model TestSubmodel"
            + "    end TestSubmodel;"
            + "  end TestModel;"
            + "end TestPackage;";

        ASTStoredDefinition ast = modelica.parse(contents);
        testResolvedTypeCount(1, 0, ast.getMostSpecificScope(), true, "TestPackage", "TestModel", "TestSubmodel");
    }


    @Test
    void nonAbsoluteResolutionTest() {
        String contents =
              "package TestPackage"
            + "  model TestModel"
            + "    model TestSubmodel"
            + "    end TestSubmodel;"
            + "  end TestModel;"
            + "end TestPackage;";

        ASTStoredDefinition ast = modelica.parse(contents);
        testResolvedTypeCount(1, 0, ast.getMostSpecificScope(), false, "TestPackage", "TestModel", "TestSubmodel");
    }

    @Test
    void multipleResolutionTest() {
        String contents =
              "package TestPackage"
            + "  model TestModel"
            + "    model A"
            + "    end A;"
            + "  end TestModel;"
            + "  model A"
            + "  end A;"
            + "  Real x;"
            + "end TestPackage;";

        ASTStoredDefinition ast = modelica.parse(contents);

        ResolutionResult<ResolvableEntity> testModelCandidates = testResolvedTypeCount(1, 0, ast.getMostSpecificScope(), true, "TestPackage", "TestModel");
        ModelicaClassScope testModelScope = ((ModelicaClassType) testModelCandidates.getBestCandidates().get(0)).getClassScope();
        assertEquals(
                "#ROOT#FILE#Class:TestPackage#Class:TestModel",
                testModelScope.getNestingRepresentation()
        );

        ResolutionResult<ResolvableEntity> aCandidates = testLexicallyResolvedComponents(1, 1, testModelScope, false, "A");
        ModelicaClassType aBest = (ModelicaClassType) aCandidates.getBestCandidates().get(0);
        ModelicaClassType aHidden = (ModelicaClassType) aCandidates.getHiddenCandidates().get(0);
        assertEquals("#ROOT#FILE#Class:TestPackage#Class:TestModel#Class:A",
                aBest.getClassScope().getNestingRepresentation());
        assertEquals("#ROOT#FILE#Class:TestPackage#Class:A",
                aHidden.getClassScope().getNestingRepresentation());
    }

    @Test
    void constantComponentResolutionTest() {
        String contents =
              "model Test"
            + "  model A"
            + "    constant Real x = 1;"
            + "  end A;"
            + "end Test;";

        ASTStoredDefinition ast = modelica.parse(contents);

        List<ResolvableEntity> xs = testResolvedTypeCount(1, 0, ast.getMostSpecificScope(), false, "Test", "A", "x").getBestCandidates();
        assertEquals(
            "#ROOT#FILE#Class:Test#Class:A",
                ((ModelicaComponentDeclaration) xs.get(0)).getContainingScope().getNestingRepresentation()
        );
    }

    @Test
    void nestedStoredDefinitionTest() {
        String contents =
              "within TestPackage.SubPackage;\n"
            + "model Test\n"
            + "end Test;\n";

        ASTStoredDefinition ast = modelica.parse(contents);
        RootScope rootScope = (RootScope) ast.getMostSpecificScope().getParent();

        List<ResolvableEntity> nestedTest = testResolvedTypeCount(1, 0, rootScope, false, "TestPackage", "SubPackage", "Test").getBestCandidates();
        assertEquals(
                "#ROOT#FILE#Class:Test",
                ((ModelicaClassType) nestedTest.get(0)).getClassScope().getNestingRepresentation()
        );

        // Simple names are visible from within the same file
        testResolvedTypeCount(1, 0, ast.getMostSpecificScope(), false, "Test");

        // ... but from other files they should be resolved w.r.t. the within clause
        testResolvedTypeCount(0, 0, rootScope, false, "Test");
    }

    @Test
    void extendsTest() {
        String contents =
              "model A\n"
            + "  model X\n"
            + "  end X;\n"
            + "end A;\n"
            + "model B\n"
            + "  extends A;"
            + "end B;";

        ASTStoredDefinition ast = modelica.parse(contents);

        testResolvedTypeCount(1, 0, ast.getMostSpecificScope(), false, "B", "X");
    }

    @Test
    void importTest() {
        String contents =
              "model I\n"
            + "  model Z\n"
            + "  end Z;\n"
            + "end I;\n"
            + "model A\n"
            + "  import I.Z;\n"
            + "  model X\n"
            + "  end X;\n"
            + "end A;\n"
            + "model B\n"
            + "  extends A;"
            + "end B;";

        ASTStoredDefinition ast = modelica.parse(contents);

        testResolvedTypeCount(1, 0, ast.getMostSpecificScope(), false, "A", "Z");
        testResolvedTypeCount(0, 0, ast.getMostSpecificScope(), false, "B", "Z");
    }

    @Test
    void builtinTest() {
        String contents =
              "model A"
            + "  encapsulated model B"
            + "    Real x;"
            + "  end B;"
            + "end A;";

        ASTStoredDefinition ast = modelica.parse(contents);

        List<ResolvableEntity> xs = testResolvedComponentCount(1, 0, ast.getMostSpecificScope(), true, "A", "B", "x").getBestCandidates();
        ModelicaComponentDeclaration x = (ModelicaComponentDeclaration) xs.get(0);
        ResolutionResult<ModelicaType> xTypes = x.getTypeCandidates();
        ensureCounts(xTypes, 1, 0);
        ResolvableEntity tpe = xTypes.getBestCandidates().get(0);
        assertTrue(tpe instanceof ModelicaBuiltinType);
        assertEquals(ModelicaBuiltinType.BaseType.REAL, ((ModelicaBuiltinType) tpe).getBaseType());
    }

    @Test
    void testRepeatingNameResolution() {
        String contents =
                  "package Test"
                + "  model X"
                + "    model X"
                + "    end X;"
                + "    Test.X.X mdl;"
                + "  end X;"
                + "end Test;";

        ASTStoredDefinition ast = modelica.parse(contents);

        testResolvedTypeCount(1, 0, ast.getMostSpecificScope(), true, "Test", "X", "X");
        testResolvedTypeCount(1, 0, ast.getMostSpecificScope(), false, "Test", "X", "X");

        ResolutionResult<ResolvableEntity> result = testResolvedComponentCount(1, 0, ast.getMostSpecificScope(), false, "Test", "X", "mdl");
        ModelicaComponentDeclaration mdl = (ModelicaComponentDeclaration) result.getBestCandidates().get(0);
        ensureCounts(mdl.getTypeCandidates(), 1, 0);
    }
}
