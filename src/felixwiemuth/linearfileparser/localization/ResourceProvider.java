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
package felixwiemuth.linearfileparser.localization;

/**
 * An interface to allow localization of the library by providing resources (for
 * now only String resources). Can be used with any existing localization
 * framework (e.g. Android resources) by implementing an adapter. The adapter
 * should map {@link R}s to resources of the other localization framework. If
 * the latter uses Strings as keys, to ensure uniqueness use {@link R#name()
 * } prefixed by "felixwiemuth.linearfileparser" as key.
 *
 * @author Felix Wiemuth
 */
public interface ResourceProvider {

    String getString(R key);

}
