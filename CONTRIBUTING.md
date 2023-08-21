# How to contribute to PMD

First off, thanks for taking the time to contribute!

Please note that this project is released with a Contributor Code of Conduct.
By participating in this project you agree to abide by its terms.

You can find the code of conduct in the file [code_of_conduct.md](code_of_conduct.md).

| NB: the rule designer is developed over at [pmd/pmd-designer](https://github.com/pmd/pmd-designer). Please refer to the specific [contributor documentation](https://github.com/pmd/pmd-designer/blob/master/CONTRIBUTING.md) if your issue, feature request or PR touches the designer.  |
| --- |

## Pull requests

*   Please create your pull request against the `master` branch. We will rebase/merge it to the maintenance
    branches, if necessary.

*   We are using [checkstyle](http://checkstyle.sourceforge.net/) to enforce a common code style.
    The check is integrated into the default build - so, make sure, you can [build PMD](BUILDING.md) without errors.
    See [code style](#code-style) for more info.


## Bug reports

We use the issue tracker on Github. Please report new bugs at <https://github.com/pmd/pmd/issues>.

When filing a bug report, please provide as much information as possible, so that we can reproduce the issue:

*   The name of the rule, that is buggy
*   A code snippet, which triggers a false positive/negative or crash
*   How do you execute PMD? (command line, ant, maven, gradle, other)


## Documentation

There is some documentation available under <https://docs.pmd-code.org/latest>. Feel free to create a bug report if
documentation is missing, incomplete or outdated. See [Bug reports](#bug-reports).

The documentation is generated as a Jekyll site, the source is available at: <https://github.com/pmd/pmd/tree/master/docs>. You can find build instructions there.
For more on contributing documentation check <https://docs.pmd-code.org/latest/pmd_devdocs_writing_documentation.html>

## Questions

There are various channels, on which you can ask questions:

*   On [StackOverflow](https://stackoverflow.com/questions/tagged/pmd): Make sure, to tag your question with "pmd".

*   Create a new discussion for your question at <https://github.com/pmd/pmd/discussions>.

*   Ask your question in our [Gitter room](https://app.gitter.im/#/room/#pmd_pmd:gitter.im).

## Code Style

PMD uses [checkstyle](http://checkstyle.sourceforge.net/) to enforce a common code style.

See [pmd-checkstyle-config.xml](https://github.com/pmd/build-tools/blob/master/src/main/resources/net/sourceforge/pmd/pmd-checkstyle-config.xml) for the configuration and
[the eclipse configuration files](https://github.com/pmd/build-tools/tree/master/eclipse) that can
be imported into a fresh workspace.

## Add yourself as contributor

We use [All Contributors](https://allcontributors.org/en).

To add yourself to the table of contributors, follow the
[bot usage instructions](https://allcontributors.org/docs/en/bot/usage) ;).

Or use the CLI:

1. Install the CLI: `npm i` (in PMD's top level directory)
2. Add yourself: `npx all-contributors add <username> <contribution>`

Where `username` is your GitHub username and `contribution` is a `,`-separated list
of contributions. See [Emoji Key](https://allcontributors.org/docs/en/emoji-key) for a list
of valid types. Common types are: "code", "doc", "bug", "blog", "talk", "test", "tutorial".

See also [cli documentation](https://allcontributors.org/docs/en/cli/usage)
