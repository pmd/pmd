# Tacit Knowledge fork of PMD.  https://github.com/pmd/pmd.git

This fork supports a couple new CPD features for Java projects
    1. ignoreAnnotations to skip CPD processing of annotations. Annotations often create many redundant lines and false positives.
    2. @SuppressWarnings("CPD-START")
There's a pull request with more information on these. 
    https://github.com/pmd/pmd/pull/6 

## Why the fork?

Its temporary and we'd like to retire the fork.  

    1. We have a pull request open: https://github.com/pmd/pmd/pull/6 
    2. Once that gets done, we'll look forward to the new maven library being released 

## Samples

```
    //enable suppression
    @SuppressWarnings("CPD-START")
    public Object someParameterizedFactoryMethod(int x) throws Exception {

    }
    //disable suppression
    @SuppressWarnings("CPD-END)
    public void nextMethod() {
    }
```

To disable annotations, add ignore_annotations=true to the cpd properties.  Just like ignore_literals


## Start Using It?
If you can't wait for this to get integrated into the main pmd project, you can start now.
Feel free to pull and build it.  Additionally you can include noexcuses in your Maven project via: 
```
<dependency>
  <groupId>com.tacitknowledge</groupId>
    <artifactId>pmd</artifactId>
    <name>PMD</name>
    <!-- version below matches the tag at https://github.com/tacitknowledge/pmd.git on the tkpmd branch -->
    <version>5.1.0-TK-1.0</version>
</dependency>
```

## Licensing
This fork inherits the BSD style license from the orginal PMD project.

Copyright (c) 2002-2009, InfoEther, Inc
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are
met:

    * Redistributions of source code must retain the above copyright
notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
notice, this list of conditions and the following disclaimer in the
documentation and/or other materials provided with the distribution.
    * The end-user documentation included with the redistribution, if
any, must include the following acknowledgement:
      "This product includes software developed in part by support from
the Defense Advanced Research Project Agency (DARPA)"
    * Neither the name of InfoEther, LLC nor the names of its
contributors may be used to endorse or promote products derived from
this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
          
## Contributing

1. We strongly suggest contributing to the main pmd project located at https://github.com/pmd/pmd.git 
