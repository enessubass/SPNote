package com.farshidabz.supernote.view.ui.base;

import android.support.annotation.StringRes;

/**
 * Created by FarshidAbz.
 * Since 4/15/2017.
 */

public interface MvpView {
    void showLoading();

    void hideLoading();

    void onError(@StringRes int resId);

    void onError(String message);
}
