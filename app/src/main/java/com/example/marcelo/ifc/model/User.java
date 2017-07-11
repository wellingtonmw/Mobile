package com.example.marcelo.ifc.model;

import com.example.marcelo.ifc.exception.UserException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class User {
    private static final int MAX_LENGTH_PASSWORD = 10;
    private static final int MIN_LENGTH_PASSWORD = 6;

    private String name;
    private String email;
    private String password;

    public static final String NAME_CANT_BE_EMPTY = "O nome "
            + "não pode ser vazio.";
    public static final String EMAIL_CANT_BE_EMPTY= "O email "
            + "não pode ser vazio.";
    public static final String INVALID_EMAIL = "Ops, esse e-mail é inválido.";
    public static final String PASSWORD_CANT_BE_EMPTY = "A senha não "
            + "pode ser vazia.";
    public static final String PASSWORD_CANT_BE_LESS_THAN_6 = "A senha"
            + " deve conter no mínimo 6 caractéres.";
    public static final String PASSWORD_CANT_BE_HIGHER_THAN_10 = "A senha"
            + " deve ter no máximo 10 caractéres";

    public User() {

    }

    public User(String email, String name, String password) throws UserException{
        setName(name);
        setEmail(email);
        setPassword(password);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) throws UserException {
        if (stringIsNotNull(name)) {
            this.name=name;
        } else {
            throw new UserException(NAME_CANT_BE_EMPTY);
        }

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) throws UserException {
        if (stringIsNotNull(email)) {
            if (validateEmailFormat(email)) {
                    this.email=email;
            } else {
                throw new UserException(INVALID_EMAIL);
            }
        } else {
            throw new UserException(EMAIL_CANT_BE_EMPTY);
        }
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) throws UserException {
        if (stringIsNotNull(password)) {
            if (validateStringLengthLessThanMax(password, MAX_LENGTH_PASSWORD)) {
                if (validateStringLengthMoreThanMin(password, MIN_LENGTH_PASSWORD)) {
                    this.password = password;
                } else {
                    throw new UserException(PASSWORD_CANT_BE_LESS_THAN_6);
                }
            } else {
                throw new UserException(PASSWORD_CANT_BE_HIGHER_THAN_10);
            }
        } else {
            throw new UserException(PASSWORD_CANT_BE_EMPTY);
        }
    }

    private boolean stringIsNotNull(final String string) {
        if (string == null || string.trim().isEmpty()) {
            return false;
        }
        return  true;
    }

    private boolean validateStringLengthLessThanMax(final String string, final int MAX) {
        if (string.length() > MAX) {
            return false;
        }
        return  true;
    }

    private boolean validateStringLengthMoreThanMin(final String string, final int MIN) {
        if (string.length() < MIN) {
            return false;
        }
        return  true;
    }

    private boolean validateEmailFormat(final String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            return true;
        }
        return false;
    }
}
