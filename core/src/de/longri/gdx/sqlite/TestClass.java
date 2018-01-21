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

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.async.AsyncExecutor;
import com.badlogic.gdx.utils.async.AsyncTask;
import de.longri.gdx.sqlite.tests.TestMain;

/**
 * Created by Longri on 18.12.2017.
 */
public class TestClass extends ApplicationAdapter {
    SpriteBatch batch;
    Texture img;

    int state = -1;
    Color actBackColor = Color.BLUE;
    private static final AsyncExecutor asyncExecutor = new AsyncExecutor(50);


    @Override
    public void create() {
        batch = new SpriteBatch();
        img = new Texture("badlogic.jpg");

        postAsync(new Runnable() {
            @Override
            public void run() {
                state = 0;

                if (TestMain.runTests(
                        GdxSqliteTest.class,
                        GdxSqlitePreparedStatementTest.class,
                        GdxSqliteTransactionTest.class,
                        CB_DB_Tests.class,
                        GdxSqliteCursorTest.class,
                        MalformedExceptionTest.class)) {
                    state = 1;
                } else {
                    state = 2;
                }
            }
        });

    }


    @Override
    public void render() {
        switch (state) {
            case 0:
                actBackColor = Color.YELLOW;
                break;
            case 1:
                actBackColor = Color.GREEN;
                break;
            case 2:
                actBackColor = Color.RED;
                break;
        }
        Gdx.gl.glClearColor(actBackColor.r, actBackColor.g, actBackColor.b, actBackColor.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(img, 0, 0);
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        img.dispose();
    }

    public static void postAsync(final Runnable runnable) {
        asyncExecutor.submit(new AsyncTask<Void>() {
            @Override
            public Void call() {
                try {
                    runnable.run();
                } catch (final Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
    }
}
