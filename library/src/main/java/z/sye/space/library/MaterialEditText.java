/**
 * @(#) z.sye.space.cleanedittextlibrary 2016/1/11;
 * <p/>
 * Copyright (c), 2009 深圳孔方兄金融信息服务有限公司（Shenzhen kfxiong
 * Financial Information Service Co. Ltd.）
 * <p/>
 * 著作权人保留一切权利，任何使用需经授权。
 */
package z.sye.space.library;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.util.AttributeSet;
import android.util.Log;
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

import z.sye.space.cleanedittextlibrary.R;
import z.sye.space.library.listeners.OnErrorListener;
import z.sye.space.library.listeners.OnGetFocusListener;
import z.sye.space.library.listeners.OnLostFocusListener;

/**
 * Created by Syehunter on 2016/1/11.
 */
public class MaterialEditText extends FrameLayout {

    private Context mContext;
    private TextView mHint;
    private FrameLayout mEditTextContent;
    private FrameLayout mEditTextLayout;
    private ImageView mIcon;
    private EditText mEditText;
    private FrameLayout mCleanLayout;
    private ImageView mCleanIcon;
    private TextView mError;
    private TextView mWordCount;

    //EditText config
    private float mInputTextSize = -1;
    private int mInputColor = -1;
    private int mInputIconId = -1;
    private int mCleanIconId = -1;
    private int mUnderlineColor = -1;
    private int mCursorColor = -1;

    //Hint config
    private String mHintText = "";
    private float mHintScale = 0.7f;
    private int mHintColor = -1;
    private int mHintScaleColor = -1;

    //Error config
    private float mErrorSize = -1;
    private int mErrorColor = -1;
    private boolean mErrorShow = false;

    //Length config
    private int mWordCountColor = -1;
    private int mMaxLength = 0;
    private boolean mWordCountEnabled = true;

    /**
     * if mEditText could expand or not
     */
    private boolean mExpand = true;
    private int mMinEditTextHeight = 2;
    private int mEditTextLayoutHeight;
    private int mHintHeight;

    private long ANIMATION_DURATION = 100;

    private OnGetFocusListener mOnGetFocusListener;
    private OnLostFocusListener mOnLostFocusListener;
    private OnErrorListener mOnErrorListener;

    public MaterialEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        parseAttrs(mContext.obtainStyledAttributes(attrs, R.styleable.CleanEdt));
        init();
    }

    private void parseAttrs(TypedArray a) {
        mMinEditTextHeight = DimensUtils.dp2px(mContext, mMinEditTextHeight);//convert minEditTextHeight to dp

        mInputTextSize = a.getDimension(R.styleable.CleanEdt_inputTextSize,
                getResources().getDimension(R.dimen.hint_size_default));
        mInputColor = a.getColor(R.styleable.CleanEdt_inputTextColor,
                getResources().getColor(R.color.black));
        mInputIconId = a.getResourceId(R.styleable.CleanEdt_inputIcon, mInputIconId);
        mCleanIconId = a.getResourceId(R.styleable.CleanEdt_cleanIcon, mCleanIconId);
        mUnderlineColor = a.getColor(R.styleable.CleanEdt_underlineColor,
                getResources().getColor(R.color.underline));
        mCursorColor = a.getColor(R.styleable.CleanEdt_underlineColor,
                getResources().getColor(R.color.underline));
        mHintText = a.getString(R.styleable.CleanEdt_hint);
        mHintScale = a.getFloat(R.styleable.CleanEdt_hintScale, mHintScale);
        mHintColor = a.getColor(R.styleable.CleanEdt_hintColor,
                getResources().getColor(R.color.black));
        mHintScaleColor = a.getColor(R.styleable.CleanEdt_hintScaleColor, mHintScaleColor);
        mErrorSize = a.getDimension(R.styleable.CleanEdt_errorSize,
                getResources().getDimension(R.dimen.error_size_defalut));
        mErrorColor = a.getColor(R.styleable.CleanEdt_errorColor,
                getResources().getColor(R.color.red));
        mMaxLength = a.getInteger(R.styleable.CleanEdt_length, mMaxLength);
        mWordCountEnabled = a.getBoolean(R.styleable.CleanEdt_wordCountEnabled, mWordCountEnabled);
        mWordCountColor = a.getColor(R.styleable.CleanEdt_wordCountColor,
                getResources().getColor(R.color.black));
        ANIMATION_DURATION = a.getInt(R.styleable.CleanEdt_expandDuration, (int) ANIMATION_DURATION);
        a.recycle();
    }

    private void init() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.edittext_clean_material, this);
        mHint = (TextView) view.findViewById(R.id.tv_material_hint);
        mEditTextContent = (FrameLayout) view.findViewById(R.id.fl_material_content);
        mEditTextLayout = (FrameLayout) view.findViewById(R.id.fl_material_edittext);
        mIcon = (ImageView) view.findViewById(R.id.iv_material_icon);
        mEditText = (EditText) view.findViewById(R.id.edt_material);
        mCleanLayout = (FrameLayout) view.findViewById(R.id.fl_material_clean);
        mCleanIcon = (ImageView) view.findViewById(R.id.iv_material_clean);
        mError = (TextView) findViewById(R.id.tv_material_error);
        mWordCount = (TextView) findViewById(R.id.tv_material_wordcount);

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
        cursorColor(mCursorColor);
        mEditText.setOnFocusChangeListener(mOnFocusChangeListener);

        //show no icon as default
        if (mInputIconId == -1)
            mIcon.setVisibility(GONE);

        //show no error as default
        mError.setVisibility(GONE);

        //set editText height to 3
        mEditTextLayout.getLayoutParams().height = mMinEditTextHeight;
        mEditTextLayout.setBackgroundColor(mUnderlineColor);

        mHint.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("mHintId", mHint.hashCode() + "");
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

    /**
     * init Configurations defined in xml
     */
    private void initConfigurations() {
        this.inputSize(TypedValue.COMPLEX_UNIT_PX, mInputTextSize);
        this.inputColor(mInputColor);
        if (mInputIconId != -1)
            this.icon(mInputIconId);
        if (mCleanIconId != -1)
            this.cleanIcon(mCleanIconId);
        this.hint(mHintText);
        this.hintColor(mHintColor);
        this.hintScale(mHintScale);
        if (mHintScaleColor != -1)
            this.hintScaleColor(mHintScaleColor);
        this.errorColor(mErrorColor);
        this.errorSize(TypedValue.COMPLEX_UNIT_PX, mErrorSize);
        if (mMaxLength != 0)
            this.maxLength(mMaxLength);
        this.wordCountEnabled(mWordCountEnabled);
        if (mWordCountColor != -1)
            this.wordCountColor(mWordCountColor);
    }

    private OnFocusChangeListener mOnFocusChangeListener = new OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                mCleanLayout.setVisibility(TextUtils.isEmpty(getText()) ? GONE : VISIBLE);
                mEditText.addTextChangedListener(mTextWatcher);
                handleEditTextLength();
            } else {
                if (TextUtils.isEmpty(getText()) && !mExpand) {
                    //when lost focus and noting input
                    //if could reduce
                    reduceEditText();
                }
                mCleanLayout.setVisibility(GONE);
                if (null != mOnLostFocusListener) {
                    mOnLostFocusListener.onLostFocus();
                }
                if (null != mOnErrorListener) {
                    mError.setVisibility(mOnErrorListener.onError(getText()) ? VISIBLE : GONE);
                }
                hideWordCount();
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
            handleEditTextLength();
        }

        @Override
        public void afterTextChanged(Editable s) {
            mCleanLayout.setVisibility(TextUtils.isEmpty(getText()) ? GONE : VISIBLE);
            if (null != mOnGetFocusListener) {
                mOnGetFocusListener.afterTextChanged(s);
            }
        }
    };

    private void handleEditTextLength() {
        if (mWordCountEnabled && !TextUtils.isEmpty(getText()) &&
                (mMaxLength = getEditTextMaxLength() == 0 ? mMaxLength : getEditTextMaxLength()) != 0) {
            //only when word count enabled and editText has both input and length limit
            //word count show
            showWordCount(getText().length(), mMaxLength);
        } else {
            hideWordCount();
        }
    }

    private void showWordCount(int currentLength, int maxLength) {
        mWordCount.setText(currentLength + " / " + maxLength);
        mWordCount.setVisibility(VISIBLE);
    }

    private void hideWordCount() {
        mWordCount.setText("");
        mWordCount.setVisibility(GONE);
    }

    /**
     * @return max length of editText
     */
    private int getEditTextMaxLength() {
        int length = 0;
        try {
            InputFilter[] filters = mEditText.getFilters();
            for (InputFilter filter : filters) {
                Class<?> clazz = filter.getClass();
                if (clazz.getName().equals("android.text.InputFilter$LengthFilter")) {
                    Field[] fields = clazz.getDeclaredFields();
                    for (Field field : fields) {
                        if (field.getName().equals("mMax")) {
                            field.setAccessible(true);
                            length = (int) field.get(filter);
                        }
                    }
                }
            }
        } catch (Exception ignoreException) {
        }
        return length;
    }

    /**
     * expand the editText and reduce the hint
     */
    private void expandEditText() {
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
                ObjectAnimator.ofFloat(mHint, "alpha", 0.6f),
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
                Log.i("mEditLayoutIdExpand", mEditTextLayout.hashCode() + "");
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (mHintScaleColor != -1) {
                    mHint.setTextColor(mHintScaleColor);
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
        ValueAnimator reduceAnimator = ValueAnimator.ofInt(mEditTextLayoutHeight, mMinEditTextHeight);
        reduceAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
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
                reduceAnimator
        );
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mHint.setTextColor(mHintColor);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mEditTextLayout.setBackgroundColor(mUnderlineColor);
                Log.i("mEditLayoutIdReduce", mEditTextLayout.hashCode() + "");
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
     *
     * @param resId
     */
    public MaterialEditText icon(int resId) {
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
     *
     * @param resId
     */
    public MaterialEditText cleanIcon(int resId) {
        mCleanIconId = resId;
        mCleanIcon.setImageResource(mCleanIconId);
        return this;
    }

    /**
     * Set the cursor color of the EditText
     *
     * @param color
     */
    public MaterialEditText cursorColor(int color) {
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
     *
     * @param onGetFocusListener
     */
    public MaterialEditText setOnGetFocusListener(OnGetFocusListener onGetFocusListener) {
        mOnGetFocusListener = onGetFocusListener;
        return this;
    }

    /**
     * Do sth. when EditText lost focus
     *
     * @param onLostFocusListener
     */
    public MaterialEditText setOnLostFocusListener(OnLostFocusListener onLostFocusListener) {
        mOnLostFocusListener = onLostFocusListener;
        return this;
    }

    /**
     * Do sth. when error occurs
     *
     * @param onErrorListener
     * @return
     */
    public MaterialEditText setOnErrorListener(OnErrorListener onErrorListener) {
        mOnErrorListener = onErrorListener;
        return this;
    }

    /**
     * Set text for EditText
     *
     * @param s
     * @return
     */
    public MaterialEditText inputText(String s) {
        mEditText.setText(s);
        //move the cursor position
        mEditText.setSelection(s.length());
        return this;
    }

    /**
     * Set EditText textSize and LargeHintSize
     *
     * @param size
     * @return
     */
    public MaterialEditText inputSize(float size) {
        mInputTextSize = size;
        mEditText.setTextSize(mInputTextSize);
        mHint.setTextSize(mInputTextSize);
        return this;
    }

    public MaterialEditText inputSize(int unit, float size) {
        mInputTextSize = size;
        mEditText.setTextSize(unit, mInputTextSize);
        mHint.setTextSize(unit, mInputTextSize);
        return this;
    }

    /**
     * Set editText textColor
     *
     * @param color
     * @return
     */
    public MaterialEditText inputColor(int color) {
        mInputColor = color;
        mEditText.setTextColor(mInputColor);
        return this;
    }

    /**
     * @param hint
     * @return
     */
    public MaterialEditText hint(@NonNull String hint) {
        mHint.setText(hint);
        return this;
    }

    /**
     * Set the color of Large hint
     *
     * @param color
     * @return
     */
    public MaterialEditText hintColor(int color) {
        mHintColor = color;
        mHint.setTextColor(mHintColor);
        return this;
    }

    /**
     * Set the hint scale for ObjectAnimator, defaultValue is 0.7f
     *
     * @param scale
     * @return
     */
    public MaterialEditText hintScale(float scale) {
        mHintScale = scale;
        return this;
    }

    /**
     * Set the small hint textColor
     *
     * @param color
     * @return
     */
    public MaterialEditText hintScaleColor(int color) {
        mHintScaleColor = color;
        return this;
    }

    /**
     * Set the underline color of editText
     *
     * @param color
     * @return
     */
    public MaterialEditText underlineColor(int color) {
        mUnderlineColor = color;
        mEditText.getBackground().setColorFilter(mUnderlineColor, PorterDuff.Mode.SRC_ATOP);
        if (!mExpand) {
            mEditTextLayout.setBackgroundColor(mUnderlineColor);
        }
        return this;
    }

    /**
     * Set the error promt
     *
     * @param error
     * @return
     */
    public MaterialEditText error(String error) {
        mError.setText(error);
        return this;
    }

    /**
     * Set error textSize
     *
     * @param size
     * @return
     */
    public MaterialEditText errorSize(float size) {
        mErrorSize = size;
        mError.setTextSize(mErrorSize);
        return this;
    }

    /**
     * Set error textSize
     *
     * @param size
     * @return
     */
    public MaterialEditText errorSize(int unit, float size) {
        mErrorSize = size;
        mError.setTextSize(unit, mErrorSize);
        return this;
    }

    /**
     * Set error textColor
     *
     * @param color
     * @return
     */
    public MaterialEditText errorColor(int color) {
        mErrorColor = color;
        mError.setTextColor(mErrorColor);
        return this;
    }

    /**
     * Discourage user from using this method
     * you'd better control the error's visibility in OnErrorListener
     * <p/>
     * However, if u don't want use this view as an error view, do what u want
     *
     * @param show
     * @return
     */
    public MaterialEditText errorShow(boolean show) {
        mErrorShow = show;
        mError.setVisibility(mErrorShow ? VISIBLE : GONE);
        return this;
    }

    /**
     * Set the word count textcolor
     *
     * @param color
     * @return
     */
    public MaterialEditText wordCountColor(int color) {
        mWordCountColor = color;
        mWordCount.setTextColor(mWordCountColor);
        return this;
    }

    /**
     * Set expand animator duration, default is 100
     *
     * @param duration
     * @return
     */
    public MaterialEditText duration(long duration) {
        ANIMATION_DURATION = duration;
        return this;
    }

    public MaterialEditText inputType(int type) {
        mEditText.setInputType(type);
        return this;
    }

    /**
     * Set editText max length
     *
     * @param length
     * @return
     */
    public MaterialEditText maxLength(int length) {
        mMaxLength = length;
        mEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(mMaxLength)});
        return this;
    }

    /**
     * Return false if length calculation not allow
     *
     * @param lengthEnabled
     * @return
     */
    public MaterialEditText wordCountEnabled(boolean lengthEnabled) {
        mWordCountEnabled = lengthEnabled;
        mWordCount.setVisibility(mWordCountEnabled ? VISIBLE : GONE);
        return this;
    }

    public MaterialEditText filters(InputFilter[] inputFilters) {
        mEditText.setFilters(inputFilters);
        return this;
    }

    public MaterialEditText keyListener(KeyListener input) {
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
     *
     * @return
     */
    public EditText real() {
        return mEditText;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        MdEditTextSavedState ss = new MdEditTextSavedState(super.onSaveInstanceState());
        ss.inputTextSize = mInputTextSize;
        ss.inputColor = mInputColor;
        ss.inputIconId = mInputIconId;
        ss.cleanIconId = mCleanIconId;
        ss.underlineColor = mUnderlineColor;
        ss.cursorColor = mCursorColor;
        ss.hintText = mHintText;
        ss.hintScale = mHintScale;
        ss.hintColor = mHintColor;
        ss.hintScaleColor = mHintScaleColor;
        ss.errorSize = mErrorSize;
        ss.errorColor = mErrorColor;
        ss.wordCountColor = mWordCountColor;
        ss.maxLength = mMaxLength;
        ss.wordCountEnabled = mWordCountEnabled;
        return ss;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof MdEditTextSavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        MdEditTextSavedState ss = (MdEditTextSavedState) state;
        super.onRestoreInstanceState(ss);
        mInputTextSize = ss.inputTextSize;
        mInputColor = ss.inputColor;
        mInputIconId = ss.inputIconId;
        mCleanIconId = ss.cleanIconId;
        mUnderlineColor = ss.underlineColor;
        mCursorColor = ss.cursorColor;
        mHintText = ss.hintText;
        mHintScale = ss.hintScale;
        mHintColor = ss.hintColor;
        mHintScaleColor = ss.hintScaleColor;
        mErrorSize = ss.errorSize;
        mErrorColor = ss.errorColor;
        mWordCountColor = ss.wordCountColor;
        mMaxLength = ss.maxLength;
        mWordCountEnabled = ss.wordCountEnabled;
    }

    static class MdEditTextSavedState extends BaseSavedState {

        Float inputTextSize;
        Integer inputColor;
        Integer inputIconId;
        Integer cleanIconId;
        Integer underlineColor;
        Integer cursorColor;
        String hintText;
        Float hintScale;
        Integer hintColor;
        Integer hintScaleColor;
        Float errorSize;
        Integer errorColor;
        Boolean errorShow;
        Integer wordCountColor;
        Integer maxLength;
        Boolean wordCountEnabled;

        private static final Parcelable.Creator<MdEditTextSavedState> mCreator = new Creator<MdEditTextSavedState>() {
            @Override
            public MdEditTextSavedState createFromParcel(Parcel source) {
                return new MdEditTextSavedState(source);
            }

            @Override
            public MdEditTextSavedState[] newArray(int size) {
                return new MdEditTextSavedState[size];
            }
        };

        public MdEditTextSavedState(Parcelable superState) {
            super(superState);
        }

        private MdEditTextSavedState(Parcel source) {
            super(source);
            inputTextSize = source.readFloat();
            inputColor = source.readInt();
            inputIconId = source.readInt();
            cleanIconId = source.readInt();
            underlineColor = source.readInt();
            cursorColor = source.readInt();
            hintText = source.readString();
            hintScale = source.readFloat();
            hintColor = source.readInt();
            hintScaleColor = source.readInt();
            errorSize = source.readFloat();
            errorColor = source.readInt();
            errorShow = source.readByte() == 1;
            wordCountColor = source.readInt();
            maxLength = source.readInt();
            wordCountEnabled = source.readByte() == 1;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeFloat(inputTextSize);
            out.writeInt(inputColor);
            out.writeInt(inputIconId);
            out.writeInt(cleanIconId);
            out.writeInt(underlineColor);
            out.writeInt(cursorColor);
            out.writeString(hintText);
            out.writeFloat(hintScale);
            out.writeInt(hintColor);
            out.writeInt(hintScaleColor);
            out.writeFloat(errorSize);
            out.writeInt(errorColor);
            out.writeByte((byte) (errorShow ? 1 : 0));
            out.writeInt(wordCountColor);
            out.writeInt(maxLength);
            out.writeByte((byte) (wordCountEnabled ? 1 : 0));
        }
    }
}
