How to build PMD ?
==================

Simply use maven: $ mvn compile

PMD now uses a small plugin to generate its website, so if you want to build the
website ($ mvn site), you'll need to install it:

$ cd ../maven-plugin-pmd-build
$ mvn clean install

That's all !

How to quickly build a "release" (zipfiles - for testing purpose only) ?
------------------------------------------------------------------------

$ mvn -Dmaven.test.skip=true -Dmaven.clover.skip=true verify post-site

Full release process is documented in src/site/xdocs/pmd-release-process.xml
