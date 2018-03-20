# How to contribute to PMD

First off, thanks for taking the time to contribute!


## Pull requests

*   Please create your pull request against the `master` branch. We will rebase/merge it to the maintenance
    branches, if necessary.

*   We are using [checkstyle](http://checkstyle.sourceforge.net/) to enforce a common code style.
    The check is integrated into the default build - so, make sure, you can [build PMD](BUILDING.md) without errors.
    See [code style](#code-style) for more info.


## Bug reports

We used to use Sourceforge for bug tracking, but we are in the process of moving to github issues.

*   Old bugs are still available at <https://sourceforge.net/p/pmd/bugs/>.
*   Please report new bugs at <https://github.com/pmd/pmd/issues>.

When filing a bug report, please provide as much information as possible, so that we can reproduce the issue:

*   The name of the rule, that is buggy
*   A code snippet, which triggers a false positive/negative or crash
*   How do you execute PMD? (command line, ant, maven, gradle, other)


## Documentation

There are some documentation available under <https://pmd.github.io>. Feel free to create a bug report if
documentation is missing, incomplete or outdated. You can do that [here](https://github.com/pmd/pmd/issues).

The documentation is generated as a Jekyll site, the source is available at: <https://github.com/pmd/pmd/tree/master/docs>. You can find build instructions there.
For more on contributing to documentation check <https://pmd.github.io/pmd-6.1.0/pmd_devdocs_writing_documentation.html>

## Questions

There are various channels, on which you can ask questions:

*   The mailing list: [pmd-devel](https://lists.sourceforge.net/lists/listinfo/pmd-devel)

*   The discussion forums on sourceforge: <https://sourceforge.net/p/pmd/discussion/>

*   On [StackOverflow](https://stackoverflow.com/questions/tagged/pmd): Make sure, to tag your question with "pmd".

## Code Style

PMD uses [checkstyle](http://checkstyle.sourceforge.net/) to enforce a common code style.

See [pmd-checkstyle-config.xml](https://github.com/pmd/build-tools/blob/master/config/src/main/resources/net/sourceforge/pmd/pmd-checkstyle-config.xml) for the configuration and
[the eclipse configuration files](https://github.com/pmd/build-tools/tree/master/config/eclipse) that can
be imported into a fresh workspace.


