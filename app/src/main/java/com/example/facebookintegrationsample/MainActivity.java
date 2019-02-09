package com.example.facebookintegrationsample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private CallbackManager callbackManager;
    private static final String EMAIL = "email";
    private static final String PUBLIC_PROFILE = "public_profile";
    private static final String USER_BIRTHDAY = "user_birthday";
    private static final String GENDER = "gender";
    private LoginButton loginButton;
    private TextView user_name, txtEmail;
    private ImageView imageView;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;
    private Button mFacebookBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        user_name = (TextView) findViewById(R.id.user_name);
        txtEmail = (TextView) findViewById(R.id.email);
        imageView = (ImageView) findViewById(R.id.imageView);
        // loginButton=(LoginButton)findViewById(R.id.login_button);
        mFacebookBtn = (Button) findViewById(R.id.facebook_id);

        callbackManager = CallbackManager.Factory.create();

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {

            }
        };

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {

            }
        };

        accessTokenTracker.startTracking();
        profileTracker.startTracking();
        //loginButton.setReadPermissions(Arrays.asList(EMAIL,PUBLIC_PROFILE,USER_BIRTHDAY));
        // If you are using in a fragment, call loginButton.setFragment(this);

        // Callback registration
//        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
//            @Override
//            public void onSuccess(LoginResult loginResult) {
//                Log.e("TAG","Facebook:onSuccess"+loginResult.toString());
//                // App code
//                getUserProfile(loginResult.getAccessToken());
//            }
//
//            @Override
//            public void onCancel() {
//                // App code
//                Log.e("TAG","Facebook:onCancel");
//
//            }
//
//            @Override
//            public void onError(FacebookException exception) {
//                // App code
//                Log.e("TAG","Facebook:onError");
//
//            }
//        });

        /**If you customize the login button */
        mFacebookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(MainActivity.this, Arrays.asList(EMAIL, PUBLIC_PROFILE, USER_BIRTHDAY));
                LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.e("TAG", "Facebook:onSuccess" + loginResult.toString());
                        // App code
                        getUserProfile(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        // App code
                        Log.e("TAG", "Facebook:onCancel");
                    }

                    @Override
                    public void onError(FacebookException error) {
                        // App code
                        Log.e("TAG", "Facebook:onError");
                    }
                });
            }
        });

    }

    private void getUserProfile(AccessToken currentAccessToken) {
        GraphRequest request = GraphRequest.newMeRequest(currentAccessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                Log.d("TAG", object.toString());
                try {
                    String birthday = "";
                    if (object.has("birthday")) {
                        birthday = object.getString("birthday"); // 01/31/1980 format
                    }
                    String first_name = object.getString("first_name");
                    String last_name = object.getString("last_name");
                    String email = object.getString("email");
                    //String gender = object.getString("gender");
                    String id = object.getString("id");
                    String image_url = "https://graph.facebook.com/" + id + "/picture?type=normal";

                    user_name.setText("First Name: " + first_name + "\nLast Name: " + last_name + "\nBirthday: " + birthday);
                    txtEmail.setText(email);
                    Picasso.with(MainActivity.this).load(image_url).into(imageView);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "first_name,last_name,email,id,birthday,gender");
        request.setParameters(parameters);
        request.executeAsync();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onStop() {
        super.onStop();
        accessTokenTracker.stopTracking();
        profileTracker.stopTracking();
    }

    @Override
    public void onResume() {
        super.onResume();
        Profile profile = Profile.getCurrentProfile();

    }
}
