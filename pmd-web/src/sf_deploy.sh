#!/bin/bash

#ssh -l tomcopeland pmd.sourceforge.net "cd /home/groups/p/pm/pmd/cgi-bin && mkdir PMD"
#ssh -l tomcopeland pmd.sourceforge.net "cd /home/groups/p/pm/pmd/cgi-bin && mkdir jobs/"
scp webpmd.pl tomcopeland@pmd.sourceforge.net:/home/groups/p/pm/pmd/cgi-bin/
scp PMD/Project.pm tomcopeland@pmd.sourceforge.net:/home/groups/p/pm/pmd/cgi-bin/PMD/
scp pmd.rb tomcopeland@pmd.sourceforge.net:pmdweb/
scp processor.rb tomcopeland@pmd.sourceforge.net:pmdweb/
#scp jobs.tar.gz tomcopeland@pmd.sourceforge.net:pmdweb/

