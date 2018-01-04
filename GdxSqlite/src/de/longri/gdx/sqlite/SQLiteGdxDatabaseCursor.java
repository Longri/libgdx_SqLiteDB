/*
 * Copyright (C) 2017 team-cachebox.de
 *
 * Licensed under the : GNU General  License (GPL);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.longri.gdx.sqlite;


/**
 * This  interface contains all the methods to expose results from a query on a SQLiteDatabase.
 * This is not thread-safe.
 *
 *
 * Created by Longri on 22.12.2017.
 */
 interface SQLiteGdxDatabaseCursor {

    /**
     * Returns the value of the requested column as a byte array.
     *
     * @param columnIndex the zero-based index of the target column.
     * @return the value of that column as a byte array.
     */
     byte[] getBlob(int columnIndex);

    /**
     * Returns the value of the requested column as a double.
     *
     * @param columnIndex the zero-based index of the target column.
     * @return the value of that column as a double.
     */
     double getDouble(int columnIndex);

    /**
     * Returns the value of the requested column as a float.
     *
     * @param columnIndex the zero-based index of the target column.
     * @return the value of that column as a float.
     */
     float getFloat(int columnIndex);

    /**
     * Returns the value of the requested column as a int.
     *
     * @param columnIndex the zero-based index of the target column.
     * @return the value of that column as a int.
     */
     int getInt(int columnIndex);

    /**
     * Returns the value of the requested column as a long.
     *
     * @param columnIndex the zero-based index of the target column.
     * @return the value of that column as a long.
     */
     long getLong(int columnIndex);

    /**
     * Returns the value of the requested column as a short.
     *
     * @param columnIndex the zero-based index of the target column.
     * @return the value of that column as a short.
     */
     short getShort(int columnIndex);

    /**
     * Returns the value of the requested column as a string.
     *
     * @param columnIndex the zero-based index of the target column.
     * @return the value of that column as a string.
     */
     String getString(int columnIndex);

    /**
     * Move the cursor to the next row.
     *
     * @return whether the move was successful.
     */
     boolean next();

    /**
     * Returns the numbers of rows in the cursor.
     *
     * @return number of rows
     * @throws SQLiteGdxException
     */
     int getCount();

    /**
     * Closes the Cursor, releasing all of its resources and making it completely invalid.
     */
     void close();

    /**
     * Shifts the cursor position to the first row.
     */
     void moveToFirst();

    /**
     * Gets if the cursor is after the last row.
     *
     * @return {@code true} if the cursor is after the last row,
     * {@code false} if the cursor is at any other position.
     */
     boolean isAfterLast();

    /**
     * Move the cursor to the next row.
     */
     void moveToNext();

    /**
     * Returns TRUE if the value of the requested column are NULL
     */
     boolean isNull(int columnIndex);
}
