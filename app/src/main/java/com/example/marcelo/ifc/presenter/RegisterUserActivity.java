package com.example.marcelo.ifc.presenter;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.marcelo.ifc.R;
import com.example.marcelo.ifc.exception.UserException;
import com.example.marcelo.ifc.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.app.ProgressDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class RegisterUserActivity extends AppCompatActivity {
    private static final String TAG = "RegisterUserActivity";

    @InjectView(R.id.input_name) EditText _nameText;
    @InjectView(R.id.input_email) EditText _emailText;
    @InjectView(R.id.input_password) EditText _passwordText;
    @InjectView(R.id.btn_signup) Button _signupButton;
    @InjectView(R.id.link_login) TextView _loginLink;
    private FirebaseAuth mAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);
        ButterKnife.inject(this);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });

        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validateUser()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(RegisterUserActivity.this,
                R.style.AppTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Criando conta...");
        progressDialog.show();

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        User user = null;

        try {
            user = new User(name, email, password);
        } catch (UserException e) {
            e.printStackTrace();
        }

        // TODO: Implement your own signup logic here.
        saveUser(user);

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        progressDialog.dismiss();

                        if(saveUserIsSuccessful) {
                            onSignupSuccess();
                        } else {
                            onSignupFailed();
                        }
                    }
                }, 3000);
    }


    public void onSignupSuccess() {
        //When registering, the user is logged in, but he did not login.
        signOut();

        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Falha no cadastro!",
                Toast.LENGTH_SHORT).show();
        _signupButton.setEnabled(true);
    }

    private boolean saveUserIsSuccessful;

    private void saveUser(final User user){
        mAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.

                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            // On complete call either onSignupSuccess or onSignupFailed
                            // depending on success
                            saveUserIsSuccessful = true;

                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());

                            // On complete call either onSignupSuccess or onSignupFailed
                            // depending on success
                            saveUserIsSuccessful = false;

                            // Verify that the email exists.
                            // Verify that the email is already registered.
                            // These are two possible causes of exception of task in this else branch
                        }

                        // ...
                    }
                });
    }

    private void signOut() {
        mAuth.signOut();
    }

    public boolean validateUser() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        User validateUser = new User();

        try {
            validateUser.setName(name);
            _nameText.setError(null);
        } catch (UserException userException) {
            _nameText.setError(userException.getMessage());
            valid = false;
        }

        try {
            validateUser.setEmail(email);
            _emailText.setError(null);
        } catch (UserException userException) {
            _emailText.setError(userException.getMessage());
            valid = false;
        }

        try {
            validateUser.setPassword(password);
            _passwordText.setError(null);
        } catch (UserException userException) {
            _passwordText.setError(userException.getMessage());
            valid = false;
        }

        return valid;
    }
}