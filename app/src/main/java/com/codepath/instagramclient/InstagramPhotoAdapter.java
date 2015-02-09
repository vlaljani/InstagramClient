package com.codepath.instagramclient;

import android.content.Context;
import android.graphics.Point;
import android.text.Html;
import android.text.format.DateUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

/**
 * Created by vibhalaljani on 2/4/15.
 */
public class InstagramPhotoAdapter extends ArrayAdapter<InstagramPhoto> {

    private static int NUM_COMMENTS_SHOW = 2;

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

    // Use the template to display each photo
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
            viewHolder.ivPhoto = (ImageView) convertView.findViewById(R.id.ivPhoto);

            viewHolder.tvUsername = (TextView) convertView.findViewById(R.id.tvUsername);
            viewHolder.tvCaption = (TextView) convertView.findViewById(R.id.tvCaption);
            viewHolder.tvLikes = (TextView) convertView.findViewById(R.id.tvLikes);
            viewHolder.tvCreatedAt = (TextView) convertView.findViewById(R.id.tvCreatedAt);
            viewHolder.tvViewComments = (TextView) convertView.findViewById(R.id.tvViewComments);

            viewHolder.lvComments = (LinearLayout) convertView.findViewById(R.id.lvComments);

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

            // This section of code is an attempt to color all the hashtags blue.
            // The idea: Find the index of each tag in the lower case version of the caption
            // such that there's a # at index - 1, starting at 1 so that index - 1 is always valid.
            // It doesn't cover cases like #beautyPaegent #beauty because beauty is the first part
            // of beautyPaegent, OR if the same tag is used multiple times.
            // Currently, it does not actually linkify the tag, just colors it blue to look like the
            // actual Instagram client.
            //
            // I did try to cover @ references. However, they were not under users_in_photo or tags,
            // so it didn't work out.
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

        // This piece allows us to get , in the like count - just like the real Instagram client
        viewHolder.tvLikes.setText(String.format("%,d", photo.getLikesCount()) + " " +
                                   getContext().getResources().getString(R.string.like_str));

        // Insert into ImageView using Picasso - but this takes time so meanwhile we can clear out
        // imageView
        viewHolder.ivPhoto.setImageResource(0); // clears out current image, if any (for recycling)
        viewHolder.ivProfilePic.setImageResource(0);

        // The idea here is to make the image square like Instagram does.

        // This first piece calculates the aspect ratio of the Instagram image, which is, ofc, 1,
        // but just to make things generic.
        int aspectRatio = photo.getImgWidth() / photo.getImgHeight();

        // This piece of code gets the display width, which is sufficient because we know we've
        // set ivPhoto's width to match_parent.
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int targetWidth = size.x;

        // This resizes the image before loading it so that the aspect ratio is maintained.
        // (giving us a square image in this case)
        Picasso.with(getContext()).
                load(photo.getImgUrl()).
                resize(targetWidth, targetWidth / aspectRatio).
                placeholder(R.drawable.loading).
                error(R.drawable.error).
                into(viewHolder.ivPhoto); //asynchronous request

        // Edit: no longer needed because we're not using com.makeramen.RoundedImageView
        //Picasso.with(getContext()).load(photo.getProfilePicUrl()).into(viewHolder.ivProfilePic);

        // This uses the circle transform so the profile picture is loaded into a circle
        Picasso.with(getContext()).load(photo.getProfilePicUrl()).transform(new CircleTransform())
                .into(viewHolder.ivProfilePic);

        //Comments
        if (photo.getNumComments() > NUM_COMMENTS_SHOW) {
            viewHolder.tvViewComments.setText(getContext().getResources().getString(
                    R.string.viewComments1) + " " + photo.getNumComments() + " " +
                    getContext().getResources().getString(R.string.viewComments2));
        } else {
            viewHolder.tvViewComments.setText("");
        }

        // The idea here is to nest a view within the listView because we don't want to be doing
        // nested listviews.
        viewHolder.lvComments.removeAllViews();
        JSONArray commentsArr = photo.getAllComments();
        int i = NUM_COMMENTS_SHOW - 1;
        while (i >= 0) {
            JSONObject commenti = null;
            String commenter = null;
            String comment = null;
            try {
                int curr_index = commentsArr.length() - 1 - i;
                if (curr_index >= 0 && curr_index < commentsArr.length()) {
                    commenti = (JSONObject) commentsArr.get(curr_index);
                    comment = commenti.getString("text");
                    commenter = commenti.getJSONObject("from").getString("username");
                }
                i--;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (commenti != null && commenter != null && comment != null) {
                View line = LayoutInflater.from(getContext()).inflate(R.layout.item_comment,
                        parent, false);
                TextView tvCommenter = (TextView) line.findViewById(R.id.tvCommenter);
                TextView tvComment = (TextView) line.findViewById(R.id.tvComment);
                tvComment.setText(comment);
                tvCommenter.setText(commenter);
                viewHolder.lvComments.addView(line);
            }
        }
        return convertView;
    }
}
