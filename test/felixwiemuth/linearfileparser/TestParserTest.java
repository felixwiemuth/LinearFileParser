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
package felixwiemuth.linearfileparser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * This test runs some example parsers on example files.
 *
 * @author Felix Wiemuth
 */
public class TestParserTest {

    public TestParserTest() {
    }

    /**
     * Asserts that {@link TestParser1} does not throw exceptions for the valid
     * test file.
     *
     * @throws IOException
     * @throws FileNotFoundException
     * @throws UnknownSectionException
     * @throws ParseException
     */
    @Test
    public void test1() throws IOException, FileNotFoundException, UnknownSectionException, ParseException {
        TestParser1 parser = new TestParser1();
        parser.parse(new File("test/felixwiemuth/linearfileparser/TestFile1"));
    }

    /**
     * Asserts that {@link TestParser2} does not throw exceptions for the valid
     * test file.
     *
     * @throws IOException
     * @throws FileNotFoundException
     * @throws UnknownSectionException
     * @throws ParseException
     */
    @Test
    public void test2() throws IOException, FileNotFoundException, UnknownSectionException, ParseException {
        TestParser2 parser = new TestParser2();
        parser.parse(new File("test/felixwiemuth/linearfileparser/TestFile2"));
    }

    /**
     * Asserts that {@link TestParser3} does not throw exceptions for the valid
     * test file.
     *
     * @throws IOException
     * @throws FileNotFoundException
     * @throws UnknownSectionException
     * @throws ParseException
     */
    @Test
    public void test3() throws IOException, FileNotFoundException, UnknownSectionException, ParseException {
        TestParser3 parser = new TestParser3();
        parser.parse(new File("test/felixwiemuth/linearfileparser/TestFile3"));
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @BeforeMethod
    public void setUpMethod() throws Exception {
    }

    @AfterMethod
    public void tearDownMethod() throws Exception {
    }
}
