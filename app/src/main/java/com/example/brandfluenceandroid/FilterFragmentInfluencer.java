package com.example.brandfluenceandroid;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class FilterFragmentInfluencer extends BottomSheetDialogFragment {

    private EditText budgetInput;
    private Spinner platformSpinner;
    private Button applyFilterBtn;

    public interface FilterListener {
        void onFilterApplied(@Nullable String platform, @Nullable Double budget);
    }

    private FilterListener listener;

    public void setFilterListener(FilterListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filter_brand, container, false); // ✅ reuse same styled layout

        budgetInput = view.findViewById(R.id.filterMinFollowers); // reuse this ID as budget input
        platformSpinner = view.findViewById(R.id.filterPlatformSpinner);
        applyFilterBtn = view.findViewById(R.id.applyFilterBtn);

        // ✅ Setup platform dropdown
        String[] platforms = {"All", "Instagram", "YouTube", "TikTok", "Twitter", "Facebook"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, platforms);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        platformSpinner.setAdapter(adapter);

        applyFilterBtn.setOnClickListener(v -> {
            String budgetStr = budgetInput.getText().toString();
            String selectedPlatform = platformSpinner.getSelectedItem().toString();

            Double budget = null;
            if (!budgetStr.isEmpty()) {
                try {
                    budget = Double.parseDouble(budgetStr);
                } catch (NumberFormatException e) {
                    budget = null;
                }
            }

            if (selectedPlatform.equals("All")) selectedPlatform = null;

            if (listener != null) {
                listener.onFilterApplied(selectedPlatform, budget);
            }
            dismiss();
        });

        return view;
    }
}
