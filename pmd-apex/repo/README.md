# Local Maven Repo for the Apex Jorje Parser library

You can download the needed libraries from:
<https://github.com/forcedotcom/idecore/tree/master/com.salesforce.ide.apex.core/lib>

Apex Reference:
<https://developer.salesforce.com/docs/atlas.en-us.apexcode.meta/apexcode/apex_reference.htm>

In order to add a the jar files to the local repo, use the following commands:

    mvn install:install-file -Dfile=apex-jorje-data-1.0-sfdc-224-SNAPSHOT.jar \
                             -DgroupId=apex \
                             -DartifactId=apex-jorje-data \
                             -Dversion=1.0-sfdc-224-SNAPSHOT \
                             -Dpackaging=jar \
                             -DlocalRepositoryPath=./repo
    
    mvn install:install-file -Dfile=apex-jorje-ide-1.0-sfdc-224-SNAPSHOT.jar \
                             -DgroupId=apex \
                             -DartifactId=apex-jorje-ide \
                             -Dversion=1.0-sfdc-224-SNAPSHOT-3083815933 \
                             -Dpackaging=jar \
                             -DlocalRepositoryPath=./repo
    
    mvn install:install-file -Dfile=apex-jorje-parser-1.0-sfdc-224-SNAPSHOT.jar \
                             -DgroupId=apex \
                             -DartifactId=apex-jorje-parser \
                             -Dversion=1.0-sfdc-224-SNAPSHOT-3083815933 \
                             -Dpackaging=jar \
                             -DlocalRepositoryPath=./repo
    
    mvn install:install-file -Dfile=apex-jorje-semantic-1.0-sfdc-224-SNAPSHOT.jar \
                             -DgroupId=apex \
                             -DartifactId=apex-jorje-semantic \
                             -Dversion=1.0-sfdc-224-SNAPSHOT-3083815933 \
                             -Dpackaging=jar \
                             -DlocalRepositoryPath=./repo
    
    mvn install:install-file -Dfile=apex-jorje-services-1.0-sfdc-224-SNAPSHOT.jar \
                             -DgroupId=apex \
                             -DartifactId=apex-jorje-services \
                             -Dversion=1.0-sfdc-224-SNAPSHOT-3083815933 \
                             -Dpackaging=jar \
                             -DlocalRepositoryPath=./repo
    
    mvn install:install-file -Dfile=apex-jorje-tools-1.0-sfdc-224-SNAPSHOT.jar \
                             -DgroupId=apex \
                             -DartifactId=apex-jorje-tools \
                             -Dversion=1.0-sfdc-224-SNAPSHOT-3083815933 \
                             -Dpackaging=jar \
                             -DlocalRepositoryPath=./repo
    

For the PMD 6.0.0 Release, the versions from
<https://github.com/forcedotcom/idecore/tree/3083815933c2d015d03417986f57bd25786d58ce/com.salesforce.ide.apex.core/lib>
have been taken.
