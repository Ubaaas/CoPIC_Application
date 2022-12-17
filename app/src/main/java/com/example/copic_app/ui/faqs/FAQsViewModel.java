package com.example.copic_app.ui.faqs;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class FAQsViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private static MutableLiveData<String> mText;

    public FAQsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is FQA fragment");
    }

    public static LiveData<String> getText() {
        return mText;
    }
}
