---
title: Support lifecycle
permalink: pmd_about_support_lifecycle.html
author: Andreas Dangel <andreas.dangel@pmd-code.org>
last_updated: June 2024 (PMD 7.3.0)
---

{% capture latest_release %}{{site.pmd.version}} ({{site.pmd.date | date: "%Y-%m-%d" }}){% endcapture %}

| Major version | Initial release    | Latest Release       | Required Java Version | In development / still supported? |
|---------------|--------------------|----------------------|-----------------------|-----------------------------------|
| 7.x           | 7.0.0 (2024-03-22) | {{ latest_release }} | 8                     | ✔ yes                             |
| 6.x           | 6.0.0 (2017-12-15) | 6.55.0 (2023-02-25)  | 7                     | ❌ no                              |
| 5.x           | 5.0.0 (2012-05-01) | 5.8.1 (2017-07-01)   | 7                     | ❌ no                              |
| 4.x           | 4.0 (2007-07-20)   | 4.3 (2011-11-04)     | 5                     | ❌ no                              |
| 3.x           | 3.0 (2005-03-23)   | 3.9 (2006-12-19)     | 4                     | ❌ no                              |
| 2.x           | 2.0 (2004-10-19)   | 2.3 (2005-02-01)     |                       | ❌ no                              |
| 1.x           | 1.0 (2002-11-04)   | 1.9 (2004-07-14)     |                       | ❌ no                              |

In general, only the latest major version is in active development and regularly will receive new features
and bug fixes etc.
Once a new version is released, the previous version becomes unsupported.
We recommend to always update to the latest version to benefit from new features and bug fixes.

See also [Release process and version policies](pmd_about_release_policies.html).
