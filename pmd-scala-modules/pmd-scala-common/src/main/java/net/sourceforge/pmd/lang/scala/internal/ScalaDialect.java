/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.internal;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.LanguageVersion;

import scala.meta.Dialect;

public final class ScalaDialect {
    private ScalaDialect() {}

    public static @NonNull Dialect dialectOf(LanguageVersion v) {
        switch (v.getVersion()) {
        case "2.10": return scala.meta.dialects.package$.MODULE$.Scala210();
        case "2.11": return scala.meta.dialects.package$.MODULE$.Scala211();
        case "2.12": return scala.meta.dialects.package$.MODULE$.Scala212();
        case "2.13": return scala.meta.dialects.package$.MODULE$.Scala213();
        default:
            throw new IllegalArgumentException(v.getVersion());
        }
    }
}
