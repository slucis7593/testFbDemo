package com.vuduc.android.fblogindemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Collection;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private static final String TAG = MainActivityFragment.class.getSimpleName();
    private TextView mTvMessage;
    private CallbackManager mCallbackManager;
    private ProfileTracker mProfileTracker;
    private Button mBtnFacebook;
    private Button mBtnBirthday;

    public MainActivityFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(mCallbackManager, null);

        mProfileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile profile, Profile profile1) {
                updateFacebook(profile1);
            }
        };
        mProfileTracker.startTracking();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTvMessage = (TextView) view.findViewById(R.id.text);

        mBtnFacebook = (Button) view.findViewById(R.id.facebook_button);
        mBtnFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Profile.getCurrentProfile() == null) {
                    LoginManager.getInstance().logInWithReadPermissions(MainActivityFragment.this, Arrays.asList("public_profile", "user_friends"));
                } else {
                    LoginManager.getInstance().logOut();
                }
            }
        });

        mBtnBirthday = (Button) view.findViewById(R.id.get_birthday_button);
        mBtnBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Profile.getCurrentProfile() != null) {

                    Collection<String> permissions = AccessToken.getCurrentAccessToken().getPermissions();

                    if(!permissions.contains("user_birthday"))
                        LoginManager.getInstance().logInWithReadPermissions(MainActivityFragment.this, Arrays.asList("user_birthday"));

                    Bundle params = new Bundle();
                    params.putString("fields", "birthday");

                    new GraphRequest(
                            AccessToken.getCurrentAccessToken(),
                            "/me",
                            params,
                            HttpMethod.GET,
                            new GraphRequest.Callback() {
                                public void onCompleted(GraphResponse response) {
                                    JSONObject json = response.getJSONObject();
                                    try {
                                        mTvMessage.setText(mTvMessage.getText().toString() + json.getString("birthday"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                    ).executeAsync();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateFacebook(Profile.getCurrentProfile());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mProfileTracker.stopTracking();
    }

    private void updateFacebook(Profile profile) {
        if (profile != null) {
            String message = "Logged In " + profile.getFirstName() + " " + profile.getMiddleName() + " " + profile.getLastName();
            mTvMessage.setText(message);
            mBtnFacebook.setText("Log out");



        } else {
            String message = "You are not logged in";
            mTvMessage.setText(message);
            mBtnFacebook.setText("Log in");
        }
    }
}
