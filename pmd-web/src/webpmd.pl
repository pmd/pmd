#!/usr/bin/perl
$| =1;

use CGI qw(:standard escapeHTML);
use CGI::Carp qw(fatalsToBrowser);
use Time::localtime;

$query = new CGI();
print $query->header();

sub nopage() {
 print $query->p("How'd we get here?");
}

sub default() {
 print start_html("Run PMD on your Sourceforge project");
 
 print "<center><a href=\"http://pmd.sourceforge.net/\"><img src=\"http://sourceforge.net/sflogo.php?group_id=56262&type=5\" alt=\"Project Ultra*Log @ DARPA\" border=\"0\" /></a></center>";

 print h3("<center>PMD-WEB</center>");

 if (param("title")) {
  addProject(param("title"),param("unixname"), param("moduledirectory"), param("srcdir"));
  print p();
  my $title = param("title");
  print b("Added ${title} to the schedule");
 } 

 print "PMD is run hourly (at 10 minutes past the hour) on these projects:";
 print loadProjectList();

 printStats();

 print hr(); 
 print "Want to run PMD every hour on your Java Sourceforge project?  Fill in the blanks and hit go:";
 print start_form();
 print "Project title (i.e., PMD): ", textfield(-name=>'title',-default=>'',-override=>1);
 print br(), "Project's Unix name (i.e., pmd): ", textfield(-name=>'unixname',-default=>'',-override=>1);
 print br(), "Module directory (i.e., pmd-dcpd): ", textfield(-name=>'moduledirectory',-default=>'',-override=>1);
 print br(), "Source directory (i.e., pmd-dcpd/src): ", textfield(-name=>'srcdir',-default=>'',-override=>1);
 my $cachebuster=`date`;
 print $query->hidden(-name=>'cachebuster', -value=>${cachebuster});
 print br(), submit(-value=>'Go');
 print end_form();
 
 print hr(); 
 print "Comments?  Questions?  Please post them <a href=\"http://sourceforge.net/forum/forum.php?forum_id=188192\">here</a>";
}

sub printStats() {
 print hr(); 
 print b("Stats:"), br(), "There are ", getTimeUntil(), " minutes until the next run";
 open(FILE,"lastruntime.txt");
 my $lastruntime=<FILE>;
 print br();
 print "The last run took ${lastruntime} seconds";
 print br();
 
}

sub getTimeUntil() {
 my $tm = localtime;
 my $timeuntil = 70 - $tm->min;
 if ($tm->min<=10) {
  $timeuntil = 10 - $tm->min; 
 }
 return $timeuntil;
}

sub loadProjectList() {
 my $result="<table><tr><th>Project</th><th></th><th>Home page</th><th>Problems found</th></tr>";
 opendir(DIR, "jobs/") or return "can't open jobs directory!";
 while (defined($file=readdir(DIR))) {
  if ($file =~ /txt/) {
   open(FILE,"jobs/${file}");
   my $jobdata=<FILE>;
   my ($title,$unixname, $mod, $src) = split(":", $jobdata);
   my $jobtext="";
   if (-e "../htdocs/reports/${unixname}_${mod}.html") {
    $jobtext="<a href=\"http://pmd.sf.net/reports/${unixname}_${mod}.html\">${title}</a>";
   } else {
    $jobtext=$title;
   }
   my $lines = getLines("../htdocs/reports/${unixname}_${mod}.html");
   $result="${result}<tr><td>${jobtext}</td><td></td><td><a href=\"http://${unixname}.sf.net/\">http://${unixname}.sf.net/</a></td><td>${lines}</td>";
  }
 }
 $result = "${result}</table>";
 return $result;
}

sub getLines() {
 my ($filename) = @_;
 open(FILE,$filename);
 my @x = <FILE>;
 close(FILE);
 my $y = @x;
 my $lines = sprintf("%0.f", ($y-5)/4);
 if ($lines == "-0" || $lines == "-1") {
  $lines = "0";
 }
 return $lines;
}


sub addProject() {
 my ($title, $unixname,$moduleDirectory,$srcdir) = @_;
 my $cmd="echo \"${title}:${unixname}:${moduleDirectory}:${srcdir}\" > jobs/${unixname}_${moduleDirectory}.txt";
 `${cmd}`;
}

$page=param("state") || "default";

%states = (
 'default'      =>      \&default
);

if ($states{$page}) {
 $states{$page}->();
} else {
 nopage();
}

print $query->end_html();

