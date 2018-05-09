package com.savvysherpa.loginlibrary;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.StyleableRes;
import android.support.design.widget.TextInputLayout;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.savvysherpa.loginlibrary.R;
import com.savvysherpa.loginlibrary.listeners.SubmitListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class LoginView extends LinearLayout {
    @StyleableRes
    int styleRes0 = 0;
    @StyleableRes
    int styleRes1 = 1;
    @StyleableRes
    int styleRes2 = 2;
    @StyleableRes
    int styleRes3 = 3;
    @StyleableRes
    int styleRes4 = 4;

    TextView SBLoginPassword, SBLoginTitle, SBLoginUsername;
    TextInputLayout SBLoginUsernameHint, SBLoginPasswordHint;
    Button SBLoginSubmit;
    ImageView SBLoginImage;
    SubmitListener submitListener;
    LinearLayout loginForm;
    private View mLoginFormView;
    private View mProgressView;
    EditorActionListener editorActionListener = new EditorActionListener();

    public final String METHOD_GET = "AUTH_GET";
    public final String METHOD_POST = "AUTH_POST";
    private String USERNAME_KEY = "username";
    private String PASSWORD_KEY = "password";
    private String AUTH_ENDPOINT = "";
    private String METHOD_USED = METHOD_GET;

    private JSONObject extraParams = new JSONObject();


    public LoginView(Context context) {
        super(context);
        init(context, null);
    }

    public LoginView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public LoginView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }


    private void init(Context context, AttributeSet attrs) {
        inflate(context, R.layout.login_view, this);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LoginView);
        CharSequence SBLoginHint = typedArray.getText(R.styleable.LoginView_username_hint);
        CharSequence SBPasswordHint = typedArray.getText(R.styleable.LoginView_password_hint);
        CharSequence SBSubmitButtonText = typedArray.getText(R.styleable.LoginView_submit_button_text);
        CharSequence SBLoginTitle = typedArray.getText(R.styleable.LoginView_title);
        Drawable SBLoginImageDrawable = typedArray.getDrawable(R.styleable.LoginView_image);
        setupUserInterface();
        setUsernameHint(SBLoginHint);
        setPasswordHint(SBPasswordHint);
        setLoginTitle(SBLoginTitle);
        setSBLoginImage(SBLoginImageDrawable);
        setSubmitButtonText(SBSubmitButtonText);
        typedArray.recycle();
    }

    private void setupUserInterface() {
        mLoginFormView = findViewById(R.id.SBLoginForm);
        SBLoginUsername = findViewById(R.id.SBLoginUsername);
        SBLoginUsernameHint = findViewById(R.id.SBLoginUsernameHint);
        SBLoginPasswordHint = findViewById(R.id.SBLoginPasswordHint);
        SBLoginPassword = findViewById(R.id.SBLoginPassword);
        SBLoginSubmit = findViewById(R.id.SBLoginSubmit);
        SBLoginTitle = findViewById(R.id.SBLoginTitle);
        SBLoginImage = findViewById(R.id.SBLoginImage);
        SBLoginSubmit.setOnClickListener(new ClickListener());
        loginForm = findViewById(R.id.SBLoginForm);
        SBLoginUsername.setOnEditorActionListener(editorActionListener);
        SBLoginPassword.setOnEditorActionListener(editorActionListener);
        mLoginFormView = findViewById(R.id.SBLoginForm);
        mProgressView = findViewById(R.id.SBLoginProgress);
    }

    private class EditorActionListener implements TextView.OnEditorActionListener {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            switch (actionId) {
                case 1:
                    // v.clearFocus();
                    // SBLoginPassword.requestFocus();
                    break;
                case 2:
                    v.clearFocus();
                    try {
                        getParamsAndSubmit();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
            return false;
        }
    }

    public void setUsernameHint(CharSequence value) {
        SBLoginUsernameHint.setHint(value);
    }

    public void setPasswordHint(CharSequence value) {
        SBLoginPasswordHint.setHint(value);
    }

    public void setLoginTitle(CharSequence value) {
        SBLoginTitle.setText(value);
    }

    public void setSubmitButtonText(CharSequence value) {
        SBLoginSubmit.setText(value);
    }

    @SuppressWarnings("Deprecated")
    public void setSBLoginImage(Drawable value) {
        SBLoginImage.setBackgroundDrawable(value);
    }


    /**
     * @param submitListener Callback when Submit happens.
     *                       Pressing enter after inputting password or clicking submit
     */
    public void setOnSubmitResponse(@Nullable SubmitListener submitListener) {
        this.submitListener = submitListener;
    }


    /**
     * @param userNameKey Key used for username value when sending to a server.
     *                    By default set to "username"
     */
    public void setUserNameKey(String userNameKey) {
        this.USERNAME_KEY = userNameKey;
    }

    public String getUserNameKey() {
        return this.USERNAME_KEY;
    }

    /**
     * @param passwordKey Key used for username value when sending to a server.
     *                    By default set to "username"
     */
    public void setPasswordKey(String passwordKey) {
        this.PASSWORD_KEY = passwordKey;
    }

    public String getPasswordKey() {
        return this.PASSWORD_KEY;
    }

    public void showSpinnerProgress(boolean show) {
        showProgress(show);
    }

    private void getParamsAndSubmit() throws Exception {
        if (SBLoginUsername.getText().toString().isEmpty()) {
            SBLoginUsername.setError("Username should not be empty");
            return;
        }
        if (submitListener != null)
            submitListener.Submit(this, SBLoginUsername.getText().toString(), SBLoginPassword.getText().toString());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(this.USERNAME_KEY, SBLoginUsername.getText().toString());
            jsonObject.put(this.PASSWORD_KEY, SBLoginPassword.getText().toString());
            if (extraParams.names() != null) {
                JSONArray keys = extraParams.names();
                for (int i = 0; i < keys.length(); ++i) {
                    try {
                        String key = keys.getString(i);
                        String value = extraParams.getString(key);
                        jsonObject.put(key, value);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            throw e;
        }
    }

    private class ClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case 0x7f0c0007:
                    try {
                        getParamsAndSubmit();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }

        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        SBLoginSubmit.setEnabled(!show);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


}
