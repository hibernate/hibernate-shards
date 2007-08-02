Hibernate Shards
================
Version: 3.0.0.Beta2, 02.08.2007

Description
-----------

You can't always put all your relational data in a single relational database.
Sometimes you simply have too much data. Sometimes you have a distributed deployment architecture
(network latency between California and India might be too high to have a single database). There might
even be non-technical reasons (a potential customer simply won't do the deal unless her company's data lives in its
own db instance). Whatever your reasons, talking to multiple relational databases inevitably complicates
the development of your application. Hibernate Shards is a framework that is designed to encapsulate and
reduce this complexity by adding support for horizontal partitioning on top of Hibernate Core. Simply put, 
we aim to provide a unified view of multiple databases via Hibernate.

Instructions
------------

Unzip to installation directory, read doc/reference


Contact
------------

Latest Documentation:

   http://hibernate.org
   http://shards.hibernate.org

Bug Reports:

   Hibernate JIRA (preferred)

Discussion:

  hibernate-shards-dev@googlegroups.com

Free Technical Support:

   http://forum.hibernate.org


Notes
-----------

If you want to contribute, go to http://www.hibernate.org/

This software and its documentation are distributed under the terms of the
FSF Lesser Gnu Public License (see lgpl.txt).