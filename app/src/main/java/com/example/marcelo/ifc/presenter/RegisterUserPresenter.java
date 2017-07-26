package com.example.marcelo.ifc.presenter;

import android.content.Context;

import com.example.marcelo.ifc.exception.UserException;
import com.example.marcelo.ifc.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterUserPresenter {

    private static final String TAG = "RegisterUserPresenter";
    private static RegisterUserPresenter instance = null;
    private final Context context;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private RegisterUserPresenter(final Context contextParameter) {
        this.context = contextParameter;
    }

    public static RegisterUserPresenter getInstance(final Context context) {
        if (instance == null) {
            instance = new RegisterUserPresenter(context);
        } else {
			/* ! Nothing To Do. */
        }
        return instance;
    }

    public void writeNewUser(String userId, String name, String email) {
        User user = null;
        mDatabase = FirebaseDatabase.getInstance().getReference();

        try {
            user = new User(name, email);
        } catch (UserException e) {
            e.printStackTrace();
        }

        mDatabase.child("users").child(userId).setValue(user);
    }

    public void signOut() {
        mAuth = FirebaseAuth.getInstance();

        mAuth.signOut();
    }

    public String validateUserName(final String userName) {
        User user = new User();
        String valid = null;

        try {
            user.setName(userName);
        } catch (UserException userException) {
            valid = userException.getMessage();
        }

        return valid;
    }

    public String validateUserEmail(final String userEmail) {
        User user = new User();
        String valid = null;

        try {
            user.setEmail(userEmail);
        } catch (UserException userException) {
            valid = userException.getMessage();
        }

        return valid;
    }

    public String validateUserPassword(final String userPassword) {
        User user = new User();
        String valid = null;

        try {
            user.setPassword(userPassword);
        } catch (UserException userException) {
            valid = userException.getMessage();
        }

        return valid;
    }
}
