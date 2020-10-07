package com.usungsoft.usLibrary.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.usungsoft.usLibrary.R;

/***
 * @apiNote 주로 상품찾기 같은 게이지가 필요한 레이아웃에서 사용.
 */
public class UsHorizontalProgress extends ConstraintLayout {
    Context mContext;
    ProgressBar mProgressBar;
    TextView mTvPercentage, mTvLow, mTvHigh;
    Button mBtnExpandCollapse;

    private boolean mUsePercentage = true;
    private boolean mCollapseState = true;
    private static final int LOW_COLOR = Color.parseColor("#F25E5E");
    private static final int NORMAL_COLOR = Color.parseColor("#F39256");
    private static final int HIGH_COLOR = Color.parseColor("#6DC055");

    public UsHorizontalProgress(Context context) {
        super(context);
        init(null);
    }

    public UsHorizontalProgress(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public UsHorizontalProgress(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        mContext = getContext();
        inflate(mContext, R.layout.us_horizontal_progress, this);
        mProgressBar = findViewById(R.id.progress);
        mTvLow = findViewById(R.id.tv_low);
        mTvHigh = findViewById(R.id.tv_high);
        mTvPercentage = findViewById(R.id.tv_percentage);
        mBtnExpandCollapse = findViewById(R.id.btn_expand_collapse);
        initExpandCollapseListener();

        if (attrs != null) {
            TypedArray ta = mContext.obtainStyledAttributes(attrs, R.styleable.UsHorizontalProgress);

            int n = ta.getIndexCount();
            for (int i = 0; i < n; i++) {
                int attr = ta.getIndex(i);

                if (attr == R.styleable.UsHorizontalProgress_use_precentage_text)
                    usePercentageText(ta.getBoolean(attr, true));
            }
        }
    }

    private void initExpandCollapseListener() {
        mBtnExpandCollapse.setOnClickListener(v -> {
            mCollapseState = !mCollapseState;
            changeComponentsVisibility(mCollapseState);
        });
    }

    /**
     * @param expand is true then ProgressBar, TextView visibility visible, if false then gone.
     */
    private void changeComponentsVisibility(boolean expand) {
        int visible = expand ? View.VISIBLE : View.GONE;

        mProgressBar.setVisibility(expand ? View.VISIBLE : View.INVISIBLE);
        mTvLow.setVisibility(visible);
        if (mUsePercentage)
            mTvPercentage.setVisibility(visible);
        mTvHigh.setVisibility(visible);
        mBtnExpandCollapse.setText(expand ? "-" : "+");
    }

    public void usePercentageText(boolean usePercentageText) {
        mTvPercentage.setVisibility(usePercentageText ? View.VISIBLE : View.GONE);
        mUsePercentage = usePercentageText;
    }
}
