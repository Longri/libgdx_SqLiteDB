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


import com.badlogic.gdx.utils.Array;

/**
 * Created by Longri on 22.12.2017.
 */
public class GdxSqliteCursor {

    private Array<Object[]> valueRows = new Array<>();
    private int actRow = 0;
    private Object[] actRowValues;

    private void chkCursorposition() {
        if (actRowValues == null)
            throw new SQLiteGdxException("Invalid cursor position, try moveToFirst");
    }


    /**
     * Returns the value of the requested column as a byte array.
     *
     * @param columnIndex the zero-based index of the target column.
     * @return the value of that column as a byte array.
     */
    public byte[] getBlob(int columnIndex) {
        chkCursorposition();
        if (isNull(columnIndex)) return null;
        return (byte[]) actRowValues[columnIndex];
    }

    /**
     * Returns the value of the requested column as a double.
     *
     * @param columnIndex the zero-based index of the target column.
     * @return the value of that column as a double.
     */
    public double getDouble(int columnIndex) {
        chkCursorposition();
        if (isNull(columnIndex)) return 0.0;
        return (double) actRowValues[columnIndex];
    }

    /**
     * Returns the value of the requested column as a float.
     *
     * @param columnIndex the zero-based index of the target column.
     * @return the value of that column as a float.
     */
    public float getFloat(int columnIndex) {
        chkCursorposition();
        if (isNull(columnIndex)) return 0.0f;
        return ((Double) actRowValues[columnIndex]).floatValue();
    }

    /**
     * Returns the value of the requested column as a int.
     *
     * @param columnIndex the zero-based index of the target column.
     * @return the value of that column as a int.
     */
    public int getInt(int columnIndex) {
        chkCursorposition();
        if (isNull(columnIndex)) return 0;
        return ((Long) actRowValues[columnIndex]).intValue();
    }

    /**
     * Returns the value of the requested column as a long.
     *
     * @param columnIndex the zero-based index of the target column.
     * @return the value of that column as a long.
     */
    public long getLong(int columnIndex) {
        chkCursorposition();
        if (isNull(columnIndex)) return 0;
        return (long) actRowValues[columnIndex];
    }

    /**
     * Returns the value of the requested column as a short.
     *
     * @param columnIndex the zero-based index of the target column.
     * @return the value of that column as a short.
     */
    public short getShort(int columnIndex) {
        chkCursorposition();
        if (isNull(columnIndex)) return 0;
        return ((Long) actRowValues[columnIndex]).shortValue();
    }

    /**
     * Returns the value of the requested column as a boolean. <br><br>
     * TRUE if int value > 0 <br>
     * FALSE if int value == 0 <br>
     *
     * @param columnIndex the zero-based index of the target column.
     * @return the value of that column as a boolean.
     */
    public boolean getBoolean(int columnIndex) {
        return getInt(columnIndex) > 0;
    }

    /**
     * Returns the value of the requested column as a string.
     *
     * @param columnIndex the zero-based index of the target column.
     * @return the value of that column as a string.
     */
    public String getString(int columnIndex) {
        chkCursorposition();
        if (isNull(columnIndex)) return null;
        return (String) actRowValues[columnIndex];
    }

    /**
     * Returns TRUE if the value of the requested column are NULL
     */
    public boolean isNull(int columnIndex) {
        return actRowValues[columnIndex] == null;
    }

    /**
     * Move the cursor to the next row.
     *
     * @return whether the move was successful.
     */
    public boolean next() {
        this.actRow++;
        boolean re = !isAfterLast();
        if (re)
            actRowValues = valueRows.get(this.actRow);
        return re;
    }

    /**
     * Returns the numbers of rows in the cursor.
     *
     * @return number of rows
     * @throws SQLiteGdxException
     */
    public int getCount() {
        return valueRows.size;
    }

    /**
     * Closes the Cursor, releasing all of its resources and making it completely invalid.
     */
    public void close() {
        actRowValues = null;
        for (int i = 0; i < valueRows.size; i++) {
            valueRows.pop(); // with pop the Array item will set to NULL!
        }
        valueRows.clear();
        valueRows = null;
    }

    /**
     * Shifts the cursor position to the first row.
     */
    public void moveToFirst() {
        this.actRow = 0;
        actRowValues = valueRows.get(this.actRow);
    }

    /**
     * Gets if the cursor is after the last row.
     *
     * @return {@code true} if the cursor is after the last row,
     * {@code false} if the cursor is at any other position.
     */
    public boolean isAfterLast() {
        return this.actRow >= valueRows.size;
    }

    /**
     * Move the cursor to the next row.
     */
    public void moveToNext() {
        this.actRow++;
        if (!isAfterLast())
            actRowValues = valueRows.get(this.actRow);

    }


    void addRow(String[] columnNames, Object[] values) {
        //TODO store column names

        // the Object array is every time the same, so we must copy the values
        int length = values.length;
        Object[] newValues = new Object[values.length];
        System.arraycopy(values, 0, newValues, 0, length);
        valueRows.add(newValues);
    }


}
