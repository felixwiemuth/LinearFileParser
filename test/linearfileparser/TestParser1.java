/*
 * Copyright (C) 2015 Felix Wiemuth
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
package linearfileparser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ListIterator;

/**
 * A parser to demonstrate parsing mechanisms by output.
 *
 * @author Felix Wiemuth
 */
public class TestParser1 extends LinearFileParser {

    public TestParser1() {
        super("#", "@", "@", "first", true);

        // sections
        addSection("first", new Action() {
            @Override
            public void run(ListIterator<String> it) {
                System.out.println("Entered section FIRST, beginning on line " + (it.nextIndex() + 1));
            }
        }, new Action() {
            @Override
            public void run(ListIterator<String> it) {
                int line = it.previousIndex() + 1;
                if (it.hasNext()) {
                    line--;
                }
                System.out.println("Left section FIRST after line " + line);
            }
        });
        addSection("second", new Action() {
            @Override
            public void run(ListIterator<String> it) {
                System.out.println("Entered section SECOND, beginning on line " + (it.nextIndex() + 1));
            }
        }, new Action() {
            @Override
            public void run(ListIterator<String> it) {
                System.out.println("Left section SECOND after line " + (it.previousIndex() - 1 + 1));
            }
        });

        // global keys
        addKeyProcessor(new KeyProcessor("print") {
            @Override
            public void process(String arg, ListIterator it) throws ParseException {
                System.out.println("*** " + arg + " ***");
            }
        });

        addKeyProcessor(new KeyProcessor("switchSection") {
            @Override
            public void process(String arg, ListIterator it) throws ParseException {
                System.out.println("### Manual switch section ###");
                changeSection(arg);
            }
        });

        // local keys
        addKeyProcessor("first", new KeyProcessor("printSection") {
            @Override
            public void process(String arg, ListIterator it) throws ParseException {
                System.out.println("Current section at line " + (it.previousIndex() + 1) + ": FIRST");
            }
        });
        addKeyProcessor("second", new KeyProcessor("printSection") {
            @Override
            public void process(String arg, ListIterator it) throws ParseException {
                System.out.println("Current section at line " + (it.previousIndex() + 1) + ": SECOND");
            }
        });
        addKeyProcessor("second", new KeyProcessor("printThisLine") {
            @Override
            public void process(String arg, ListIterator it) throws ParseException {
                System.out.println("Current line: " + it.previous());
                it.next();
            }
        });
    }

    public void parse(File file) throws IOException, FileNotFoundException, UnknownKeyException, UnknownSectionException, ParseException {
        _parse(file);
    }

}
