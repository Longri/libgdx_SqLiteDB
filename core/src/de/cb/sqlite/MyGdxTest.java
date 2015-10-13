package de.cb.sqlite;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.io.File;

public class MyGdxTest extends ApplicationAdapter {
    Object[] listEntries = {"This is a list entry1", "And another one1", "The meaning of life1", "Is hard to come by1",
            "This is a list entry2", "And another one2", "The meaning of life2", "Is hard to come by2", "This is a list entry3",
            "And another one3", "The meaning of life3", "Is hard to come by3", "This is a list entry4", "And another one4",
            "The meaning of life4", "Is hard to come by4", "This is a list entry5", "And another one5", "The meaning of life5",
            "Is hard to come by5"};

    Skin skin;
    Stage stage;
    Texture texture1;
    Texture texture2;
    Label fpsLabel;
    private TextArea msgLabel;


    public void create() {
        skin = new Skin(Gdx.files.internal("data/uiskin.json"));
        stage = new Stage(new FitViewport(640, 480));
        Gdx.input.setInputProcessor(stage);

        //createDemoUI();
        createTestUI();
    }

    private void createTestUI() {

        //Button for close
        Button btnClose = new TextButton("CLOSE", skin);
        btnClose.setX((stage.getWidth() - btnClose.getWidth()) - 5);
        btnClose.setY(stage.getHeight() - btnClose.getHeight() - 5);
        btnClose.addListener(new ClickListener(Input.Buttons.LEFT) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        stage.addActor(btnClose);


        //Button for create DB
        Button btnCreateDB = new TextButton("Create\nDB", skin);
        btnCreateDB.setWidth(100);
        btnCreateDB.setX((stage.getWidth() - btnCreateDB.getWidth()) / 2);
        btnCreateDB.setY(stage.getHeight() - btnCreateDB.getHeight() - 10);
        btnCreateDB.addListener(new ClickListener(Input.Buttons.LEFT) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                createDB();
            }
        });
        stage.addActor(btnCreateDB);

        // Label for massage output
        msgLabel = new TextArea("this is some text.", skin);
        msgLabel.setWidth(stage.getWidth());
        msgLabel.setHeight(stage.getHeight() / 2);
        msgLabel.setAlignment(Align.topLeft);
        //msgLabel.setDisabled(true);
        msgLabel.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                msgLabel.setCursorPosition(msgLabel.getText().length());
                msgLabel.layout();
            }
        });
        stage.addActor(msgLabel);


        //save type selection
        final SelectBox selectBox = new SelectBox(skin);
        selectBox.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                writeMsg("Switch save type to: " + selectBox.getSelected());
            }
        });
        selectBox.setItems("InternSD", "ExtSD", "Sandbox");
        selectBox.setSelected("InternSD");
        selectBox.setWidth(150);
        selectBox.setY(btnCreateDB.getY() - selectBox.getHeight());
        stage.addActor(selectBox);
    }


    private void writeMsg(String msg) {
        msgLabel.setText(msgLabel.getText() + "\n" + msg);
        msgLabel.setCursorPosition(msgLabel.getText().length());
        msgLabel.layout();
    }


    private void createDemoUI() {
        texture1 = new Texture(Gdx.files.internal("data/badlogicsmall.jpg"));
        texture2 = new Texture(Gdx.files.internal("data/badlogic.jpg"));
        TextureRegion image = new TextureRegion(texture1);
        TextureRegion imageFlipped = new TextureRegion(image);
        imageFlipped.flip(true, true);
        TextureRegion image2 = new TextureRegion(texture2);

        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle(skin.get(Button.ButtonStyle.class));
        style.imageUp = new TextureRegionDrawable(image);
        style.imageDown = new TextureRegionDrawable(imageFlipped);
        ImageButton iconButton = new ImageButton(style);

        Button buttonMulti = new TextButton("Multi\nLine\nToggle", skin, "toggle");
        Button imgButton = new Button(new Image(image), skin);
        Button imgToggleButton = new Button(new Image(image), skin, "toggle");

        Label myLabel = new Label("this is some text.", skin);
        myLabel.setWrap(true);

        Table t = new Table();
        t.row();
        t.add(myLabel);

        t.layout();

        final CheckBox checkBox = new CheckBox(" Continuous rendering", skin);
        checkBox.setChecked(true);
        final Slider slider = new Slider(0, 10, 1, false, skin);
        slider.setAnimateDuration(0.3f);
        TextField textfield = new TextField("", skin);
        textfield.setMessageText("Click here!");
        textfield.setAlignment(Align.center);
        final SelectBox selectBox = new SelectBox(skin);
        selectBox.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                System.out.println(selectBox.getSelected());
            }
        });
        selectBox.setItems("Android1", "Windows1 long text in item", "Linux1", "OSX1", "Android2", "Windows2", "Linux2", "OSX2",
                "Android3", "Windows3", "Linux3", "OSX3", "Android4", "Windows4", "Linux4", "OSX4", "Android5", "Windows5", "Linux5",
                "OSX5", "Android6", "Windows6", "Linux6", "OSX6", "Android7", "Windows7", "Linux7", "OSX7");
        selectBox.setSelected("Linux6");
        Image imageActor = new Image(image2);
        ScrollPane scrollPane = new ScrollPane(imageActor);
        List list = new List(skin);
        list.setItems(listEntries);
        list.getSelection().setMultiple(true);
        list.getSelection().setRequired(false);
        // list.getSelection().setToggle(true);
        ScrollPane scrollPane2 = new ScrollPane(list, skin);
        scrollPane2.setFlickScroll(false);
        SplitPane splitPane = new SplitPane(scrollPane, scrollPane2, false, skin, "default-horizontal");
        fpsLabel = new Label("fps:", skin);

        // configures an example of a TextField in password mode.
        final Label passwordLabel = new Label("Textfield in password mode: ", skin);
        final TextField passwordTextField = new TextField("", skin);
        passwordTextField.setMessageText("password");
        passwordTextField.setPasswordCharacter('*');
        passwordTextField.setPasswordMode(true);

        buttonMulti.addListener(new TextTooltip("This is a tooltip!", skin));
        Table tooltipTable = new Table(skin);
        tooltipTable.pad(10).background("default-round");
        tooltipTable.add(new TextButton("Fancy tooltip!", skin));
        imgButton.addListener(new Tooltip(tooltipTable));

        // window.debug();
        Window window = new Window("Dialog", skin);
        window.getTitleTable().add(new TextButton("X", skin)).height(window.getPadTop());
        window.setPosition(0, 0);
        window.defaults().spaceBottom(10);
        window.row().fill().expandX();
        window.add(iconButton);
        window.add(buttonMulti);
        window.add(imgButton);
        window.add(imgToggleButton);
        window.row();
        window.add(checkBox);
        window.add(slider).minWidth(100).fillX().colspan(3);
        window.row();
        window.add(selectBox).maxWidth(100);
        window.add(textfield).minWidth(100).expandX().fillX().colspan(3);
        window.row();
        window.add(splitPane).fill().expand().colspan(4).maxHeight(200);
        window.row();
        window.add(passwordLabel).colspan(2);
        window.add(passwordTextField).minWidth(100).expandX().fillX().colspan(2);
        window.row();
        window.add(fpsLabel).colspan(4);
        window.pack();

        // stage.addActor(new Button("Behind Window", skin));
        stage.addActor(window);

        textfield.setTextFieldListener(new TextField.TextFieldListener() {
            public void keyTyped(TextField textField, char key) {
                if (key == '\n') textField.getOnscreenKeyboard().show(false);
            }
        });

        slider.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("UITest", "slider: " + slider.getValue());
            }
        });

        iconButton.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                new Dialog("Some Dialog with verry long Text   ....  ...  !", skin, "dialog") {
                    protected void result(Object object) {
                        System.out.println("Chosen: " + object);
                    }
                }.text("Are you enjoying this demo?").button("Yes", true).button("No", false).key(Input.Keys.ENTER, true)
                        .key(Input.Keys.ESCAPE, false).show(stage);
            }
        });

        checkBox.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.graphics.setContinuousRendering(checkBox.isChecked());
            }
        });

    }


    @Override
    public void render() {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (fpsLabel != null) fpsLabel.setText("fps: " + Gdx.graphics.getFramesPerSecond());

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        if (texture1 != null) texture1.dispose();
        if (texture2 != null) texture2.dispose();
        msgLabel = null;

    }


    //##################################################################################
    //      Database creation
    //##################################################################################
    public static final int LatestDatabaseChange = 1;

    private void createDB() {

        String path = "./testdata/test.db3";
        File databaseFile=new File(path);

        writeMsg("Create DB instance on " + databaseFile.getAbsolutePath());
        TestDB test = new TestDB(DatabaseFactory.getInstanz(path, alternate));

        writeMsg("Reset DB");
        test.Reset();

        writeMsg("DB Start up");
        test.StartUp();

        if(databaseFile.exists()){
           writeMsg("DB File exists");
        }else{
            writeMsg("ERROR DB File not exists");
        }

        writeMsg("Databaseschema version:" + test.db.GetDatabaseSchemeVersion());

        writeMsg("close DB");
        test.Close();

        writeMsg("");

        writeMsg("ALTERNATE DB create table");

        writeMsg("Create DB instance on " + databaseFile.getAbsolutePath());
        TestDB test2 = new TestDB(DatabaseFactory.getInstanz(path, alternate2));

        writeMsg("DB Start up");
        test2.StartUp();

        if(databaseFile.exists()){
            writeMsg("DB File exists");
        }else{
            writeMsg("ERROR DB File not exists");
        }
        writeMsg("Databaseschema version:" + test2.db.GetDatabaseSchemeVersion());

        writeMsg("write data");
        test2.db.beginTransaction();

        Parameters para = new Parameters();
        para.put("TestId", 0);
        para.put("TEXT", "testText");
        test2.db.insert("TESTTABLE", para);
        test2.db.endTransaction();

        //read
        writeMsg("read data");
        String resultString = "";
        CoreCursor c = test2.db.rawQuery("select TEXT from TESTTABLE where TestId=?", new String[]
                { String.valueOf(0) });
        c.moveToFirst();
        while (c.isAfterLast() == false)
        {
            resultString = c.getString(0);
            break;
        }

        if(resultString.equals("testText")){
            writeMsg("Result Ok");
        }else writeMsg("ERROR wrong result" + resultString);
        writeMsg("close DB");
        test2.Close();


    }

    private AlternateDatabase alternate = new AlternateDatabase() {
        @Override
        public void alternateDatabase(SQLite db, int databaseSchemeVersion) {

        }

        @Override
        public int databaseSchemeVersion() {
            return 0;
        }
    };

    private AlternateDatabase alternate2 = new AlternateDatabase() {
        @Override
        public void alternateDatabase(SQLite db, int databaseSchemeVersion) {
                if(databaseSchemeVersion<1){
                    db.execSQL("CREATE TABLE [TESTTABLE] ([Id] integer not null primary key autoincrement, [TestId] bigint NULL, [TEXT] nvarchar (12) NULL);");
                }
        }

        @Override
        public int databaseSchemeVersion() {
            return 1;
        }
    };
}
