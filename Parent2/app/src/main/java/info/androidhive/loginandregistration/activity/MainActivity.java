package info.androidhive.loginandregistration.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import info.androidhive.loginandregistration.helper.SQLiteHandler;
import info.androidhive.loginandregistration.helper.SessionManager;

public class MainActivity extends Activity {

	public static final String EXTRA_LAT = "latitude";
	public static final String EXTRA_LONG = "longitude";

	public static final String EXTRA_EMAIL = "email";
	public static final String EXTRA_PASS = "password";
	public static final String E_INT = "ent1";



	private static final String TAG = RegisterActivity.class.getSimpleName();
	private ProgressDialog pDialog;
	private TextView txtName;
	private TextView txtEmail;
	private Button btnLogout;
	private Button btnRecInfos;
	private Button btnContacts;
	private Button btnAppels;
	private Button btnSms;

	private Button btnConfirmer;
	private Button btnRegisterFils;

	private SQLiteHandler db;
	private SessionManager session;

	String email = null;
	String password = null;
	//Ajout coordonnes
	String latitude;
	String longitude;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		txtName = (TextView) findViewById(R.id.name);
		txtEmail = (TextView) findViewById(R.id.email);




		btnLogout = (Button) findViewById(R.id.btnLogout);
		btnRecInfos = (Button) findViewById(R.id.btnRecInfos);
		btnRecInfos.setEnabled(false);
		btnContacts = (Button) findViewById(R.id.btnContacts);
		btnContacts.setEnabled(false);
		btnAppels = (Button) findViewById(R.id.btnAppels);
		btnAppels.setEnabled(false);
		btnConfirmer = (Button) findViewById(R.id.btnConfirmer);
		btnRegisterFils = (Button) findViewById(R.id.buttonRegisterFils);
		btnSms = (Button) findViewById(R.id.btnSms);
		btnSms.setEnabled(false);

		pDialog = new ProgressDialog(this);
		pDialog.setCancelable(false);

		// SqLite database handler
		db = new SQLiteHandler(getApplicationContext());

		// session manager
		session = new SessionManager(getApplicationContext());

		if (!session.isLoggedIn()) {
			logoutUser();
		}

		// Fetching user details from SQLite
		HashMap<String, String> user = db.getUserDetails();

		String name = user.get("name");
		email = user.get("email");
		password = user.get("password");

		//Displaying the user details on the screen
		//txtName.setText(name);
		//txtEmail.setText(email);







		// Logout button click event
		btnLogout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				logoutUser();
			}
		});

		btnRecInfos.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {


				// Check for empty data in the form
				if (!email.isEmpty() && !password.isEmpty() ) {
					// login user
					checkLogin(email.trim(), password.trim());
				} else {
					// Prompt user to enter credentials
					Toast.makeText(getApplicationContext(),
							"Please enter the credentials!", Toast.LENGTH_LONG)
							.show();
				}

			}
		});

		btnContacts.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {

				Intent intent = new Intent(MainActivity.this, InfosActivity.class);

				intent.putExtra(EXTRA_EMAIL, email);
				intent.putExtra(EXTRA_PASS, password);
				intent.putExtra(E_INT, "C");
				startActivity(intent);


			}
		});


		btnAppels.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {

				Intent intent = new Intent(MainActivity.this, InfosActivity.class);

				intent.putExtra(EXTRA_EMAIL, email);
				intent.putExtra(EXTRA_PASS, password);
				intent.putExtra(E_INT, "A");
				startActivity(intent);


			}
		});



		btnConfirmer.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {


				// Check for empty data in the form


				//Intent intent = new Intent(MainActivity.this, IdentifiantsActivity.class);

				EditText editText3 = (EditText) findViewById(R.id.editText3);
				EditText editText4 = (EditText) findViewById(R.id.editText4);

				email = editText3.getText().toString();
				password = editText4.getText().toString();


				new AlertDialog.Builder(MainActivity.this)
						.setTitle("Confirm")
						.setMessage("Do you want really to continue ?")
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog, int whichButton) {
								//Toast.makeText(MainActivity.this, "Yaay", Toast.LENGTH_SHORT).show();
								btnContacts.setEnabled(true);
								btnRecInfos.setEnabled(true);
								btnAppels.setEnabled(true);
								btnSms.setEnabled(true);

							}})
						.setNegativeButton(android.R.string.no, null).show();



			}
		});

		btnRegisterFils.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Intent registerFils = new Intent(MainActivity.this, RegisterFilsActivity.class);
				startActivity(registerFils);

			}
		});

		btnSms.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Intent i = new Intent(MainActivity.this, GetSmsActivity.class);
				i.putExtra(EXTRA_EMAIL, email);
				i.putExtra(EXTRA_PASS, password);
				startActivity(i);

			}
		});


		startService(new Intent(this, SmsNotificationService.class));

	}



	/**
	 * Logging out the user. Will set isLoggedIn flag to false in shared
	 * preferences Clears the user data from sqlite users table
	 * */
	private void logoutUser() {
		session.setLogin(false);

		db.deleteUsers();

		// Launching the login activity
		Intent intent = new Intent(MainActivity.this, LoginActivity.class);
		startActivity(intent);
		finish();
	}




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

						String latitude = user.getString("latitude");
						String longitude = user.getString("longitude");

						System.out.println("--------------------------------------------------------------");
						System.out.println(latitude);
						System.out.println(longitude);

						Intent myIntent = new Intent(MainActivity.this, MapsActivity.class);
						myIntent.putExtra(EXTRA_LAT, latitude);
						myIntent.putExtra(EXTRA_LONG, longitude);
						startActivity(myIntent);

						// Inserting row in users table
						//db.addUser(name, email, uid, created_at, updated_at, latitude, longitude);



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
