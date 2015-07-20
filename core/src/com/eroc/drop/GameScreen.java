package com.eroc.drop;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class GameScreen implements Screen {
    final Drop game;

    private ParticleEffect effect;
    private SpriteBatch batchE;


    Texture dropImage;
    Texture bucketImage;
    Texture bground1;
    Texture bground2;
    Texture bground3;
    Texture bground4;
    Texture bground5;
    Texture bground6;
    Sound dropSound1;
    Sound dropSound2;
    Sound dropSound3;
    Sound dropSound4;
    Music killingspree;
    Music rainMusic;
    Music pancake;
    Music dominating;
    Music unstoppable;
    Music godlike;
    OrthographicCamera camera;
    Rectangle bucket;
    Array<Rectangle> raindrops;
    long lastDropTime;
    int dropsGathered = 99;
    int rand;

    Preferences prefs = Gdx.app.getPreferences("leaderboard");
    Integer score = prefs.getInteger("highscore", 0);

    public GameScreen(final Drop gam){
        this.game = gam;

        dropImage = new Texture(Gdx.files.internal("dablet.png"));
        bucketImage = new Texture(Gdx.files.internal("nail.png"));
        bground1= new Texture(Gdx.files.internal("bground1.jpg"));
        bground2 = new Texture(Gdx.files.internal("bground2.jpg"));
        bground3 = new Texture(Gdx.files.internal("bground3.jpg"));
        bground4 = new Texture(Gdx.files.internal("bground4.jpg"));
        bground5 = new Texture(Gdx.files.internal("bground5.jpg"));
        bground6 = new Texture(Gdx.files.internal("bground6.jpg"));


        dropSound1 = Gdx.audio.newSound(Gdx.files.internal("synth1.wav"));
        dropSound2 = Gdx.audio.newSound(Gdx.files.internal("synth1.wav"));
        dropSound3 = Gdx.audio.newSound(Gdx.files.internal("synth4.wav"));
        dropSound4 = Gdx.audio.newSound(Gdx.files.internal("synth4.wav"));

        killingspree = Gdx.audio.newMusic(Gdx.files.internal("killingspree.mp3"));
        pancake = Gdx.audio.newMusic(Gdx.files.internal("pancake.mp3"));
        unstoppable = Gdx.audio.newMusic(Gdx.files.internal("unstoppable.mp3"));
        dominating = Gdx.audio.newMusic(Gdx.files.internal("dominating.mp3"));
        godlike = Gdx.audio.newMusic(Gdx.files.internal("godlike.mp3"));

        rainMusic = Gdx.audio.newMusic(Gdx.files.internal("thebest.mp3"));
        rainMusic.setLooping(true);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);

        bucket = new Rectangle();
        bucket.x = 800/2 - 64/2;
        bucket.y = 20;

        bucket.width = 64;
        bucket.height = 64;

        raindrops = new Array<Rectangle>();
        spawnRaindrop();
    }

    private void spawnRaindrop(){
        Rectangle raindrop = new Rectangle();
        raindrop.x = MathUtils.random(0, 800 - 64);
        raindrop.y = 480;
        raindrop.width = 64;
        raindrop.height = 64;
        raindrops.add(raindrop);
        lastDropTime = TimeUtils.nanoTime();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0.2f,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();


        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        if(dropsGathered < 75){
            game.batch.draw(bground1, 0, 0);
        }
        if(dropsGathered < 100 && dropsGathered >=75 ){
            game.batch.draw(bground2, 0, 0);
        }
        if(dropsGathered < 125 && dropsGathered >=100 ){
            game.batch.draw(bground3, 0, 0);
        }
        if(dropsGathered < 150 && dropsGathered >=125 ){
            game.batch.draw(bground4, 0, 0);
        }
        if(dropsGathered < 200 && dropsGathered >=150 ){
            game.batch.draw(bground5, 0, 0);
        }
        if(dropsGathered >=200 ){
            game.batch.draw(bground6, 0, 0);
        }
        game.font.draw(game.batch, "Dabs Collected: " + dropsGathered, 5, 480);
        game.font.draw(game.batch, "Highscore: " + prefs.getInteger("highscore"), 680, 480);
        game.batch.draw(bucketImage, bucket.x, bucket.y);
        for (Rectangle raindrop: raindrops){
            game.batch.draw(dropImage, raindrop.x, raindrop.y);
        }
        game.batch.end();

        batchE.begin();
        effect.draw(batchE,delta);
        batchE.end();


        if(Gdx.input.isTouched()){
            Vector3 touchPos = new Vector3();
            Vector3 bucketPos = new Vector3();
            bucketPos.set(bucket.getX()+ 64/2,bucket.getY() + 64, 0);
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            bucket.x = touchPos.x - 64 /2;
            camera.project(bucketPos);
            effect.setPosition(bucketPos.x, bucketPos.y );
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {

            bucket.x -= 200 * Gdx.graphics.getDeltaTime();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            bucket.x += 200 * Gdx.graphics.getDeltaTime();

        }
        if (bucket.x < 0)
            bucket.x = 0;
        if (bucket.x > 800 - 64)
            bucket.x = 800 - 64;

        if (TimeUtils.nanoTime() - lastDropTime > 1000000000/(dropsGathered/100 +1) )
            spawnRaindrop();

        Iterator<Rectangle> iter = raindrops.iterator();
        while (iter.hasNext()) {
            Rectangle raindrop =  iter.next();
            raindrop.y -= (dropsGathered*0.35 + 200) * Gdx.graphics.getDeltaTime();
            if (raindrop.y + 64 < 0){
                iter.remove();
                dropsGathered = 0;
            }
            if (raindrop.overlaps(bucket)){
                effect.start();
                dropsGathered++;
               /* rand = 1 + (int)(Math.random() * ((4 - 1) + 1));
                if (rand == 1) {
                    dropSound1.play();
                }else if (rand == 2) {
                    dropSound2.play();
                }else if (rand == 3) {
                    dropSound3.play();
                }else if (rand == 4) {
                    dropSound4.play();
                }*/
                iter.remove();
            }
        }

        if(dropsGathered > prefs.getInteger("highscore")){
            prefs.putInteger("highscore", dropsGathered);
        }
        if(dropsGathered == 75){
            killingspree.play();
        }
        if(dropsGathered == 100){
            dominating.play();
        }
        if(dropsGathered == 125){
            unstoppable.play();
        }
        if(dropsGathered == 150){
            godlike.play();
        }
        if(dropsGathered == 200){
            pancake.play();
        }
        prefs.flush();

    }

    @Override
    public void show() {
        rainMusic.play();
        batchE = new SpriteBatch();
        effect = new ParticleEffect();
        effect.load(Gdx.files.internal("smoke.p"), Gdx.files.internal("effects"));
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        dropImage.dispose();
        bucketImage.dispose();
        dropSound1.dispose();
        dropSound2.dispose();
        dropSound3.dispose();
        dropSound4.dispose();
        killingspree.dispose();
        dominating.dispose();
        pancake.dispose();
        unstoppable.dispose();
        godlike.dispose();
        rainMusic.dispose();
        effect.dispose();
        bground1.dispose();
        bground2.dispose();
        bground3.dispose();
        bground4.dispose();
        bground5.dispose();
        bground6.dispose();
    }
}
