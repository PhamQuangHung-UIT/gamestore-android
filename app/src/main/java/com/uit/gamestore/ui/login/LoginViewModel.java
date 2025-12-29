package com.uit.gamestore.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.uit.gamestore.data.remote.dto.LoginResponse;
import com.uit.gamestore.data.repository.AuthRepository;
import com.uit.gamestore.domain.model.Result;

public class LoginViewModel extends ViewModel {

    private final MutableLiveData<Result<LoginResponse>> loginResult = new MutableLiveData<>();
    private final MutableLiveData<String> email = new MutableLiveData<>("");
    private final MutableLiveData<String> password = new MutableLiveData<>("");
    private final MutableLiveData<Boolean> passwordVisible = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    private final AuthRepository authRepository = new AuthRepository();

    public void togglePasswordVisible() {
        passwordVisible.setValue(Boolean.FALSE.equals(passwordVisible.getValue()));
    }

    public void login() {
        String emailValue = email.getValue();
        String passwordValue = password.getValue();

        if (emailValue == null || emailValue.trim().isEmpty()) {
            loginResult.setValue(new Result.Error<>("Email is required"));
            return;
        }

        if (passwordValue == null || passwordValue.isEmpty()) {
            loginResult.setValue(new Result.Error<>("Password is required"));
            return;
        }

        isLoading.setValue(true);
        authRepository.login(emailValue.trim(), passwordValue, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(LoginResponse response) {
                isLoading.postValue(false);
                loginResult.postValue(new Result.Success<>(response));
            }

            @Override
            public void onError(String message) {
                isLoading.postValue(false);
                loginResult.postValue(new Result.Error<>(message));
            }
        });
    }

    public LiveData<Result<LoginResponse>> getLoginResult() {
        return loginResult;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
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
