package com.example.marcelo.ifc.presenter;

import android.content.Context;

import com.example.marcelo.ifc.exception.UserException;
import com.example.marcelo.ifc.model.User;

public class LoginPresenter {
    private static LoginPresenter instance = null;
    private final Context context;

    private LoginPresenter(final Context contextParameter) {
        this.context = contextParameter;
    }

    public static LoginPresenter getInstance(final Context context) {
        if (instance == null) {
            instance = new LoginPresenter(context);
        } else {
			/* ! Nothing To Do. */
        }
        return instance;
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
