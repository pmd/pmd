package net.sourceforge.pmd.ast;

// FUTURE Rename this to AccessNode
// FUTURE Remove non JavaBean setters
/**
 * This interface captures Java access modifiers.
 */
public interface AccessNodeInterface {

	int PUBLIC = 0x0001;
	int PROTECTED = 0x0002;
	int PRIVATE = 0x0004;
	int ABSTRACT = 0x0008;
	int STATIC = 0x0010;
	int FINAL = 0x0020;
	int SYNCHRONIZED = 0x0040;
	int NATIVE = 0x0080;
	int TRANSIENT = 0x0100;
	int VOLATILE = 0x0200;
	int STRICTFP = 0x1000;

	int getModifiers();

	void setModifiers(int modifiers);

	boolean isPublic();

	/**
	 * @deprecated Use setPublic(boolean) instead.
	 */
	void setPublic();

	void setPublic(boolean isPublic);

	boolean isProtected();

	/**
	 * @deprecated Use setProtected(boolean) instead.
	 */
	void setProtected();

	void setProtected(boolean isProtected);

	boolean isPrivate();

	/**
	 * @deprecated Use setPrivate(boolean) instead.
	 */
	void setPrivate();

	void setPrivate(boolean isPrivate);

	boolean isAbstract();

	/**
	 * @deprecated Use setAbstract(boolean) instead.
	 */
	void setAbstract();

	void setAbstract(boolean isAbstract);

	boolean isStatic();

	/**
	 * @deprecated Use setStatic(boolean) instead.
	 */
	void setStatic();

	void setStatic(boolean isStatic);

	boolean isFinal();

	/**
	 * @deprecated Use setFinal(boolean) instead.
	 */
	void setFinal();

	void setFinal(boolean isFinal);

	boolean isSynchronized();

	/**
	 * @deprecated Use setSynchronized(boolean) instead.
	 */
	void setSynchronized();

	void setSynchronized(boolean isSynchronized);

	boolean isNative();

	/**
	 * @deprecated Use setNative(boolean) instead.
	 */
	void setNative();

	void setNative(boolean isNative);

	boolean isTransient();

	/**
	 * @deprecated Use setTransient(boolean) instead.
	 */
	void setTransient();

	void setTransient(boolean isTransient);

	boolean isVolatile();

	/**
	 * @deprecated Use setVolatile(boolean) instead.
	 */
	void setVolatile();

	void setVolatile(boolean isVolatile);

	boolean isStrictfp();

	/**
	 * @deprecated Use setStrictfp(boolean) instead.
	 */
	void setStrictfp();

	void setStrictfp(boolean isStrictfp);

	boolean isPackagePrivate();
}
