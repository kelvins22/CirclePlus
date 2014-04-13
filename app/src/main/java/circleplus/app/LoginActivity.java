package circleplus.app;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import circleplus.app.http.CirclePlusApi;
import circleplus.app.types.BaseType;
import circleplus.app.types.User;
import circleplus.app.utils.UserUtils;

public class LoginActivity extends ActionBarActivity {

    private EditText mUsernameEdit, mPasswordEdit;
    private Button mLoginButton;
    private LoginTask mTask;

    private static final int GO_TO_REGISTER_REQ_CODE = 0x1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.login_layout);

        setResult(Activity.RESULT_CANCELED);

        mUsernameEdit = (EditText) findViewById(R.id.login_username_edit);
        mPasswordEdit = (EditText) findViewById(R.id.login_password_edit);
        mLoginButton = (Button) findViewById(R.id.login_button);
        Button mToRegisterButton = (Button) findViewById(R.id.go_to_register_button);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mLoginButton.setEnabled(usernameEditTextFieldIsValid()
                        && passwordEditTextFieldIsValid());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

            private boolean usernameEditTextFieldIsValid() {
                return !TextUtils.isEmpty(mUsernameEdit.getText());
            }

            private boolean passwordEditTextFieldIsValid() {
                return !TextUtils.isEmpty(mUsernameEdit.getText());
            }
        };
        mUsernameEdit.addTextChangedListener(textWatcher);
        mPasswordEdit.addTextChangedListener(textWatcher);

        mLoginButton.setEnabled(false);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginData data = new LoginData();
                data.username = mUsernameEdit.getText().toString();
                data.password = mPasswordEdit.getText().toString();
                mTask = new LoginTask();
                mTask.execute(data);
            }
        });

        mToRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,
                        RegisterActivity.class);
                startActivityForResult(intent, GO_TO_REGISTER_REQ_CODE);
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mTask != null) {
            mTask.cancel(true);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == GO_TO_REGISTER_REQ_CODE) {
            Bundle bundle = data.getExtras();
            String username = "", password = "";
            if (bundle.containsKey("key_register_username")) {
                username = (String) bundle.get("key_register_username");
            }
            if (bundle.containsKey("key_register_password")) {
                password = (String) bundle.get("key_register_password");
            }
            if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
                mTask.cancel(true);
                mTask = new LoginTask();
                LoginData loginData = new LoginData();
                loginData.username = username;
                loginData.password = password;
                mTask.execute(loginData);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class LoginTask extends AsyncTask<LoginData, Void, BaseType> {
        @Override
        protected BaseType doInBackground(LoginData... params) {
            CirclePlusApi api = new CirclePlusApi();
            BaseType result = null;
            try {
                result = api.login(params[0].username, params[0].password);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            LoginActivity.this.setProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected void onPostExecute(BaseType status) {
            super.onPostExecute(status);
            LoginActivity.this.setProgressBarIndeterminateVisibility(false);
            if (status == null) {
                Toast.makeText(LoginActivity.this, "Network error",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (status instanceof circleplus.app.types.Status) {
                Toast.makeText(LoginActivity.this,
                        ((circleplus.app.types.Status) status).getMessage(),
                        Toast.LENGTH_LONG).show();
            } else if (status instanceof circleplus.app.types.User) {
                final User user = (User) status;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        UserUtils.storeUserInfo(LoginActivity.this, user);
                        setResult(Activity.RESULT_OK);
                        finish();
                    }
                }).start();
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            LoginActivity.this.setProgressBarIndeterminateVisibility(false);
        }
    }

    private static class LoginData {
        String username;
        String password;
    }
}
