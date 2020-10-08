package com.usungsoft.usLibrary.views;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.usungsoft.usLibrary.R;
import com.usungsoft.usLibrary.utils.UsFunction;

import java.util.ArrayList;
import java.util.List;

/***
 * @apiNote 주로 상품찾기 같은 게이지가 필요한 레이아웃에서 사용.
 */
public class UsHorizontalProgress extends ConstraintLayout {
    Context mContext;
    ProgressBar mProgressBar;
    TextView mTvPercentage, mTvLow, mTvHigh, mTvStatus;
    Button mBtnExpandCollapse;
    ToggleButton mToggleStatus;

    private boolean mUseRssiLocStatus = true;
    private boolean mUsePercentage = true;
    private boolean mCollapseState = true;
    private final List<Integer> mRssiDbmTable = new ArrayList<>(100);
    private ObjectAnimator mProgressAnimator;
    private DecelerateInterpolator mDecelerateInterpolator = new DecelerateInterpolator();

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
        mToggleStatus = findViewById(R.id.btn_toggle);
        mTvStatus = findViewById(R.id.tv_status);

        initExpandCollapseListener();

        if (attrs != null) {
            TypedArray ta = mContext.obtainStyledAttributes(attrs, R.styleable.UsHorizontalProgress);

            int n = ta.getIndexCount();
            for (int i = 0; i < n; i++) {
                int attr = ta.getIndex(i);

                if (attr == R.styleable.UsHorizontalProgress_use_precentage_text)
                    usePercentageText(ta.getBoolean(attr, true));
                else if (attr == R.styleable.UsHorizontalProgress_use_rssi_loc_status)
                    useRssiLocStatus(ta.getBoolean(attr, true));
            }
        }

        initRssiDbmToPercentage();
    }

    private void startProgressAnimate(int from) {
        mProgressAnimator = ObjectAnimator.ofInt(mProgressBar, "progress", from, 0);
        mProgressAnimator.setDuration(500).setInterpolator(mDecelerateInterpolator);
        mProgressAnimator.start();
    }

    /**
     * @apiNote Conversion for Cisco [Converting_Signal_Strength] - Cisco 기준
     * @apiNote rssi 값으로 ArrayList의 index를 추출
     * @apiNote 해당 index 값이 percentage로 사용.
     * @apiNote 만약, rssi 값이 -27보다 같거나 크다면(-26, -25...) 90% 이상으로 본다.
     */
    private void initRssiDbmToPercentage() {
        for (int dbm = -113; dbm <= -10; dbm++) {
            if (mRssiDbmTable.size() == 100) break;
            mRssiDbmTable.add(dbm);
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
        if (mUseRssiLocStatus) {
            mToggleStatus.setVisibility(visible);
            mTvStatus.setVisibility(visible);
        }
        mTvHigh.setVisibility(visible);
        mBtnExpandCollapse.setText(expand ? "-" : "+");
    }

    public void usePercentageText(boolean usePercentageText) {
        mTvPercentage.setVisibility(usePercentageText ? View.VISIBLE : View.GONE);
        mUsePercentage = usePercentageText;
    }

    public void useRssiLocStatus(boolean useRssiLocStatus) {
        mToggleStatus.setVisibility(useRssiLocStatus ? View.VISIBLE : View.GONE);
        mTvStatus.setVisibility(useRssiLocStatus ? View.VISIBLE : View.GONE);
        mUseRssiLocStatus = useRssiLocStatus;
    }

    public void subscribeToggleStatus(UsFunction.P1<Boolean, Boolean> callback) {
        if (mUseRssiLocStatus)
            mToggleStatus.setOnCheckedChangeListener((buttonView, isChecked) -> {
                mTvStatus.setText(isChecked ? "리딩거리 측정" : "태그응답 측정");
                if (callback != null)
                    callback.apply(isChecked);
            });
    }

    public void clearProgress() {
        if (mContext == null || !mCollapseState) return;

        if (mProgressBar.getProgress() > 0) {
            if (mProgressBar.getSecondaryProgress() > 50)
                mProgressBar.setSecondaryProgress(0);
            startProgressAnimate(mProgressBar.getProgress());
        }
        if (mUsePercentage) mTvPercentage.setText("0%");
        if (!mToggleStatus.isEnabled()) mToggleStatus.setEnabled(true);
    }

    public void updateProgress(double rssiOrLoc) {
        if (mContext == null || !mCollapseState) return;
        if (mToggleStatus.isEnabled())
            mToggleStatus.setEnabled(false);

        int percentVal = 0;

        if (rssiOrLoc < 0)
            for (int percentage = 0; percentage < mRssiDbmTable.size(); percentage++) {
                int rssiValue = (mRssiDbmTable.get(percentage)) * -1;
                int receivedRssi = (int) Math.round(rssiOrLoc) * -1;

                if (rssiValue == receivedRssi) {
                    if (receivedRssi <= 27) percentage += 9; // 사실상 리더기에 태그 바로 앞에 놓고 읽으면 -25 ~ -27
                    if (percentage > 100) percentage = 100;

                    percentVal = percentage;
                    break;
                }
            }
        else {
            int percentage = (int) rssiOrLoc;
            if (percentage > 0) percentage = 100;

            percentVal = percentage;
        }

        if (percentVal > 50) {
            mProgressBar.setProgress(50);
            mProgressBar.setSecondaryProgress(50 + (percentVal - 50));
        } else {
            mProgressBar.setProgress(percentVal);
            mProgressBar.setSecondaryProgress(0);
        }

        mTvPercentage.setText(percentVal + "%");
    }
}
