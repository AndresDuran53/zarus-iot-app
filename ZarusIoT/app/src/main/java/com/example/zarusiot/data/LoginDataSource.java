package com.example.zarusiot.data;

import com.example.zarusiot.controller.ZarusRequest;
import com.example.zarusiot.data.model.LoggedInUser;

import java.io.DataInput;
import java.io.IOException;

import javax.security.auth.login.LoginException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    public Result<LoggedInUser> login(String username, String password) {

        try {
            // TODO: handle loggedInUser authentication
            ZarusRequest zarusRequest = new ZarusRequest();
            String userId = zarusRequest.verifyLogin(username,password);
            LoggedInUser userAux = new LoggedInUser(userId, "Jane Doe");
            if(!userId.isEmpty()) return new Result.Success<>(userAux);
            else return new Result.Error(new LoginException("No user found"));
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }


}