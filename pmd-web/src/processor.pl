#!/usr/bin/perl

chdir("/home/groups/p/pm/pmd/cgi-bin");

if (-e "runthis.txt") {
 open(FILE,"runthis.txt");
 $data=<FILE>;
 close(FILE);
 `rm -f runthis.txt`;
 my ($project,$srcdir,$zilch) = split(":",$data);
 `cvs -d:pserver:anonymous\@cvs.${project}.sourceforge.net:/cvsroot/${project} co ${project}`;
 $cmd="java -jar pmd-0.9.jar ${project}/${srcdir} html rulesets/unusedcode.xml";
 `${cmd} > results.html`;
 `rm -rf ${project}`;
} else {
 print "nothing to run";
}
