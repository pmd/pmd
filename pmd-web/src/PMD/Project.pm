package PMD::Project;

use Exporter;

@ISA = ('Exporter');
push @EXPORT, '&new';
push @EXPORT, '&getTitle';
push @EXPORT, '&getUnixName';
push @EXPORT, '&getModuleDir';
push @EXPORT, '&getSrcDir';
push @EXPORT, '&getString';
push @EXPORT, '&getJobsFile';
push @EXPORT, '&getRptFile';
push @EXPORT, '&getRptURL';
push @EXPORT, '&getLines';
push @EXPORT, '&getLocation';

sub new {
 my $self = {};
 bless($self);
 if ((scalar(@_)-1) == 1) {
  ($self->{LOCATION},$self->{TITLE},$self->{UNIXNAME}, $self->{MODULEDIR}, $self->{SRCDIR}) = split(":", @_[1]);
 } else {
  $self->{LOCATION} = @_[1];
  $self->{TITLE} = @_[2];
  $self->{UNIXNAME} = @_[3];
  $self->{MODULEDIR} = @_[4];
  $self->{SRCDIR} = @_[5];
 }
 return $self;
}

sub getLines() {
 my $self = shift;
 open(FILE,getRptFile($self));
 my @x = <FILE>;
 close(FILE);
 my $lines;
 foreach (@x) {
  $lines = $lines + 1 if $_ =~ "<td ";
 }
 return sprintf("%0.f", $lines/3);
}
sub getLocation() {
 my $self = shift;
 return $self->{LOCATION}
}

sub getTitle() {
 my $self = shift;
 return $self->{TITLE}
}

sub getUnixName() {
 my $self = shift;
 return $self->{UNIXNAME}
}

sub getModuleDir() {
 my $self = shift;
 return $self->{MODULEDIR}
}

sub getSrcDir() {
 my $self = shift;
 return $self->{SRCDIR}
}

sub getString() {
 my $self = shift;
 return "$self->{LOCATION}:$self->{TITLE}:$self->{UNIXNAME}:$self->{MODULEDIR}:$self->{SRCDIR}"; 
}

sub getJobsFile() {
 my $self = shift;
 return "jobs/$self->{UNIXNAME}_$self->{MODULEDIR}.txt";
}

sub getRptURL() {
 my $self = shift;
 my $name = $self->getReportName();
 return "http://pmd.sf.net/reports/${name}";
}

sub getRptFile() {
 my $self = shift;
 my $name = $self->getReportName();
 return "../htdocs/reports/${name}";
}

sub getReportName() {
 my $self = shift;
 my $modwithoutspaces = $self->{MODULEDIR};
 $modwithoutspaces=~s/\s+//g;
 return "$self->{UNIXNAME}_${modwithoutspaces}.html";
}

sub getHomePage() {
 my $self = shift;
 if ($self->getLocation() =~ "Sourceforge") {
  return "<a href=\"http://@{[$self->getUnixName()]}.sf.net/\">http://@{[$self->getUnixName()]}.sf.net/</a>";
 } elsif ($self->getLocation() =~ "Jakarta xml") {
  return "<a href=\"http://xml.apache.org/\">http://xml.apache.org/</a>";
 } else {
  return "<a href=\"http://jakarta.apache.org/\">http://jakarta.apache.org/</a>";
 }
}

1;
