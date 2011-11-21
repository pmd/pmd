package net.sourceforge.pmd.cpd;

import java.io.FilenameFilter;

import net.sourceforge.pmd.util.filter.Filters;

public abstract class AbstractLanguage implements Language {
	private final Tokenizer tokenizer;
	private final FilenameFilter fileFilter;

	public AbstractLanguage(Tokenizer tokenizer, String... extensions) {
		this.tokenizer = tokenizer;
		fileFilter = Filters.toFilenameFilter(Filters.getFileExtensionOrDirectoryFilter(extensions));
	}

	public FilenameFilter getFileFilter() {
		return fileFilter;
	}

	public Tokenizer getTokenizer() {
		return tokenizer;
	}
}
