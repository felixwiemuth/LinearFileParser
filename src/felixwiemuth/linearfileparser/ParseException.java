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

/**
 * Indicates an error while parsing due to a not properly formatted source file
 * (syntax error).
 *
 * @author Felix Wiemuth
 */
public class ParseException extends Exception {

    private final int line;

    /**
     *
     * @param line the line number in the source file which is not properly
     * formatted
     */
    public ParseException(int line) {
        this.line = line;
    }

    /**
     *
     * @param line the line number in the source file which is not properly
     * formatted
     * @param msg a detailed message of the syntax error
     */
    public ParseException(int line, String msg) {
        super(msg);
        this.line = line;
    }

    public int getLine() {
        return line;
    }

    /**
     * Returns the detail message prefixed with the line number.
     *
     * @return
     */
    @Override
    public String getMessage() {
        return buildMessage(super.getMessage());
    }

    /**
     * Construct an error message including the line number and an optional
     * additional message.
     *
     * @param msg
     * @return
     */
    protected String buildMessage(String msg) {
        StringBuilder sb = new StringBuilder();
        sb.append("Error at line ").append(line);
        if (msg != null) {
            sb.append(": ").append(msg);
        }
        return sb.toString();
    }

}
