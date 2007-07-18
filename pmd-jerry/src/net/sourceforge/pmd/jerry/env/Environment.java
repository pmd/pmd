package net.sourceforge.pmd.jerry.env;

public interface Environment {
	/**
	 * Lookup in the given EnvironmentComponentEnum the value for the given
	 * symbol.
	 */
	Object get(EnvironmentComponentEnum environmentComponentEnum, String symbol);

	/**
	 * Update in the given EnvironmentComponentEnum the value for the given
	 * symbol. This will result in a new Environment reference in which the
	 * symbol will have the new value.
	 */
	Environment put(EnvironmentComponentEnum environmentComponentEnum,
			String symbol, Object value);
}
