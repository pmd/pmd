package PMD::Project;

use Exporter;

@ISA = ('Exporter');
@EXPORT = qw(&new &getTitle &getUnixName &getModuleDir &getSrcDir &getString &getJobsFile);

sub new {
 my $self = {};
 bless($self);
 $self->{TITLE} = @_[1];
 $self->{UNIXNAME} = @_[2];
 $self->{MODULEDIR} = @_[3];
 $self->{SRCDIR} = @_[4];
 return $self;
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


1;
