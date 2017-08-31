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

import felixwiemuth.linearfileparser.localization.R;

/**
 * Indicates an attempt to switch to a section which was not defined.
 *
 * @author Felix Wiemuth
 */
public class UnknownSectionException extends ParseException {

    private final String sectionID;

    public UnknownSectionException(int line, String sectionID) {
        super(line);
        this.sectionID = sectionID;
    }

    public String getSectionID() {
        return sectionID;
    }

    @Override
    protected String getMsg() {
        return String.format(getRp().getString(R.UNKNOWN_SECTION), sectionID);
    }

}
