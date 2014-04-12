package circleplus.app;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import circleplus.app.http.CirclePlusApi;

public class RegisterActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.register_layout);

        final EditText mUsernameEdit = (EditText) findViewById(R.id.username_edit);
        final EditText mPasswordEdit = (EditText) findViewById(R.id.password_edit);
        final EditText mEmailEdit = (EditText) findViewById(R.id.email_edit);
        final EditText mPhoneEdit = (EditText) findViewById(R.id.phone_edit);
        final Button mRegisterButton = (Button) findViewById(R.id.register_button);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mRegisterButton.setEnabled(usernameEditTextFieldIsValid()
                        && passwordEditTextFieldIsValid()
                        && emailEditTextFieldIsValid()
                        && phoneNumberEditTextFieldIsValid());
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

            private boolean emailEditTextFieldIsValid() {
                return !TextUtils.isEmpty(mUsernameEdit.getText());
            }

            private boolean phoneNumberEditTextFieldIsValid() {
                return !TextUtils.isEmpty(mUsernameEdit.getText());
            }
        };
        mUsernameEdit.addTextChangedListener(textWatcher);
        mPasswordEdit.addTextChangedListener(textWatcher);
        mEmailEdit.addTextChangedListener(textWatcher);
        mPhoneEdit.addTextChangedListener(textWatcher);

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterData data = new RegisterData();
                data.username = mUsernameEdit.getText().toString();
                data.password = mPasswordEdit.getText().toString();
                data.email = mEmailEdit.getText().toString();
                data.phone = mPhoneEdit.getText().toString();
                // TODO:
                data.gender = "Male";

                StringBuilder sb = new StringBuilder();
                sb.append("get data field \n");
                sb.append(data.username);
                sb.append(data.password);
                sb.append(data.email);
                sb.append(data.phone);
                sb.append(data.gender);
                Toast.makeText(RegisterActivity.this, sb.toString(), Toast.LENGTH_LONG).show();

                (new RegisterTask()).execute(data);
            }
        });

        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private class RegisterTask extends AsyncTask<RegisterData, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(RegisterData... params) {
            CirclePlusApi api = new CirclePlusApi();
            circleplus.app.types.Status status = null;
            try {
                status = api.register(
                        params[0].username, params[0].password, params[0].email,
                        params[0].phone, params[0].gender);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return status == null ? "" : status.getContent();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(RegisterActivity.this, s, Toast.LENGTH_LONG).show();
        }
    }

    private static class RegisterData {
        String username;
        String password;
        String email;
        String phone;
        String gender;
    }
}
