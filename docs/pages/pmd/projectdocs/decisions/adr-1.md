---
title: ADR 1 - Use architecture decision records
sidebar: pmd_sidebar
permalink: pmd_projectdocs_decisions_adr_1.html
sidebaractiveurl: /pmd_projectdocs_decisions.html
adr: true
# Proposed / Accepted / Deprecated / Superseded
adr_status: "Accepted"
last_updated: September 2022
---

## Context

PMD has grown over 20 years as an open-source project. Along the way many decisions have been made, but they are not
explicitly documented. PMD is also developed by many individuals and the original developers might
not even be around anymore.

Without having documentation records about decisions it is hard for new developers to understand the reasons
of past decisions. This might lead to either ignore these past (unknown) decisions and change it without
fully understanding its consequences. This could create new issues down the road, e.g. a decision supporting
a requirement that is not tested.

On the other hand, accepting the past decisions without challenging it might slow down the project and
possible innovations. It could lead to a situation where the developers are afraid to change anything
in order to not break the system.

Past decisions have been made within context and the context can change. Therefore, past decisions can still be
valid today, or they don't apply anymore. In that case, the decision should be revisited.

See also the blog post [Documenting Architecture Decisions](https://cognitect.com/blog/2011/11/15/documenting-architecture-decisions)
by Michael Nygard.

There are many templates around to choose from. <https://github.com/joelparkerhenderson/architecture-decision-record>
gives a nice summary. The page <https://adr.github.io/> gives a good overview on ADR and for adr-related tooling.

## Decision

We will document the decisions we make as a project as a collection of "Architecture Decision Records".
In order to keep it simple, we will use only a simple template proposed by Michael Nygard.
The documents are stored together with the source code and are part of the generated documentation site.

A new ADR should be proposed with a pull request to open the discussion.
The initial status of the new ADR is "Proposed". When maintainer consensus is reached during the PR
review, then the status is changed to "Accepted" when the PR is merged.
A new entry in the "Change History" section should be added, when the PR is merged.

In order to propose a change to an existing ADR a new pull request should be opened which modifies the ADR.
The change can be to amend the ADR or to challenge it and maybe deprecate it. A new entry in the
"Change History" section should be added to summary the change. When maintainer consensus is reached
during the PR review, then the PR can be merged and the ADR is updated.

## Status

{{ page.adr_status }} (Last updated: {{ page.last_updated }})

## Consequences

Explicitly documenting decisions has the benefit that new developers joining the projects know about the decisions
and can read the context and consequences of the decisions. This will likely also improve the overall quality
as the decisions need to be formulated and written down. Everybody is on the same page.

However, this also adds additional tasks, and it takes time to write down and document the decisions.

## Change History

2022-09-30: Status changed to "Accepted". ([#4072](https://github.com/pmd/pmd/pull/4072))

2022-09-06: Added section "Change History" to the template. Added "Last updated" to "Status" section.

2022-07-28: Proposed initial version.
