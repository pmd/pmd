#!/usr/bin/perl
$| =1;

use CGI qw(:standard escapeHTML);
use CGI::Carp qw(fatalsToBrowser);
use Time::localtime;
use PMD::Project;

$query = new CGI();
print $query->header();

sub nopage() {
 print $query->p("How'd we get here?");
}

sub default() {
 print start_html("Run PMD on your Sourceforge/Jakarta project");
 
 print "<center><a href=\"http://pmd.sourceforge.net/\"><img src=\"http://sourceforge.net/sflogo.php?group_id=56262&type=5\" alt=\"Project Ultra*Log @ DARPA\" border=\"0\" /></a></center>";

 print h3("<center>PMD-WEB</center>");

 if (param("location")) {
  my $project = PMD::Project->new(param("location"),param("title"),param("unixname"), param("moduledirectory"), param("srcdir"));
  addProject($project);
  print p(), b("Added "), b($project->getTitle()), b(" to the schedule"), p();
 } 

 print "PMD is run every two hours (at 10 minutes past the hour) on these projects:";
 print loadProjectList();

 printStats();

 print hr(); 
 print "Want to run PMD on your Java Sourceforge/Jakarta project?  Fill in the blanks and hit go:";
 print start_form();
 print "Project title (i.e., PMD): ", textfield(-name=>'title',-default=>'',-override=>1);
 print br(), "Project location: ", radio_group(-name=>'location',-values=>['Sourceforge','Jakarta'],-default=>'Sourceforge',-override=>1);
 print br(), "Project's Unix name (i.e., pmd): ", textfield(-name=>'unixname',-default=>'',-override=>1);
 print br(), "Module directory (i.e., pmd-dcpd): ", textfield(-name=>'moduledirectory',-default=>'',-override=>1);
 print br(), "Source directory (including module directory, i.e., pmd-dcpd/src): ", textfield(-name=>'srcdir',-default=>'',-override=>1);
 my $cachebuster=`date`;
 print $query->hidden(-name=>'cachebuster', -value=>${cachebuster});
 print br(), submit(-value=>'Go');
 print end_form();
 
 print hr(); 
 print "Comments?  Questions?  Please post them <a href=\"http://sourceforge.net/forum/forum.php?forum_id=188192\">here</a>";
}

sub printStats() {
 print hr(); 
 print b("Stats"); 
 if (-e "currentjob.txt") {
  open(FILE, "currentjob.txt");
  my $currentjob = <FILE>;
  close(FILE);
  print br(), "Currently processing $currentjob";
 }
 print br(), "There are ", getTimeUntil(), " minutes until the next scheduled run";
 open(FILE,"lastruntime.txt");
 my $lastruntime=<FILE>;
 close(FILE);
 print br();
 print "The last run took ", sprintf("%.0f", $lastruntime/60), " minutes";
 print br();
}

sub getTimeUntil() {
 # we're starting each build at 10 past the hour, so...
 my $offset = 10;
 my $everyXHours = 2;
 my $minutes = ((60 + $offset) - localtime()->min) % 60;
 if (localtime()->hour % $everyXHours != 0) {
  $minutes += 60;
 }
 return $minutes;
}

sub loadProjectList() {
 my $result="<table><tr><th>Project</th><th></th><th>Home page</th><th>Problems found</th></tr>";
 opendir(DIR, "jobs/") or return "can't open jobs directory!";
 while (defined($file=readdir(DIR))) {
  if ($file =~ /txt/) {
   open(FILE,"jobs/${file}");
   my $jobdata=<FILE>;
   close(FILE);
   my $project = PMD::Project->new($jobdata);
   my $jobtext=$project->getTitle();
   if (-e $project->getRptFile()) {
    $jobtext="<a href=\"@{[$project->getRptURL]}\">@{[$project->getTitle()]}</a>";
   }
   $result="${result}<tr><td>${jobtext}</td><td></td><td>@{[$project->getHomePage()]}</td><td>@{[$project->getLines()]}</td>";
  }
 }
 $result = "${result}</table>";
 return $result;
}

sub addProject() {
 my ($project) = @_;
 my $cmd = "echo \"@{[$project->getString()]}\" > @{[$project->getJobsFile()]}";
 eval {
  # for some reason this succeeds, but the CGI script fails.  Very odd.
  `${cmd}`;
 }
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

