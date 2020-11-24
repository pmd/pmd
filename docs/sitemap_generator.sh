#!/usr/bin/env bash

# Sitemap generator for pmd.github.io main landing page.
# Assumes we have the latest version of the site under "latest"
# https://www.sitemaps.org/protocol.html

WEBSITE_PREFIX="https://pmd.github.io/"
DOC_PREFIX="latest/"
DATE=`date +%Y-%m-%d`
# Priority is relative to the website, can be chosen in {0.1, 0.2, ..., 1}
# Default priority is 0.5
LATEST_PRIORITY=0.8


# Writes to standard output

cat << HEADER_END
<?xml version="1.0" encoding="UTF-8"?>
<urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">

    <url>
        <loc>${WEBSITE_PREFIX}index.html</loc>
        <priority>1</priority>
        <changefreq>monthly</changefreq>
        <lastmod>$DATE</lastmod>
    </url>

    <url>
        <loc>${WEBSITE_PREFIX}${DOC_PREFIX}index.html</loc>
        <priority>0.9</priority>
        <changefreq>monthly</changefreq>
        <lastmod>$DATE</lastmod>
    </url>



HEADER_END


for page in ${DOC_PREFIX}pmd_*.html
do

    cat << ENTRY_END
    <url>
        <loc>${WEBSITE_PREFIX}$page</loc>
        <priority>$LATEST_PRIORITY</priority>
        <changefreq>monthly</changefreq>
        <lastmod>$DATE</lastmod>
    </url>

ENTRY_END

done

echo "</urlset>"

