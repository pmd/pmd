#!/bin/bash

#ssh -l tomcopeland pmd.sourceforge.net "cd /home/groups/p/pm/pmd/cgi-bin && mkdir PMD"
#ssh -l tomcopeland pmd.sourceforge.net "cd /home/groups/p/pm/pmd/cgi-bin && mkdir jobs/"
scp ../src/webpmd.pl tomcopeland@pmd.sourceforge.net:/home/groups/p/pm/pmd/cgi-bin/
scp ../src/PMD/Project.pm tomcopeland@pmd.sourceforge.net:/home/groups/p/pm/pmd/cgi-bin/PMD/
scp ../src/pmd.rb tomcopeland@pmd.sourceforge.net:pmdweb/
scp ../src/processor.rb tomcopeland@pmd.sourceforge.net:pmdweb/
#scp jobs.tar.gz tomcopeland@pmd.sourceforge.net:pmdweb/

