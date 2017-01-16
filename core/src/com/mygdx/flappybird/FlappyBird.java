package com.mygdx.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Random;

class FlappyBird extends ApplicationAdapter {

    private SpriteBatch batch;
    private Texture[] bird;
    private Texture background;
    private Texture lowerPipe;
    private Texture upperPipe;
    private Texture gameOver;
    private Random randomValue;
    private BitmapFont font;
    private BitmapFont message;
    private Circle birdCircle;
    private Rectangle upperPipeRectangle;
    private Rectangle lowerPipeRectangle;
    //private ShapeRenderer shape;

    private float width;
    private float height;
    private int gamestate = 0; // 0 = off, 1 = on, 2 = game over
    private int score = 0;

    private float variance = 0;
    private float fallSpeed = 0;
    private float verticalStartPos;
    private float posMovementPipeHorizontal;
    private float spaceBetweenPipes;
    private float spaceBetweenPipesRandom;
    private boolean scored;

    private OrthographicCamera camera;
    private Viewport viewport;
    private final float VIRTUAL_WIDTH = 768;
    private final float VIRTUAL_HEIGHT = 1024;


    @Override
    public void create() {
        batch = new SpriteBatch();
        randomValue = new Random();
        birdCircle = new Circle();
        lowerPipeRectangle = new Rectangle();
        upperPipeRectangle = new Rectangle();
        //shape = new ShapeRenderer();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(6);

        message = new BitmapFont();
        message.setColor(Color.WHITE);
        message.getData().setScale(3);

        bird = new Texture[3];
        bird[0] = new Texture("passaro1.png");
        bird[1] = new Texture("passaro2.png");
        bird[2] = new Texture("passaro3.png");

        background = new Texture("fundo.png");
        lowerPipe = new Texture("cano_baixo.png");
        upperPipe = new Texture("cano_topo.png");
        gameOver = new Texture("game_over.png");

        camera = new OrthographicCamera();
        camera.position.set(VIRTUAL_WIDTH/2, VIRTUAL_HEIGHT/2, 0);
        viewport = new StretchViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);

        width = VIRTUAL_WIDTH;
        height = VIRTUAL_HEIGHT;

        verticalStartPos = height / 2;
        posMovementPipeHorizontal = width;
        spaceBetweenPipes = 400;
    }

    @Override
    public void render() {

        camera.update();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        float deltaTime = Gdx.graphics.getDeltaTime();
        variance += deltaTime * 10;
        if (variance > 2) variance = 0;

        if (gamestate == 0) {
            if (Gdx.input.justTouched()) {
                gamestate = 1;
            }
        } else {
            fallSpeed++;
            if (verticalStartPos > 0 || fallSpeed < 0) {
                verticalStartPos -= fallSpeed;
            }

            if (gamestate == 1) {
                posMovementPipeHorizontal -= deltaTime * 200;

                if (Gdx.input.justTouched()) {
                    fallSpeed = -20;
                }

                if (posMovementPipeHorizontal < -lowerPipe.getWidth()) {
                    posMovementPipeHorizontal = width;
                    spaceBetweenPipesRandom = randomValue.nextInt(400) - 200;
                    scored = false;
                }

                if (posMovementPipeHorizontal < width / 2) {
                    if (!scored) {
                        score++;
                        scored = true;
                    }
                }
            } else {
                if (Gdx.input.justTouched()) {
                    gamestate = 0;
                    score = 0;
                    fallSpeed = 0;
                    verticalStartPos = height / 2;
                    posMovementPipeHorizontal = width;
                    scored = false;
                }
            }
        }

        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        batch.draw(background, 0, 0, width, height);
        batch.draw(lowerPipe, posMovementPipeHorizontal, height / 2 - spaceBetweenPipes / 2 - lowerPipe.getHeight() + spaceBetweenPipesRandom);
        batch.draw(upperPipe, posMovementPipeHorizontal, height / 2 + spaceBetweenPipes / 2 + spaceBetweenPipesRandom);
        batch.draw(bird[(int) variance], width / 2, verticalStartPos);
        font.draw(batch, String.valueOf(score), width / 2, height - 50);

        if (gamestate == 2) {
            batch.draw(gameOver, width / 2 - gameOver.getWidth() / 2, height / 2 - gameOver.getHeight() / 2);
            message.draw(batch, "Touch to play again", width / 2 - 200, height / 2 - gameOver.getHeight() / 2);
        }

        batch.end();

        birdCircle.set(width / 2 + bird[0].getWidth() / 2, verticalStartPos + bird[0].getHeight() / 2, bird[0].getWidth() / 2);
        lowerPipeRectangle.set(posMovementPipeHorizontal,
                height / 2 - spaceBetweenPipes / 2 - lowerPipe.getHeight() + spaceBetweenPipesRandom,
                lowerPipe.getWidth(), lowerPipe.getHeight());
        upperPipeRectangle.set(posMovementPipeHorizontal,
                height / 2 + spaceBetweenPipes / 2 + spaceBetweenPipesRandom,
                upperPipe.getWidth(), upperPipe.getHeight());


        /*
        shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.circle(birdCircle.x, birdCircle.y, birdCircle.radius);
        shape.rect(lowerPipeRectangle.x, lowerPipeRectangle.y, lowerPipeRectangle.width, lowerPipeRectangle.height);
        shape.rect(upperPipeRectangle.x, upperPipeRectangle.y, upperPipeRectangle.width, upperPipeRectangle.height);
        shape.setColor(Color.RED);
        shape.end();
        */

        if (Intersector.overlaps(birdCircle, lowerPipeRectangle) || Intersector.overlaps(birdCircle, upperPipeRectangle)
                || verticalStartPos <= 0 || verticalStartPos >= height) {
            gamestate = 2;
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void dispose() {
    }
}
