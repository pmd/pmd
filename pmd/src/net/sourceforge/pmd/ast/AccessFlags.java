package net.sourceforge.pmd.ast;

public interface AccessFlags {
    // Stolen Shamelessly from BCEL
    /** Access flags for classes, fields and methods.
     */
    public final static short ACC_PUBLIC = 0x0001;
    public final static short ACC_PRIVATE = 0x0002;
    public final static short ACC_PROTECTED = 0x0004;
    public final static short ACC_STATIC = 0x0008;

    public final static short ACC_FINAL = 0x0010;
    public final static short ACC_SYNCHRONIZED = 0x0020;
    public final static short ACC_VOLATILE = 0x0040;
    public final static short ACC_TRANSIENT = 0x0080;

    public final static short ACC_NATIVE = 0x0100;
    public final static short ACC_INTERFACE = 0x0200;
    public final static short ACC_ABSTRACT = 0x0400;
    public final static short ACC_STRICT = 0x0800;

    // Applies to classes compiled by new compilers only
    public final static short ACC_SUPER = 0x0020;

}
