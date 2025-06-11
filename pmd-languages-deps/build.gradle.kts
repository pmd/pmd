// build.gradle.kts
plugins {
    // Apply the Java Library plugin for dependency management capabilities,
    // even if this project itself doesn't produce a JAR.
    `java-library`
    java
}

//group = "net.sourceforge.pmd"
//version = "7.15.0-SNAPSHOT"

description = "PMD Languages Dependencies"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // The 'api' configuration is typically used when you want these dependencies
    // to be part of the API of this module, meaning any project depending on
    // 'pmd-languages-deps' will transitively get these dependencies.
    // If these are internal dependencies for compilation only, 'implementation' might be more appropriate.
    // Given the Maven POM's purpose, 'api' seems to be the closer equivalent.

    // PMD Language Modules
    api("net.sourceforge.pmd:pmd-apex:7.15.0-SNAPSHOT")
    api("net.sourceforge.pmd:pmd-coco:7.15.0-SNAPSHOT")
    api("net.sourceforge.pmd:pmd-cpp:7.15.0-SNAPSHOT")
    api("net.sourceforge.pmd:pmd-cs:7.15.0-SNAPSHOT")
    api("net.sourceforge.pmd:pmd-dart:7.15.0-SNAPSHOT")
    api("net.sourceforge.pmd:pmd-fortran:7.15.0-SNAPSHOT")
    api("net.sourceforge.pmd:pmd-gherkin:7.15.0-SNAPSHOT")
    api("net.sourceforge.pmd:pmd-go:7.15.0-SNAPSHOT")
    api("net.sourceforge.pmd:pmd-groovy:7.15.0-SNAPSHOT")
    api("net.sourceforge.pmd:pmd-html:7.15.0-SNAPSHOT")
    api("net.sourceforge.pmd:pmd-java:7.15.0-SNAPSHOT")
    api("net.sourceforge.pmd:pmd-javascript:7.15.0-SNAPSHOT")
    api("net.sourceforge.pmd:pmd-jsp:7.15.0-SNAPSHOT")
    api("net.sourceforge.pmd:pmd-julia:7.15.0-SNAPSHOT")
    api("net.sourceforge.pmd:pmd-kotlin:7.15.0-SNAPSHOT")
    api("net.sourceforge.pmd:pmd-lua:7.15.0-SNAPSHOT")
    api("net.sourceforge.pmd:pmd-matlab:7.15.0-SNAPSHOT")
    api("net.sourceforge.pmd:pmd-modelica:7.15.0-SNAPSHOT")
    api("net.sourceforge.pmd:pmd-objectivec:7.15.0-SNAPSHOT")
    api("net.sourceforge.pmd:pmd-perl:7.15.0-SNAPSHOT")
    api("net.sourceforge.pmd:pmd-php:7.15.0-SNAPSHOT")
    api("net.sourceforge.pmd:pmd-plsql:7.15.0-SNAPSHOT")
    api("net.sourceforge.pmd:pmd-python:7.15.0-SNAPSHOT")
    api("net.sourceforge.pmd:pmd-ruby:7.15.0-SNAPSHOT")
    api("net.sourceforge.pmd:pmd-rust:7.15.0-SNAPSHOT")
    api("net.sourceforge.pmd:pmd-scala_2.13:7.15.0-SNAPSHOT")
    api("net.sourceforge.pmd:pmd-swift:7.15.0-SNAPSHOT")
    api("net.sourceforge.pmd:pmd-tsql:7.15.0-SNAPSHOT")
    api("net.sourceforge.pmd:pmd-visualforce:7.15.0-SNAPSHOT")
    api("net.sourceforge.pmd:pmd-velocity:7.15.0-SNAPSHOT")
    api("net.sourceforge.pmd:pmd-xml:7.15.0-SNAPSHOT")
}

java {
    // Option A: Specific compatibility versions
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17

    // Option B: Using toolchains (recommended for managing JDKs)
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}