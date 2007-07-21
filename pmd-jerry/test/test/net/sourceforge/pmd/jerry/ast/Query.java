package test.net.sourceforge.pmd.jerry.ast;

public class Query {

	private final String xpath;

	private final String abbreviated;

	private final String unabbreviated;

	private final String core;

	public Query(final String xpath) {
		this(xpath, null, null, null);
	}

	public Query(final String xpath, final String abbreviated,
			final String unabbreviated, final String core) {
		super();
		this.xpath = xpath;
		this.abbreviated = abbreviated;
		this.unabbreviated = unabbreviated;
		this.core = core;
	}

	public String getXPath() {
		return xpath;
	}

	public String getAbbreviated() {
		return abbreviated != null ? abbreviated : xpath;
	}

	public String getUnabbreviated() {
		return unabbreviated != null ? unabbreviated : xpath;
	}

	public String getCore() {
		return core;
	}

	public String toString() {
		return xpath;
	}
}
