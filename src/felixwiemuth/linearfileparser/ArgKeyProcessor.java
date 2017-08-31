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

import java.util.ListIterator;

/**
 * A key processor requiring an argument. This class automatically checks
 * whether the given argument to a key is null (in which case it throws
 * {@link MissingArgumentException}) before calling the process method of the
 * implementing class.
 *
 * @author Felix Wiemuth
 */
public abstract class ArgKeyProcessor extends LinearFileParser.KeyProcessor {

    /**
     *
     * @param key the key to be processed
     * @param oneShot whether this key may only be used once in the section of
     * this processor - when used a second time, {@link RepeatedKeyException} is
     * thrown
     */
    public ArgKeyProcessor(String key, boolean oneShot) {
        super(key, oneShot);
    }

    /**
     *
     * @param key the key to be processed
     */
    public ArgKeyProcessor(String key) {
        super(key);
    }

    @Override
    public void process(String arg, ListIterator<String> it) throws MissingArgumentException, ParseException {
        if (arg == null) {
            MissingArgumentException ex = new MissingArgumentException(it.nextIndex(), key);
            ex.setResourceProvider(getRp());
            throw ex;
        }
        _process(arg, it);
    }

    /**
     * Process the key at the current line.
     *
     * @param arg the argument given with the key which is guaranteed to be not
     * null when this method is called (otherwise
     * {@link MissingArgumentException} is thrown)
     * @param it iterator over the list of lines, pointing to the line after the
     * current - the iterator can be used to modify the list or the current
     * position and the parser will continue with this same iterator
     * @throws ParseException
     */
    public abstract void _process(String arg, ListIterator<String> it) throws ParseException;
}
