---
title: Release schedule and version policies
permalink: pmd_about_release_policies.html
author: Andreas Dangel <andreas.dangel@pmd-code.org>
last_updated: June 2024 (PMD 7.3.0)
---

## Release schedule

PMD uses a time-based release schedule.

We release a new minor version **every month**, usually on the last Friday of the month.
A patch release will only be done if necessary (e.g. blocker bugs).

## Version policy

PMD aims to follow [SemVer](https://semver.org/), that means, versions are numbered in the form MAJOR.MINOR.PATCH.

A **major** release can break any compatibility, and it means more effort to upgrade to the next major version (like
it was from 6.x to 7.x, see [Migration Guide for PMD 7](pmd_userdocs_migrating_to_pmd7.html).

A **minor** release tries to be compatible so that an effortless (aka "drop-in replacement") upgrade is possible
with some exceptions.

Such releases might contain:

* fixed false-positive (FP) issues for rules
* fixed false-negative (FN) issues for rules: These fixes might break your builds, as new violations might be found.
* new rules: these new rules are not used by default _if_ you use custom rulesets, so they shouldn't affect your builds.
* deprecations of existing functionality

In summary: we only guarantee stability on how you integrate / use the tool, but builds may start failing because
we fixed FNs, or introduced a new rule (ie: for people referencing whole categories).
We stick to our current approach when moving / renaming rules of deprecating them, and referencing
the new one until the next major.

A **patch** release absolutely is a drop-in replacement. So only bugs (ie: crashes or obviously broken stuff,
like rules not being applied at all), or security issues (dependency updates, hardening, etc.) are part of
a patch release.

See also

* [ADR 3 - API evolution principles](pmd_projectdocs_decisions_adr_3.html)
* [Rule deprecation policy](pmd_devdocs_rule_deprecation_policy.html)

## Git branches/tags policy

* Main development happens on the main branch (currently called `master`).
* PR and enhancements are done on the main branch.
* Release are usually done directly from the main branch, we don't create release branches.
* Each release has its own tag named `pmd_releases/MAJOR.MINOR.PATCH`.
* In case of a patch release, we either do it from the main branch (if there was no development ongoing)
  or create a separate branch off the last release tag.
* See also [Release process](pmd_projectdocs_committers_releasing.html).

