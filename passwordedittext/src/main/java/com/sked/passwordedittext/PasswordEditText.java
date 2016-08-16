package com.sked.passwordedittext;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatEditText;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;

import com.sked.passwordedittext.blowfish.Blowfish;

/**
 * All Rights Reserved, QuikSeek
 * Created by Sanjeet on 12-08-2016.
 */
public class PasswordEditText extends RelativeLayout {

    private int width;
    private int height;
    private SharedPreferences sharedPreferences;
    private AppCompatEditText passwordEditText;
    private AppCompatCheckBox hideShowCheckBox;
    private AppCompatCheckBox savePasswordCheckBox;

    private Blowfish blowfish;
    private boolean useTextInputLayout;

    private OnSavePasswordCheckedChangeListener onSavePasswordCheckedChangeListener;

    public PasswordEditText(Context context) {
        super(context);
        initFromAttributes(context, null);
        initView(context);
    }


    public PasswordEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initFromAttributes(context, attrs);
        initView(context);
    }

    public PasswordEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initFromAttributes(context, attrs);
        initView(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PasswordEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initFromAttributes(context, attrs);
        initView(context);
    }

    @SuppressWarnings("ResourceType")
    private void initFromAttributes(Context context, AttributeSet attrs) {
        TypedArray tb = context.obtainStyledAttributes(attrs, R.styleable.PasswordEditText, 0, 0);
        int[] attrsArray = new int[]{android.R.attr.layout_width, android.R.attr.layout_height};
        TypedArray ta = context.obtainStyledAttributes(attrs, attrsArray);
        width = ta.getLayoutDimension(0, ViewGroup.LayoutParams.MATCH_PARENT);
        height = ta.getLayoutDimension(1, ViewGroup.LayoutParams.WRAP_CONTENT);
        useTextInputLayout = tb.getBoolean(R.styleable.PasswordEditText_useTextInputLayout, false);
        ta.recycle();
        tb.recycle();
        blowfish = new Blowfish();
        blowfish.setKey("3y1z1elCW7gKUUH");
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    /**
     * Initializes and creates view elements required for PasswordEditText instance.
     */
    private void initView(Context context) {

        passwordEditText = new AppCompatEditText(context);
        passwordEditText.setId(android.R.id.edit);
        LayoutParams editTextLayoutParams = generateLayoutParams();
        editTextLayoutParams.width = width;
        editTextLayoutParams.height = height;
        passwordEditText.setLayoutParams(editTextLayoutParams);
        passwordEditText.setHint(R.string.label_password);
        passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        passwordEditText.setText(blowfish.decrypt(sharedPreferences.getString("password", "")));


        hideShowCheckBox = new AppCompatCheckBox(context);
        final LayoutParams checkBoxLayoutParams = generateLayoutParams();
        checkBoxLayoutParams.setMargins(0, 0, 0, passwordEditText.getPaddingBottom());
        checkBoxLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        checkBoxLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_END);
        if (useTextInputLayout) {
            checkBoxLayoutParams.addRule(RelativeLayout.ALIGN_BASELINE, android.R.id.inputExtractEditText);
            checkBoxLayoutParams.addRule(RelativeLayout.ALIGN_BOTTOM, android.R.id.inputExtractEditText);
        } else {
            checkBoxLayoutParams.addRule(RelativeLayout.ALIGN_BASELINE, android.R.id.edit);
            checkBoxLayoutParams.addRule(RelativeLayout.ALIGN_BOTTOM, android.R.id.edit);
        }
        hideShowCheckBox.setLayoutParams(checkBoxLayoutParams);
        hideShowCheckBox.setText(R.string.label_show);
        hideShowCheckBox.setTypeface(Typeface.MONOSPACE);
        hideShowCheckBox.setButtonDrawable(R.drawable.ic_visibility);

        final int paddingRight = passwordEditText.getPaddingRight();
        hideShowCheckBox.addOnLayoutChangeListener(new OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                hideShowCheckBox.setPadding(0, 0, paddingRight, 0);
                passwordEditText.setPadding(passwordEditText.getPaddingLeft(), passwordEditText.getPaddingTop(), paddingRight + v.getWidth(), passwordEditText.getPaddingBottom());
            }
        });
        hideShowCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    hideShowCheckBox.setText(R.string.label_hide);
                    passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT);
                } else {
                    passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    hideShowCheckBox.setText(R.string.label_show);
                }
            }
        });

        savePasswordCheckBox = new AppCompatCheckBox(context);
        final LayoutParams savePasswordCheckBoxLayoutParams = generateLayoutParams();
        savePasswordCheckBoxLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        savePasswordCheckBoxLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_START);
        if (useTextInputLayout) {
            savePasswordCheckBoxLayoutParams.addRule(RelativeLayout.BELOW, android.R.id.inputExtractEditText);
        } else {
            savePasswordCheckBoxLayoutParams.addRule(RelativeLayout.BELOW, android.R.id.edit);
        }
        savePasswordCheckBox.setLayoutParams(savePasswordCheckBoxLayoutParams);
        savePasswordCheckBox.setText(R.string.label_save_password);
        savePasswordCheckBox.setChecked(sharedPreferences.getBoolean("save_password", false));

        savePasswordCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sharedPreferences.edit().putBoolean("save_password", isChecked).apply();
                if (onSavePasswordCheckedChangeListener != null) {
                    onSavePasswordCheckedChangeListener.onSavePasswordCheckedChange(isChecked);
                }
            }
        });

        if (useTextInputLayout) {
            TextInputLayout textInputLayout = new TextInputLayout(context);
            textInputLayout.setId(android.R.id.inputExtractEditText);
            LayoutParams layoutParams = generateLayoutParams();
            layoutParams.width = width;
            layoutParams.height = height;
            textInputLayout.setLayoutParams(layoutParams);
            textInputLayout.addView(passwordEditText);
            this.addView(textInputLayout);
        } else {
            this.addView(passwordEditText);
        }

        this.addView(hideShowCheckBox);
        this.addView(savePasswordCheckBox);


    }

    /**
     * Setter for the checkbox check / un-check state
     *
     * @param onSavePasswordCheckedChangeListener the listener instance
     */
    public void setOnSavePasswordCheckedChangeListener(OnSavePasswordCheckedChangeListener onSavePasswordCheckedChangeListener) {
        this.onSavePasswordCheckedChangeListener = onSavePasswordCheckedChangeListener;
    }

    /**
     * Creates default layout parameters
     *
     * @return default layout parameters
     */
    private LayoutParams generateLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (savePasswordCheckBox.isChecked())
            sharedPreferences.edit().putString("password", blowfish.encrypt(passwordEditText.getText().toString())).apply();
        else sharedPreferences.edit().remove("password").apply();
    }

    /**
     * Method returns whether saving of password is enabled / checkbox is checked or not
     *
     * @return is check box checked true/false
     */
    private boolean isSavePasswordEnabled() {
        return savePasswordCheckBox.isChecked();
    }

    /**
     * Encrypts the plain password using the Blowfish
     *
     * @return encrypted password
     */
    public String getPassword() {
        return blowfish.encrypt(passwordEditText.getText().toString());
    }

    /**
     * By default the password is encrypted with BlowFish Encryption Algorithm
     *
     * @return Plain password
     */
    public String getPlainPassword() {
        return passwordEditText.getText().toString();
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
    }

    @Override
    public void addView(View child, int index) {
        super.addView(child, index);
    }

    public void addView(View child) {
        super.addView(child);
    }

    /**
     * Interface used to give callback when savePasswordCheckBox is checked or unchecked
     */
    interface OnSavePasswordCheckedChangeListener {
        void onSavePasswordCheckedChange(boolean isChecked);
    }
}
