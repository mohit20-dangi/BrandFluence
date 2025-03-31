package com.example.brandfluenceandroid;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class InfluencerAdapter extends RecyclerView.Adapter<InfluencerAdapter.ViewHolder> {

    private Context context;
    private List<Influencer> influencerList;

    public InfluencerAdapter(Context context, List<Influencer> influencerList) {
        this.context = context;
        this.influencerList = influencerList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView influencerName, platformUsed, followerCount, influencerCategory, influencerSocialLink;
        ImageView profileImageView; // ✅ NEW

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            influencerName = itemView.findViewById(R.id.influencerName);
            platformUsed = itemView.findViewById(R.id.platformUsed);
            followerCount = itemView.findViewById(R.id.followerCount);
            influencerCategory = itemView.findViewById(R.id.influencerCategory);
            influencerSocialLink = itemView.findViewById(R.id.influencerSocialLink);
            profileImageView = itemView.findViewById(R.id.profileImageView); // ✅ NEW
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_influencer_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Influencer influencer = influencerList.get(position);

        holder.influencerName.setText(influencer.getName());
        holder.platformUsed.setText("Platform: " + influencer.getPlatforms());
        holder.followerCount.setText("Followers: " + influencer.getFollowers());
        holder.influencerCategory.setText("Category: " + influencer.getCategory());
        holder.influencerSocialLink.setText("Social: " + influencer.getSocialMediaLink());

        if (influencer.getProfileImageBase64() != null && !influencer.getProfileImageBase64().isEmpty()) {
            byte[] decodedBytes = Base64.decode(influencer.getProfileImageBase64(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            holder.profileImageView.setImageBitmap(bitmap);
        }

        holder.itemView.setOnClickListener(v -> {
            String socialLink = influencer.getSocialMediaLink();
            if (socialLink != null && !socialLink.isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(socialLink));
                context.startActivity(intent);
            } else {
                Toast.makeText(context, "No social media link provided", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return influencerList.size();
    }
}
