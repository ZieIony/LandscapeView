package tk.zielony.landscapeview;

import android.graphics.Canvas;

/**
 * Created by Marcin on 2017-03-23.
 */
class Star {
    private LandscapeView landscapeView;
    private final float x;
    private final float y;
    private final float size;
    private final int color;

    Star(LandscapeView landscapeView, float x, float y, float size, int color) {
        this.landscapeView = landscapeView;
        this.x = x;
        this.y = y;
        this.size = size;
        this.color = color;
    }

    void draw(Canvas canvas) {
        landscapeView.paint.setColor(color);
        canvas.drawCircle(x, y, size, landscapeView.paint);
    }
}
