#!/usr/bin/perl
$| =1;

use CGI qw(:standard escapeHTML);
use CGI::Carp qw(fatalsToBrowser);

$query = new CGI();
print $query->header();

sub nopage() {
 print $query->p("How'd we get here?");
}

sub default() {
 print start_html("Run PMD on your Sourceforge project");
 if (param("title")) {
  addProject(param("title"),param("unixname"), param("moduledirectory"), param("srcdir"));
  print p();
  my $title = param("title");
  print b("Added ${title} to the schedule");
 } 
 print p("PMD is run hourly on these projects:");
 print p(loadProjectList());
 #print p("Stats:");
 print p("Want to run PMD on your Sourceforge project?  Fill in the blanks and hit go");
 print start_form();
 print p(), "Project title (i.e., PMD): ", textfield(-name=>'title',-default=>'',-override=>1);
 print p(), "Project's Unix name (i.e., pmd): ", textfield(-name=>'unixname',-default=>'',-override=>1);
 print p(), "Module directory (i.e., pmd-dcpd): ", textfield(-name=>'moduledirectory',-default=>'',-override=>1);
 print p(), "Source directory (i.e., pmd-dcpd/src): ", textfield(-name=>'srcdir',-default=>'',-override=>1);
 my $cachebuster=`date`;
 print $query->hidden(-name=>'cachebuster', -value=>${cachebuster});
 print p(), submit(-value=>'Go');
 print end_form();
}

sub loadProjectList() {
 my $result="<table>";
 opendir(DIR, "jobs/") or return "can't open jobs directory!";
 while (defined($file=readdir(DIR))) {
  if ($file =~ /txt/) {
   open(FILE,"jobs/${file}");
   my $jobdata=<FILE>;
   my ($title,$unixname, $mod, $src) = split(":", $jobdata);
   my $jobtext="";
   if (-e "../htdocs/reports/${unixname}.html") {
    $jobtext="<a href=\"http://pmd.sf.net/reports/${unixname}.html\">${title}</a>";
   } else {
    $jobtext=$title;
   }
   $result="${result}<tr><td>${jobtext}</td>";
  }
 }
 $result = "${result}</table>";
 return $result;
}

sub addProject() {
 my ($title, $unixname,$moduleDirectory,$srcdir) = @_;
 my $cmd="echo \"${title}:${unixname}:${moduleDirectory}:${srcdir}\" > jobs/${moduleDirectory}.txt";
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

