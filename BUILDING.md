# How to build PMD

PMD uses [Maven](https://maven.apache.org/).

Additionally you'll need to have a `~/.m2/toolchains.xml` file setup
with jdk 1.6 (for pmd 5.3.x), jdk 1.7 (for pmd 5.4.x and pmd 5.5.x) and jdk 1.8 (for some features in pmd 5.5.x).
See [maven toolchains](https://maven.apache.org/guides/mini/guide-using-toolchains.html).
A example file can be found here: [example-toolchains.xml](https://github.com/pmd/pmd/blob/master/example-toolchains.xml).

Use maven in the top-level directory:

    mvn clean verify

This will create the zip files in the directory `pmd-dist/target`:

    cd pmd-dist/target
    ls *.zip

That's all !

## How to build the documentation (maven site)?

Building the maven site is done with the following commands:

    mvn clean install -DskipTests=true
    mvn install site site:stage -Psite

You'll find the built site in the directory `target/staging/`.
