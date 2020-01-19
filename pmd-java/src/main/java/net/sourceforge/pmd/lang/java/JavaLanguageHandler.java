/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java;

import java.util.Arrays;
import java.util.List;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.MethodLikeNode;
import net.sourceforge.pmd.lang.java.metrics.JavaMetrics;
import net.sourceforge.pmd.lang.java.metrics.api.JavaClassMetricKey;
import net.sourceforge.pmd.lang.java.metrics.api.JavaOperationMetricKey;
import net.sourceforge.pmd.lang.metrics.MetricKey;
import net.sourceforge.pmd.lang.metrics.internal.AbstractLanguageMetricsProvider;

/**
 * @deprecated This is internal API, use {@link LanguageVersion#getLanguageVersionHandler()}.
 */
@Deprecated
@InternalApi
public class JavaLanguageHandler extends AbstractJavaHandler {
    private final int jdkVersion;
    private final boolean preview;

    public JavaLanguageHandler(int jdkVersion) {
        this(jdkVersion, false);
    }

    public JavaLanguageHandler(int jdkVersion, boolean preview) {
        this.jdkVersion = jdkVersion;
        this.preview = preview;
    }

    @Override
    public Parser getParser(ParserOptions parserOptions) {
        return new JavaLanguageParser(jdkVersion, preview, parserOptions);
    }

    public static class JavaMetricsProvider extends AbstractLanguageMetricsProvider<ASTAnyTypeDeclaration, MethodLikeNode> {

        JavaMetricsProvider() {
            super(ASTAnyTypeDeclaration.class, MethodLikeNode.class);
        }

        @Override
        protected List<MethodLikeNode> findOps(ASTAnyTypeDeclaration astAnyTypeDeclaration) {
            return JavaMetrics.findOps(astAnyTypeDeclaration);
        }

        @Override
        public List<? extends MetricKey<ASTAnyTypeDeclaration>> getAvailableTypeMetrics() {
            return Arrays.asList(JavaClassMetricKey.values());
        }


        @Override
        public List<? extends MetricKey<MethodLikeNode>> getAvailableOperationMetrics() {
            return Arrays.asList(JavaOperationMetricKey.values());
        }
    }
}
