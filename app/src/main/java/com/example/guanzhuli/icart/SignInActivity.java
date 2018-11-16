package com.example.guanzhuli.icart;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.guanzhuli.icart.data.SPManipulation;
import com.example.guanzhuli.icart.utils.AppController;
import com.facebook.*;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



// http://rjtmobile.com/ansari/shopingcart/androidapp/shop_login.php?mobile=123456&password=ansari

public class SignInActivity extends AppCompatActivity {

    private static final String LOGIN_URL = "http://rjtmobile.com/ansari/shopingcart/androidapp/shop_login.php?";
    private LoginButton mLoginButton;
    private CallbackManager callbackManager;
    private Button mButtonSignIn;
    private Button mFbButtonSignIn;
    private TextView mTextUsername, mTextPassword, mTextSignUp;
    private AppController mController;
    private RequestQueue mRequestQueue;
    private SPManipulation mSPManipulation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppEventsLogger.activateApp(this);
        setContentView(R.layout.activity_signin);
        mSPManipulation = SPManipulation.getInstance(this);
        mController = AppController.getInstance();
        mRequestQueue = mController.getRequestQueue();
        mTextUsername = (TextView) findViewById(R.id.Sign_in_username);
        mTextPassword = (TextView) findViewById(R.id.sign_in_password);
        mButtonSignIn = (Button) findViewById(R.id.button_sign_in);
        if (mSPManipulation.hasUserLoggedIn()) {
            startActivity(new Intent(SignInActivity.this, MainActivity.class));
            finish();
            return;
        }

        mButtonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               startActivity(new Intent(SignInActivity.this, MainActivity.class));

                final String mobile = mTextUsername.getText().toString();
                final String pwd = mTextPassword.getText().toString();

                String Url = LOGIN_URL + "mobile=" + mobile + "&password=" + pwd;

                StringRequest stringRequest = new StringRequest(Request.Method.GET, Url,
                        new Response.Listener<String>() {
                                @Override
                            public void onResponse(String s) {
                                try {
                                   // Toast.makeText(SignInActivity.this, "response："+s, Toast.LENGTH_LONG).show();
                                    JSONObject jsonObject = new JSONObject(s);
                                    String msg = jsonObject.getString("msg");
                                   // String msg ="success";
                                 //   Toast.makeText(SignInActivity.this, ""+msg, Toast.LENGTH_LONG).show();
                                    if (msg.contains("failure")) {
                                        mTextPassword.setText("");
                                        Toast.makeText(SignInActivity.this,
                                                "Username and password does not match!", Toast.LENGTH_LONG).show();
                                    }
                                    return;
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    JSONArray array = new JSONArray(s);
                                    JSONObject obj = array.getJSONObject(0);
                                    String msg = obj.getString("msg");
                                    if (msg.contains("success")) {
                                     //    new SPManipulation().save(SignInActivity.this, s);
                                        String username = obj.getString("UserName");
                                        String email = obj.getString("UserEmail");
                                        mSPManipulation.saveName(username);
                                        mSPManipulation.saveEmail(email);
                                        mSPManipulation.savePwd(pwd);
                                        mSPManipulation.saveMobile(mobile);
                                        Intent i = new Intent(SignInActivity.this, MainActivity.class);
                                        startActivity(i);
                                        finish();
                                    }
                                    return;
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(SignInActivity.this, "network error!", Toast.LENGTH_LONG).show();
                      //  VolleyLog.d(TAG, "Error: " + error.getMessage());
                      //  hideProgressDialog();
                    }
                }) {

                    @Override
                    protected Response<String> parseNetworkResponse(NetworkResponse response) {
                        int mStatusCode = response.statusCode;
                        return super.parseNetworkResponse(response);
                    }

                };
                mRequestQueue.add(stringRequest);
            }
        });
        mTextSignUp = (TextView) findViewById(R.id.to_sign_up);
        mTextSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(i);
            }
        });
        mLoginButton = new LoginButton(this);
        mLoginButton.setReadPermissions("email");
        callbackManager = CallbackManager.Factory.create();
        mLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.e("fblogin", "success");
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                try {

                                    String email = object.getString("email");
                                    Log.e("fblogin", email);
                                    String id = object.getString("id");
                                    Log.e("fblogin", id);
                                    String name = object.getString("first_name").toLowerCase();
                                    Log.e("fblogin", name);
                                    mSPManipulation.saveName(name);
                                    mSPManipulation.saveEmail(email);
                                    mSPManipulation.savePwd(" ");
                                    mSPManipulation.saveMobile(id);
                                    startActivity(new Intent(SignInActivity.this, MainActivity.class));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,first_name,email");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Log.e("fblogin", "cancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.e("fblogin", "error");

            }
        });
        mFbButtonSignIn = (Button)findViewById(R.id.button_fb_sign_in);
        mFbButtonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLoginButton.performClick();
            }
        });

    }
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
