# PMD Documentation

The documentation is available at: <https://pmd.github.io/pmd/>

The documentation for the latest release is at: <https://pmd.github.io/latest/>

## Site Theme

This site was built using the tomjohnson1492/documentation-theme-jekyll theme

A Jekyll-based theme designed for documentation and help systems. See the link for detailed instructions on setting up and configuring everything. http://idratherbewriting.com/documentation-theme-jekyll/

## Building using Script

    bash build-docs.sh

This will run bundler to fetch and potentially update the ruby gems.
And then it will execute jekyll and build a offline site.
Open the file `_site/index.html` with your browser to see the site.

## Building using Bundler

    bundle install # once
    bundle exec jekyll serve

Go to: http://localhost:4005/

This variant is useful to get constant updates: When you modify a file, jekyll will automatically rebuild
the site, so you just need to hit Refresh in the browser to see the update.

## Building using Docker

    docker build --no-cache -t pmd-doc . # once
    docker run --rm=true -v "$PWD:/src" -p 4005:4005 pmd-doc serve -H 0.0.0.0

Go to: http://localhost:4005/
