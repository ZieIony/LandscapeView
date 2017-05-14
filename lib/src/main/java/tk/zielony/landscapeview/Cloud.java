package tk.zielony.landscapeview;

import android.graphics.Canvas;
import android.graphics.RadialGradient;
import android.graphics.Shader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

class Cloud implements Drawable {
    private static final int MAX_PUFFS = 12, MIN_PUFFS = 8;
    private static final int MAX_PUFF_SIZE = 30;
    public static final int MIN_PUFF_SIZE = 10;
    public static final int MAX_PUFF_DIST = 40;
    private static final int PUFF_OFFSET = 5;

    public float x;
    private final float y;
    private final float scale;

    private class Puff {

        private float x;
        private float y;
        private float size;
        private int color;

        public Puff(float x, float y, float size, int color) {
            this.x = x;
            this.y = y;
            this.size = size;
            this.color = color;
        }

    }

    private List<Puff> puffs = new ArrayList<>();

    public Cloud(float x, float y, int color,float scale) {
        this.x = x;
        this.y = y;
        this.scale = scale;
        int puffCount = LandscapeView.random.nextInt(MAX_PUFFS - MIN_PUFFS) + MIN_PUFFS;
        for (int i = 0; i < puffCount; i++) {
            float dist = LandscapeView.random.nextFloat() * MAX_PUFF_DIST * 2 - MAX_PUFF_DIST;
            puffs.add(new Puff(dist, Math.abs(dist) / 3 - LandscapeView.random.nextInt(PUFF_OFFSET),
                    MathUtils.map(0, MAX_PUFF_DIST, MAX_PUFF_SIZE, MIN_PUFF_SIZE, Math.abs(dist)),
                    color));
        }
        Collections.sort(puffs, new Comparator<Puff>() {
            @Override
            public int compare(Puff o1, Puff o2) {
                return (int) (MathUtils.dist(0, 0, o2.x, o2.y) - MathUtils.dist(0, 0, o1.x, o1.y));
            }
        });
    }

    public void draw(Canvas canvas) {
        canvas.save(Canvas.MATRIX_SAVE_FLAG);
        canvas.scale(scale, 0.8f * scale, x, y);
        for (Puff p : puffs) {
            LandscapeView.paint.setShader(new RadialGradient(x + p.x, y + p.y - p.size / 2, p.size, new int[]{0xffffffff, p.color}, new float[]{0.9f, 1}, Shader.TileMode.CLAMP));
            LandscapeView.paint.setAlpha(255);
            canvas.drawCircle(x + p.x, y + p.y, p.size, LandscapeView.paint);
        }
        canvas.restore();
    }

    @Override
    public void update() {

    }
}
