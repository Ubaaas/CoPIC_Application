package com.example.copic_app.ui.faqs;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.copic_app.databinding.FragmentFAQsBinding;

public class FAQsFragment extends Fragment {

    private FragmentFAQsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        FAQsViewModel faQsViewModel =
                new ViewModelProvider(this).get(FAQsViewModel.class);

        binding = FragmentFAQsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textFaqs;
        FAQsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}