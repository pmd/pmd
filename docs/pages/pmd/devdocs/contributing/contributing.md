---
title: Contributor's Guide
summary: How to contribute to PMD
tags: [devdocs]
permalink: pmd_devdocs_contributing.html
author: Andreas Dangel <andreas.dangel@pmd-code.org>
last_updated: January 2026 (7.21.0)
---

First off, thanks for taking the time to contribute!

Please note that this project is released with a Contributor Code of Conduct.
By participating in this project you agree to abide by its terms.
You can find the code of conduct in the file [code_of_conduct.md](https://github.com/pmd/pmd/blob/main/code_of_conduct.md).

## Getting started

You can find a lot of detailed information on this page and in the related pages:
* [Developer Resources](pmd_devdocs_development.html)
* [Newcomers' Guide](pmd_devdocs_contributing_newcomers_guide.html)
* [Building PMD](pmd_devdocs_building_general.html)

Here we'll try to provide a concise overview.

## Pull requests

*   Pull requests are welcomed. If the task is a bit bigger (say it touches more than 5 files), it might
    make sense to create an issue first to discuss the intended change.

*   Please create your pull request against the `main` branch. We will rebase/merge it to the maintenance
    branches, if necessary.

*   We are using [Checkstyle](https://checkstyle.org/) to enforce a common code style.
    The check is integrated into the default build - so, make sure, you can
    [build PMD](pmd_devdocs_building_general.html) without errors.  
    See [code style](#code-style) for more info.

*   Your pull request will be built automatically. If the build was successful, our
    [PMD Regression Tester](pmd_devdocs_pmdtester.html), which runs PMD against a couple of test projects
    and creates a report with the found new violations (or removed violations). This helps to
    avoid accidentally introducing false positives or negatives.

## Bug reports

We use the issue tracker on GitHub. Please report new bugs at <https://github.com/pmd/pmd/issues>.

When filing a bug report, please provide as much information as possible, so that we can reproduce the issue:

*   The name of the rule, that is buggy
*   A code snippet, which triggers a false positive/negative or crash
*   How do you execute PMD? (command line, ant, maven, gradle, other)

## Reporting Security Issues

See [SECURITY.md](https://github.com/pmd/pmd/blob/main/SECURITY.md)

## Documentation

There is some documentation available under <https://docs.pmd-code.org/latest>. Feel free to create a bug report if
documentation is missing, incomplete or outdated. See [Bug reports](#bug-reports).

The documentation is generated as a Jekyll site, the source is available in the subfolder `docs` or at:
<https://github.com/pmd/pmd/tree/main/docs>. You can find build instructions there.
See also [writing documentation](pmd_devdocs_writing_documentation.html) for detailed information.

## PMD Designer

The rule designer is developed over at [pmd/pmd-designer](https://github.com/pmd/pmd-designer).
Please refer to the specific [contributor documentation](https://github.com/pmd/pmd-designer/blob/main/CONTRIBUTING.md)
if your issue, feature request or PR touches the designer.

## Questions

There are various channels, on which you can ask questions:

*   On [StackOverflow](https://stackoverflow.com/questions/tagged/pmd): Make sure, to tag your question with "pmd".

*   Create a new discussion for your question at <https://github.com/pmd/pmd/discussions>.

*   Ask your question in our [Gitter room](https://app.gitter.im/#/room/#pmd_pmd:gitter.im).

*   Ask your question our [PMD Guru at Gurubase](https://gurubase.io/g/pmd).

## Code Style

PMD uses [Checkstyle](https://checkstyle.org/) to enforce a common code style.

See [pmd-checkstyle-config.xml](https://github.com/pmd/build-tools/blob/main/src/main/resources/net/sourceforge/pmd/pmd-checkstyle-config.xml) for the configuration and
[the eclipse configuration files](https://github.com/pmd/build-tools/tree/main/eclipse) that can
be imported into a fresh workspace.

## Add yourself as contributor

We use [All Contributors](https://allcontributors.org/en) - all our contributors are listed on the page [Credits](pmd_projectdocs_credits.html).

To add yourself to the table of contributors, follow the
[bot usage instructions](https://allcontributors.org/docs/en/bot/usage) ;).

Or use the CLI:

1. Install the CLI: `npm i` (in PMD's top level directory)
2. Add yourself: `npx all-contributors add <username> <contribution>`
3. Commit the changes: `git commit -am "Add @<username> as contributor`

Where `username` is your GitHub username and `contribution` is a `,`-separated list
of contributions. See [Emoji Key](https://allcontributors.org/docs/en/emoji-key) for a list
of valid types. Common types are: "code", "doc", "bug", "blog", "talk", "test", "tutorial".

See also [cli documentation](https://allcontributors.org/docs/en/cli/usage)
