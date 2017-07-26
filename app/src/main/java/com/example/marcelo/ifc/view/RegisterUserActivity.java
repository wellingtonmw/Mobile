package com.example.marcelo.ifc.view;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.marcelo.ifc.R;
import com.example.marcelo.ifc.presenter.RegisterUserPresenter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

import android.app.ProgressDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.Executor;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class RegisterUserActivity extends AppCompatActivity {
    private static final String TAG = "RegisterUserActivity";

    @InjectView(R.id.input_name) EditText _nameText;
    @InjectView(R.id.input_email) EditText _emailText;
    @InjectView(R.id.input_password) EditText _passwordText;
    @InjectView(R.id.btn_signup) Button _signupButton;
    @InjectView(R.id.link_login) TextView _loginLink;

    private RegisterUserPresenter registerUserPresenter;
    private FirebaseAuth mAuth;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);
        ButterKnife.inject(this);

        registerUserPresenter = RegisterUserPresenter.getInstance(getBaseContext());
        mAuth = FirebaseAuth.getInstance();

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

        final ProgressDialog progressDialog=new ProgressDialog(RegisterUserActivity.this,
                R.style.AppTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Criando conta...");
        progressDialog.show();

        String name=_nameText.getText().toString();
        String email=_emailText.getText().toString();
        String password=_passwordText.getText().toString();

        // TODO: Implement your own signup logic here.
        saveUserInAuth(email, name, password);

        new Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        progressDialog.dismiss();

                        if (saveUserIsSuccessful == CREATE_USER_TASK_IS_SUCCESSFUL) {
                            onSignupSuccess();
                        } else {
                            onSignupFailed();
                        }
                    }
                }, 3000);
    }


    public void onSignupSuccess() {
        //When registering, the user is logged in, but he did not login.
        registerUserPresenter.signOut();

        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    private int saveUserIsSuccessful;
    final public int CREATE_USER_TASK_IS_SUCCESSFUL = 0;
    final public int CREATE_USER_TASK_IS_NOT_SUCCESSFUL = 1;
    final public int USER_AUTH_COLLISION = 2;
    final public int USER_AUTH_INVALID_CREDENTIALS = 3;

    public void saveUserInAuth(final String email, final String name, final String password){
        mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email, password)
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
                            saveUserIsSuccessful = CREATE_USER_TASK_IS_SUCCESSFUL;

                            //Write the new user in database
                            FirebaseUser userLogged = mAuth.getCurrentUser();
                            final String userId = userLogged.getUid();

                            registerUserPresenter.writeNewUser(userId, name, email);

                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());

                            // On complete call either onSignupSuccess or onSignupFailed
                            // depending on success
                            saveUserIsSuccessful = CREATE_USER_TASK_IS_NOT_SUCCESSFUL;

                            // Verify that the email is already registered.
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                saveUserIsSuccessful = USER_AUTH_COLLISION;
                                // Verify that the email exists.
                            } else if (task.getException() instanceof
                                    FirebaseAuthInvalidCredentialsException) {
                                saveUserIsSuccessful = USER_AUTH_INVALID_CREDENTIALS;
                            }

                            // These are two possible causes of exception of task in this else branch
                        }

                        // ...
                    }
                });
    }


    public void onSignupFailed() {
        switch (saveUserIsSuccessful) {
            case CREATE_USER_TASK_IS_NOT_SUCCESSFUL:
                Toast.makeText(getBaseContext(), "Falha no cadastro!",
                        Toast.LENGTH_SHORT).show();
                break;
            case USER_AUTH_COLLISION:
                Toast.makeText(getBaseContext(), "Email já cadastrado!",
                        Toast.LENGTH_SHORT).show();
                _emailText.setError("Email já cadastrado.");
                break;
            case USER_AUTH_INVALID_CREDENTIALS:
                Toast.makeText(getBaseContext(), "Email inválido!",
                        Toast.LENGTH_SHORT).show();
                _emailText.setError("Ops, esse e-mail é inválido.");
                break;
        }

        _signupButton.setEnabled(true);
    }

    public boolean validateUser() {
        boolean valid=false;

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        String validateUserName=registerUserPresenter.validateUserName(name);
        _nameText.setError(validateUserName);

        String validateUserEmail=registerUserPresenter.validateUserEmail(email);
        _emailText.setError(validateUserEmail);

        String validateUserPassword=registerUserPresenter.validateUserPassword(password);
        _passwordText.setError(validateUserPassword);

        Log.d("validateUserEmail: ", validateUserEmail == null ? "null" : validateUserEmail);
        Log.d("validateUserName: ", validateUserName == null ? "null" : validateUserName);
        Log.d("validateUserPassword: ", validateUserPassword == null ? "null" : validateUserPassword);

        if (validateUserEmail == null && validateUserName == null && validateUserPassword == null) {
            valid=true;
        }

        Log.d("Validation: ", String.valueOf(valid));

        return valid;
    }
}