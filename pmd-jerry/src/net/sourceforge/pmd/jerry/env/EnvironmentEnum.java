package net.sourceforge.pmd.jerry.env;

public enum EnvironmentEnum {
	/**
	 * The static environment.
	 */
	STATIC("statEnv"),

	/**
	 * The dynamic environment.
	 */
	DYNAMIC("dynEnv");

	private final String name;

	EnvironmentEnum(String name) {
		this.name = name;
	}

	public String toString() {
		return name;
	}
}
