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
 * Indicates that a one-shot key has been used a second time.
 *
 * @author Felix Wiemuth
 */
public class RepeatedKeyException extends IllegalKeyException {

    private final int firstOccurence;

    public RepeatedKeyException(int line, String key, int firstOccurence) {
        super(line, key, "The key \"" + key + "\" was already used at line " + firstOccurence);
        this.firstOccurence = firstOccurence;
    }

    public int getFirstOccurence() {
        return firstOccurence;
    }
}