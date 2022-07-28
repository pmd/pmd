---
title: ADR 1 - Use architecture decision records
sidebar: pmd_sidebar
permalink: pmd_projectdocs_decisions_adr_1.html
sidebaractiveurl: /pmd_projectdocs_decisions.html
adr: true
# Proposed / Accepted / Deprecated / Superseded
adr_status: "Proposed"
last_updated: July 2022
---

# Context

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
gives a nice summary.

# Decision

We will document the decisions we make as a project as a collection of "Architecture Decision Records".
In order to keep it simple, we will use only a simple template proposed by Michael Nygard.
The documents are stored together with the source code and are part of the generated documentation site.

# Status

{{ page.adr_status }}

# Consequences

Explicitly documenting decisions has the benefit that new developers joining the projects know about the decisions
and can read the context and consequences of the decisions. This will likely also improve the overall quality
as the decisions need to be formulated and written down. Everybody is on the same page.

However, this also adds additional tasks, and it takes time to write down and document the decisions.
