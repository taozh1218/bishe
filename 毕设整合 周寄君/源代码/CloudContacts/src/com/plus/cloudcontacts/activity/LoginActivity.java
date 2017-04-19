package com.plus.cloudcontacts.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;


import com.google.gson.Gson;
import com.plus.cloudcontacts.R;
import com.plus.cloudcontacts.bean.Contact;
import com.plus.cloudcontacts.http.PostWork;
import com.plus.cloudcontacts.service.WmService;
import com.plus.cloudcontacts.util.DBManager;
import com.plus.cloudcontacts.util.MyApplication;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends Activity {
	/**
	 * A dummy authentication store containing known user names and passwords.
	 * TODO: remove after connecting to a real authentication system.
	 */
	private static final String[] DUMMY_CREDENTIALS = new String[] {
			"foo@example.com:hello", "bar@example.com:world" };

	/**
	 * The default Username to populate the Username field with.
	 */
	public static final String EXTRA_Username = "com.example.android.authenticatordemo.extra.Username";

	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserLoginTask mAuthTask = null;

	// Values for Username and password at the time of the login attempt.
	private String mUsername;
	private String mPassword;

	// UI references.
	private EditText mUsernameView;
	private EditText mPasswordView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;
	private DBManager dbm;
	
	private static final int SIGN_IN = 0;
	private static final int SIGN_UP = 1;
	private static final int DATABASE_WORK_SUCCESS = 100;

	private MyApplication app;
	
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			//Toast.makeText(getApplicationContext(), msg.obj.toString(), 3000).show();
			if(msg.what==1){
				Gson gson = new Gson();
		        String session = msg.getData().getString("msg");
		        app.getThreadPool().submit(new DatabaseWorkThread(session,gson.toJson(msg.obj)));
			}else if (msg.what==DATABASE_WORK_SUCCESS){
				Gson gson = new Gson();
				Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
				intent.putExtra("bean", gson.toJson(msg.obj));
				startActivity(intent);
			}
		}

		class DatabaseWorkThread implements Runnable{
			String session;
			String json;
			public DatabaseWorkThread(String session, String json){
				this.session = session;
				this.json = json;
			}
			@Override
			public void run() {
				// TODO Auto-generated method stub
				dbm.newSession(session);
		        app.setSessionId(session);
		        Gson gson = new Gson();
		        Contact bean = gson.fromJson(json,Contact.class);
		        app.setMe(bean);
		        dbm.saveContact(bean,null);
		        Message msg = new Message();
		        msg.what = DATABASE_WORK_SUCCESS;
		        msg.obj = bean;
		        mHandler.sendMessage(msg);
			}
			
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);

		app = (MyApplication) getApplication();
        dbm = new DBManager(LoginActivity.this);
		// Set up the login form.
		mUsername = getIntent().getStringExtra(EXTRA_Username);
		mUsernameView = (EditText) findViewById(R.id.username);
		mUsernameView.setText(mUsername);

		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							attemptLogin(SIGN_IN);
							return true;
						}
						return false;
					}
				});

		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);
		
        
		findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptLogin(SIGN_IN);
					}
				});
		findViewById(R.id.sign_up_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptLogin(SIGN_UP);
					}
				});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid Username, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin(int opt) {
		if (mAuthTask != null) {
			return;
		}

		// Reset errors.
		mUsernameView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mUsername = mUsernameView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} else if (mPassword.length() < 4) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}

		// Check for a valid Username address.
		if (TextUtils.isEmpty(mUsername)) {
			mUsernameView.setError(getString(R.string.error_field_required));
			focusView = mUsernameView;
			cancel = true;
//		} else if (!mUsername.contains("@")) {
//			mUsernameView.setError(getString(R.string.error_invalid_email));
//			focusView = mUsernameView;
//			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			if(opt==SIGN_IN){
				mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			}else{
				mLoginStatusMessageView.setText(R.string.login_progress_signing_up);
			}
			showProgress(true);
			mAuthTask = new UserLoginTask(opt);
			mAuthTask.execute();
		}
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
		private int opt;
		public UserLoginTask(int opt){
			this.opt = opt;
		}
		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.

//			try {
//				// Simulate network access.
//				Thread.sleep(2000);
//				
//			} catch (InterruptedException e) {
//				return false;
//			}
			Map<String,String> param = new HashMap<String,String>();
			param.put("username", mUsername);
			param.put("password", mPassword);
			PostWork pw;
			if (opt==SIGN_IN){
				pw = new PostWork(PostWork.URL_LOGIN,param,mHandler);
			}else{
				pw = new PostWork(PostWork.URL_SIGN_UP,param,mHandler);
			}
			return pw.work();
			
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;
			showProgress(false);

			if (success) {
				finish();
			} else {
				if (opt==SIGN_IN){
					mPasswordView.setError(getString(R.string.error_incorrect_password));
					mPasswordView.requestFocus();
				}else{
					mUsernameView.setError(getString(R.string.error_username_exist));
					mUsernameView.requestFocus();
				}
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}
}
