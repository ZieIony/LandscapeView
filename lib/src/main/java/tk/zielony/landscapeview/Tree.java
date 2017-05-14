package tk.zielony.landscapeview;

import android.graphics.Canvas;
import android.graphics.Path;

/**
 * Created by Marcin on 2017-03-23.
 */
class Tree implements Drawable {
    private LandscapeView landscapeView;
    private final float x;
    private final float y;
    private float size;
    private Path path;

   public Tree(LandscapeView landscapeView, float x, float y, float size) {
        this.landscapeView = landscapeView;
        this.x = x;
        this.y = y;
        this.size = landscapeView.getMaximumHeight() / (LandscapeView.random.nextInt(8) + 12) * size;
        path = new Path();
        update();
    }

    public void draw(Canvas canvas) {
        canvas.drawPath(path, LandscapeView.paint);
    }

    @Override
    public void update() {
        path.reset();
        path.addRect(x - size / 10, y - size / 6, x + size / 10, y + size / 10, Path.Direction.CCW);
        path.moveTo(x - size / 3, y - size / 6);
        path.quadTo(
                x - size / 6, y - size * 7 / 12,
                x + landscapeView.wind, y - size);
        path.quadTo(
                x + size / 6, y - size * 7 / 12,
                x + size / 3, y - size / 6);
        path.close();
    }
}
