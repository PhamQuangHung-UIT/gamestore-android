package com.uit.gamestore.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.uit.gamestore.data.repository.LoginRepository;
import com.uit.gamestore.domain.model.Result;
import com.uit.gamestore.domain.model.User;

public class LoginViewModel extends ViewModel {
    private final MutableLiveData<Result<User>> loginResult = new MutableLiveData<>();

    private final MutableLiveData<String> email = new MutableLiveData<>();
    private final MutableLiveData<String> password = new MutableLiveData<>();

    private final MutableLiveData<Boolean> passwordVisible = new MutableLiveData<>(false);

    private final LoginRepository loginRepository = new LoginRepository();

    public void togglePasswordVisible() {
        passwordVisible.setValue(Boolean.FALSE.equals(passwordVisible.getValue()));
    }

    public void login() {
        var emailValue = email.getValue();
        var passwordValue = password.getValue();
        if (emailValue != null && passwordValue != null) {
            var result = loginRepository.login(emailValue, passwordValue);
            loginResult.setValue(result);
        }
    }

    public LiveData<Result<User>> getLoginResult() {
        return loginResult;
    }

    public void setEmail(String newEmail) {
        email.setValue(newEmail);
    }

    public void setPassword(String newPassword) {
        password.setValue(newPassword);
    }

    public LiveData<Boolean> getPasswordVisible() {
        return passwordVisible;
    }
}
