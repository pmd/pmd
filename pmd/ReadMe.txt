How to build PMD ?
==================

Simply use maven: $ mvn compile

PMD now uses a small plugin to generate its website, so if you want to build the
website ($ mvn site), you'll need to install it:

$ cd ../maven-plugin-pmd-build
$ mvn clean install

That's all !
