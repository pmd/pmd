package net.sourceforge.pmd.util.filter;

public class FilterBuilder {

	public static Filter<String> buildRegexFilter(String[] includeRegexes,
			String[] excludeRegexes) {
		OrFilter<String> includeFilter = new OrFilter<String>();
		if (includeRegexes == null || includeRegexes.length == 0) {
			includeFilter.addFilter(new RegexStringFilter(".*"));
		} else {
			for (String includeRegex : includeRegexes) {
				includeFilter.addFilter(new RegexStringFilter(includeRegex));
			}
		}

		OrFilter<String> excludeFilter = new OrFilter<String>();
		if (excludeRegexes != null) {
			for (String excludeRegex : excludeRegexes) {
				excludeFilter.addFilter(new RegexStringFilter(excludeRegex));
			}
		}

		return new AndFilter<String>(includeFilter, new NotFilter<String>(
				excludeFilter));
	}
}
