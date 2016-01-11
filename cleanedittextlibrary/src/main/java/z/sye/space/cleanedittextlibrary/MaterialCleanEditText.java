/**
 * @(#) z.sye.space.cleanedittextlibrary 2016/1/11;
 * <p>
 * Copyright (c), 2009 深圳孔方兄金融信息服务有限公司（Shenzhen kfxiong
 * Financial Information Service Co. Ltd.）
 * <p>
 * 著作权人保留一切权利，任何使用需经授权。
 */
package z.sye.space.cleanedittextlibrary;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Syehunter on 2016/1/11.
 */
public class MaterialCleanEditText extends FrameLayout {

    private Context mContext;
    private TextView mHint;
    private FrameLayout mEditTextContent;
    private FrameLayout mEditTextLayout;
    private ImageView mIcon;
    private EditText mEditText;
    private FrameLayout mClean;
    private ImageView mCleanIcon;
    private TextView mError;

    private int mMinEditTextHeight = 3;

    /**
     * if mEditText could expand or not
     */
    private boolean mExpand = true;
    private int mHintHeight;
    private int mEditTextLayoutHeight;
    private int mErrorHeight;

    private long ANIMATION_DURATION = 150;
    private LayoutParams mEditTextParams;

    public MaterialCleanEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    private void init() {
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.edittext_clean_material, this);
        mHint = (TextView) rootView.findViewById(R.id.tv_material_hint);
        mEditTextContent = (FrameLayout) rootView.findViewById(R.id.fl_material_content);
        mEditTextLayout = (FrameLayout) rootView.findViewById(R.id.fl_material_edittext);
        mIcon = (ImageView) rootView.findViewById(R.id.iv_material_icon);
        mEditText = (EditText) rootView.findViewById(R.id.edt_material);
        mClean = (FrameLayout) rootView.findViewById(R.id.fl_material_clean);
        mCleanIcon = (ImageView) rootView.findViewById(R.id.iv_material_clean);
        mError = (TextView) findViewById(R.id.tv_material_error);

        mHintHeight = getMeasureHeight(mHint);
        mEditTextLayoutHeight = getMeasureHeight(mEditTextLayout);
        mEditTextContent.getLayoutParams().height = (int) (mHintHeight * 0.9 + mEditTextLayoutHeight);

        FrameLayout.LayoutParams hintParams = (LayoutParams) mHint.getLayoutParams();
        hintParams.topMargin = (int) (mHintHeight * 0.7);
        hintParams.width = LayoutParams.MATCH_PARENT;
        hintParams.height = LayoutParams.MATCH_PARENT;
        mHint.setLayoutParams(hintParams);
        mHint.setGravity(Gravity.CENTER_VERTICAL);

        mEditTextParams = new LayoutParams(mEditText.getLayoutParams());

        mErrorHeight = getMeasureHeight(mError);

        //shows no icon as default
        mIcon.setVisibility(GONE);
        //set editText height to 3
        mEditTextLayout.getLayoutParams().height = mMinEditTextHeight;

        mHint.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mExpand) {
                    expand();
                } else {
                    reduce();
                }
            }
        });
        Log.i(this.toString(), "init");
    }

    /**
     * Set the Icon for the EditText
     * @param resId
     */
    public void setIcon(int resId) {
        mIcon.setImageResource(resId);
        showIcon();
    }

    /**
     * Set the Icon for the EditText
     * @param drawable
     */
    public void setIcon(Drawable drawable) {
        mIcon.setImageDrawable(drawable);
        showIcon();
    }

    /**
     * Set the Icon for the EditText
     * @param bitmap
     */
    public void setIcon(Bitmap bitmap) {
        mIcon.setImageBitmap(bitmap);
        showIcon();
    }

    /**
     * Show Icon && adjust EditText layout
     */
    private void showIcon() {
        mIcon.setVisibility(VISIBLE);
        mEditTextParams.leftMargin = DimensUtils.dip2px(mContext, 40);
        mEditText.setLayoutParams(mEditTextParams);
    }

    /**
     * expand the editText and reduce the hint
     */
    private void expand() {

        ValueAnimator expandAnimator = ValueAnimator.ofInt(mMinEditTextHeight, mEditTextLayoutHeight);
        expandAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator va) {
                mEditTextLayout.getLayoutParams().height = (Integer) va.getAnimatedValue();
                mEditTextLayout.requestLayout();
            }
        });

        AnimatorSet set = new AnimatorSet();
        set.playTogether(
                ObjectAnimator.ofFloat(mHint, "alpha", 0.4f),
                ObjectAnimator.ofFloat(mHint, "scaleX", 0.7f),
                ObjectAnimator.ofFloat(mHint, "scaleY", 0.7f),
                ObjectAnimator.ofFloat(mHint, "translationX", -mHint.getMeasuredWidth() * 0.15f),
                ObjectAnimator.ofFloat(mHint, "translationY", -mHintHeight * 0.9f),
                ObjectAnimator.ofFloat(mIcon, "alpha", 1),
                ObjectAnimator.ofFloat(mEditText, "alpha", 1),
                expandAnimator
        );

        set.setDuration(ANIMATION_DURATION);
        set.start();
        mExpand = false;
        mEditText.requestFocus();

        //show softinput
        ((InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE))
                .showSoftInput(mEditText, InputMethodManager.SHOW_IMPLICIT);
    }

    /**
     * reduce the editText and expand the hint
     */
    private void reduce() {

        ValueAnimator expandAnimator = ValueAnimator.ofInt(mEditTextLayoutHeight, mMinEditTextHeight);
        expandAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator va) {
                mEditTextLayout.getLayoutParams().height = (Integer) va.getAnimatedValue();
                mEditTextLayout.requestLayout();
            }
        });

        AnimatorSet set = new AnimatorSet();
        set.playTogether(
                ObjectAnimator.ofFloat(mHint, "alpha", 1),
                ObjectAnimator.ofFloat(mHint, "scaleX", 1),
                ObjectAnimator.ofFloat(mHint, "scaleY", 1),
                ObjectAnimator.ofFloat(mHint, "translationX", 0),
                ObjectAnimator.ofFloat(mHint, "translationY", 0),
                ObjectAnimator.ofFloat(mIcon, "alpha", 0),
                ObjectAnimator.ofFloat(mEditText, "alpha", 0),
                expandAnimator
        );

        set.setDuration(ANIMATION_DURATION);
        set.start();
        mExpand = true;
        mEditText.clearFocus();

        //hide softinput
        ((InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
    }

    private int getMeasureHeight(View view) {
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        return view.getMeasuredHeight();
    }
}
