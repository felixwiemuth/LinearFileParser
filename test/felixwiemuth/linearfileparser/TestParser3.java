/*
 * Copyright (C) 2017 Felix Wiemuth
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package felixwiemuth.linearfileparser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ListIterator;

/**
 * A parser to demonstrate parsing mechanisms by output.
 *
 * @author Felix Wiemuth
 */
public class TestParser3 extends LinearFileParser {

    public TestParser3() {
        super("//", ">>", "@sec ", "0", true);

        // sections
        addSection("1", null, null);
        addSection("2", null, null);
        addSection("3", null, null);
        addSection("0");

        // global keys
        addKeyProcessor(new KeyProcessor("?") {
            @Override
            public void process(String arg, ListIterator it) throws ParseException {
                System.out.println("Usage: How to use this tool.");
            }
        });

        addKeyProcessor("0", new KeyProcessor("") {
            @Override
            public void process(String arg, ListIterator it) throws ParseException {
                System.out.println(">>> " + arg);
            }
        });
        setDefaultProcessor(new DefaultProcessor() {
            @Override
            public boolean run(String line, ListIterator<String> it) throws IllegalLineException, ParseException {
                System.out.println(">>> I don't understand \"" + line + "\"");
                return true;
            }
        });

        // local keys
        addKeyProcessor("1", new KeyProcessor("1") {
            @Override
            public void process(String arg, ListIterator it) throws ParseException {
                System.out.println("Magic found at line " + getCurrentLineNumber());
            }
        });
        addKeyProcessor("1", new KeyProcessor("") {
            boolean found = false;

            @Override
            public void process(String arg, ListIterator it) throws ParseException {
                if (arg.equals("1")) {
                    found = true;
                    System.out.println(">>> Magic found at line " + getCurrentLineNumber());
                } else if (!found) {
                    System.out.println(">>> It's not \"" + arg + "\"");
                } else {
                    System.out.println(">>> " + arg);
                }
            }
        });
        addKeyProcessor("2", new KeyProcessor("") {
            boolean found = false;

            @Override
            public void process(String arg, ListIterator it) throws ParseException {
                if (arg.equals("second")) {
                    found = true;
                    System.out.println(">>> Magic found at line " + getCurrentLineNumber());
                } else if (!found) {
                    System.out.println(">>> It's not \"" + arg + "\"");
                } else {
                    System.out.println(">>> " + arg);
                }
            }
        });
        addKeyProcessor("3", new KeyProcessor("") {
            boolean found = false;

            @Override
            public void process(String arg, ListIterator it) throws ParseException {
                if (arg.equals("3rd")) {
                    found = true;
                    System.out.println(">>> Magic found at line " + getCurrentLineNumber());
                } else if (!found) {
                    System.out.println(">>> It's not \"" + arg + "\"");
                } else {
                    System.out.println(">>> " + arg);
                }
            }
        });
    }

    public void parse(File file) throws IOException, FileNotFoundException, UnknownKeyException, UnknownSectionException, ParseException {
        _parse(file);
    }

}
