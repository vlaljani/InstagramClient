package com.codepath.instagramclient;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.format.DateUtils;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.makeramen.RoundedImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
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
        TextView tvViewComments;

        ImageView ivProfilePic;
        ImageView ivPhoto;

        LinearLayout lvComments;
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
            viewHolder.lvComments = (LinearLayout) convertView.findViewById(R.id.lvComments);
            viewHolder.tvViewComments = (TextView) convertView.findViewById(R.id.tvViewComments);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String fontHtmlBeg = "<font color=\"" + getContext().getResources().
                getColor(R.color.instagram_blue) + "\">";
        String fontHtmlEnd = "</font>";

        String locationFontHtmlBeg = "<font color=\"" + getContext().getResources().
                getColor(R.color.location_blue) + "\" size=\"2\">";
        String locationFontHtmlEnd = "</font>";

        String username_section_html = "<b>" + photo.getUsername() + "</b>";
        if (photo.getLocation() != null) {
            username_section_html = username_section_html + "<\\br>" + locationFontHtmlBeg
                    + photo.getLocation() + locationFontHtmlEnd;
        }

        viewHolder.tvUsername.setText(Html.fromHtml(username_section_html));

        viewHolder.tvCreatedAt.setText(DateUtils.getRelativeTimeSpanString(photo.getCreatedAt() * 1000,
                System.currentTimeMillis(),DateUtils.SECOND_IN_MILLIS));

        String photoCaption = photo.getCaption();
        StringBuilder formattedCaption = new StringBuilder();
        formattedCaption.append("");
        if (photoCaption != null) {
            formattedCaption.append(fontHtmlBeg + "<b>" + photo.getUsername() + "</b>" +
                    fontHtmlEnd + " ");
            JSONArray tags = photo.getTags();

            if (tags.length() > 0) {
                int[] indices = new int[tags.length()];
                int[] lastIndices = new int[tags.length()];
                String photoCaption_lc = photoCaption.toLowerCase();
                for (int i = 0; i < tags.length(); i++) {

                    try {
                        String curr_tag = tags.getString(i);
                        int index_curr_tag = photoCaption_lc.indexOf(curr_tag, 1);

                        while ((index_curr_tag < photoCaption.length()) &&
                                (photoCaption.charAt(index_curr_tag - 1) != '#')) {
                            index_curr_tag = photoCaption_lc.indexOf(curr_tag,
                                    index_curr_tag + curr_tag.length());
                        }
                        indices[i] = index_curr_tag - 1;

                        if (indices[i] >= 0) {
                            lastIndices[i] = index_curr_tag + curr_tag.length() - 1;
                        } else {
                            lastIndices[i] = -1;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                Arrays.sort(indices);
                Arrays.sort(lastIndices);

                int j = 0, k = 0;

                for (int i = 0; i < photoCaption.length(); i++) {
                     if (j < indices.length) {
                         if (i == indices[j]) { // We are at a tag
                             formattedCaption.append(fontHtmlBeg);
                             j++;
                         }
                     }
                     formattedCaption.append(photoCaption.charAt(i));
                     if (k < lastIndices.length) {
                         if (i == lastIndices[k]) {
                             formattedCaption.append(fontHtmlEnd);
                             k++;
                         }
                     }
                }
            } else {
                formattedCaption.append(photoCaption);
            }
        }
        viewHolder.tvCaption.setText(Html.fromHtml(formattedCaption.toString()));
        viewHolder.tvLikes.setText(String.format("%,d", photo.getLikesCount()) + " " +
                                   getContext().getResources().getString(R.string.like_str));

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

        //Comments
        if (photo.getNumComments() > 2) {
            viewHolder.tvViewComments.setText(getContext().getResources().getString(
                    R.string.viewComments1) + " " + photo.getNumComments() + " " +
                    getContext().getResources().getString(R.string.viewComments2));
        } else {
            viewHolder.tvViewComments.setText("");
        }
        viewHolder.lvComments.removeAllViews();
        ArrayList<String> comments = photo.getRecentComments();
        ArrayList<String> commenters = photo.getRecentCommenters();
        for (int i = comments.size() - 1 ; i >= 0; i--) {
            Log.i("DEBUG", "comments section");
            Log.i("DEBUG", comments.get(i) + " " + commenters.get(i));
            View line = LayoutInflater.from(getContext()).inflate(R.layout.item_comment,
                    parent, false);
            TextView tvCommenter = (TextView) line.findViewById(R.id.tvCommenter);
            TextView tvComment = (TextView) line.findViewById(R.id.tvComment);
            tvComment.setText(comments.get(i));
            tvCommenter.setText(commenters.get(i));
            viewHolder.lvComments.addView(line);
        }
        return convertView;
    }
}
