package net.sourceforge.pmd.jerry.env;

import java.util.HashMap;
import java.util.Map;

public class DefaultEnvironmentComponent implements EnvironmentComponent {

	private final Map<String, Object> values = new HashMap<String, Object>();

	public Object get(String symbol) {
		return values.get(symbol);
	}
}
