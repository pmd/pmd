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

sub new {
 my $self = {};
 bless($self);
 if ((scalar(@_)-1) == 1) {
  ($self->{TITLE},$self->{UNIXNAME}, $self->{MODULEDIR}, $self->{SRCDIR}) = split(":", @_[1]);
 } else {
  $self->{TITLE} = @_[1];
  $self->{UNIXNAME} = @_[2];
  $self->{MODULEDIR} = @_[3];
  $self->{SRCDIR} = @_[4];
 }
 return $self;
}

sub getLines() {
 my $self = shift;
 open(FILE,getRptFile($self));
 my @x = <FILE>;
 close(FILE);
 my $y = @x;
 my $lines = sprintf("%0.f", ($y-5)/4);
 if ($lines == "-0" || $lines == "-1") {
  $lines = "0";
 }
 return $lines;
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
 return "$self->{TITLE}:$self->{UNIXNAME}:$self->{MODULEDIR}:$self->{SRCDIR}"; 
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

1;
