# How to build PMD

PMD uses [Maven](https://maven.apache.org/).

You'll need to have a `~/.m2/toolchains.xml` file setup
with jdk 1.7 and jdk 1.8 (for some features in pmd).
See [maven toolchains](https://maven.apache.org/guides/mini/guide-using-toolchains.html).
A example file can be found here: [example-toolchains.xml](https://github.com/pmd/pmd/blob/master/example-toolchains.xml).

PMD uses the [maven wrapper](https://github.com/takari/maven-wrapper), so you can simply build PMD as following:

*   `./mvnw clean verify` (on Unix-like platform such as Linux and Mac OS X)
*   `mvnw.cmd clean verify` (on Windows)

This will create the zip files in the directory `pmd-dist/target`:

    cd pmd-dist/target
    ls *.zip

That's all !

## How to build the documentation (maven site)?

Building the maven site is done with the following commands:

    ./mvnw clean install -DskipTests=true
    ./mvnw install site site:stage -Psite

You'll find the built site in the directory `target/staging/`.
