package com.codepath.instagramclient;

import android.content.Context;
import android.text.Html;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.makeramen.RoundedImageView;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vibhalaljani on 2/4/15.
 */
public class InstagramPhotoAdapter extends ArrayAdapter<InstagramPhoto> {

    private static class ViewHolder {
        TextView tvUsername;
        TextView tvCaption;
        TextView tvLikes;
        TextView tvCreatedAt;

        ImageView ivProfilePic;
        ImageView ivPhoto;
    }

    // Constructor - what data do we need from activity - context, data source
    public InstagramPhotoAdapter(Context context, List<InstagramPhoto> objects) {
        super(context, android.R.layout.simple_list_item_1, objects);
    }

    //Use the template to display each photo
    // Check out guide using an arrayadapter to listview
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        InstagramPhoto photo = getItem(position);

        ViewHolder viewHolder;

        if (convertView == null) { // If we are not reusing a view
            viewHolder = new ViewHolder();

            // Create a new view from template
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_photo,
                                                                    parent, false);

            viewHolder.ivProfilePic = (ImageView) convertView.findViewById(R.id.ivProfilePic);
            viewHolder.tvUsername = (TextView) convertView.findViewById(R.id.tvUsername);
            viewHolder.tvCaption = (TextView) convertView.findViewById(R.id.tvCaption);
            viewHolder.ivPhoto = (ImageView) convertView.findViewById(R.id.ivPhoto);
            viewHolder.tvLikes = (TextView) convertView.findViewById(R.id.tvLikes);
            viewHolder.tvCreatedAt = (TextView) convertView.findViewById(R.id.tvCreatedAt);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvUsername.setText(photo.getUsername());
        viewHolder.tvCreatedAt.setText(DateUtils.getRelativeTimeSpanString(photo.getCreatedAt() * 1000,
                System.currentTimeMillis(),DateUtils.SECOND_IN_MILLIS));
        String instagram_color = "#2A5C83";
        String formattedCaption = "";
        if (photo.getCaption() != null) {
            formattedCaption = "<font color=\"" +
                    instagram_color + "\"><b>" + photo.getUsername() + "</b></font> "
                    + photo.getCaption();
        }

        viewHolder.tvCaption.setText(Html.fromHtml(formattedCaption.toString()));
        viewHolder.tvLikes.setText(String.format("%,d", photo.getLikesCount()) + " likes");

        // Insert into ImageView using Picasso - but this takes time so meanwhile we can clear out
        // imageView
        viewHolder.ivPhoto.setImageResource(0); // clears out current image, if any (for recycling)
        viewHolder.ivProfilePic.setImageResource(0);

        // can add fun settings here to change the image after it gets it
        Picasso.with(getContext()).
                load(photo.getImgUrl()).
                placeholder(R.drawable.loading).
                error(R.drawable.error).
                into(viewHolder.ivPhoto); //asynchronous request
        //Picasso.with(getContext()).load(photo.getProfilePicUrl()).into(viewHolder.ivProfilePic);
        Picasso.with(getContext()).load(photo.getProfilePicUrl()).transform(new CircleTransform())
                .into(viewHolder.ivProfilePic);
        return convertView;
    }
}
