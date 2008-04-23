#!/bin/sh
# require one arg, the target filename
# usage is : etc/updating_rulesets.sh rulesets/rulesets.properties

# The following variable contains a list of keyword to remove rulesets files that 
# shouldn't appear in the rulesets.properties
EXCLUSION_LIST="migrating_
favorites
scratchpad 
basic-jsf.xml 
basic-jsp.xml 
typeresolution.xml"
if [ "$1" = "" ] ; then
	echo "Missing target filename."	
else
	# listing all the rulesets files (without the excluded files)
	QUERY="sed "
	for FILE_TO_EXCLUDE in ${EXCLUSION_LIST}
	do
		QUERY="${QUERY} -e /${FILE_TO_EXCLUDE}/d"
	done 
	FILE_LIST="`ls -1 rulesets/*.xml | ${QUERY}`"
	# replacing spaces by comma, to match property syntax
	PROPERTY_VALUE="`echo ${FILE_LIST} | sed -e 's/ /,/g'`"
	# generating file
	echo "rulesets.filenames=${PROPERTY_VALUE}" > ${1}
	echo "rulesets.testnames=${PROPERTY_VALUE}" >> ${1}
fi
