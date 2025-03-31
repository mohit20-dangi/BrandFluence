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
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.List;

public class InfluencerListAdapter extends ArrayAdapter<Influencer> {

    private Context context;
    private List<Influencer> influencerList;

    public interface OnLongClickListener {
        void onLongClick(Influencer influencer);
    }

    private OnLongClickListener longClickListener;

    public InfluencerListAdapter(Context context, List<Influencer> influencers, OnLongClickListener listener) {
        super(context, 0, influencers);
        this.context = context;
        this.influencerList = influencers;
        this.longClickListener = listener;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Influencer influencer = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_simple_influencer, parent, false);
        }

        TextView name = convertView.findViewById(R.id.influencerName);
        TextView platform = convertView.findViewById(R.id.influencerPlatform);
        TextView followers = convertView.findViewById(R.id.influencerFollowers);
        ImageView profileImage = convertView.findViewById(R.id.influencerProfileImage);

        if (influencer != null) {
            name.setText(influencer.getName());
            platform.setText("Platform: " + influencer.getPlatforms());
            followers.setText("Followers: " + influencer.getFollowers());

            // Load Base64-encoded profile image
            if (influencer.getProfileImageBase64() != null && !influencer.getProfileImageBase64().isEmpty()) {
                try {
                    byte[] decodedBytes = Base64.decode(influencer.getProfileImageBase64(), Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                    profileImage.setImageBitmap(bitmap);
                } catch (Exception e) {
                    profileImage.setImageResource(R.drawable.ic_profile);
                }
            } else {
                profileImage.setImageResource(R.drawable.ic_profile);
            }

            convertView.setOnClickListener(v -> {
                if (influencer.getSocialMediaLink() != null && !influencer.getSocialMediaLink().isEmpty()) {
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(influencer.getSocialMediaLink())));
                } else {
                    Toast.makeText(context, "No social media link", Toast.LENGTH_SHORT).show();
                }
            });

            convertView.setOnLongClickListener(v -> {
                if (longClickListener != null) {
                    longClickListener.onLongClick(influencer);
                }
                return true;
            });
        }

        return convertView;
    }
}
