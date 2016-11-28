package com.nearby.syncpad;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.nearby.syncpad.models.Participant;
import com.nearby.syncpad.models.User;
import com.nearby.syncpad.storedata.ProfileStore;
import com.nearby.syncpad.util.Constants;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";

    @Inject
    DatabaseReference mDatabase;

    @Inject
    FirebaseAuth mAuth;

    @BindView(R.id.field_email)
    EditText fieldEmail;

    @BindView(R.id.field_password)
    EditText fieldPassword;

    @BindView(R.id.button_sign_in)
    Button buttonSignIn;

    @BindView(R.id.button_sign_up)
    Button buttonSignUp;

    @BindView(R.id.forgot_password)
    TextView forgotPassword;

    private ProgressDialog mProgressDialog;

    @Inject
    ProfileStore mProfileStore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        ((SyncPadApplication) getApplication()).getMyApplicationComponent().inject(this);

        setClickListeners();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void setClickListeners() {
        // Click listeners
        buttonSignIn.setOnClickListener(this);
        buttonSignUp.setOnClickListener(this);
        forgotPassword.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Check auth on Activity start
        if (mAuth.getCurrentUser() != null) {
            onAuthSuccess(mAuth.getCurrentUser());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_sign_in:
                signIn();
                break;
            case R.id.button_sign_up:
                signUp();
                break;
            case R.id.forgot_password:
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
        }
    }

    private void signIn() {
        Log.d(TAG, "signIn");
        if (!validateForm()) {
            return;
        }

        showProgressDialog();
        String email = fieldEmail.getText().toString();
        String password = fieldPassword.getText().toString();


        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signIn:onComplete:" + task.isSuccessful());
                        hideProgressDialog();

                        if (task.isSuccessful()) {
                            onAuthSuccess(task.getResult().getUser());
                        } else {
                            Toast.makeText(LoginActivity.this, R.string.sign_in_failed,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void signUp() {
        Log.d(TAG, "signUp");
        if (!validateForm()) {
            return;
        }

        showProgressDialog();
        String password = fieldPassword.getText().toString();
        String email = fieldEmail.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUser:onComplete:" + task.isSuccessful());
                        hideProgressDialog();

                        if (task.isSuccessful()) {
                            onAuthSuccess(task.getResult().getUser());
                        } else {
                            Toast.makeText(LoginActivity.this, R.string.sign_up_falied,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void onAuthSuccess(FirebaseUser user) {
        String username = usernameFromEmail(user.getEmail());

        //Save username and email in my profile shared preferences
        saveBasicInfoToProfile(user, username);
        // Write new user
        writeNewUser(user.getUid(), username, user.getEmail());

        // Go to MainActivity
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }

    private void saveBasicInfoToProfile(FirebaseUser user, String username) {
        Participant participant = new Participant();
        participant.setEmailAddress(user.getEmail());
        participant.setName(username);
        mProfileStore.saveProfile(participant);
    }

    private String usernameFromEmail(String email) {
        if (email.contains(getString(R.string.at_the_rate))) {
            return email.split(getString(R.string.at_the_rate))[0];
        } else {
            return email;
        }
    }

    private boolean validateForm() {
        boolean result = true;
        if (TextUtils.isEmpty(fieldEmail.getText().toString())) {
            fieldEmail.setError(getString(R.string.required));
            result = false;
        } else {
            fieldEmail.setError(null);
        }

        if (TextUtils.isEmpty(fieldPassword.getText().toString())) {
            fieldPassword.setError(getString(R.string.required));
            result = false;
        } else {
            fieldPassword.setError(null);
        }

        return result;
    }

    // [START basic_write]
    private void writeNewUser(String userId, String name, String email) {
        User user = new User(name, email);
        mDatabase.child(Constants.USERS).child(userId).setValue(user);
    }
    // [END basic_write]

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage(getString(R.string.loading));
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

}
