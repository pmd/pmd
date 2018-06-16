---
title: Developer Resources
tags: [devdocs]
permalink: pmd_devdocs_development.html
last_updated: August 2017
---
The next version of PMD will be developed in parallel with this release. We will release additional bugfix versions as needed.

## Source Code

The complete source code can be found on github:

*   [github.com/pmd/pmd](https://github.com/pmd/pmd) - main PMD repository. Includes all the code to support all languages, including this documentation.
*   [github.com/pmd/pmd.github.io](https://github.com/pmd/pmd.github.io) - Contains the landing page [https://pmd.github.io](https://pmd.github.io)
*   [github.com/pmd/build-tools](https://github.com/pmd/build-tools) - Contains the checkstyle rules we use
*   [github.com/pmd/pmd-eclipse-plugin](https://github.com/pmd/pmd-eclipse-plugin) - The PMD eclipse plugin
*   [github.com/pmd](https://github.com/pmd) - PMD Organization at github. There are a couple of more repositories

## Continuous Integration

We use [Travis CI](https://travis-ci.org/pmd) as our ci service. The main repo and the eclipse plugin are built for
every push. Each pull request is built as well.

The maven snapshot artifacts are deployed at [Sonatypes OSS snapshot repository](https://oss.sonatype.org/content/repositories/snapshots/net/sourceforge/pmd/pmd/).

Ready-to-use binary packages are uploaded to sourceforge at <https://sourceforge.net/projects/pmd/files/pmd/>.

## Documentation and Webpages

A [snapshot](http://pmd.sourceforge.net/snapshot) of the web site for the new version is generated travis-ci as well.

## Contributing

First off, thanks for taking the time to contribute!

Please have a look at [CONTRIBUTING.md](https://github.com/pmd/pmd/blob/master/CONTRIBUTING.md) and
[BUILDING.md](https://github.com/pmd/pmd/blob/master/BUILDING.md).
