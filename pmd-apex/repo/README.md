# Local Maven Repo for the Apex Jorje Parser library

You can download the needed libraries from:
<https://github.com/forcedotcom/idecore/tree/master/com.salesforce.ide.apex.core/lib>

Apex Reference:
<https://developer.salesforce.com/docs/atlas.en-us.apexcode.meta/apexcode/apex_reference.htm>

In order to add a the jar files to the local repo, use the following commands:

    mvn install:install-file -Dfile=path/to/apex-jorje-ide-1.0-sfdc-187-SNAPSHOT.jar \
                             -DgroupId=apex \
                             -DartifactId=apex-jorje-ide \
                             -Dversion=1.0-sfdc-187-SNAPSHOT \
                             -Dpackaging=jar \
                             -DlocalRepositoryPath=./repo
    
    mvn install:install-file -Dfile=path/to/apex-jorje-semantic-1.0-sfdc-187-SNAPSHOT-tests.jar \
                             -DgroupId=apex \
                             -DartifactId=apex-jorje-semantic \
                             -Dversion=1.0-sfdc-187-SNAPSHOT \
                             -Dclassifier=tests \
                             -Dpackaging=jar \
                             -DlocalRepositoryPath=./repo

For the PMD 5.5.0 Release, the versions from
<https://github.com/forcedotcom/idecore/tree/55042bfc2ebeb4c9f38c445b577c3bdd6a18fc5a/com.salesforce.ide.apex.core/lib>
have been taken.
