/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.treeexport;

import static org.hamcrest.Matchers.containsString;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import net.sourceforge.pmd.lang.ast.DummyNode;
import net.sourceforge.pmd.lang.ast.xpath.Attribute;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertySource;

/**
 *
 */
public class TreeExportCliTest {

    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    @Test
    public void testReadStandardInput() {
        IoSpy spy = IoSpy.withStdin("(a(b))");
        int status = spy.runMain("-i", "-f", "xml");
        Assert.assertEquals(0, status);
        spy.assertThatStdout(containsString("<?xml version='1.0' encoding='UTF-8' ?>\n"
                                            + "<dummyNode Image=''>\n"
                                            + "    <dummyNode Image='a'>\n"
                                            + "        <dummyNode Image='b' />\n"
                                            + "    </dummyNode>\n"
                                            + "</dummyNode>"));
    }

    @Test
    public void testReadFile() throws IOException {
        File file = newFileWithContents("(a(b))");
        IoSpy spy = new IoSpy();
        int status = spy.runMain("--file", file.getAbsolutePath(), "-f", "xml");
        Assert.assertEquals(0, status);
        spy.assertThatStdout(containsString("<?xml version='1.0' encoding='UTF-8' ?>\n"
                                            + "<dummyNode Image=''>\n"
                                            + "    <dummyNode Image='a'>\n"
                                            + "        <dummyNode Image='b' />\n"
                                            + "    </dummyNode>\n"
                                            + "</dummyNode>"));
    }

    private File newFileWithContents(String data) throws IOException {
        File file = tmp.newFile();
        try (BufferedWriter br = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8)) {
            br.write(data);
        }
        return file;
    }

    private static InputStream stdinContaining(String input) {
        return IOUtils.toInputStream(input, StandardCharsets.UTF_8);
    }

    static class IoSpy {

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final ByteArrayOutputStream err = new ByteArrayOutputStream();
        final Io io;

        IoSpy(InputStream stdin) {
            io = new Io(new PrintStream(out), new PrintStream(err), stdin);
        }

        IoSpy() {
            this(stdinContaining(""));
        }

        void assertThatStdout(Matcher<? super String> str) {
            MatcherAssert.assertThat("stdout", out.toString(), str);
        }

        int runMain(String... args) {
            return new TreeExportCli(io).runMain(args);
        }

        static IoSpy withStdin(String contents) {
            return new IoSpy(stdinContaining(contents));
        }
    }

    @Test
    public void testXmlPropertiesAvailable() {


        PropertySource properties = TreeRenderers.XML.newPropertyBundle();

        MatcherAssert.assertThat(properties.getPropertyDescriptors(),
                                 Matchers.<PropertyDescriptor<?>>containsInAnyOrder(TreeRenderers.XML_LINE_SEPARATOR,
                                                                                    TreeRenderers.XML_RENDER_COMMON_ATTRIBUTES,
                                                                                    TreeRenderers.XML_RENDER_PROLOG,
                                                                                    TreeRenderers.XML_USE_SINGLE_QUOTES));

    }

    @Test
    public void testXmlDescriptorDump() throws IOException {

        PropertySource bundle = TreeRenderers.XML.newPropertyBundle();

        bundle.setProperty(TreeRenderers.XML_RENDER_PROLOG, false);
        bundle.setProperty(TreeRenderers.XML_USE_SINGLE_QUOTES, false);
        bundle.setProperty(TreeRenderers.XML_LINE_SEPARATOR, "\n");

        TreeRenderer renderer = TreeRenderers.XML.produceRenderer(bundle);

        StringBuilder out = new StringBuilder();

        renderer.renderSubtree(dummyTree1(), out);
        Assert.assertEquals("<dummyNode foo=\"bar\" ohio=\"4\">\n"
                            + "    <dummyNode o=\"ha\" />\n"
                            + "    <dummyNode />\n"
                            + "</dummyNode>\n", out.toString());

    }


    public MyDummyNode dummyTree1() {
        MyDummyNode dummy = new MyDummyNode();

        dummy.setXPathAttribute("foo", "bar");
        dummy.setXPathAttribute("ohio", "4");

        MyDummyNode dummy1 = new MyDummyNode();

        dummy1.setXPathAttribute("o", "ha");

        MyDummyNode dummy2 = new MyDummyNode();

        dummy.jjtAddChild(dummy1, 0);
        dummy.jjtAddChild(dummy2, 1);
        return dummy;
    }

    private static class MyDummyNode extends DummyNode {


        private final Map<String, String> attributes = new HashMap<>();

        public void setXPathAttribute(String name, String value) {
            attributes.put(name, value);
        }

        @Override
        public Iterator<Attribute> getXPathAttributesIterator() {

            List<Attribute> attrs = new ArrayList<>();
            for (String name : attributes.keySet()) {
                attrs.add(new Attribute(this, name, attributes.get(name)));
            }

            return attrs.iterator();
        }


    }


}
