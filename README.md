Linear File Parser
==================
Copyright (C) 2015, 2017 Felix Wiemuth

License
-------

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.


About
-----
LinearFileParser is a simple line-based parser for text files with a custom format. The file format is defined in a simple way in Java by `KeyProcessor`s where the basic idea is that keywords at the beginning of a line specify what to do with the content of that line.
The parser is "line-based" because its purpose is to dispatch lines to predefined processors while the parsing of content in a line has to be implemented separately.
LinearFileParser can, for example, be used to parse user-friendly configuration files or import (an unspecified amount of) structured data.
See the Javadoc of class [LinearFileParser](src/felixwiemuth/linearfileparser/LinearFileParser.java) for details.

Project status
--------------
The current and recommended version is 2.0. It should be quite stable as it works reliably in [LinCal](https://github.com/felixwiemuth/LinCal) but apart from the few included tests there is no extensive testing. Version 1.2 was reliable in LinCal for quite long but contains a bug that would lead to erroneous behaviour when using key and section prefixes of different length. Localization has been tested to work in Java as well as on Android. Further development is not planned apart from small improvements should they become necessary in projects using LinearFileParser.

Usage
-----
Simply extend [LinearFileParser](src/felixwiemuth/linearfileparser/LinearFileParser.java) and add your own sections and key processors. See the class' Javadoc for an introduction on how to use LinearFileParser. Also read the remaining Javadoc for details.

Make sure to specify the correct input encoding when reading files (see the different variants of `_parse`). When given an `InputStream`, UTF-8 is used.

Example
-------
To see how LinearFileParser is used in practice, look at the Android app [LinCal](https://github.com/felixwiemuth/LinCal) with its [LinCalParser](https://github.com/felixwiemuth/LinCal/blob/master/app/src/main/java/felixwiemuth/lincal/parser/LinCalParser.java).


Adding LinearFileParser to your project
---------------------------------------
### General
- If you are using git, simply add the library as a [submodule](https://git-scm.com/book/en/v2/Git-Tools-Submodules) somewhere in your module's directory (e.g. in `app/lib/` for an Android app): `$ git submodule add https://github.com/felixwiemuth/LinearFileParser`. This allows you to add the library at a specific commit (and easily update it) without having to actually add the files to the repository. If you don't use git, simply use `$ git clone` with the same URL.
- Then add the `src` folder to the project's source directories and the `res` folder to the resource directories. The packages in `res` must be available in Java's classs path. However, it is only required if you don't use your own localization framework to localize LinearFileParser (see below).
  - If you use gradle (e.g. Android Studio project): Add the following to section `android ` in your module's `build.gradle`:
        sourceSets {
            main.java.srcDirs += 'libs/LinearFileParser/src'
            main.resources.srcDirs += 'libs/LinearFileParser/res'
        }


### Localization
LinearFileParser uses localized Strings in exception messages. The available languages/locales can be seen in the [res](res/felixwiemuth/linearfileparser/localization/) folder. It might be useful to adapt the error messages to the context of your application. To do so, implement [ResourceProvider](src/felixwiemuth/linearfileparser/localization/ResourceProvider.java) where you either delegate to your own `ResourceBundle` as in [DefaultResourceProvider](src/felixwiemuth/linearfileparser/localization/DefaultResourceProvider.java) or implement an adapter to the resource framework used by your application.

Note that in normal operation without exceptions, localization does not play a role and does not incur any performance overhead.

All localized messages have been tested in Java and on Android. Note, however, that Android does not display umlauts etc. correctly (e.g. from de_DE). This is probably due to Android not correctly reading the ISO-8859-1 file as required by Java, further discussed [here](https://stackoverflow.com/questions/27481140/resourcebundle-usage-on-android). For Android either provide a properties file that works (try UTF-8) or better use Android's resource framework.

Changes
-------
### Version 2.0 (2017-09-15)
- Added localization
- The precedence of key processors has changed: now section key processors take precedence over global processors
- Added more ways to provide input(files)
  - Character encoding can now be manually specified (by providing an InputStreamReader)
  - When possible, UTF-8 is chosen instead of system default
- The top-level package name has changed and has to be corrected in existing code using LinearFileParser
- Existing code throwing ParseExceptions outside key processors has to be changed to specially intitialize these exceptions first (see doc of ParseException)
