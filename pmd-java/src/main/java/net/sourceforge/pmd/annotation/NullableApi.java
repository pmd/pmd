package net.sourceforge.pmd.annotation;

import java.lang.annotation.ElementType;
import javax.annotation.Nonnull;
import javax.annotation.meta.TypeQualifierDefault;
import javax.annotation.meta.When;

@Nonnull(when = When.MAYBE)
@TypeQualifierDefault( {ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE_USE})
public @interface NullableApi {
}