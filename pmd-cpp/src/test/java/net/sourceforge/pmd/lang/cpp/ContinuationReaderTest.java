package net.sourceforge.pmd.lang.cpp;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Test;

public class ContinuationReaderTest {
	@Test
	public void testHappyPath() throws IOException {
		assertEquals("empty", "", filter(""));
		assertEquals("anything", "anything", filter("anything"));

		assertEquals("partial: BS", "\\", filter("\\"));
		assertEquals("partial: BS LF", "\\\r", filter("\\\r"));
		assertEquals("full: BS CR", "", filter("\\\n"));
		assertEquals("full: BS LF CR", "", filter("\\\r\n"));

		assertEquals("partial: BS: prefix", "prefix\\", filter("prefix\\"));
		assertEquals("partial: BS LF: prefix", "prefix\\\r", filter("prefix\\\r"));
		assertEquals("full: BS CR: prefix", "prefix", filter("prefix\\\n"));
		assertEquals("full: BS LF CR: prefix", "prefix", filter("prefix\\\r\n"));

		assertEquals("partial: BS: suffix", "\\suffix", filter("\\suffix"));
		assertEquals("partial: BS LF: suffix", "\\\rsuffix", filter("\\\rsuffix"));
		assertEquals("full: BS CR: suffix", "suffix", filter("\\\nsuffix"));
		assertEquals("full: BS LF CR: suffix", "suffix", filter("\\\r\nsuffix"));

		assertEquals("partial: BS: prefix, suffix", "prefix\\suffix", filter("prefix\\suffix"));
		assertEquals("partial: BS LF: prefix, suffix", "prefix\\\rsuffix", filter("prefix\\\rsuffix"));
		assertEquals("full: BS CR: prefix, suffix", "prefixsuffix", filter("prefix\\\nsuffix"));
		assertEquals("full: BS LF CR: prefix, suffix", "prefixsuffix", filter("prefix\\\r\nsuffix"));

		assertEquals("complex mixed", "abc", filter("a\\\r\nb\\\n\\\n\\\r\nc"));
	}

	private static String filter(String s) throws IOException {
		ContinuationReader reader = new ContinuationReader(new StringReader(s));
		try {
			StringBuilder buf = new StringBuilder();
			int c;
			while ((c = reader.read()) >= 0) {
				buf.append((char) c);
			}
			return buf.toString();
		} finally {
			reader.close();
		}
	}
}
