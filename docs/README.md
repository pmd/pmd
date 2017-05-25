# PMD-New-Site
New Site For PMD Core Open Source Project

## Site Theme

This site was built using the tomjohnson1492/documentation-theme-jekyll theme

A Jekyll-based theme designed for documentation and help systems. See the link for detailed instructions on setting up and configuring everything. http://idratherbewriting.com/documentation-theme-jekyll/


Build the site to see the instructions for using it. Or just go here: [http://idratherbewriting.com/documentation-theme-jekyll/](http://idratherbewriting.com/documentation-theme-jekyll/)

Run `bundle exec jekyll serve --watch` to fire up Jekyll on local machine

## Using Docker

One time: `docker build --no-cache -t mydocs .`

Now run the site with `docker run -v "$PWD:/src" -p 4005:4005 mydocs serve -H 0.0.0.0`

Go to: http://localhost:4005/

