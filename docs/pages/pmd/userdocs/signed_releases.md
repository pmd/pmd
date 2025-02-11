---
title: Signed Releases
tags: [userdocs]
permalink: pmd_userdocs_signed_releases.html
author: Andreas Dangel <andreas.dangel@pmd-code.org>
last_updated: February 2025 (7.11.0)
---

Since PMD 7.11.0 we provide GPG signatures along our binary distribution files on [GitHub Releases](https://github.com/pmd/pmd/releases).
You can use the signatures to verify that the downloads you have not been tampered with since we built them.

## How to verify a release?

Download the binary zip file and the corresponding `asc` file from the release assets from [GitHub Releases](https://github.com/pmd/pmd/releases)
into the same directory. Then use `gpg` to verify the download:

```shell
gpg --verify pmd-dist-{{site.pmd.version}}-bin.zip.asc pmd-dist-{{site.pmd.version}}-bin.zip
```

If you do not currently have PMD's public release signing key you will get a message such as this:
```
gpg: Signature made Thu Feb  6 14:58:22 2025 CET
gpg:                using RSA key 1E046C19ED2873D8C08AF7B8A0632691B78E3422
gpg:                issuer "releases@pmd-code.org"
gpg: Can't check signature: No public key
```

You first need to acquire our public key to get rid of the "Can't check signature" message.
You can download it from a trusted GPG server, for example to use the Ubuntu key server
run this command:

```shell
gpg --keyserver keyserver.ubuntu.com --recv-keys 1E046C19ED2873D8C08AF7B8A0632691B78E3422
```

If you then run the verify command again you will get a message indicating the newly imported
key has not been trusted:

```
gpg: Signature made Thu Feb  6 14:58:22 2025 CET
gpg:                using RSA key 1E046C19ED2873D8C08AF7B8A0632691B78E3422
gpg:                issuer "releases@pmd-code.org"
gpg: Good signature from "PMD Release Signing Key <releases@pmd-code.org>" [unknown]
gpg: WARNING: This key is not certified with a trusted signature!
gpg:          There is no indication that the signature belongs to the owner.
Primary key fingerprint: 2EFA 55D0 785C 31F9 56F2  F87E A0B5 CA1A 4E08 6838
     Subkey fingerprint: 1E04 6C19 ED28 73D8 C08A  F7B8 A063 2691 B78E 3422
```

While the “Good signature” message gives you some confidence that the download is valid, to fully trust the
certificate and remove the final warning you can run the following then follow the prompts to grant ultimate
trust to it:

```
gpg --edit-key 1E046C19ED2873D8C08AF7B8A0632691B78E3422 trust
```

The verification should then succeed as follows:

```
gpg: Signature made Thu Feb  6 14:58:22 2025 CET
gpg:                using RSA key 1E046C19ED2873D8C08AF7B8A0632691B78E3422
gpg:                issuer "releases@pmd-code.org"
gpg: checking the trustdb
gpg: marginals needed: 3  completes needed: 1  trust model: pgp
gpg: depth: 0  valid:   1  signed:   0  trust: 0-, 0q, 0n, 0m, 0f, 1u
gpg: Good signature from "PMD Release Signing Key <releases@pmd-code.org>" [ultimate]
```

## The Release Signing Key

PMD's release signing key consists of the primary key with the fingerprint
`2EFA 55D0 785C 31F9 56F2  F87E A0B5 CA1A 4E08 6838`.

It currently contains one subkey, that is used for signing. The subkey's fingerprint is
`1E04 6C19 ED28 73D8 C08A  F7B8 A063 2691 B78E 3422`.

```shell
gpg --list-keys --fingerprint --with-subkey-fingerprint releases@pmd-code.org
pub   rsa4096 2025-01-04 [C] [expires: 2027-01-04]
      2EFA 55D0 785C 31F9 56F2  F87E A0B5 CA1A 4E08 6838
uid           [ultimate] PMD Release Signing Key <releases@pmd-code.org>
sub   rsa4096 2025-01-04 [S] [expires: 2027-01-04]
      1E04 6C19 ED28 73D8 C08A  F7B8 A063 2691 B78E 3422
```

The public key is available under the identity `releases@pmd-code.org` at

* <https://keyserver.ubuntu.com/pks/lookup?search=2EFA+55D0+785C+31F9+56F2++F87E+A0B5+CA1A+4E08+6838&fingerprint=on&op=index>
* <https://keys.openpgp.org/search?q=releases%40pmd-code.org>

{%capture note%}
The key `1E04 6C19 ED28 73D8 C08A  F7B8 A063 2691 B78E 3422` is in use since 7.10.0 for signing artifacts
in Maven Central and since 7.11.0 to sign the binary distribution files.

Before that, we used a different key for signing maven artifacts. We had to revoke the key because
the passphrase was compromised. See [GHSA-88m4-h43f-wx84](https://github.com/pmd/pmd/security/advisories/GHSA-88m4-h43f-wx84)
for more information.
{%endcapture%}
{%include note.html content=note%}

## Maven Central

The artifacts we deploy to maven central under the group id [net.sourceforge.pmd](https://repo.maven.apache.org/maven2/net/sourceforge/pmd/)
are signed with the same key.

You can manually verify the artifacts with the same method:

```shell
wget https://repo.maven.apache.org/maven2/net/sourceforge/pmd/pmd-core/{{site.pmd.version}}/pmd-core-{{site.pmd.version}}.jar
wget https://repo.maven.apache.org/maven2/net/sourceforge/pmd/pmd-core/{{site.pmd.version}}/pmd-core-{{site.pmd.version}}.jar.asc
gpg --verify pmd-core-{{site.pmd.version}}.jar.asc pmd-core-{{site.pmd.version}}.jar
```

This gives you e.g.

```
gpg: Signature made Fri Jan 31 10:45:52 2025 CET
gpg:                using RSA key 1E046C19ED2873D8C08AF7B8A0632691B78E3422
gpg: Good signature from "PMD Release Signing Key <releases@pmd-code.org>" [ultimate]
```

## References
This page is heavily inspired by <https://adoptium.net/blog/2022/07/gpg-signed-releases/>.
