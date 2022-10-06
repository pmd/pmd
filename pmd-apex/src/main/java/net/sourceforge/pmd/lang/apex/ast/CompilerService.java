/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import net.sourceforge.pmd.annotation.InternalApi;

import com.google.common.collect.ImmutableList;

/**
 * Central point for interfacing with the compiler. Based on <a href=
 * "https://github.com/forcedotcom/idecore/blob/master/com.salesforce.ide.apex.core/src/com/salesforce/ide/apex/internal/core/CompilerService.java"
 * > CompilerService</a> but with Eclipse dependencies removed.
 *
 * @author nchen
 *
 */
@Deprecated
@InternalApi
public class CompilerService {
    // public static final CompilerService INSTANCE = new CompilerService();
    // private final SymbolProvider symbolProvider;
    // private final AccessEvaluator accessEvaluator;
    // private QueryValidator queryValidator;
    //
    // /**
    //  * Configure a compiler with the default configurations:
    //  */
    // CompilerService() {
    //     this(EmptySymbolProvider.get(), new TestAccessEvaluator(), new TestQueryValidators.Noop());
    // }
    //
    // /**
    //  * Configure a compiler with the following configurations:
    //  *
    //  * @param symbolProvider
    //  *            A way to retrieve symbols, where symbols are names of types.
    //  * @param accessEvaluator
    //  *            A way to check for accesses to certain fields in types.
    //  * @param queryValidator
    //  *            A way to validate your queries.
    //  */
    // public CompilerService(SymbolProvider symbolProvider, AccessEvaluator accessEvaluator,
    //         QueryValidator queryValidator) {
    //     this.symbolProvider = symbolProvider;
    //     this.accessEvaluator = accessEvaluator;
    //     this.queryValidator = queryValidator;
    // }
    //
    //
    // /** @throws ParseException If the code is unparsable */
    // public ApexCompiler visitAstFromString(String source, AstVisitor<AdditionalPassScope> visitor) {
    //     return visitAstsFromStrings(ImmutableList.of(source), visitor, CompilerStage.POST_TYPE_RESOLVE);
    // }
    //
    // /** @throws ParseException If the code is unparsable */
    // public ApexCompiler visitAstsFromStrings(List<String> sources, AstVisitor<AdditionalPassScope> visitor) {
    //     return visitAstsFromStrings(sources, visitor, CompilerStage.POST_TYPE_RESOLVE);
    // }
    //
    // /** @throws ParseException If the code is unparsable */
    // public ApexCompiler visitAstsFromStrings(List<String> sources, AstVisitor<AdditionalPassScope> visitor,
    //         CompilerStage compilerStage) {
    //     List<SourceFile> sourceFiles = sources.stream().map(s -> SourceFile.builder().setBody(s).build())
    //             .collect(Collectors.toList());
    //     CompilationInput compilationUnit = createCompilationInput(sourceFiles, visitor);
    //     return compile(compilationUnit, compilerStage);
    // }
    //
    // private ApexCompiler compile(CompilationInput compilationInput, CompilerStage compilerStage) {
    //     ApexCompiler compiler = ApexCompiler.builder().setInput(compilationInput).build();
    //     compiler.compile(compilerStage);
    //     callAdditionalPassVisitor(compiler);
    //     throwParseErrorIfAny(compiler);
    //     return compiler;
    // }
    //
    // private void throwParseErrorIfAny(ApexCompiler compiler) {
    //     // this ignores semantic errors
    //
    //     ParseException parseError = null;
    //     for (CompilationException error : compiler.getErrors()) {
    //         if (error instanceof ParseException) {
    //             if (parseError == null) {
    //                 parseError = (ParseException) error;
    //             } else {
    //                 parseError.addSuppressed(error);
    //             }
    //         }
    //     }
    //     if (parseError != null) {
    //         throw parseError;
    //     }
    // }
    //
    // private CompilationInput createCompilationInput(List<SourceFile> sourceFiles,
    //                                                 AstVisitor<AdditionalPassScope> visitor) {
    //     return new CompilationInput(sourceFiles, symbolProvider, accessEvaluator, queryValidator, visitor,
    //                                 NoopCompilerProgressCallback.get());
    // }
    //
    // /**
    //  * This is temporary workaround to bypass the validation stage of the
    //  * compiler while *still* doing the additional_validate stage. We are
    //  * bypassing the validation stage because it does a deep validation that we
    //  * don't have all the parts for yet in the offline compiler. Rather than
    //  * stop all work on that, we bypass it so that we can still do useful things
    //  * like find all your types, find all your methods, etc.
    //  *
    //  */
    // @SuppressWarnings("unchecked")
    // private void callAdditionalPassVisitor(ApexCompiler compiler) {
    //     try {
    //         List<CodeUnit> allUnits = (List<CodeUnit>) FieldUtils.readDeclaredField(compiler, "allUnits", true);
    //         CompilerContext compilerContext = (CompilerContext) FieldUtils.readDeclaredField(compiler,
    //                 "compilerContext", true);
    //
    //         for (CodeUnit unit : allUnits) {
    //             CompilerOperation operation = (CompilerOperation)
    //                     MethodUtils.invokeMethod(CompilerStage.ADDITIONAL_VALIDATE, true, "getOperation");
    //             operation.invoke(compilerContext, unit);
    //         }
    //     } catch (IllegalArgumentException | ReflectiveOperationException e) {
    //         throw new RuntimeException(e);
    //     }
    // }
    // TODO(b/239648780)
}
