package com.example.brandfluenceandroid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CampaignAdapter extends RecyclerView.Adapter<CampaignAdapter.CampaignViewHolder> {

    private Context context;
    private List<Campaign> campaignList;
    private CampaignClickListener clickListener;



    // Custom Interface for click handling
    public interface CampaignClickListener {
        void onCampaignClick(Campaign campaign);
    }

    public CampaignAdapter(Context context, List<Campaign> campaignList, CampaignClickListener clickListener) {
        this.context = context;
        this.campaignList = campaignList;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public CampaignViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_campaign_card, parent, false);
        return new CampaignViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CampaignViewHolder holder, int position) {
        Campaign campaign = campaignList.get(position);
        holder.campaignTitle.setText(campaign.getTitle());
        holder.campaignDesc.setText(campaign.getDescription());
        holder.campaignPlatform.setText("Platform: " + campaign.getPlatforms());


        String brandName = campaign.getBrandName();

        if (brandName == null || brandName.trim().isEmpty()) {
            // Hide the brand name by setting height to 0dp
            ViewGroup.LayoutParams params = holder.campaignBrand.getLayoutParams();
            params.height = 0;
            holder.campaignBrand.setLayoutParams(params);
        } else {
            // Show the brand name
            holder.campaignBrand.setText("By: " + brandName);
            ViewGroup.LayoutParams params = holder.campaignBrand.getLayoutParams();
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            holder.campaignBrand.setLayoutParams(params);
        }

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onCampaignClick(campaign);
            }
        });
    }


    @Override
    public int getItemCount() {
        return campaignList.size();
    }

    public void updateCampaigns(List<Campaign> newCampaigns) {
        this.campaignList.clear();
        this.campaignList.addAll(newCampaigns);
        notifyDataSetChanged();
    }


    public static class CampaignViewHolder extends RecyclerView.ViewHolder {
        TextView campaignTitle, campaignDesc, campaignBrand, campaignPlatform;;

        public CampaignViewHolder(@NonNull View itemView) {
            super(itemView);
            campaignTitle = itemView.findViewById(R.id.campaignTitle);
            campaignDesc = itemView.findViewById(R.id.campaignDescription);
            campaignBrand = itemView.findViewById(R.id.campaignBrand);
            campaignPlatform = itemView.findViewById(R.id.campaignPlatform);

        }

    }
}
