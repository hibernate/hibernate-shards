Hibernate Shards
================

You can't always put all your relational data in a single relational database.
Sometimes you simply have too much data. Sometimes you have a distributed deployment architecture
(network latency between California and India might be too high to have a single database). There might
even be non-technical reasons (a potential customer simply won't do the deal unless her company's data lives in its
own db instance). Whatever your reasons, talking to multiple relational databases inevitably complicates
the development of your application. Hibernate Shards is a framework that is designed to encapsulate and
reduce this complexity by adding support for horizontal partitioning on top of Hibernate Core. Simply put, 
we aim to provide a unified view of multiple databases via Hibernate.

## License

This software and its documentation are distributed under the terms of the
FSF Lesser Gnu Public License (see lgpl.txt).

## Building Instructions

This build is in flux from Ant which was used originally to now use Gradle.  Specifically, tests are known to
be broken currently because the tests use Ant-specific hooks.  This will be addressed moving forward.

## Report issues

Report issues (bugs, enhancement requests, etc) to the [Hibernate Jira](https://hibernate.onjira.com/browse/HSHARDS)
