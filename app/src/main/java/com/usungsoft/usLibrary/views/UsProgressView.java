package com.usungsoft.usLibrary.views;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;


import com.usungsoft.usLibrary.R;
import com.usungsoft.usLibrary.utils.UsFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class UsProgressView extends ConstraintLayout {
    Context mContext;
    CustomProgressView mProgressView;
    TextView mTvPercentage, mTvLow, mTvHigh, mTvStatus;
    Button mBtnExpandCollapse;
    ToggleButton mToggleStatus;

    private boolean mUseRssiLocStatus = true;
    private boolean mUsePercentage = true;
    private boolean mCollapseState = true;
    private boolean mIsAverageCalculate = true; // 1초 동안의 읽힌 rssi or loc 값을 평균낼 것인지 여부
    private Timer mTimer;
    private TimerTask mTimerTask;
    private List<Integer> mReadedValues = new ArrayList<>();

    private int mTimerScheduleMilliSec = 1000;
    private float mUpdatingFloatValue = 0.01f;

    private final List<Integer> mRssiDbmTable = new ArrayList<>(100);

    public UsProgressView(Context context) {
        super(context);
        init(null);
    }

    public UsProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public UsProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        mContext = getContext();
        inflate(mContext, R.layout.us_progress_view, this);
        mProgressView = findViewById(R.id.progress);
        mTvLow = findViewById(R.id.tv_low);
        mTvHigh = findViewById(R.id.tv_high);
        mTvPercentage = findViewById(R.id.tv_percentage);
        mBtnExpandCollapse = findViewById(R.id.btn_expand_collapse);
        mToggleStatus = findViewById(R.id.btn_toggle);
        mTvStatus = findViewById(R.id.tv_status);

        initExpandCollapseListener();

        if (attrs != null) {
            TypedArray ta = mContext.obtainStyledAttributes(attrs, R.styleable.UsProgressView);

            int n = ta.getIndexCount();
            for (int i = 0; i < n; i++) {
                int attr = ta.getIndex(i);

                if (attr == R.styleable.UsProgressView_use_precentage_text)
                    usePercentageText(ta.getBoolean(attr, true));
                else if (attr == R.styleable.UsProgressView_use_rssi_loc_status)
                    useRssiLocStatus(ta.getBoolean(attr, true));
            }
        }

        initRssiDbmToPercentage();
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
    public void changeComponentsVisibility(boolean expand) {
        int visible = expand ? View.VISIBLE : View.GONE;

        mProgressView.setVisibility(expand ? View.VISIBLE : View.INVISIBLE);
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

    public void toggleChange(boolean isCheck) {
        mToggleStatus.setChecked(isCheck);
    }

    /**
     * @apiNote 상품찾기의 스타일 | 색상 | 사이즈 | Tag 에서 Tag가 선택되면 false로 넘겨주세요.
     * @apiNote {@code mProgress.setAverageCalculate(!(checkedId == R.id.rb_search_tag));}
     * @param isAverageCalculate 가 {@code true}면 1초동안 들어온 rssi or loc 값을 평균내어 progress에 표현. {@code true}면 실시간 update.
     */
    public void setAverageCalculate(boolean isAverageCalculate) {
        this.mIsAverageCalculate = isAverageCalculate;
        recycleAverageTimer(this.mIsAverageCalculate);
    }

    public void setTimerScheduleMilliSec(int milliSec) {
        this.mTimerScheduleMilliSec = milliSec;
    }

    public void setUpdatingFloatValue(float updatingValue) {
        this.mUpdatingFloatValue = updatingValue;
    }

    private void recycleAverageTimer(boolean isAverageCalculate) {
        if (isAverageCalculate) {
            if (mTimer != null) return;

            mTimer = new Timer();
            mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    if (mReadedValues.size() > 0) {
                        ((Activity) mContext).runOnUiThread(() -> {
                            int valueSum = 0;
                            for (Integer readedValue : mReadedValues)
                                valueSum += readedValue;
                            try {
                                int percentage = valueSum / mReadedValues.size();
                                mProgressView.setProgress(percentage * mUpdatingFloatValue);
                                mTvPercentage.setText(percentage + "%");
                                mReadedValues.clear();
                            } catch (ArithmeticException e) {  }
                        });
                    }
                }
            };
            mTimer.schedule(mTimerTask, 0, mTimerScheduleMilliSec); // 1000ms -> 1초
        } else {
            if (mTimer != null) {
                mTimer.cancel();
                mTimer = null;
                mTimerTask = null;
            }
        }
    }

    public void clearProgress() {
        if (mContext == null || !mCollapseState) return;

        if (mProgressView.getProgress() > 0)
            mProgressView.setProgress(0);

        if (mUsePercentage) mTvPercentage.setText("0%");
        if (!mToggleStatus.isEnabled()) mToggleStatus.setEnabled(true);
        if (mReadedValues.size() > 0) mReadedValues.clear();
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
            if (percentage > 100) percentage = 100;

            percentVal = percentage;
        }

        if (!mIsAverageCalculate) {
            mProgressView.setProgress(percentVal * 0.01f);
            mTvPercentage.setText(percentVal + "%");
        } else
            mReadedValues.add(percentVal);
    }
}
