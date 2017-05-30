package info.androidhive.loginandregistration.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import info.androidhive.loginandregistration.R;
import info.androidhive.loginandregistration.app.AppConfig;
import info.androidhive.loginandregistration.app.AppController;
import info.androidhive.loginandregistration.helper.SessionManager;

import static info.androidhive.loginandregistration.activity.MainActivity.EXTRA_EMAIL;
import static info.androidhive.loginandregistration.activity.MainActivity.EXTRA_PASS;
import static info.androidhive.loginandregistration.activity.MainActivity.E_INT;


public class InfosActivity extends Activity {




        StringBuffer strBuf=new StringBuffer();
        TextView tex;
        private static final String TAG = RegisterActivity.class.getSimpleName();
        private ProgressDialog pDialog;


        private SessionManager session;

        String email;
        String password;
        String det;



        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_infos);



            pDialog = new ProgressDialog(this);
            pDialog.setCancelable(false);


            // session manager
            session = new SessionManager(getApplicationContext());

            tex = (TextView) findViewById(R.id.textView);
            tex.setMovementMethod(new ScrollingMovementMethod());

            Intent intent=getIntent();
            det = intent.getStringExtra(E_INT);
            password = intent.getStringExtra(EXTRA_PASS);
            email = intent.getStringExtra(EXTRA_EMAIL);


            if (!email.isEmpty() && !password.isEmpty() ) {
                // login user
                checkLogin(email.trim(), password.trim());
            } else {
                // Prompt user to enter credentials
                Toast.makeText(getApplicationContext(),
                        "Please enter the credentials!", Toast.LENGTH_LONG)
                        .show();
            }


            checkLogin(email.trim(), password.trim());

        }





        //fonction importee
        private void checkLogin(final String email, final String password) {
            this.email = email;
            this.password = password;
            // Tag used to cancel the request
            String tag_string_req = "req_login";

            pDialog.setMessage("Logging in ...");
            showDialog();

            StringRequest strReq = new StringRequest(Request.Method.POST,
                    AppConfig.URL_GETINFOS, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    Log.d(TAG, "Login Response: " + response.toString());
                    hideDialog();
                    try {
                        JSONObject jObj = new JSONObject(response);
                        boolean error = jObj.getBoolean("error");

                        // Check for error node in json
                        if (!error) {

                            // user successfully logged in
                            // Create login session
                            session.setLogin(true);

                            // Now store the user in SQLite
                            String uid = jObj.getString("uid");

                            JSONObject user = jObj.getJSONObject("user");


                            if(det.equals("C")) {
                                System.out.println("ezfzrgr");
                                String contacts = user.getString("contacts");

                                tex.setText(strBuf.append(contacts));
                            }
                            else if(det.equals("A")){
                                String appels = user.getString("appels");
                                tex.setText(strBuf.append(appels));
                            }



                            ///finit
                        } else {
                            // Error in login. Get the error message
                            String errorMsg = jObj.getString("error_msg");
                            Toast.makeText(getApplicationContext(),
                                    errorMsg, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        // JSON error
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }


                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "Login Error: " + error.getMessage());
                    Toast.makeText(getApplicationContext(),
                            error.getMessage(), Toast.LENGTH_LONG).show();
                    hideDialog();
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    // Posting parameters to login url
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("email", email);
                    params.put("password", password);


                    return params;
                }


            };

            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

        }



        private void showDialog() {
            if (!pDialog.isShowing())
                pDialog.show();
        }


        private void hideDialog() {
            if (pDialog.isShowing())
                pDialog.dismiss();
        }
}





