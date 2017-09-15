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
import felixwiemuth.linearfileparser.localization.ResourceProvider;

/**
 * Indicates an error while parsing due to a not properly formatted source file
 * (syntax error). Messages are only constructed when calling {@link #getMessage()
 * } (which includes reading resources). Messages requiring {@link #getRp() }
 * are specified by overriding {@link #getMsg()
 * }, if this is not overridden the usual exception message is used. IMPORTANT:
 * Every {@link ParseException} thrown outside a
 * {@link felixwiemuth.linearfileparser.LinearFileParser.KeyProcessor} must be
 * passed to {@link LinearFileParser#setupException(felixwiemuth.linearfileparser.ParseException)
 * }. For convenience use {@link LinearFileParser#newParseException(int, java.lang.String)
 * }.
 *
 * @author Felix Wiemuth
 */
public class ParseException extends Exception {

    private ResourceProvider rp;
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

    public void setResourceProvider(ResourceProvider rp) {
        this.rp = rp;
    }

    /**
     * For library-internal use only: get the {@link ResourceProvider} set by
     * {@link LinearFileParser}.
     *
     * @return
     */
    protected ResourceProvider getRp() {
        return rp;
    }

    public int getLine() {
        return line;
    }

    /**
     * Returns the detail message prefixed with the {@link R#ERROR_AT_LINE}
     * string and the line number.
     *
     * @return
     */
    @Override
    public String getMessage() {
        return buildMessage(getMsg());
    }

    /**
     * Construct an error message including the line number and an optional
     * additional message.
     *
     * @param msg
     * @return
     */
    private String buildMessage(String msg) {
        StringBuilder sb = new StringBuilder();
        sb.append(rp.getString(R.ERROR_AT_LINE)).append(line);
        if (msg != null) {
            sb.append(": ").append(msg);
        }
        return sb.toString();
    }

    /**
     * Get the message to be displayed. Default is {@link Exception#getMessage()
     * }.
     *
     * @return
     */
    protected String getMsg() {
        return super.getMessage();
    }
}
