package nickgao.com.viewpagerswitchexample.view;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import nickgao.com.viewpagerswitchexample.DensityUtil;
import nickgao.com.viewpagerswitchexample.R;

public class SportStepCountView extends View {

    private int startColor = 0xFF7BE7E7;
    private int endColor = 0xFF49C9C9;
    private int defaultColor = 0x00F5F5F5;
    private int defaultColor1 = 0xFFF5F5F5;

    private int percentEndColor;

    private int strokeWidth;
    private float percent = 0.0006f;
    private int mFootStep = 15;

    // 用于渐变
    private Paint paint;

    private int deltaR, deltaB, deltaG;
    private int startR, startB, startG;

    private TextPaint mValuePaint;
    private TextPaint mUnitPaint;

    private ValueAnimator mAnimator;
    private long mAnimTime = 1000;

    private Paint mInitRedCirclePaint;
    private Paint mLittleCirclePaint;
    private int mLittleCircleSize;
    private Paint mBackgroundCirclePaint;
    private Paint startPaint;
    private Paint endPaint;

    private final RectF rectF = new RectF();

    private int[] customColors;
    private int[] fullColors;
    private int[] emptyColors;
    private float[] customPositions;
    private float[] extremePositions;

    private float mValueOffset;
    private CharSequence mHint;
    private CharSequence mUnit;
    private float mHintOffset;
    private float mUnitOffset;
    private float mTextOffsetPercentInRadius = 0.5f;
    private float mValueFix = 18.0f;
    private int mTarget = 100;
    private Context mContext;
    private int mDefaultTarget = 100;
    private static final String TAG = "SportStepCountView";


    public SportStepCountView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SportStepCountView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }


    private void init(final Context context, final AttributeSet attrs) {
        float defaultPercent = -1;
        mContext = context;

        final int strokeWdithDefaultValue = (int) (17 * getResources().getDisplayMetrics().density + 0.5f);

        TypedArray typedArray = null;
        try {
            typedArray = context.obtainStyledAttributes(attrs, R.styleable.SportStepCountView);
            percent = typedArray.getFloat(R.styleable.SportStepCountView_mpc_percent, defaultPercent);
            strokeWidth = (int) typedArray.getDimension(R.styleable.SportStepCountView_mpc_stroke_width, strokeWdithDefaultValue);
            startColor = typedArray.getColor(R.styleable.SportStepCountView_mpc_start_color, startColor);
            endColor = typedArray.getColor(R.styleable.SportStepCountView_mpc_end_color, endColor);
            defaultColor = typedArray.getColor(R.styleable.SportStepCountView_mpc_default_color, defaultColor);
        } finally {
            if (typedArray != null) {
                typedArray.recycle();
            }
        }

        mLittleCircleSize = (int) (4 * getResources().getDisplayMetrics().density + 0.5f);

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(strokeWidth);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);

        mValuePaint = new TextPaint();
        mValuePaint.setAntiAlias(true);

        mValuePaint.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.circle_foot_step_size));
        mValuePaint.setColor(context.getResources().getColor(R.color.pregnancy_color_181818));
        mValuePaint.setTypeface(Typeface.DEFAULT_BOLD);
        mValuePaint.setTextAlign(Paint.Align.CENTER);

        mUnitPaint = new TextPaint();
        mUnitPaint.setAntiAlias(true);
        mUnitPaint.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.circle_foot_step_title_size));
        mUnitPaint.setColor(context.getResources().getColor(R.color.pregnancy_color_888888));
        mUnitPaint.setTextAlign(Paint.Align.CENTER);

        mLittleCirclePaint = new Paint();
        mLittleCirclePaint.setAntiAlias(true);
        mLittleCirclePaint.setStyle(Paint.Style.FILL);
        mLittleCirclePaint.setColor(context.getResources().getColor(R.color.white));

        mInitRedCirclePaint = new Paint();
        mInitRedCirclePaint.setAntiAlias(true);
        mInitRedCirclePaint.setStyle(Paint.Style.FILL);
        mInitRedCirclePaint.setColor(context.getResources().getColor(R.color.pregnancy_color_49c9c9));

        int samllCircleSize = (int) (7 * getResources().getDisplayMetrics().density + 0.5f);

        mBackgroundCirclePaint = new Paint();
        mBackgroundCirclePaint.setAntiAlias(true);
        mBackgroundCirclePaint.setColor(context.getResources().getColor(R.color.pregnancy_color_f5f5f5));
        mBackgroundCirclePaint.setStyle(Paint.Style.STROKE);
        mBackgroundCirclePaint.setStrokeJoin(Paint.Join.ROUND);
        mBackgroundCirclePaint.setStrokeCap(Paint.Cap.ROUND);
        mBackgroundCirclePaint.setStrokeWidth(samllCircleSize);

        startPaint = new Paint();
        startPaint.setColor(startColor);
        startPaint.setAntiAlias(true);
        startPaint.setStyle(Paint.Style.FILL);


        endPaint = new Paint();
        endPaint.setAntiAlias(true);
        endPaint.setStyle(Paint.Style.FILL);

        refreshDelta();

        customColors = new int[]{startColor, percentEndColor, defaultColor, defaultColor};
        fullColors = new int[]{startColor, endColor};
        emptyColors = new int[]{defaultColor, defaultColor};

        customPositions = new float[4];
        customPositions[0] = 0;
        customPositions[3] = 1;

        extremePositions = new float[]{0, 1};


        mHint = "步数";
        mUnit = "目标: "+mTarget;
        initRed();
    }

    public void setValue(float value,int target) {
        if(target == 0) {
            target = mTarget;
        }

        //如果是恢复期，target=0的情况,这个时候mDefaultTarget=0
        if(target == 0) {
            mFootStep = (int) value;
            mUnit = "目标: "+target;
            startAnimator(0, 1, mAnimTime);
            return;
        }

        float start = 0;
        float end = value / (target*(1.0f));

        //重置目标
        mUnit = "目标: "+target;

        if(value > target) {
            end = 1;
        }
        if (value < mValueFix) {
            end = mValueFix / target;
        }
        if (mFootStep != 0) {
            percent = end;
            mFootStep = (int) value;
            startAnimator(start, end, mAnimTime);
        } else {
            mFootStep = (int) value;
            startAnimator(start, end, mAnimTime);
        }

    }

    public void setValueDuringRefresh(float value, int target) {
        if(target == 0) {
            target = mTarget;
        }

        //如果是恢复期，target=0的情况,这个时候mDefaultTarget=0
        if(target == 0) {
            percent = 1;
            mFootStep = (int) value;
            invalidate();
            return;
        }

        float start = 0;
        float end = value / target;

        //重置目标
        mUnit = "目标: "+target;
        if(value > target) {
            end = 1;
        }
        if (value < mValueFix) {
            end = mValueFix / target;
        }

        if (mFootStep != 0) {
            percent = end;
            mFootStep = (int) value;
            invalidate();

        } else {
            mFootStep = (int) value;
            startAnimator(start, end, mAnimTime);
        }
    }


    private void initRed() {
        float end = mValueFix / mTarget;
        if(mValueFix >= mTarget) {
            end = mValueFix / 5000.0f;
        }
        percent = end;
        mFootStep = 0;
        invalidate();
    }


    private void startAnimator(float start, float end, long animTime) {
        mAnimator = ValueAnimator.ofFloat(start, end);
        mAnimator.setDuration(animTime);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                percent = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        mAnimator.start();
    }


    private void calculatePercentEndColor(final float percent) {
        percentEndColor = ((int) (deltaR * percent + startR) << 16) +
                ((int) (deltaG * percent + startG) << 8) +
                ((int) (deltaB * percent + startB)) + 0xFF000000;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        this.rectF.left = getMeasuredWidth() / 2 - strokeWidth / 2;
        this.rectF.top = 0;
        this.rectF.right = getMeasuredWidth() / 2 + strokeWidth / 2;
        this.rectF.bottom = strokeWidth;
    }


    // 目前由于SweepGradient赋值只在构造函数，无法pre allocate & reuse instead
    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        final int restore = canvas.save();

        final int cx = getMeasuredWidth() / 2;
        final int cy = getMeasuredHeight() / 2;
        final int radius = getMeasuredWidth() / 2 - strokeWidth / 2;

        float drawPercent = percent;
        if (drawPercent > 0.97 && drawPercent < 1) {
            drawPercent = 0.97f;
        }

        // 画渐变圆
        canvas.save();
        canvas.rotate(-90, cx, cy);
        int[] colors;
        float[] positions;

        if (drawPercent < 1 && drawPercent > 0) {
            calculatePercentEndColor(drawPercent);
            customColors[1] = percentEndColor;
            colors = customColors;
            customPositions[1] = drawPercent;
            customPositions[2] = drawPercent;
            positions = customPositions;
        } else if (drawPercent == 1) {
            colors = fullColors;
            positions = extremePositions;
        } else {
            colors = emptyColors;
            positions = extremePositions;
        }

        //这个是灰色的大圆环
        canvas.drawCircle(cx, cy, radius, mBackgroundCirclePaint);

        final SweepGradient sweepGradient = new SweepGradient(getMeasuredWidth() / 2, getMeasuredHeight() / 2, colors, positions);
        paint.setShader(sweepGradient);
        //paint.setShadowLayer(13.5f,0,0.5f,getResources().getColor(R.color.circle_shadow_color));
        canvas.drawCircle(cx, cy, radius, paint);

        canvas.restore();

        //中间的步数写在这里
        mValueOffset = cy + getBaselineOffsetFromY(mValuePaint)- DensityUtil.dp2px(mContext,20);
        //这个是写步数
        canvas.drawText(String.valueOf(mFootStep), cx, mValueOffset, mValuePaint);

        mHintOffset = cy - radius * mTextOffsetPercentInRadius + getBaselineOffsetFromY(mUnitPaint);
        mUnitOffset = cy + radius * mTextOffsetPercentInRadius + getBaselineOffsetFromY(mUnitPaint) -DensityUtil.dp2px(mContext,30);


        if (mHint != null) {
            canvas.drawText(mHint.toString(), cx, mHintOffset-10, mUnitPaint);
        }

        if (mUnit != null) {
            canvas.drawText(mUnit.toString(), cx, mUnitOffset+10, mUnitPaint);
        }


        if (drawPercent > 0) {
            // 绘制结束的半圆
            if (drawPercent < 1) {
                canvas.save();
                endPaint.setColor(percentEndColor);
                canvas.rotate((int) Math.floor(360.0f * drawPercent) - 1, cx, cy);
                canvas.drawArc(rectF, -90f, 180f, true, endPaint);
                canvas.restore();
            }

            canvas.save();
            // 绘制开始的半圆
            canvas.drawArc(rectF, 90f, 180f, true, startPaint);
            canvas.restore();
        }

        //这个是y顶部非常小的白色小圆，像扣子一样
        canvas.drawCircle(cx, cy - radius, mLittleCircleSize, mLittleCirclePaint);
        canvas.restoreToCount(restore);
    }


    private float getBaselineOffsetFromY(Paint paint) {
        return measureTextHeight(paint) / 2;
    }

    public static float measureTextHeight(Paint paint) {
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        return (Math.abs(fontMetrics.ascent) - fontMetrics.descent);
    }

    private void refreshDelta() {
        int endR = (endColor & 0xFF0000) >> 16;
        int endG = (endColor & 0xFF00) >> 8;
        int endB = (endColor & 0xFF);

        this.startR = (startColor & 0xFF0000) >> 16;
        this.startG = (startColor & 0xFF00) >> 8;
        this.startB = (startColor & 0xFF);

        deltaR = endR - startR;
        deltaG = endG - startG;
        deltaB = endB - startB;
    }


}
