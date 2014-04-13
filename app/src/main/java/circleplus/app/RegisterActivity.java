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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.IOException;

import circleplus.app.http.CirclePlusApi;
import circleplus.app.http.ResponseCode;

public class RegisterActivity extends ActionBarActivity {

    private EditText mUsernameEdit;
    private EditText mPasswordEdit;
    private EditText mEmailEdit;
    private EditText mPhoneEdit;

    private String mGender = "Male";
    private String mUsername = null;
    private String mPassword = null;

    private RegisterTask mTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.register_layout);

        setResult(Activity.RESULT_CANCELED);

        mUsernameEdit = (EditText) findViewById(R.id.username_edit);
        mPasswordEdit = (EditText) findViewById(R.id.password_edit);
        mEmailEdit = (EditText) findViewById(R.id.email_edit);
        mPhoneEdit = (EditText) findViewById(R.id.phone_edit);
        final Button mRegisterButton = (Button) findViewById(R.id.register_button);
        final RadioGroup mRadioGroup = (RadioGroup) findViewById(R.id.gender_group);
        final RadioButton mMaleButton = (RadioButton) findViewById(R.id.male_button);
        final RadioButton mFemaleButton = (RadioButton) findViewById(R.id.female_button);

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

        mRegisterButton.setEnabled(false);
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterData data = new RegisterData();
                data.username = mUsernameEdit.getText().toString();
                data.password = mPasswordEdit.getText().toString();
                data.email = mEmailEdit.getText().toString();
                data.phone = mPhoneEdit.getText().toString();
                data.gender = mGender;

                // store value for activity result
                mUsername = data.username;
                mPassword = data.password;

                mTask = new RegisterTask();
                mTask.execute(data);
            }
        });

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == mMaleButton.getId()) {
                    mGender = "Male";
                } else if (checkedId == mFemaleButton.getId()) {
                    mGender = "Female";
                }
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
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

    @Override
    protected void onStop() {
        super.onStop();

        if (mTask != null) {
            mTask.cancel(true);
        }
    }

    private class RegisterTask extends AsyncTask<RegisterData, Void, circleplus.app.types.Status> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            RegisterActivity.this.setProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected circleplus.app.types.Status doInBackground(RegisterData... params) {
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
            return status;
        }

        @Override
        protected void onPostExecute(circleplus.app.types.Status s) {
            super.onPostExecute(s);
            RegisterActivity.this.setProgressBarIndeterminateVisibility(false);
            if (s == null) {
                Toast.makeText(RegisterActivity.this, "Network error",
                        Toast.LENGTH_SHORT).show();
                setResult(Activity.RESULT_CANCELED);
                return;
            }

            Toast.makeText(RegisterActivity.this, s.getMessage(),
                    Toast.LENGTH_LONG).show();
            if (s.getStatusCode() == ResponseCode.STATUS_OK) {
                Editable editable = mUsernameEdit.getText();
                if (editable != null) {
                    editable.clear();
                }
                editable = mPasswordEdit.getText();
                if (editable != null) {
                    editable.clear();
                }
                editable = mEmailEdit.getText();
                if (editable != null) {
                    editable.clear();
                }
                editable = mPhoneEdit.getText();
                if (editable != null) {
                    editable.clear();
                }

                Bundle bundle = new Bundle();
                bundle.putString("key_register_username", mUsername);
                bundle.putString("key_register_password", mPassword);
                Intent intent = new Intent();
                intent.putExtras(bundle);
                setResult(Activity.RESULT_OK, intent);
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            RegisterActivity.this.setProgressBarIndeterminateVisibility(false);
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
