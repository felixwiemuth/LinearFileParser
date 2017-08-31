/*
 * Copyright (C) 2015, 2017 Felix Wiemuth
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

import felixwiemuth.linearfileparser.localization.DefaultResourceProvider;
import felixwiemuth.linearfileparser.localization.ResourceProvider;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

/**
 * A line-based parser for simple text files with keywords. This is a base class
 * which can be extended to implement a specific parser. The subclass should
 * provide a 'parse()' method which also returns the result.
 *
 * The basic file structure supported by this parser is a text file where each
 * line has a content of a specific type. The type of a line is specified by a
 * keyword at the beginning of the line. For each keyword, a
 * {@link KeyProcessor} must be registered at the parser which contains the
 * actual code for processing lines of that type.
 *
 * In the document to be parsed, different sections can be specified (using the
 * sectionPrefix explained below). For each section, a different behaviour of
 * the parser is defined by adding section specific processors.
 *
 * Subclasses can keep state while parsing to be referred to between different
 * calls to the processors. In the processors, further lines can be parsed
 * manually using the parser's iterator on the list of lines.
 *
 * When creating a parser, three types of line prefixes can be defined:
 * <ul>
 * <li>commentPrefix: prefix to indicate comments (comments can online start at
 * a new line, null disables comments)</li>
 * <li>sectionPrefix: indicates to switch to the section specified after the
 * prefix and switch to the corresponding parsing mode. If sectionPrefix is
 * null, sections are not switched automatically. Sections can be switched
 * manually by subclasses using {@link #changeSection}</li>
 * <li>keyPrefix: following this prefix is a key which specifies how the parser
 * should handle the current line</li>
 * </ul>
 * Note that no space is expected after a prefix - if desired, a space must be
 * added to the prefix itself.
 *
 * Every line is processed as follows (in the given order) where empty lines are
 * ignored if specified at construction. If it starts with
 * <ul>
 * <li>the commentPrefix, the current line is ignored and the parser continues
 * with the next line</li>
 * <li>the sectionPrefix, the following substring specifies the section to
 * switch to. If the section was not registered with {@link #addSection()}, then
 * {@link UnknownSectionException} is thrown. Exemption: sectionPrefix equals
 * keyPrefix - then the check for keys as described below follows.</li>
 * <li>the keyPrefix, the following substring ending before the next whitespace
 * is identified as a key and passed to a registered {@link KeyProcessor} with
 * everything after the whitespace as argument (or null as argument if the line
 * ends after the key). First it is looked for a global processor for this key
 * and then for a processor for the current section. Only the first one found is
 * invoked. If the key is not found (was not registered with
 * {@link addKeyProcessor()}), then {@link UnknownKeyException} is thrown.</li>
 * </ul>
 * If the line starts with non of the listed prefixes, the line is passed to the
 * default processor set by {@link #setDefaultProcessor()}. If no default
 * processor exists or if it returns false, then {@link IllegalLineException} is
 * thrown.
 *
 * @author Felix Wiemuth
 */
public class LinearFileParser {

    /**
     * Represents how to process a key with the given argument on an input line.
     */
    public abstract static class KeyProcessor {

        private ResourceProvider rp;

        public final String key;
        private final boolean oneShot;
        private int lastOccurrence = -1;

        /**
         *
         * @param key the key to be processed
         * @param oneShot whether this key may only be used once in the section
         * of this processor - when used a second time,
         * {@link RepeatedKeyException} is thrown
         */
        public KeyProcessor(String key, boolean oneShot) {
            this.key = key;
            this.oneShot = oneShot;
        }

        /**
         *
         * @param key the key to be processed
         */
        public KeyProcessor(String key) {
            this(key, false);
        }

        public void setResourceProvider(ResourceProvider rp) {
            this.rp = rp;
        }

        protected ResourceProvider getRp() {
            return rp;
        }

        private void _process(String arg, ListIterator<String> it) throws RepeatedKeyException, ParseException {
            if (oneShot) {
                if (lastOccurrence != -1) {
                    RepeatedKeyException ex = new RepeatedKeyException(it.nextIndex(), key, lastOccurrence);
                    ex.setResourceProvider(rp);
                    throw ex;
                } else {
                    lastOccurrence = it.nextIndex();
                }
            }
            process(arg, it);
        }

        /**
         * Process the key at the current line.
         *
         * @param arg the argument given with the key, which is everything
         * following the space after the key until end of line or 'null' if line
         * ends after key
         * @param it iterator over the list of lines, pointing to the line after
         * the current - the iterator can be used to modify the list or the
         * current position and the parser will continue with this same iterator
         * @throws ParseException to indicate a syntax or semantic error (use
         * {@link #getCurrentLineNumber()} for the current line number).
         */
        public abstract void process(String arg, ListIterator<String> it) throws ParseException;

    }

    public interface Action {

        /**
         *
         * @param it iterator over the list of lines, pointing to the line after
         * the current - the iterator can be used to modify the list or the
         * current position and the parser will continue with this same iterator
         */
        void run(ListIterator<String> it);
    }

    public interface DefaultProcessor {

        /**
         *
         * @param line
         * @param it
         * @return true, if the line could be processed (false causes the parser
         * to throw {@link IllegalLineException})
         * @throws IllegalLineException
         */
        boolean run(String line, ListIterator<String> it) throws IllegalLineException, ParseException;
    }

    private class Section {

        private final Action actionOnEnter;
        private final Action actionOnLeave;
        private final HashMap<String, KeyProcessor> keyProcessors = new HashMap<>();

        public Section() {
            this(null, null);
        }

        /**
         *
         * @param actionOnEnter action to be performed before entering this
         * section (iterator points to first line after switching to the
         * section)
         * @param actionOnLeave action to be performed after leaving this
         * section (iterator points to the line which specifies a new section or
         * the last line if all lines were processed)
         */
        public Section(Action actionOnEnter, Action actionOnLeave) {
            this.actionOnEnter = actionOnEnter;
            this.actionOnLeave = actionOnLeave;
        }

        public void addKeyProcessor(KeyProcessor keyProcessor) throws KeyProcessorAlreadyExistsException {
            if (keyProcessors.containsKey(keyProcessor.key)) {
                throw new KeyProcessorAlreadyExistsException();
            }
            keyProcessors.put(keyProcessor.key, keyProcessor);
        }

        public boolean containsKey(String key) {
            return keyProcessors.containsKey(key);
        }

        public void process(String key, String arg, ListIterator<String> it) throws UnknownKeyException, RepeatedKeyException, ParseException {
            if (!keyProcessors.containsKey(key)) {
                UnknownKeyException ex = new UnknownKeyException(getCurrentLineNumber(), key);
                ex.setResourceProvider(rp);
                throw ex;
            }
            keyProcessors.get(key)._process(arg, it);
        }

        public void enter(ListIterator<String> it) {
            if (actionOnEnter != null) {
                actionOnEnter.run(it);
            }
        }

        public void leave(ListIterator<String> it) {
            if (actionOnLeave != null) {
                actionOnLeave.run(it);
            }
        }

    }

    private ResourceProvider rp = new DefaultResourceProvider();

    public final String START_SECTION;

    private final String commentPrefix;
    private final String sectionPrefix;
    private final String keyPrefix;
    private final boolean SKIP_EMPTY_LINES;
    private final HashMap<String, Section> sections = new HashMap<>(); // section -> key -> processor

    private Section section;
    private ListIterator<String> it;
    private final Section GLOBAL_PROCESSORS = new Section(); // processors valid in every section
    private DefaultProcessor defaultProcessor; // to be used to process a line where no other processing applies

    /**
     * Create a new parser without sections. Skips lines containing whitespaces
     * only.
     *
     * @param commentPrefix prefix introducing a comment
     * @param keyPrefix prefix for all keywords
     */
    public LinearFileParser(String commentPrefix, String keyPrefix) {
        this(commentPrefix, keyPrefix, null, null, true);
    }

    /**
     * Create a new parser using sections. Skips lines containing whitespaces
     * only.
     *
     * @param commentPrefix prefix introducing a comment
     * @param keyPrefix prefix for all keywords
     * @param sectionPrefix prefix to specify sections (null disables to switch
     * sections with a command)
     * @param startSection the section the parser assumes starting from the
     * first line
     */
    public LinearFileParser(String commentPrefix, String keyPrefix, String sectionPrefix, String startSection) {
        this(commentPrefix, keyPrefix, sectionPrefix, startSection, true);
    }

    /**
     * Create a new parser using sections.
     *
     * @param commentPrefix prefix introducing a comment
     * @param keyPrefix prefix for all keywords
     * @param sectionPrefix prefix to specify sections (null disables to switch
     * sections with a command)
     * @param startSection the section the parser assumes at the beginning of
     * the file
     * @param skipEmptyLines whether to skip lines containing whitespaces only
     */
    public LinearFileParser(String commentPrefix, String keyPrefix, String sectionPrefix, String startSection, boolean skipEmptyLines) {
        this.commentPrefix = commentPrefix;
        this.sectionPrefix = sectionPrefix;
        this.keyPrefix = keyPrefix;
        this.START_SECTION = startSection;
        this.SKIP_EMPTY_LINES = skipEmptyLines;
    }

    public void setResourceProvider(ResourceProvider resourceProvider) {
        this.rp = resourceProvider;
    }

    /**
     * Add a new section to the parser.
     *
     * @param sectionID unique ID to identify the section and to refer to the
     * section in the file to be parses
     * @param actionOnEnter action to be performed before entering this section
     * (iterator points to first line after switching to the section)
     * @param actionOnLeave action to be performed after leaving this section
     * (iterator points to the line which specifies a new section or the last
     * line if all lines were processed)
     * @throws SectionAlreadyExistsException if a section with the given ID
     * already exists
     */
    protected final void addSection(String sectionID, Action actionOnEnter, Action actionOnLeave) throws SectionAlreadyExistsException {
        if (sections.containsKey(sectionID)) {
            throw new SectionAlreadyExistsException();
        }
        sections.put(sectionID, new Section(actionOnEnter, actionOnLeave));
    }

    /**
     * Same as {@code addSection(sectionID, null, null)}.
     *
     * @param sectionID
     * @throws SectionAlreadyExistsException
     */
    protected final void addSection(String sectionID) throws SectionAlreadyExistsException {
        addSection(sectionID, null, null);
    }

    /**
     * Add a {@link KeyProcessor} for a key. Applies to all sections.
     *
     * @param keyProcessor
     * @throws KeyProcessorAlreadyExistsException if a processor for the
     * specified key was already added using this method
     */
    protected final void addKeyProcessor(KeyProcessor keyProcessor) throws KeyProcessorAlreadyExistsException { //TODO make final (do not allow to overwrite method)?
        keyProcessor.setResourceProvider(rp);
        GLOBAL_PROCESSORS.addKeyProcessor(keyProcessor);
    }

    /**
     * Add a {@link KeyProcessor} for a key. Applies only to the specified
     * section. Should be used after all calls to
     * {@link addKeyProcessor(String key, KeyProcessor keyProcessor)} to ensure
     * that no processors are hidden by others.
     *
     * @param sectionID
     * @param keyProcessor
     * @throws SectionNotExistsException if the section specified was not added
     * with {@link #addSection()}
     * @throws KeyProcessorAlreadyExistsException if a processor for the
     * specified key was already added to apply to all sections or this section.
     */
    protected final void addKeyProcessor(String sectionID, KeyProcessor keyProcessor) throws SectionNotExistsException, KeyProcessorAlreadyExistsException {
        keyProcessor.setResourceProvider(rp);
        if (!sections.containsKey(sectionID)) {
            throw new SectionNotExistsException();
        }
        if (GLOBAL_PROCESSORS.containsKey(keyProcessor.key)) {
            throw new KeyProcessorAlreadyExistsException();
        }
        sections.get(sectionID).addKeyProcessor(keyProcessor);

    }

    protected final void setDefaultProcessor(DefaultProcessor defaultProcessor) {
        this.defaultProcessor = defaultProcessor;
    }

    /**
     * Get the number of the line currently being processed. This is the index
     * in the list of lines plus one.
     *
     * @return
     */
    protected int getCurrentLineNumber() {
        return it.nextIndex();
    }

    /**
     * Manually switch to a section.
     *
     * @param sectionID
     * @throws UnknownSectionException
     */
    protected void changeSection(String sectionID) throws UnknownSectionException {
        if (section == null) {
            throw new IllegalStateException("Implementation error (please contact developer): At line " + it.nextIndex() + ": Cannot change section when no initial section was specified at construction.");
        }
        if (!sections.containsKey(sectionID)) {
            UnknownSectionException ex = new UnknownSectionException(it.previousIndex(), sectionID);
            ex.setResourceProvider(rp);
            throw ex;
        }
        section.leave(it);
        section = sections.get(sectionID);
        section.enter(it);
    }

    /**
     * Convenience method for {@link #_parse(java.util.List)}.
     *
     * @param inputStream an input stream providing the lines to be parsed
     * @throws FileNotFoundException
     * @throws IOException
     * @throws UnknownSectionException
     * @throws UnknownKeyException
     * @throws RepeatedKeyException
     * @throws IllegalLineException
     * @throws ParseException
     */
    protected void _parse(InputStream inputStream) throws IOException, IllegalLineException, UnknownKeyException, RepeatedKeyException, UnknownSectionException, ParseException {
        _parse(new InputStreamReader(inputStream));
    }

    /**
     * Convenience method for {@link #_parse(java.util.List)}.
     *
     * @param file the file to be parsed
     * @throws FileNotFoundException
     * @throws IOException
     * @throws UnknownSectionException
     * @throws UnknownKeyException
     * @throws RepeatedKeyException
     * @throws IllegalLineException
     * @throws ParseException
     */
    protected void _parse(File file) throws FileNotFoundException, IOException, IllegalLineException, UnknownKeyException, RepeatedKeyException, UnknownSectionException, ParseException {
        _parse(new FileReader(file));
    }

    private void _parse(InputStreamReader reader) throws IOException, IllegalLineException, UnknownKeyException, RepeatedKeyException, UnknownSectionException, ParseException {
        BufferedReader bufferedReader = new BufferedReader(reader);
        List<String> lines = new ArrayList<>();
        try {
            while (true) {
                String line = bufferedReader.readLine();
                if (line == null) {
                    break;
                }
                lines.add(line);
            }
        } finally {
            bufferedReader.close();
        }
        _parse(lines);
    }

    /**
     * Parse a list of lines. If an exception occurs, the parsing procedure is
     * aborted with no result.
     *
     * @param lines
     * @throws UnknownSectionException when the parser detects an attempt to
     * switch to a section which was not defined (either by the corresponding
     * prefix in a line or by a processor)
     * @throws UnknownKeyException when the parser detects a key which was not
     * registered for all sections or the current section
     * @throws RepeatedKeyException when the key at the current line is a
     * one-shot key and it has been used before
     * @throws IllegalLineException when the line was not identified as a
     * comment, a section specification or a key and the default processor (if
     * any) returned false
     * @throws ParseException if it is none of the mentioned subclasses, it is
     * thrown by a key processor
     */
    protected void _parse(List<String> lines) throws UnknownSectionException, UnknownKeyException, RepeatedKeyException, IllegalLineException, ParseException {
        it = lines.listIterator();
        String line;
        section = sections.get(START_SECTION);
        if (section != null) {
            section.enter(it);
        }

        while (it.hasNext()) {
            line = it.next();
            if (SKIP_EMPTY_LINES && line.trim().isEmpty()) {
                // skip this line
            } else if (commentPrefix != null && line.startsWith(commentPrefix)) {
                // skip this line
            } else if (sectionPrefix != null && line.startsWith(sectionPrefix)) {
                String sectionID = line.substring(keyPrefix.length());
                try {
                    changeSection(sectionID);
                } catch (UnknownSectionException ex) {
                    // if sectionPrefix and keyPrefix are equal, we could also have a regular key here, so check for keys next
                    if (sectionPrefix.equals(keyPrefix)) {
                        parseKey(line, it);
                    } else {
                        throw ex;
                    }
                }
            } else if (line.startsWith(keyPrefix)) {
                parseKey(line, it);
            } else {
                try {
                    if (defaultProcessor == null || !defaultProcessor.run(line, it)) { // NOTE: 'run' can also throw IllegalLineException
                        throw new IllegalLineException(getCurrentLineNumber());
                    }
                } catch (IllegalLineException ex) {
                    ex.setResourceProvider(rp);
                    throw ex;
                }
            }
        }
        if (section != null) {
            section.leave(it);
        }
    }

    private void parseKey(String line, ListIterator<String> it) throws UnknownKeyException, RepeatedKeyException, ParseException {
        String keyArg = line.substring(keyPrefix.length()); // key with argument
        int endKey = keyArg.indexOf(" ");
        String key = keyArg;
        String arg = null;
        if (endKey != -1) {
            key = keyArg.substring(0, endKey);
            int startArg = endKey + 1;
            if (startArg < keyArg.length()) {
                arg = keyArg.substring(startArg);
            }
        }
        try {
            GLOBAL_PROCESSORS.process(key, arg, it);
        } catch (UnknownKeyException ex) {
            if (section != null) {
                section.process(key, arg, it);
            } else {
                throw ex;
            }
        }
    }
}
