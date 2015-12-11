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

/**
 * Indicates that a line is neither a comment, nor specifies a section or a key
 * and the default processor did not accept the line.
 *
 * @author Felix Wiemuth
 */
public class IllegalLineException extends ParseException {

    public IllegalLineException(int line) {
        super(line, "Illegal start of line (expected comment, section or key).");
    }
}
