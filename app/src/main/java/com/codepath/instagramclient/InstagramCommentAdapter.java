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

import com.squareup.picasso.Picasso;


import java.util.List;

/**
 * Created by vibhalaljani on 2/8/15.
 *
 * This is the adapter that is used to populate the comments listview in the comments
 * dialog fragment (fragment_all_comments.xml)
 */
public class InstagramCommentAdapter extends ArrayAdapter {

    // The listview does have scrolling so use a ViewHolder to optimize performance.
    private static class ViewHolder {
        TextView tvUsernameAndText;
        TextView tvCreatedAt;

        ImageView ivProfilePic;
    }

    public InstagramCommentAdapter(Context context, List<InstagramComment> objects) {
        super(context, android.R.layout.simple_list_item_1, objects);
    }

    // This method is used to disable selection on the individual listview items.
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        InstagramComment comment = (InstagramComment) getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) { // If we are not reusing a view

            viewHolder = new ViewHolder();

            // Create a new view from template
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_lv_comment,
                    parent, false);
            viewHolder.ivProfilePic = (ImageView) convertView.findViewById(R.id.ivProfilePic);
            viewHolder.tvUsernameAndText = (TextView) convertView.findViewById(R.id.tvUsernameAndText);
            viewHolder.tvCreatedAt = (TextView) convertView.findViewById(R.id.tvCreatedAt);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String fontHtmlBeg = "<font color=\"" + getContext().getResources().
                getColor(R.color.instagram_blue) + "\"><b>";
        String fontHtmlEnd = "</b></font>";
        String usernameAndText = fontHtmlBeg + comment.getUsername() + fontHtmlEnd + " "
                + comment.getText();
        viewHolder.tvUsernameAndText.setText(Html.fromHtml(usernameAndText));

        viewHolder.tvCreatedAt.setText(DateUtils.getRelativeTimeSpanString(comment.getCreated_time() * 1000,
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS));

        viewHolder.ivProfilePic.setImageResource(0);
        Picasso.with(getContext()).load(comment.getProfilePicUrl()).transform(new CircleTransform())
                .into(viewHolder.ivProfilePic);

        return convertView;
    }

}
