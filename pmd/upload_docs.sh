#!/bin/bash

#rm -rf target
#maven pmd-site

maven xdoc:transform
DOCS_FILE=docs.tar.gz
cp xdocs/cpdresults.txt target/docs/
cd target
rm $DOCS_FILE
tar zcf $DOCS_FILE docs/
scp -i ~/.ssh/identity $DOCS_FILE tomcopeland@pmd.sourceforge.net:/home/groups/p/pm/pmd/
cd ../
ssh -l tomcopeland pmd.sourceforge.net "cd /home/groups/p/pm/pmd/ && ./update_docs.sh"
