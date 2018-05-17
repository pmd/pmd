#!/bin/bash

# Sitemap generator
# Assumes we have the latest version of the site under "latest" and "pmd-${RELEASE_VERSION}"


WEBSITE_PREFIX="https://pmd.github.io/"
DOC_PREFIX="pmd-${RELEASE_VERSION}/"
LATEST_PRIORITY=0.8
DATE=`date +%Y-%m-%d`

writePage () {
local pageLoc=$1


cat << ENTRY_END >> sitemap.xml
<url>
    <loc>${WEBSITE_PREFIX}$pageLoc</loc> 
    <priority>$LATEST_PRIORITY</priority>
    <changefreq>monthly</changefreq>
    <lastmod>$DATE</lastmod>
</url>

ENTRY_END
}


# Start of the output writing

cat << HEADER_END > sitemap.xml
<?xml version="1.0" encoding="UTF-8"?>
<urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">

HEADER_END


for page in pmd-${RELEASE_VERSION}/pmd_*.html
do
	writePage $page
done


for page in latest/pmd_*.html
do
	writePage $page
done

echo "</urlset>" >> sitemap.xml 

