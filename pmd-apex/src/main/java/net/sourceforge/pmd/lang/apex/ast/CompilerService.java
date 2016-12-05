/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.reflect.FieldUtils;

import apex.jorje.semantic.ast.visitor.AdditionalPassScope;
import apex.jorje.semantic.ast.visitor.AstVisitor;
import apex.jorje.semantic.compiler.ApexCompiler;
import apex.jorje.semantic.compiler.CodeUnit;
import apex.jorje.semantic.compiler.CompilationInput;
import apex.jorje.semantic.compiler.CompilerContext;
import apex.jorje.semantic.compiler.CompilerOperation;
import apex.jorje.semantic.compiler.CompilerStage;
import apex.jorje.semantic.compiler.SourceFile;
import apex.jorje.semantic.compiler.sfdc.AccessEvaluator;
import apex.jorje.semantic.compiler.sfdc.QueryValidator;
import apex.jorje.semantic.compiler.sfdc.SymbolProvider;
import apex.jorje.semantic.tester.EmptySymbolProvider;
import apex.jorje.semantic.tester.TestAccessEvaluator;
import apex.jorje.semantic.tester.TestQueryValidators;
import com.google.common.collect.ImmutableList;

/**
 * Central point for interfacing with the compiler. Based on <a href=
 * "https://github.com/forcedotcom/idecore/blob/master/com.salesforce.ide.apex.core/src/com/salesforce/ide/apex/internal/core/CompilerService.java"
 * > CompilerService</a> but with Eclipse dependencies removed.
 * 
 * @author nchen
 * 
 */
public class CompilerService {
    public static final CompilerService INSTANCE = new CompilerService();
    private final SymbolProvider symbolProvider;
    private final AccessEvaluator accessEvaluator;
    private QueryValidator queryValidator;

    /**
     * Configure a compiler with the default configurations:
     * 
     * @param symbolProvider
     *            EmptySymbolProvider, doesn't provide any symbols that are not
     *            part of source.
     * @param accessEvaluator
     *            TestAccessEvaluator, doesn't provide any validation.
     * @param queryValidator
     *            TestQueryValidators.Noop, no validation of queries.
     */
    CompilerService() {
        this(EmptySymbolProvider.get(), new TestAccessEvaluator(), new TestQueryValidators.Noop());
    }

    /**
     * Configure a compiler with the following configurations:
     * 
     * @param symbolProvider
     *            A way to retrieve symbols, where symbols are names of types.
     * @param accessEvaluator
     *            A way to check for accesses to certain fields in types.
     * @param queryValidator
     *            A way to validate your queries.
     */
    public CompilerService(SymbolProvider symbolProvider, AccessEvaluator accessEvaluator,
            QueryValidator queryValidator) {
        this.symbolProvider = symbolProvider;
        this.accessEvaluator = accessEvaluator;
        this.queryValidator = queryValidator;
    }

    public ApexCompiler visitAstFromString(String source, AstVisitor<AdditionalPassScope> visitor) {
        return visitAstsFromStrings(ImmutableList.of(source), visitor, CompilerStage.POST_TYPE_RESOLVE);
    }

    public ApexCompiler visitAstsFromStrings(List<String> sources, AstVisitor<AdditionalPassScope> visitor) {
        return visitAstsFromStrings(sources, visitor, CompilerStage.POST_TYPE_RESOLVE);
    }

    public ApexCompiler visitAstsFromStrings(List<String> sources, AstVisitor<AdditionalPassScope> visitor,
            CompilerStage compilerStage) {
        List<SourceFile> sourceFiles = sources.stream().map(s -> SourceFile.builder().setBody(s).build())
                .collect(Collectors.toList());
        CompilationInput compilationUnit = createCompilationInput(sourceFiles, visitor);
        return compile(compilationUnit, visitor, compilerStage);
    }

    private ApexCompiler compile(CompilationInput compilationInput, AstVisitor<AdditionalPassScope> visitor,
            CompilerStage compilerStage) {
        ApexCompiler compiler = ApexCompiler.builder().setInput(compilationInput).build();
        compiler.compile(compilerStage);
        callAdditionalPassVisitor(compiler);
        return compiler;
    }

    private CompilationInput createCompilationInput(List<SourceFile> sourceFiles,
            AstVisitor<AdditionalPassScope> visitor) {
        return new CompilationInput(sourceFiles, symbolProvider, accessEvaluator, queryValidator, visitor);
    }

    /**
     * This is temporary workaround to bypass the validation stage of the
     * compiler while *still* doing the additional_validate stage. We are
     * bypassing the validation stage because it does a deep validation that we
     * don't have all the parts for yet in the offline compiler. Rather than
     * stop all work on that, we bypass it so that we can still do useful things
     * like find all your types, find all your methods, etc.
     * 
     */
    @SuppressWarnings("unchecked")
    private void callAdditionalPassVisitor(ApexCompiler compiler) {
        try {
            List<CodeUnit> allUnits = (List<CodeUnit>) FieldUtils.readDeclaredField(compiler, "allUnits", true);
            CompilerContext compilerContext = (CompilerContext) FieldUtils.readDeclaredField(compiler,
                    "compilerContext", true);

            for (CodeUnit unit : allUnits) {
                Method getOperation = CompilerStage.ADDITIONAL_VALIDATE.getDeclaringClass()
                        .getDeclaredMethod("getOperation");
                getOperation.setAccessible(true);
                CompilerOperation operation = (CompilerOperation) getOperation
                        .invoke(CompilerStage.ADDITIONAL_VALIDATE);
                operation.invoke(compilerContext, unit);
            }
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
        }
    }
}
