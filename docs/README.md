# PMD-New-Site
New Site For PMD Core Open Source Project

## Site Theme

This site was built using the tomjohnson1492/documentation-theme-jekyll theme

A Jekyll-based theme designed for documentation and help systems. See the link for detailed instructions on setting up and configuring everything. http://idratherbewriting.com/documentation-theme-jekyll/

## Building using Bundler

    bundle install # once
    bundle exec jekyll serve

Go to: http://localhost:4005/

## Building using Docker

    docker build --no-cache -t pmd-doc . # once
    docker run --rm=true -v "$PWD:/src" -p 4005:4005 pmd-doc serve -H 0.0.0.0

Go to: http://localhost:4005/
