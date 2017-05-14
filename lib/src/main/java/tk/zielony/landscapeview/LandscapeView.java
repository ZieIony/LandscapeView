package tk.zielony.landscapeview;

import android.animation.ArgbEvaluator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LandscapeView extends View {
    public static final float TREE_RANDOM = 0.6f;
    private static final int MAX_STARS = 50, MIN_STARS = 30;

    static Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    static Random random = new Random();
    ArgbEvaluator argbEvaluator = new ArgbEvaluator();

    List<Landscape> landscapes = new ArrayList<>();
    private float starSize, starX, starY;

    List<Star> stars = new ArrayList<>();

    boolean animateWind = true;
    float wind = 0;
    float maxWind;

    // params
    boolean drawStars = true, drawSun = true, drawClouds = true;
    int starColor = 0x3fffffff, sunColor, skyColor, landscapeColor, fogColor, planesCount, cloudColor;
    float landscapeHeight, skyHeight;
    public float padding;

    public LandscapeView(Context context) {
        super(context);
        init();
    }

    public LandscapeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LandscapeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LandscapeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        sunColor = getResources().getColor(R.color.landscapeView_sunColor);
        skyColor = getResources().getColor(R.color.landscapeView_skyColor);
        landscapeColor = getResources().getColor(R.color.landscapeView_landscapeColor);
        fogColor = getResources().getColor(R.color.landscapeView_fogColor);
        cloudColor = getResources().getColor(R.color.landscapeView_cloudColor);
        planesCount = 5;
        starSize = getResources().getDimension(R.dimen.landscapeView_1dip) * (random.nextInt(16) + 8);
        landscapeHeight = getResources().getDimension(R.dimen.landscapeView_1dip) * 100;
        skyHeight = getResources().getDimension(R.dimen.landscapeView_1dip) * 100;
        padding = getResources().getDimension(R.dimen.landscapeView_padding);
        maxWind = getResources().getDimension(R.dimen.landscapeView_1dip) * 8;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (!changed)
            return;
        if (getWidth() == 0 || getHeight() == 0)
            return;
        if (!landscapes.isEmpty())
            return;
        generate();
    }

    private void generate() {
        landscapes.clear();
        for (int i = 0; i < planesCount; i++) {
            float height = (float) i * landscapeHeight / (planesCount + 1);
            float fluctuation = (float) (i + 1) * landscapeHeight / (planesCount + 1);
            int color1 = (int) argbEvaluator.evaluate((float) i / planesCount, landscapeColor, fogColor);
            int color2 = (int) argbEvaluator.evaluate((float) (i + 1) / planesCount, landscapeColor, fogColor);
            landscapes.add(0, new Landscape(this, color1, color2, height, fluctuation, (float) (planesCount - i) / planesCount));
        }

        if (drawSun) {
            starX = random.nextInt((int) (getWidth() - padding * 2 - starSize)) + starSize * 0.5f + padding;
            starY = random.nextInt((int) (skyHeight - padding - starSize)) + starSize * 0.5f + padding;
        }

        if (drawStars) {
            stars.clear();
            int starCount = random.nextInt(MAX_STARS - MIN_STARS) + MIN_STARS;
            for (int i = 0; i < starCount; i++)
                stars.add(new Star(random.nextInt(getWidth()), random.nextInt((int) (skyHeight)), (random.nextInt(2) + 1) * getResources().getDimension(R.dimen.landscapeView_1dip), starColor));
        }
    }

    @Override
    protected void dispatchDraw(@NonNull Canvas canvas) {
        super.dispatchDraw(canvas);

        if (animateWind) {
            wind += (random.nextFloat() * 2 - 1) * getResources().getDimension(R.dimen.landscapeView_1dip) / 5;
            wind = MathUtils.constrain(wind, 0, maxWind);
        }

        for (Landscape l : landscapes)
            l.update();

        paint.setShader(new LinearGradient(0, getMaximumHeight() - landscapes.get(0).height - landscapes.get(0).fluctuation, 0, 0, fogColor, skyColor, Shader.TileMode.CLAMP));
        canvas.drawPaint(paint);
        paint.setShader(null);

        if (drawStars || drawSun) {
            float translate = -(getMaximumHeight() - getHeight()) / (landscapes.size() - 1);
            canvas.save();
            canvas.translate(0, translate);
            if (drawSun) {
                paint.setColor(sunColor);
                canvas.drawCircle(starX, starY, starSize, paint);
            }
            if (drawStars) {
                for (Star s : stars)
                    s.draw(canvas);
            }
            canvas.restore();
            paint.setAlpha(255);
        }

        for (Landscape l : landscapes)
            l.draw(canvas);

        if (animateWind)
            postInvalidate();
    }

    public int getMaximumHeight() {
        return ((View) getParent()).getHeight();
    }

    public boolean isDrawStarsEnabled() {
        return drawStars;
    }

    public void setDrawStarsEnabled(boolean drawStars) {
        this.drawStars = drawStars;
    }

    public boolean isDrawSunEnabled() {
        return drawSun;
    }

    public void setDrawSunEnabled(boolean drawSun) {
        this.drawSun = drawSun;
    }

    public int getStarColor() {
        return starColor;
    }

    public void setStarColor(int starColor) {
        this.starColor = starColor;
    }

    public int getSunColor() {
        return sunColor;
    }

    public void setSunColor(int sunColor) {
        this.sunColor = sunColor;
    }

    public int getSkyColor() {
        return skyColor;
    }

    public void setSkyColor(int skyColor) {
        this.skyColor = skyColor;
    }

    public int getLandscapeColor() {
        return landscapeColor;
    }

    public void setLandscapeColor(int landscapeColor) {
        this.landscapeColor = landscapeColor;
    }

    public int getFogColor() {
        return fogColor;
    }

    public void setFogColor(int fogColor) {
        this.fogColor = fogColor;
    }

    public int getPlanesCount() {
        return planesCount;
    }

    public void setPlanesCount(int planesCount) {
        this.planesCount = planesCount;
    }

    public float getLandscapeHeight() {
        return landscapeHeight;
    }

    public void setLandscapeHeight(float landscapeHeight) {
        this.landscapeHeight = landscapeHeight;
    }

    public boolean isAnimateWindEnabled() {
        return animateWind;
    }

    public void setAnimateWindEnabled(boolean animateWind) {
        this.animateWind = animateWind;
    }

    public boolean isDrawCloudsEnabled() {
        return drawClouds;
    }

    public void setDrawCloudsEnabled(boolean drawClouds) {
        this.drawClouds = drawClouds;
    }

    public int getCloudColor() {
        return cloudColor;
    }

    public void setCloudColor(int cloudColor) {
        this.cloudColor = cloudColor;
    }

    public float getSkyHeight() {
        return skyHeight;
    }

    public void setSkyHeight(float skyHeight) {
        this.skyHeight = skyHeight;
    }
}
