package tk.zielony.landscapeview;

import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Path;
import android.graphics.Shader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marcin on 2017-03-23.
 */
class Landscape implements Drawable {
    private LandscapeView landscapeView;

    private static final int MAX_CLOUDS = 3, MIN_CLOUDS = 2;

    private final int color;
    private final float scale;
    private int color2;
    public float height;
    public float fluctuation;
    Path path;
    List<Tree> trees = new ArrayList<>();
    List<Cloud> clouds = new ArrayList<>();

    Landscape(LandscapeView landscapeView, int color, int color2, float height, float fluctuation, float scale) {
        this.landscapeView = landscapeView;
        this.color = color;
        this.color2 = color2;
        this.height = height;
        this.fluctuation = fluctuation;
        this.scale = scale;

        path = new Path();
        path.moveTo(0, landscapeView.getMaximumHeight());
        path.lineTo(landscapeView.getWidth(), landscapeView.getMaximumHeight());
        float prevX = landscapeView.getWidth();
        float prevY = (float) (landscapeView.getMaximumHeight() - height - Math.random() * fluctuation);
        path.lineTo(prevX, prevY);
        int widthDiv100 = (int) (landscapeView.getWidth() / (landscapeView.getResources().getDimension(R.dimen.carbon_1dip) * 100));
        int segments = LandscapeView.random.nextInt(widthDiv100) + widthDiv100;
        for (int i = 0; i <= segments; i++) {
            float x = landscapeView.getWidth() * (segments - i) / segments;
            float y = (float) (landscapeView.getMaximumHeight() - height - Math.random() * fluctuation);
            float x33 = MathUtils.lerp(prevX, x, 0.33f);
            float x67 = MathUtils.lerp(prevX, x, 0.67f);
            path.cubicTo(x33, prevY, x67, y, x, y);
            if (LandscapeView.random.nextFloat() > LandscapeView.TREE_RANDOM)
                trees.add(new Tree(landscapeView,prevX, prevY, scale));
            if (LandscapeView.random.nextFloat() > LandscapeView.TREE_RANDOM)
                trees.add(new Tree(landscapeView,x33, MathUtils.lerp(prevY, y, 0.33f), scale));
            if (LandscapeView.random.nextFloat() > LandscapeView.TREE_RANDOM)
                trees.add(new Tree(landscapeView,x67, MathUtils.lerp(prevY, y, 0.67f), scale));
            if (LandscapeView.random.nextFloat() > LandscapeView.TREE_RANDOM)
                trees.add(new Tree(landscapeView,x, y, scale));
            prevX = x;
            prevY = y;
        }
        path.close();

        if (landscapeView.drawClouds) {
            clouds.clear();
            int cloudCount = LandscapeView.random.nextInt(MAX_CLOUDS - MIN_CLOUDS) + MIN_CLOUDS;
            for (int i = 0; i < cloudCount; i++)
                clouds.add(new Cloud(LandscapeView.random.nextInt(landscapeView.getWidth()), LandscapeView.random.nextInt((int) landscapeView.skyHeight) + landscapeView.padding, landscapeView.cloudColor,scale));
        }
    }

    public void draw(Canvas canvas) {
        float translate = MathUtils.map(0, landscapeView.landscapes.size() - 1, (landscapeView.getMaximumHeight() - landscapeView.getHeight()) / 2.0f, landscapeView.getMaximumHeight() - landscapeView.getHeight(), landscapeView.landscapes.indexOf(this));
        canvas.save(Canvas.MATRIX_SAVE_FLAG);
        canvas.translate(0, -translate);
        LandscapeView.paint.setColor(color2);
        for (Tree t : trees)
            t.draw(canvas);
        LandscapeView.paint.setShader(new LinearGradient(0, landscapeView.getMaximumHeight() - height, 0, landscapeView.getMaximumHeight() - height - fluctuation, color, color2, Shader.TileMode.CLAMP));
        canvas.drawPath(path, LandscapeView.paint);
        LandscapeView.paint.setShader(null);
        if (landscapeView.drawClouds) {
            for (Cloud c : clouds)
                c.draw(canvas);
        }
        LandscapeView.paint.setShader(null);
        LandscapeView.paint.setAlpha(255);
        canvas.restore();
    }

    @Override
    public void update() {
        for (Tree t : trees)
            t.update();

        if (landscapeView.drawClouds && landscapeView.animateWind) {
            for (Cloud c : clouds) {
                c.x = (c.x + landscapeView.wind * scale / 10);
                if (c.x > landscapeView.getWidth() + Cloud.MAX_PUFF_DIST + Cloud.MIN_PUFF_SIZE)
                    c.x = -Cloud.MAX_PUFF_DIST - Cloud.MIN_PUFF_SIZE;
                c.update();
            }
        }
    }
}
