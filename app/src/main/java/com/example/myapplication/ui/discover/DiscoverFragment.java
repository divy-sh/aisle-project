package com.example.myapplication.ui.discover;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.BlurredImage;
import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentDiscoverBinding;

public class DiscoverFragment extends Fragment {

    private DiscoverViewModel DiscoverViewModel;
    private FragmentDiscoverBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DiscoverViewModel =
                new ViewModelProvider(this).get(com.example.myapplication.ui.discover.DiscoverViewModel.class);

        binding = FragmentDiscoverBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        DiscoverViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                binding.blurImage1.setImageBitmap(BlurredImage.blur(getResources(), R.drawable.photo_2));
                binding.blurImage2.setImageBitmap(BlurredImage.blur(getResources(), R.drawable.photo_3));
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}