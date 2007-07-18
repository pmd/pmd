package net.sourceforge.pmd.jerry.env;

public class DefaultEnvironment implements Environment {

	private final Environment parent;

	private final EnvironmentComponent[] environmentComponents = new EnvironmentComponent[EnvironmentComponentEnum
			.values().length];

	public DefaultEnvironment() {
		this(null);
	}

	public DefaultEnvironment(Environment parent) {
		this.parent = parent;
	}

	public Object get(EnvironmentComponentEnum environmentComponentEnum,
			String symbol) {
		Object value = null;
		EnvironmentComponent environmentComponent = environmentComponents[(environmentComponentEnum
				.ordinal())];
		if (environmentComponent != null) {
			value = environmentComponent.get(symbol);
		}
		// Check parent if we don't have a value yet.
		if (value == null && parent != null) {
			value = parent.get(environmentComponentEnum, symbol);
		}
		return value;
	}

	public Environment put(EnvironmentComponentEnum environmentComponentEnum,
			String symbol, Object value) {
		// TODO Auto-generated method stub
		return null;
	}
}
