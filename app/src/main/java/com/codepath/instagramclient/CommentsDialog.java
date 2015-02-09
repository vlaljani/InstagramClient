package com.codepath.instagramclient;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by vibhalaljani on 2/8/15.
 *
 * This class is used to create and return a dialog fragment with the full caption and all comments
 * when a user clicks on a listview item from the main activity.
 */
public class CommentsDialog extends DialogFragment {

    private TextView tvUsernameAndCaption;
    private ImageView ivProfilePic;
    private TextView tvCreatedAt;

    private ImageButton btnBack;

    private ListView lvRecentComments;
    private ArrayList<InstagramComment> comments;
    private InstagramCommentAdapter aComments;


    public CommentsDialog() {
        // Empty constructor required for DialogFragment
    }

    public static CommentsDialog newInstance(String username,
                                             String caption,
                                             String profilePicUrl,
                                             Long createdAt,
                                             JSONArray recentComments) {

        CommentsDialog frag = new CommentsDialog();

        Bundle args = new Bundle();
        args.putString("username", username);
        args.putString("caption", caption);
        args.putString("profilePicUrl", profilePicUrl);
        args.putString("createdAt", DateUtils.getRelativeTimeSpanString(createdAt * 1000,
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString());
        ArrayList<String> usernames = new ArrayList<String>();
        ArrayList<String> texts = new ArrayList<String>();
        ArrayList<String> profilePicUrls = new ArrayList<String>();
        long[] createdTimes = new long[recentComments.length()];
        for (int i = 0; i < recentComments.length(); i++) {
            try {
                JSONObject recentComment = (JSONObject) recentComments.get(i);
                texts.add(recentComment.getString("text"));
                createdTimes[i] = recentComment.getLong("created_time");
                JSONObject from = recentComment.getJSONObject("from");
                usernames.add(from.getString("username"));
                profilePicUrls.add(from.getString("profile_picture"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        args.putStringArrayList("usernames", usernames);
        args.putStringArrayList("texts", texts);
        args.putStringArrayList("profilePicUrls", profilePicUrls);
        args.putLongArray("createdTimes", createdTimes);
        frag.setArguments(args);

        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_comments, container);

        tvUsernameAndCaption = (TextView) view.findViewById(R.id.tvUsernameAndCaption);
        ivProfilePic = (ImageView) view.findViewById(R.id.ivProfilePic);
        tvCreatedAt = (TextView) view.findViewById(R.id.tvCreatedAt);
        lvRecentComments = (ListView) view.findViewById(R.id.lvRecentComments);
        btnBack = (ImageButton) view.findViewById(R.id.btnBack);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        comments = new ArrayList<InstagramComment>();
        aComments = new InstagramCommentAdapter(getActivity(), comments);
        lvRecentComments.setAdapter(aComments);

        ArrayList<String> usernames = getArguments().getStringArrayList("usernames");
        ArrayList<String> texts = getArguments().getStringArrayList("texts");
        long[] createdTimes = getArguments().getLongArray("createdTimes");
        ArrayList<String> profilePicUrls = getArguments().getStringArrayList("profilePicUrls");
        for (int i = 0; i < usernames.size(); i++) {
            InstagramComment comment = new InstagramComment();
            comment.setUsername(usernames.get(i));
            comment.setCreated_time(createdTimes[i]);
            comment.setProfilePicUrl(profilePicUrls.get(i));
            comment.setText(texts.get(i));
            comments.add(comment);
        }
        aComments.notifyDataSetChanged();

        // Do away with the default title entirely because we have own custom title with back button
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        String fontHtmlBeg = "<font color=\"" + getResources().
                getColor(R.color.instagram_blue) + "\">";
        String fontHtmlEnd = "</font>";
        String formattedUsernameAndCaption = fontHtmlBeg + "<b>" + getArguments().
                getString("username") + "</b>" + fontHtmlEnd;
        String caption = getArguments().getString("caption");
        if (caption != null) {
            formattedUsernameAndCaption = formattedUsernameAndCaption + " " + caption;
        }
        tvUsernameAndCaption.setText(Html.fromHtml(formattedUsernameAndCaption));
        tvCreatedAt.setText(getArguments().getString("createdAt"));

        // Clear the image resource
        ivProfilePic.setImageResource(0);

        // Load the profile picture using Picasso
        Picasso.with(getActivity()).load(getArguments().getString("profilePicUrl"))
                .transform(new CircleTransform())
                .into(ivProfilePic);


        return view;
    }

}
