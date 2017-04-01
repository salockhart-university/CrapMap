package ca.team2.crapmap.controller;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import ca.team2.crapmap.R;
import ca.team2.crapmap.service.RequestHandler;
import ca.team2.crapmap.service.UserService;

public class LoginActivity extends AppCompatActivity {

    // UI references.
    private EditText mUsernameView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mUsernameView = (EditText) findViewById(R.id.login_username);

        mPasswordView = (EditText) findViewById(R.id.login_password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mLoginButton = (Button) findViewById(R.id.login_button);
        mLoginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private void attemptLogin() {
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        String username = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        }

        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            UserService.login(this, "Logging in...", username, password, new RequestHandler() {
                @Override
                public void callback(Object result) {
                    JSONObject json = (JSONObject) result;

                    if (json == null) {
                        mPasswordView.setError(getString(R.string.error_incorrect_password));
                        mPasswordView.requestFocus();
                    } else {
                        String token = null;
                        String name = null;
                        try {
                            token = json.get("token").toString();
                            name = json.get("name").toString();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        SharedPreferences settings = getSharedPreferences("LOGIN_TOKEN", 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("token", token);
                        editor.putString("name", name);
                        editor.commit();

                        finish();
                    }
                }
            });
        }
    }
}

