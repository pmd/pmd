FILE=$1
FORMAT=$2
RULESETFILES=$3
java -cp ../lib/pmd-1.03.jar:../lib/jaxen-core-1.0-fcs.jar:../lib/saxpath-1.0-fcs.jar net.sourceforge.pmd.PMD $FILE $FORMAT $RULESETFILES
