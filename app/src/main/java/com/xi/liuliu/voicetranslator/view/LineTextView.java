package com.xi.liuliu.voicetranslator.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

public class LineTextView extends AppCompatTextView {
    private Context context;
    private int viewWidth;
    private int viewHight;
    private int oneWidth;
    private int oneHight;
    private int num;
    private Paint p;
    private String str;
    private String[] texts;
    private int textSize = 15;

    public LineTextView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public LineTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        init();
        drawText(canvas);
    }

    private void drawText(Canvas canvas) {
        if (num == 0)
            return;
        int lineWidth = viewWidth / num;
        int drawY = (viewHight - oneHight) / 2 + oneHight;
        for (int i = 0; i < num; i++) {
            if (i == num - 1) {// 最后一个
                canvas.drawText(texts[i],
                        viewWidth - oneWidth - dip2px(context, 2), drawY, p);
            } else if (i != 0 && i != num - 1) {//
                canvas.drawText(texts[i], i * lineWidth + lineWidth / 2
                        - oneWidth / 2, drawY, p);
            } else {// 第一个
                canvas.drawText(texts[i], i * lineWidth + dip2px(context, 2),
                        drawY, p);
            }
        }
    }

    private void init() {
        this.setPadding(0, 0, 0, 0);
        viewWidth = this.getWidth();
        viewHight = this.getHeight();
    }

    public void setTextAndSizeAndColor(String str, int textSize,int colorId) {
        if (str == null)
            return;
        p = new Paint();
        p.setColor(context.getResources().getColor(colorId));
        p.setTextSize(dip2px(context, textSize));
        p.setTypeface(Typeface.DEFAULT_BOLD);
        this.str = str;
        num = str.length();
        int width = ce("汉").width();
        this.oneWidth = width;
        this.oneHight = ce(str).height();
        String[] strs = new String[num];
        for (int i = 0; i < num; i++) {
            strs[i] = str.substring(i, i + 1);
        }
        this.texts = strs;
        this.invalidate();
    }

    public void setTestSize(int size) {
        this.textSize = size;
        if (str != null)
            setText(str);
    }

    public Rect ce(String str) {
        Rect rect = new Rect();
        p.getTextBounds(str, 0, 1, rect);
        return rect;
    }

    /**
     * 根据手机的分辨率�? dp 的单�? 转成�? px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
