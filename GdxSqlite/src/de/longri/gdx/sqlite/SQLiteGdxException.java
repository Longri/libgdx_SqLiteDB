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

import com.badlogic.gdx.utils.GdxRuntimeException;

/**
 * Unchecked runtime exception for SQLite used in this extension.
 *
 * Created by Longri on 22.12.2017.
 */
public class SQLiteGdxException extends GdxRuntimeException {

    public SQLiteGdxException(String message) {
        super(message);
    }

    public SQLiteGdxException(Throwable t) {
        super(t);
    }

    public SQLiteGdxException(String message, Throwable t) {
        super(message, t);
    }

}
