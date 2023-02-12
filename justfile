

pmdJavaDeps := "pmd-core,pmd-lang-test,pmd-test,pmd-java"
commonBuildOpts := "-Dkotlin.compiler.incremental"

genJavaAst:
	rm -f pmd-java/target/generated-sources/javacc/last-generated-timestamp
	mvnd generate-sources -pl pmd-java

reGenAllSources:
	rm -rf pmd-*/target/generated-sources
	rm pmd-*/target/last-generated-timestamp
	mvnd generate-sources

install MOD:
    mvnd install -Dmaven.javadoc.skip -Dkotlin.compiler.incremental -pl "pmd-{{MOD}}"

alias i := install

cleanInstallEverything *FLAGS:
   mvnd clean install -Dmaven.javadoc.skip -Dkotlin.compiler.incremental -fae {{FLAGS}}

testCore *FLAGS:
   mvnd test checkstyle:check pmd:check -Dmaven.javadoc.skip -Dkotlin.compiler.incremental -pl pmd-core {{FLAGS}}

testJava *FLAGS:
   mvnd test checkstyle:check pmd:check -Dmaven.javadoc.skip -Dkotlin.compiler.incremental -pl pmd-java {{FLAGS}}

installJavaAndDeps *FLAGS:
   mvnd install -Dmaven.javadoc.skip -Dkotlin.compiler.incremental -pl {{pmdJavaDeps}}  {{FLAGS}}

installJava *FLAGS:
   mvnd install -Dmaven.javadoc.skip -Dkotlin.compiler.incremental -pl pmd-java  {{FLAGS}}


lintChanged DIFF="master" *FLAGS="":
  #!/bin/env zsh
  changed=$(git diff --name-only {{DIFF}})
  changed=${(f)changed}
  projects=${changed%%/*} # remove all but first segment
  echo $projects
  # todo filter to pmd-*
  mvnd checkstyle:check pmd:check -pl $projects -fae


lint projects="pmd-java":
  mvnd checkstyle:check pmd:check -pl {{projects}} -fae

lintAll:
  mvnd checkstyle:check pmd:check -fae

