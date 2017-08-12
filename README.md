Linear File Parser
==================
Copyright (C) 2015 Felix Wiemuth

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
See the Javadoc of class [LinearFileParser](src/linearfileparser/LinearFileParser.java) for details.
