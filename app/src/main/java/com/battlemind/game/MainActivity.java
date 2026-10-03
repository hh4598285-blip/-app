package com.battlemind.game;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends Activity {
    private GameView game;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setNavigationBarColor(Color.rgb(8, 11, 20));
        getWindow().setStatusBarColor(Color.rgb(8, 11, 20));
        game = new GameView();
        setContentView(game);
    }

    private class GameView extends View {
        private final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Random random = new Random();
        private final ArrayList<Unit> units = new ArrayList<>();

        private static final float DESIGN_W = 360f;
        private static final float DESIGN_H = 760f;
        private int screen = 0;
        private int score = 0;
        private int energy = 100;
        private long lastSpawn = 0L;

        private final int cyan = Color.rgb(0, 229, 255);
        private final int pink = Color.rgb(255, 64, 129);
        private final int bg = Color.rgb(7, 10, 20);

        GameView() {
            super(MainActivity.this);
            p.setTypeface(Typeface.create("sans", Typeface.BOLD));
            setFocusable(true);
        }

        private float sx() { return getWidth() / DESIGN_W; }
        private float sy() { return getHeight() / DESIGN_H; }
        private float s() { return Math.min(sx(), sy()); }

        private float X(float v) { return v * sx(); }
        private float Y(float v) { return v * sy(); }

        @Override
        protected void onDraw(Canvas c) {
            super.onDraw(c);
            c.drawColor(bg);
            if (screen == 0) drawMenu(c);
            else drawBattle(c);

            if (screen == 1) {
                long now = System.currentTimeMillis();
                if (now - lastSpawn >= 900 && units.size() < 16) {
                    float minX = X(38), maxX = X(322);
                    float minY = Y(140), maxY = Y(610);
                    float ux = minX + random.nextFloat() * Math.max(1, maxX - minX);
                    float uy = minY + random.nextFloat() * Math.max(1, maxY - minY);
                    units.add(new Unit(ux, uy, random.nextBoolean()));
                    lastSpawn = now;
                }
                postInvalidateDelayed(32);
            }
        }

        private void drawText(Canvas c, String value, float x, float y, float size, int color) {
            p.setTextSize(size * s());
            p.setColor(color);
            p.setStyle(Paint.Style.FILL);
            c.drawText(value, X(x), Y(y), p);
        }

        private void drawMenu(Canvas c) {
            drawText(c, "BATTLEMIND", 32, 72, 34, Color.WHITE);
            drawText(c, "TACTICS • COMBAT • LOGIC", 34, 98, 11, cyan);

            drawCard(c, 24, 145, 336, 250, "TACTICAL ARENA",
                    "Real-time strategy challenge", cyan);
            drawCard(c, 24, 268, 336, 373, "BRAIN STRIKE",
                    "Solve under pressure", pink);
            drawCard(c, 24, 391, 336, 496, "COMMANDER MODE",
                    "Build • defend • conquer", Color.YELLOW);

            drawText(c, "Designed for strategic minds", 34, 550, 12, Color.LTGRAY);
            drawText(c, "v1.1 • OFFLINE • ANDROID 11+", 34, 575, 10, Color.GRAY);
        }

        private void drawCard(Canvas c, float left, float top, float right, float bottom,
                              String title, String subtitle, int accent) {
            p.setStyle(Paint.Style.FILL);
            p.setColor(Color.rgb(18, 23, 38));
            c.drawRoundRect(X(left), Y(top), X(right), Y(bottom), X(20), X(20), p);

            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(Math.max(2f, X(2)));
            p.setColor(accent);
            c.drawRoundRect(X(left), Y(top), X(right), Y(bottom), X(20), X(20), p);

            drawText(c, title, left + 18, top + 43, 18, Color.WHITE);
            drawText(c, subtitle, left + 18, top + 70, 11, Color.LTGRAY);
        }

        private void drawBattle(Canvas c) {
            drawText(c, "TACTICAL ARENA", 18, 34, 18, Color.WHITE);
            drawText(c, "SCORE " + score, 18, 57, 11, cyan);
            drawText(c, "ENERGY " + energy, 266, 57, 11, Color.YELLOW);

            p.setStyle(Paint.Style.FILL);
            p.setColor(Color.rgb(14, 20, 34));
            c.drawRoundRect(X(12), Y(78), X(348), Y(660), X(22), X(22), p);

            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(Math.max(1f, X(1)));
            p.setColor(Color.rgb(35, 50, 72));
            for (int gx = 24; gx < 348; gx += 36) {
                c.drawLine(X(gx), Y(94), X(gx), Y(644), p);
            }
            for (int gy = 100; gy < 644; gy += 36) {
                c.drawLine(X(20), Y(gy), X(340), Y(gy), p);
            }

            p.setStyle(Paint.Style.FILL);
            for (Unit u : units) {
                p.setColor(u.enemy ? pink : cyan);
                c.drawCircle(u.x, u.y, X(12), p);
                p.setColor(Color.WHITE);
                c.drawCircle(u.x, u.y, X(4), p);
            }

            p.setColor(Color.rgb(20, 27, 45));
            c.drawRoundRect(X(14), Y(685), X(346), Y(738), X(18), X(18), p);
            drawText(c, "TAP TO DEPLOY UNIT", 34, 718, 12, Color.WHITE);
            drawText(c, "HOME", 286, 718, 11, cyan);
        }

        @Override
        public boolean onTouchEvent(MotionEvent e) {
            if (e.getAction() != MotionEvent.ACTION_UP) return true;

            float dx = e.getX() / Math.max(0.001f, sx());
            float dy = e.getY() / Math.max(0.001f, sy());

            if (screen == 0) {
                if (dy >= 140 && dy < 250) startMode(0);
                else if (dy >= 268 && dy < 373) startMode(1);
                else if (dy >= 391 && dy < 496) startMode(2);
            } else {
                if (dy >= 670) {
                    screen = 0;
                    units.clear();
                    postInvalidate();
                    return true;
                }

                if (dy >= 80 && dy <= 660) {
                    units.add(new Unit(e.getX(), e.getY(), false));
                    score += 10;
                    energy = Math.max(0, energy - 3);
                    postInvalidate();
                }
            }
            return true;
        }

        private void startMode(int mode) {
            screen = 1;
            units.clear();
            score = mode == 0 ? 0 : mode == 1 ? 100 : 250;
            energy = mode == 2 ? 120 : mode == 1 ? 75 : 100;
            lastSpawn = System.currentTimeMillis();
            postInvalidate();
        }

        private class Unit {
            final float x, y;
            final boolean enemy;
            Unit(float x, float y, boolean enemy) {
                this.x = x;
                this.y = y;
                this.enemy = enemy;
            }
        }
    }
}
