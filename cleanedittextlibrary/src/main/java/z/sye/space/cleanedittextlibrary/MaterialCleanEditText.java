/**
 * @(#) z.sye.space.cleanedittextlibrary 2016/1/11;
 * <p/>
 * Copyright (c), 2009 深圳孔方兄金融信息服务有限公司（Shenzhen kfxiong
 * Financial Information Service Co. Ltd.）
 * <p/>
 * 著作权人保留一切权利，任何使用需经授权。
 */
package z.sye.space.cleanedittextlibrary;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Field;

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
    private FrameLayout mCleanLayout;
    private ImageView mCleanIcon;
    private TextView mError;

    //EditText config
    private float mInputTextSize = -1;
    private int mInputColor = -1;
    private int mInputIconId = -1;
    private int mCleanIconId = -1;
    private int mUnderlineColor = -1;
    private int mCursorColor = -1;

    //Hint config
    private float mHintScale = 0.7f;
    private int mHintColor = -1;
    private int mHintScaleColor = -1;

    //Error config
    private float mErrorSize = -1;
    private int mErrorColor = -1;
    private boolean mErrorShow = false;

    /**
     * if mEditText could expand or not
     */
    private boolean mExpand = true;
    private int mMinEditTextHeight = 3;
    private int mHintHeight;
    private int mEditTextLayoutHeight;

    private long ANIMATION_DURATION = 150;

    private OnGetFocusListener mOnGetFocusListener;
    private OnLostFocusListener mOnLostFocusListener;
    private OnErrorListener mOnErrorListener;

    public MaterialCleanEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        parseAttrs(mContext.obtainStyledAttributes(attrs, R.styleable.CleanEdt));
        init();
    }

    private void parseAttrs(TypedArray a) {
//        mInputTextSize = a.getDimensionPixelSize(R.styleable.CleanEdt_inputTextSize, DimensUtils.sp2px(mContext, 18));
        mInputColor = a.getColor(R.styleable.CleanEdt_inputTextColor,
                getResources().getColor(R.color.black));
        mInputIconId = a.getResourceId(R.styleable.CleanEdt_inputIcon, mInputIconId);
        mCleanIconId = a.getResourceId(R.styleable.CleanEdt_cleanIcon, mCleanIconId);
        mUnderlineColor = a.getColor(R.styleable.CleanEdt_underlineColor,
                getResources().getColor(R.color.underline));
        mCursorColor = a.getColor(R.styleable.CleanEdt_underlineColor,
                getResources().getColor(R.color.underline));
        mHintScale = a.getFloat(R.styleable.CleanEdt_hintScale, mHintScale);
        mHintColor = a.getColor(R.styleable.CleanEdt_hintColor,
                getResources().getColor(R.color.black));
        mHintScaleColor = a.getColor(R.styleable.CleanEdt_hintScaleColor, mHintScaleColor);
        mErrorSize = a.getDimensionPixelSize(R.styleable.CleanEdt_errorSize, 14);
        mErrorColor = a.getColor(R.styleable.CleanEdt_errorColor,
                getResources().getColor(R.color.red));
        ANIMATION_DURATION = a.getInt(R.styleable.CleanEdt_expandDuration, (int) ANIMATION_DURATION);
        a.recycle();
    }

    private void init() {
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.edittext_clean_material, this);
        mHint = (TextView) rootView.findViewById(R.id.tv_material_hint);
        mEditTextContent = (FrameLayout) rootView.findViewById(R.id.fl_material_content);
        mEditTextLayout = (FrameLayout) rootView.findViewById(R.id.fl_material_edittext);
        mIcon = (ImageView) rootView.findViewById(R.id.iv_material_icon);
        mEditText = (EditText) rootView.findViewById(R.id.edt_material);
        mCleanLayout = (FrameLayout) rootView.findViewById(R.id.fl_material_clean);
        mCleanIcon = (ImageView) rootView.findViewById(R.id.iv_material_clean);
        mError = (TextView) findViewById(R.id.tv_material_error);

        initConfigurations();

        //init editTextLayout height
        mHintHeight = getMeasureHeight(mHint);
        mEditTextLayoutHeight = getMeasureHeight(mEditTextLayout);
        mEditTextContent.getLayoutParams().height = (int) (mHintHeight * 0.9 + mEditTextLayoutHeight);

        //init hint location
        FrameLayout.LayoutParams hintParams = (LayoutParams) mHint.getLayoutParams();
        hintParams.topMargin = (int) (mHintHeight * 0.7);
        hintParams.width = LayoutParams.MATCH_PARENT;
        hintParams.height = LayoutParams.MATCH_PARENT;
        hintParams.gravity = Gravity.CENTER_VERTICAL;
        mHint.setLayoutParams(hintParams);
        mHint.setGravity(Gravity.CENTER_VERTICAL);

        //init edittext
        mEditText.getBackground().setColorFilter(mUnderlineColor, PorterDuff.Mode.SRC_ATOP);
        setCursorDrawableColor(mCursorColor);
        mEditText.setOnFocusChangeListener(mOnFocusChangeListener);

        //show no icon as default
        mIcon.setVisibility(GONE);

        //show no error as default
        mError.setVisibility(GONE);

        //set editText height to 3
        mEditTextLayout.getLayoutParams().height = mMinEditTextHeight;
        mEditTextLayout.setBackgroundColor(mUnderlineColor);

        mHint.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mExpand) {
                    expandEditText();
                } else {
                    reduceEditText();
                }
            }
        });

        //show no cleanIcon as default
        mCleanLayout.setVisibility(GONE);
        mCleanLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditText.setText("");
            }
        });
    }

    private void initConfigurations() {
        this.setInputSize(mInputTextSize);
        this.setInputColor(mInputColor);
        if (mInputIconId != -1)
            this.setIcon(mInputIconId);
        if (mCleanIconId != -1)
            this.setCleanIcon(mCleanIconId);
        this.setHintColor(mHintColor);
        this.setHintScale(mHintScale);
        if (mHintScaleColor != -1)
            this.setHintScaleColor(mHintScaleColor);
        this.setErrorColor(mErrorColor);
        this.setErrorSize(mErrorSize);
    }

    private OnFocusChangeListener mOnFocusChangeListener = new OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                mCleanLayout.setVisibility(TextUtils.isEmpty(getText()) ? GONE : VISIBLE);
                mEditText.addTextChangedListener(mTextWatcher);
            } else {
                if (TextUtils.isEmpty(getText())) {
                    reduceEditText();
                }
                mCleanLayout.setVisibility(GONE);
                if (null != mOnLostFocusListener) {
                    mOnLostFocusListener.onLostFocus();
                }
                if (null != mOnErrorListener) {
                    mError.setVisibility(mOnErrorListener.onError(getText()) ? VISIBLE : GONE);
                }
            }
        }
    };

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if (null != mOnGetFocusListener) {
                mOnGetFocusListener.beforeTextChanged(s, start, count, after);
            }
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (null != mOnGetFocusListener) {
                mOnGetFocusListener.onTextChanged(s, start, before, count);
            }
            if (null != mOnErrorListener) {
                mError.setVisibility(mOnErrorListener.onError(s) ? VISIBLE : GONE);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            mCleanLayout.setVisibility(TextUtils.isEmpty(getText()) ? GONE : VISIBLE);
            if (null != mOnGetFocusListener) {
                mOnGetFocusListener.afterTextChanged(s);
            }
        }
    };

    /**
     * expand the editText and reduce the hint
     */
    private void expandEditText() {
        ValueAnimator expandAnimator =
                expandLayout(mMinEditTextHeight, mEditTextLayoutHeight, mEditTextLayout);

        AnimatorSet set = new AnimatorSet();
        set.playTogether(
                ObjectAnimator.ofFloat(mHint, "alpha", 0.4f),
                ObjectAnimator.ofFloat(mHint, "scaleX", mHintScale),
                ObjectAnimator.ofFloat(mHint, "scaleY", mHintScale),
                ObjectAnimator.ofFloat(mHint, "translationX",
                        -mHint.getMeasuredWidth() * (1 - mHintScale) / 2),
                ObjectAnimator.ofFloat(mHint, "translationY", -mHintHeight * 1.3f),
                ObjectAnimator.ofFloat(mIcon, "alpha", 1),
                ObjectAnimator.ofFloat(mEditText, "alpha", 1),
                expandAnimator
        );
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mEditTextLayout.setBackgroundColor(
                        getResources().getColor(android.R.color.transparent));
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (mHintScaleColor != -1) {
                    mHint.setTextColor(mHintColor);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
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
    private void reduceEditText() {
        ValueAnimator reduceAnimator =
                expandLayout(mEditTextLayoutHeight, mMinEditTextHeight, mEditTextLayout);

        AnimatorSet set = new AnimatorSet();
        set.playTogether(
                ObjectAnimator.ofFloat(mHint, "alpha", 1),
                ObjectAnimator.ofFloat(mHint, "scaleX", 1),
                ObjectAnimator.ofFloat(mHint, "scaleY", 1),
                ObjectAnimator.ofFloat(mHint, "translationX", 0),
                ObjectAnimator.ofFloat(mHint, "translationY", 0),
                ObjectAnimator.ofFloat(mIcon, "alpha", 0),
                ObjectAnimator.ofFloat(mEditText, "alpha", 0),
                reduceAnimator
        );
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mHint.setTextColor(mHintColor);
                mEditTextLayout.setBackgroundColor(mUnderlineColor);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        set.setDuration(ANIMATION_DURATION);
        set.start();
        mExpand = true;
        mEditText.clearFocus();

        //hide softinput
        ((InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
    }

    private ValueAnimator expandLayout(int fromHeight, int targetHeight, final View view) {
        ValueAnimator expandAnimator = ValueAnimator.ofInt(fromHeight, targetHeight);
        expandAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator va) {
                view.getLayoutParams().height = (Integer) va.getAnimatedValue();
                view.requestLayout();
            }
        });
        expandAnimator.setDuration(ANIMATION_DURATION);
        expandAnimator.start();
        return expandAnimator;
    }

    private int getMeasureHeight(View view) {
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        return view.getMeasuredHeight();
    }

    /**
     * Set the Icon for the EditText
     * @param resId
     */
    public MaterialCleanEditText setIcon(int resId) {
        mInputIconId = resId;
        mIcon.setImageResource(mInputIconId);
        mIcon.setVisibility(VISIBLE);
        mIcon.setAlpha(0f);
        mEditText.setPadding(
                DimensUtils.dp2px(mContext, 40),
                mEditText.getPaddingTop(),
                mEditText.getPaddingRight(),
                mEditText.getPaddingBottom());
        return this;
    }

    /**
     * Set the CleanIcon for the EditText
     * @param resId
     */
    public MaterialCleanEditText setCleanIcon(int resId) {
        mCleanIconId = resId;
        mCleanIcon.setImageResource(mCleanIconId);
        return this;
    }

    /**
     * Set the cursor color of the EditText
     * @param color
     */
    public MaterialCleanEditText setCursorDrawableColor(int color) {
        try {
            mCursorColor = color;
            Field fCursorDrawableRes =
                    TextView.class.getDeclaredField("mCursorDrawableRes");
            fCursorDrawableRes.setAccessible(true);
            int mCursorDrawableRes = fCursorDrawableRes.getInt(mEditText);
            Field fEditor = TextView.class.getDeclaredField("mEditor");
            fEditor.setAccessible(true);
            Object editor = fEditor.get(mEditText);
            Class<?> clazz = editor.getClass();
            Field fCursorDrawable = clazz.getDeclaredField("mCursorDrawable");
            fCursorDrawable.setAccessible(true);

            Drawable[] drawables = new Drawable[2];
            Resources res = mEditText.getContext().getResources();
            drawables[0] = res.getDrawable(mCursorDrawableRes);
            drawables[1] = res.getDrawable(mCursorDrawableRes);
            drawables[0].setColorFilter(color, PorterDuff.Mode.SRC_IN);
            drawables[1].setColorFilter(color, PorterDuff.Mode.SRC_IN);
            fCursorDrawable.set(editor, drawables);
        } catch (final Throwable ignored) {
        }
        return this;
    }

    /**
     * Do sth. when EditText get focus
     * @param onGetFocusListener
     */
    public MaterialCleanEditText setOnGetFocusListener(OnGetFocusListener onGetFocusListener) {
        mOnGetFocusListener = onGetFocusListener;
        return this;
    }

    /**
     * Do sth. when EditText lost focus
     * @param onLostFocusListener
     */
    public MaterialCleanEditText setOnLostFocusListener(OnLostFocusListener onLostFocusListener) {
        mOnLostFocusListener = onLostFocusListener;
        return this;
    }

    /**
     * Do sth. when error occurs
     * @param onErrorListener
     * @return
     */
    public MaterialCleanEditText setOnErrorListener(OnErrorListener onErrorListener) {
        mOnErrorListener = onErrorListener;
        return this;
    }

    /**
     * Set text for EditText
     * @param s
     * @return
     */
    public MaterialCleanEditText setText(String s) {
        mEditText.setText(s);
        //move the cursor position
        mEditText.setSelection(s.length());
        return this;
    }

    /**
     * Set EditText textSize and LargeHintSize
     * @param size
     * @return
     */
    public MaterialCleanEditText setInputSize(float size) {
        mInputTextSize = size;
        mEditText.setTextSize(mInputTextSize);
        mHint.setTextSize(mInputTextSize);
        return this;
    }

    /**
     * Set editText textColor
     * @param color
     * @return
     */
    public MaterialCleanEditText setInputColor(int color) {
        mInputColor = color;
        mEditText.setTextColor(mInputColor);
        return this;
    }

    /**
     * @param hint
     * @return
     */
    public MaterialCleanEditText setHint(@NonNull String hint) {
        mHint.setText(hint);
        return this;
    }

    /**
     * Set the color of Large hint
     * @param color
     * @return
     */
    public MaterialCleanEditText setHintColor(int color) {
        mHintColor = color;
        mHint.setTextColor(mHintColor);
        return this;
    }

    /**
     * Set the hint scale for ObjectAnimator, defaultValue is 0.7f
     * @param scale
     * @return
     */
    public MaterialCleanEditText setHintScale(float scale) {
        mHintScale = scale;
        return this;
    }

    /**
     * Set the small hint textColor
     * @param color
     * @return
     */
    public MaterialCleanEditText setHintScaleColor(int color) {
        mHintScaleColor = color;
        return this;
    }

    /**
     * Set the underline color of editText
     * @param color
     * @return
     */
    public MaterialCleanEditText setUnderlineColor(int color) {
        mUnderlineColor = color;
        mEditText.getBackground().setColorFilter(mUnderlineColor, PorterDuff.Mode.SRC_ATOP);
        if (!mExpand) {
            mEditTextLayout.setBackgroundColor(mUnderlineColor);
        }
        return this;
    }

    /**
     * Set the error promt
     * @param error
     * @return
     */
    public MaterialCleanEditText error(String error) {
        mError.setText(error);
        return this;
    }

    /**
     * Set error textSize
     * @param size
     * @return
     */
    public MaterialCleanEditText setErrorSize(float size) {
        mErrorSize = size;
        mError.setTextSize(mErrorSize);
        return this;
    }

    /**
     * Set error textColor
     * @param color
     * @return
     */
    public MaterialCleanEditText setErrorColor(int color) {
        mErrorColor = color;
        mError.setTextColor(mErrorColor);
        return this;
    }

    /**
     * Discourage user from using this method
     *      you'd better control the error's visibility in OnErrorListener
     *
     * However, if u don't want use this view as an error view, do what u want
     * @param show
     * @return
     */
    public MaterialCleanEditText errorShow(boolean show) {
        mErrorShow = show;
        mError.setVisibility(mErrorShow ? VISIBLE : GONE);
        return this;
    }

    /**
     * Set expand animator duration
     * @param duration
     * @return
     */
    public MaterialCleanEditText setAnimatorDuration(long duration) {
        ANIMATION_DURATION = duration;
        return this;
    }

    public MaterialCleanEditText setInputType(int type) {
        mEditText.setInputType(type);
        return this;
    }

    public MaterialCleanEditText setFilters(InputFilter[] inputFilters) {
        mEditText.setFilters(inputFilters);
        return this;
    }

    public MaterialCleanEditText setKeyListener(KeyListener input) {
        mEditText.setKeyListener(input);
        return this;
    }

    /**
     * @return input text
     */
    public String getText() {
        return mEditText.getText().toString().trim();
    }

    /**
     * Take methods with the real EditText you want that doesn't support in this Class
     * @return
     */
    public EditText real() {
        return mEditText;
    }
}
