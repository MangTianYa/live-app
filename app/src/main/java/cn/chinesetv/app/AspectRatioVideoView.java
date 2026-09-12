package cn.chinesetv.app;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.VideoView;

public final class AspectRatioVideoView extends VideoView {
    static final int MODE_FIT = 0;
    static final int MODE_FILL = 1;
    static final int MODE_STRETCH = 2;
    static final int MODE_16_9 = 3;
    static final int MODE_4_3 = 4;

    private int displayMode = MODE_FIT;
    private int videoWidth;
    private int videoHeight;

    public AspectRatioVideoView(Context context) {
        super(context);
    }

    public AspectRatioVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AspectRatioVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    void setDisplayMode(int mode) {
        displayMode = mode;
        requestLayout();
    }

    void setVideoDimensions(int width, int height) {
        videoWidth = width;
        videoHeight = height;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (displayMode == MODE_FIT) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        int availableWidth = View.MeasureSpec.getSize(widthMeasureSpec);
        int availableHeight = View.MeasureSpec.getSize(heightMeasureSpec);
        if (availableWidth == 0 || availableHeight == 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        if (displayMode == MODE_STRETCH) {
            setMeasuredDimension(availableWidth, availableHeight);
            return;
        }

        float aspectRatio;
        boolean crop = displayMode == MODE_FILL;
        if (crop) {
            if (videoWidth <= 0 || videoHeight <= 0) {
                setMeasuredDimension(availableWidth, availableHeight);
                return;
            }
            aspectRatio = (float) videoWidth / videoHeight;
        } else {
            aspectRatio = displayMode == MODE_4_3 ? 4f / 3f : 16f / 9f;
        }

        float availableRatio = (float) availableWidth / availableHeight;
        int measuredWidth;
        int measuredHeight;
        if ((crop && aspectRatio > availableRatio) || (!crop && aspectRatio < availableRatio)) {
            measuredHeight = availableHeight;
            measuredWidth = Math.round(measuredHeight * aspectRatio);
        } else {
            measuredWidth = availableWidth;
            measuredHeight = Math.round(measuredWidth / aspectRatio);
        }
        setMeasuredDimension(measuredWidth, measuredHeight);
    }
}
