package tk.zielony.landscapeview;

import android.graphics.Canvas;

class Star {
    private final float x;
    private final float y;
    private final float size;
    private final int color;

    Star(float x, float y, float size, int color) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.color = color;
    }

    void draw(Canvas canvas) {
        LandscapeView.paint.setColor(color);
        canvas.drawCircle(x, y, size, LandscapeView.paint);
    }
}
