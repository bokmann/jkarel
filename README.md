# LoudounCodes JKarel

## Introduction:
This code was born from the Loudoun County Public School System's use of Fairfax County's FCPSKarel computer science curriculum.

The primary person forking this code (@bokmann) has been using this code for 7 years as a Teacher's Assistant at Loudoun Valley High School.

In the intervening decade+ since the code was originally written, Java has evolved.  This is first and foremost an effort to evolve the Karel code into the present.

This version of the framework is making the code more idomatic with current Java conventions, as well as revisiting some design decisions that should make this code more suitable both for a beginner programming course, as well as a post-AP course learning about software architecture.

## Building:
### Prerequisites:
* a modern version of Java, available from a bash-like shell.
* a modern version of ant, available from a bash-like shell.
### Build Steps:
* download this code either by cloneing via git+ssh or downloading an uncompressing the zip file, above.
* from a command line at the root of the project, type 'ant'.
** a jar file and javadoc will appear is if by magic in an 'out' directory.
** a 'scratch' directory will also be created, with the intermediate results of compiling.
* typing 'ant clean' will remove the scratch and out directories, returning your directory to a pristine state (except for modifications you may have made to the source).
## Changes made to this version:
* Previous authors emails have been removed from each file, and their credit has been consolidated to 'contributors' within this readme.  This is both for modern conventions, and so that those authors are not bothered with questions about this version of the framework; it has changed enough so that key details would be unrecognizable to them.
* Copyright notices have been removed from the headers of each file and consolidated into the LICENSE.TXT file, as is modern convention.
* idioms based on C++ (like enums based on integer values) have been replaced with modern Java semantics (like Java's Enum, introduced in Java 1.5)
* XML Parsing was moved to use the built-in Java classes rather than the external Xerces library from Apache (makes the setup easier for the begining level class)
* Fixed the build system to be inline with larger organizational software development practices
  * Classes are built in an independent location; not next to java files.
  * There is an appropriately implemented 'clean' command, which removes all non-version-controlled build artefacts.
  * The jar file is built entirely by the build process, not reliant on fragments of text files.
  * the version number of the library is semantic, and included in the jar file name.
* Documentation is now entirely built with Javadoc
  * Doxyfile is no longer used in this project.
  * (effort to redocument and include diagrams is in-progress)
  * rewrite documentation from scratch (in progress. As forked, had over 50 documentation warnings or errors)
* Names were changed to a more semantic metaphor, making it easier to learn
  * As an example, Robots no longer have 'coordinates' on a 'display', they have 'locations' in an 'arena'.
  * the concept of Display Speed has been changed to the Arena Pace.  There is a Pacing enumeration that also includes a step ability.
    * lots of integer mod checking math and edge case code evaporated with this change.
* Package hierarchy was collapsed.  A 'util' package that depends on and is depended by the parent provides no value, just a layer of confusion.
* XML Package was not collapsed, because a future mod is going to introduce an interface and a runtime dependency as an example for an architecure class.
* Unused files were removed.
* Unit Tests are in progress.
* Package hierarchy was renamed to clearly delineate this from the past non-semantically compatible library, and to remove the version number from the hierarchy name. This will make package evolution and upgrade easier in the future.
* New jar file is compatible with JRuby.
* Check the git commit comments for a more detailed changelog.

## Regarding Contributions:
Contributions are welcome!  As this is intended for a class on software architecture, the impact on pull requests will be considered, so contrbutions will not be blinfly accepted.

To contribute:
* fork this project on github.
* make your change, ideally tracking it to an issue you have added to the repo.
* Commits should follow the Chris Beam's "How to Write a Commit Message"
  * https://chris.beams.io/posts/git-commit/#imperative
  * with an overriding opinion on Rule 5.  While Git commit messages that are imperative will be accepted, this project prefers commit subject lines written by humans start with a *present tense action verb*, and answer the question, "What would this commit do if I cherry-picked it into my current code?"
* make sure you add your name to the list of contributors.  While github tracks that, I hope to eventually embed attribution into the jar file manifest.
* submit a pull request.

## ToDo:
* redesign curriculum to use new classes and features.
* Add a maze generation package based on the book "Mazes For Programmers", so that make generating algorithms can be studied in an algorithms course.
* Design curriculum for a software architecture course.
  * example: Strategy Pattern in the Pacing Enum
  * Inversion/Refactoring of Dependency in the XML package
  * Interface for ArenaData, with a the xml library and a maze generation library as data producers.
  * more advanced robots, ala RoboCode or RTanque?
  * Curriculum using JRuby?

### Contributors to this version, maintained by the nonprofit LoudounCodes:
* David Bock(@bokmann)

### Previous Contributors to FCPSKarel, on which this work is based:
This work would not be possible without the following authors. Their work served for years as the foundation of the curriculum used in a classroom in which I was TA.

* Andy Street <alstreet@vt.edu> 2007 Original author of FCPS Karel. Thank you for the foundational work.
* Daniel Johnson <2010djohnson@tjhsst.edu> Summer 2009 Major contributions before we got our grubby little hands on it.

Karel the Robot is a time-tested curriculum with dozens of versions available in many languages spanning back decades. Originally developed by Richard Pattis in 1981 and used at Stanford University, a wealth of information can be found at its Wikipedia entry: https://en.wikipedia.org/wiki/Karel_(programming_language)