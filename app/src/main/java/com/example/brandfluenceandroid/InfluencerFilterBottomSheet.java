package com.example.brandfluenceandroid;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class InfluencerFilterBottomSheet extends BottomSheetDialogFragment {

    public interface OnFilterApplyListener {
        void onFilterApply(String category, int minFollowers, String platform);
    }

    private OnFilterApplyListener listener;

    public InfluencerFilterBottomSheet(OnFilterApplyListener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filter_brand, container, false);

        EditText categoryInput = view.findViewById(R.id.filterCategory);
        EditText minFollowersInput = view.findViewById(R.id.filterMinFollowers);
        Spinner platformSpinner = view.findViewById(R.id.filterPlatformSpinner);
        Button applyBtn = view.findViewById(R.id.applyFilterBtn);

        // ✅ Set platform options
        String[] platforms = {"", "Instagram", "YouTube", "TikTok", "Twitter", "Facebook"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, platforms);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        platformSpinner.setAdapter(adapter);

        applyBtn.setOnClickListener(v -> {
            String category = categoryInput.getText().toString().trim();
            String selectedPlatform = platformSpinner.getSelectedItem().toString().trim();
            int minFollowers = 0;

            try {
                minFollowers = Integer.parseInt(minFollowersInput.getText().toString().trim());
            } catch (NumberFormatException ignored) {}

            listener.onFilterApply(category, minFollowers, selectedPlatform);
            dismiss();
        });

        return view;
    }

}
