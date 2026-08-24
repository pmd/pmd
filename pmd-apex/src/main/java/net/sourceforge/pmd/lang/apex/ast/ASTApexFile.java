/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import net.sourceforge.pmd.lang.apex.ApexLanguageProcessor;
import net.sourceforge.pmd.lang.apex.multifile.ApexMultifileAnalysis;
import net.sourceforge.pmd.lang.ast.AstInfo;
import net.sourceforge.pmd.lang.ast.Parser.ParserTask;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.document.FileId;
import net.sourceforge.pmd.lang.document.TextRegion;
import net.sourceforge.pmd.reporting.ViolationSuppressor.SuppressionCommentWrapper;

import com.google.summit.ast.CompilationUnit;
import com.nawforce.apexlink.api.TypeSummary;
import io.github.apexdevtools.api.Issue;

public final class ASTApexFile extends AbstractApexNode.Single<CompilationUnit> implements RootNode {

    private static final Logger LOG = LoggerFactory.getLogger(ASTApexFile.class);

    private final AstInfo<ASTApexFile> astInfo;
    private final @NonNull ApexMultifileAnalysis multifileAnalysis;
    private Optional<Double> apiVersion;

    ASTApexFile(ParserTask task,
                CompilationUnit compilationUnit,
                Collection<? extends SuppressionCommentWrapper> suppressMap,
                @NonNull ApexLanguageProcessor apexLang) {
        super(compilationUnit);
        this.astInfo = new AstInfo<>(task, this).withSuppressionComments(suppressMap);
        this.multifileAnalysis = apexLang.getMultiFileState();
        this.setRegion(TextRegion.fromOffsetLength(0, task.getTextDocument().getLength()));
    }

    @Override
    public AstInfo<ASTApexFile> getAstInfo() {
        return astInfo;
    }

    public ASTUserClassOrInterface<?> getMainNode() {
        return firstChild(ASTUserClassOrInterface.class);
    }

    /**
     * @since 7.27.0
     */
    public ASTAnonymousBlock getAnonymousBlock() {
        return firstChild(ASTAnonymousBlock.class);
    }

    @Override
    public @NonNull ASTApexFile getRoot() {
        return this;
    }


    @Override
    protected <P, R> R acceptApexVisitor(ApexVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    public List<Issue> getGlobalIssues() {
        FileId fileId = getAstInfo().getTextDocument().getFileId();
        return multifileAnalysis.getFileIssues(fileId.getAbsolutePath());
    }

    /**
     * Returns an unmodifiable list of all type summaries in the org.
     * Returns an empty list when multifile analysis is unavailable.
     * This enables rules to perform complex cross-type analysis.
     * @since 7.24.0
     */
    public @NonNull List<TypeSummary> getTypeSummaries() {
        return multifileAnalysis.getTypeSummaries();
    }

    @Override
    public String getDefiningType() {
        // an apex file can contain only one top level type
        BaseApexClass baseApexClass = firstChild(BaseApexClass.class);
        if (baseApexClass != null) {
            return baseApexClass.getQualifiedName().toString();
        }
        return null;
    }

    /**
     * Returns the Salesforce API version declared in this file's companion
     * {@code *-meta.xml} descriptor (e.g. {@code Foo.cls-meta.xml}), if one
     * exists and can be read.
     *
     * <p>Returns {@link Optional#empty()} when there is no on-disk companion
     * metadata file (for example when analyzing in-memory source, as in most
     * unit tests), when it can't be read, or when it has no {@code <apiVersion>}
     * element. Callers should treat an empty result as "unknown", not as "low
     * version".
     *
     * @since 7.27.0
     */
    public @NonNull Optional<Double> getApiVersion() {
        if (apiVersion == null) {
            apiVersion = Optional.ofNullable(readApiVersionFromMetaXml());
        }
        return apiVersion;
    }

    private Double readApiVersionFromMetaXml() {
        FileId fileId = getAstInfo().getTextDocument().getFileId();
        Path metaXmlPath = Paths.get(fileId.getAbsolutePath() + "-meta.xml");
        if (!Files.isRegularFile(metaXmlPath)) {
            return null;
        }
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(false);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            Document document = factory.newDocumentBuilder().parse(metaXmlPath.toFile());
            NodeList nodes = document.getElementsByTagName("apiVersion");
            if (nodes.getLength() == 0) {
                LOG.debug("No apiVersion found in {}", metaXmlPath);
                return null;
            }
            return Double.parseDouble(nodes.item(0).getTextContent().trim());
        } catch (IOException | SAXException | ParserConfigurationException | NumberFormatException e) {
            LOG.debug("Could not read apiVersion from {}: {}", metaXmlPath, e.toString());
            return null;
        }
    }
}
