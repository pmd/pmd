#!/usr/bin/perl

use CGI qw(:standard escapeHTML);
use CGI::Carp qw(fatalsToBrowser);

$query = new CGI();
print $query->header();

sub nopage() {
 print $query->p("How'd we get here?");
}

sub printGreeting() {
 print start_html("Run PMD on your Sourceforge project");
 print p("Want to run PMD on your Sourceforge project?  Fill in the blanks and hit go");
 print start_form();
 print p(), "Project name (i.e., pmd): ", textfield('project');
 print p(), "Source directory (i.e., src): ", textfield('srcdir');
 my $cachebuster=`date`;
 print $query->hidden(-name=>'cachebuster', -value=>${cachebuster});
 print p(), submit(-name=>'state',-value=>'writedata');
 print end_form();
}

sub writeData() {
 my $project = $query->param('project');
 my $srcdir = $query->param('srcdir');
 `echo "${project}:${srcdir}:" > runthis.txt`;
 print start_html(-title=>'PMD Results', -head=>meta({-http_equiv=>'Refresh',-content=>'0;URL=http://pmd.sf.net/cgi-bin/webpmd.pl?state=refreshreport'}));
}

sub refreshReport() {
 print start_html(-title=>'PMD Results', -head=>meta({-http_equiv=>'Refresh',-content=>'10;URL=http://pmd.sf.net/cgi-bin/webpmd.pl?state=refreshreport'}));
 $query->p("This page will refresh with more information every 10 seconds or so");
 open(FILE,"results.html");
 print $query->p(<FILE>);
}

$page=param("state") || "default";

%states = (
 'default'      =>      \&printGreeting,
 'writedata'    =>      \&writeData,
 'refreshreport'=>      \&refreshReport,
);

if ($states{$page}) {
 $states{$page}->();
} else {
 nopage();
}

print $query->end_html();

