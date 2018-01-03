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
 * SQLite native result
 * <p>
 * <p>
 * Created by Longri on 30.12.2017.
 */
public class GdxSqliteResult {

    public long ptr;
    public int retValue;
    public String errorMsg;

    public GdxSqliteResult() {
    }

    public void setValues(long ptr, int retValue, String errorMsg) {
        this.ptr = ptr;
        this.retValue = retValue;
        this.errorMsg = errorMsg;
    }

    public GdxSqliteResult(long ptr, int retValue, String errorMsg) {
        this.ptr = ptr;
        this.retValue = retValue;
        this.errorMsg = errorMsg;
    }

}
