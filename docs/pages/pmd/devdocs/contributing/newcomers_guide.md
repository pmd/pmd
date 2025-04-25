---
title: Newcomers' Guide
tags: [devdocs]
permalink: pmd_devdocs_contributing_newcomers_guide.html
author: Andreas Dangel <andreas.dangel@pmd-code.org>
last_updated: January 2025 (7.10.0)
---

This is a small guide for GSoC Students and new contributors.

## 1. Check out PMD

As you want to engage with PMD, your first task is, to find out what PMD is - if you didn't do this already.
Read the [webpage](https://pmd.github.io), install PMD with the [Installation Guide](https://pmd.github.io/latest/pmd_userdocs_installation.html) and try it out.


Goals: Getting to know PMD, being able to run PMD locally


## 2. Meet the community

Now it's time to say hello to the community. We use mostly GitHub, mailing list and Gitter for communication
and organizing tasks.

Make sure, you use your GitHub account, when signing in into Gitter (and not your Twitter account) - this helps in recognizing you later on.

You probably already know on our GitHub presence: <https://github.com/pmd/pmd>

You can subscribe to our mailing list at: <https://lists.sourceforge.net/lists/listinfo/pmd-devel>

We have a single chat room: <https://gitter.im/pmd/pmd>

## 3. Pick an issue

The easiest way to familiarize yourself with the code base is to fix a small bug. You can see all [Good First Issues on GitHub](https://github.com/pmd/pmd/issues?utf8=%E2%9C%93&q=is%3Aopen+label%3A%22good+first+issue%22+no%3Aassignee+-label%3Ahas%3Apr).

When you have chosen an issue that you want to fix, leave a comment, so that we can assign the issue to you. That
way, we can avoid that multiple people are working on the same issue.

Goals: Familiarize with the code base

## 4. Create a fork and get the source code

In order to work on PMD you'll need the source code and an own fork. From your own fork, you'll create
pull requests later for your fix.

1. Go to [the pmd repository on GitHub](https://github.com/pmd/pmd) and click the **Fork** button on the top right.
2. Go to your fork and select **Clone or download** and choose your clone url.

   In the following example, the github user id "johndoe" is used. Replace this with your real user id.

3. Clone your fork:

    ``` shell
    $ git clone https://github.com/johndoe/pmd.git --depth=10 --no-tags
    ```

   Now you have a local copy of PMD in the directory `pmd`. Note the options "depth=10" and "no-tags": This
   is a speed-up, so that you only download and clone the latest history and not everything,
   which would be pretty big.

   Enter this directory with `cd pmd`. The following commands are executed within this directory.

4.  Create a branch for your bug fix based on the "main" branch and directly switch to it:

    ``` shell
    $ git checkout -b <branchname> main
    ```

    Assuming, you are working on issue 123, then you can create a branch called "issue-123".

    _Note:_ It's best practice, to create a separate branch ("topic branch") for the fix and not work on the main branch.

5.  Now work on the fix and make sure, your changes are actually working. Rebuild pmd and run the tests, e.g.

    ``` shell
    $ ./mvnw clean verify
    ```

    If your changes are only within one module (e.g. pmd-apex or pmd-java), then it is sufficient to only execute this
    module. In order to rebuild only pmd-apex, execute this command:

    ``` shell
    $ ./mvnw clean verify -pl pmd-apex
    ```

    We recommend to read the documentation [Building PMD General Info](pmd_devdocs_building_general.html) and
    especially the IDE specific guides, such as [Building PMD with IntelliJ IDEA](pmd_devdocs_building_intellij.html).
    These pages explain how to prepare your local development environment in order to work on PMD.

    When the build is successful, then commit your changes locally. If necessary, repeat
    this step, until the bug is fixed. You can create multiple local commits, this
    is no problem.

## 5. Push your changes

If you think, your changes are ready to be merged, commit them (if you've not done this already) and push
them into your fork:

``` shell
$ git push -u origin <branchname>
```

GitHub will display directly a link which you can open in your browser to
create a pull request.

## 6. Create a pull request

Either follow the link GitHub provided when you pushed or go to your fork on
GitHub and click the button **New pull request**.

**Congratulations!** You have now created your first pull request!

If you know, that your change is not complete yet and you are still working on it, then make sure, you
add the label "is:WIP" (work in progress) to the pull request.

## 7. Wait for the review

Now you need to wait a bit. One of the maintainers or other contributors will have a look at your pull request.
There are two possible outcomes of the review:
* The PR is accepted as is and is finally merged into the main branch.
* The PR is not accepted in the first round and some changes are requested. In that case, you can add additionally commits to your branch and push the branch afterwards. Let the reviewer know, that you have fixed/changed the PR.

While you are waiting for the review, you can also have a look at other PRs and review those. This not only helps the maintainers, it's also another way to learn PMD's code base.

## 8. Update local fork and further work

**Awesome!** Your pull request has been accepted.

But instead of going back to step 4 and repeat it and create a fresh clone/fork,
you'll learn now, how to update your local PMD code with the changes from "upstream".

We'll first add the main PMD repository as upstream. This step only needs to be done
once:

``` shell
$ git remote add upstream https://github.com/pmd/pmd
```

From now on, whenever you have finished work on a pull request and want to
start the next, you can update your local fork like this:

We'll first switch back to the main branch and then pull all the changes from
upstream and push it to your fork:

``` shell
$ git checkout main
$ git pull --ff-only upstream main
$ git push origin
```

_Note:_ You have now two remote repositories configured: "origin" is your own fork,
where you have write access and "upstream" is the main PMD repository, where you
only have read access.

_Note:_ The pull command has the option `--ff-only`, which does only a fast-forward-merge
of the upstream changes. If you have ever committed locally something to your main
branch then the pull command will fail. That's why it is important to always
work on topic branches.

Now you can go on with another issue or with a new feature.
Continue by creating a new topic branch:


``` shell
$ git checkout -b <new_branchname> main
```



-----

**References:**

This guide is heavily inspired by <https://api.coala.io/en/latest/Developers/Newcomers_Guide.html>
