package efremov.sg.domoffon;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class RegActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserRegTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private EditText mCPasswordView;
    private EditText mAddrView;
    //private EditText mCDEKView;
    private EditText mSurnameView;
    private EditText mNameView;
    private EditText mPatronymicView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);
        setupActionBar();
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mCPasswordView = (EditText) findViewById(R.id.confirm_password);
        mAddrView = (EditText) findViewById(R.id.addr);
        //mCDEKView = (EditText) findViewById(R.id.cd);
        mSurnameView = (EditText) findViewById(R.id.surname);
        mNameView = (EditText) findViewById(R.id.name);
        mPatronymicView = (EditText) findViewById(R.id.patronymic);

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.regbutton);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.reg_form);
        mProgressView = findViewById(R.id.reg_progress);
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }
    public void OpenShop(int res, String secret, String n, String s, String p, String c, String a, String phone){
        Intent intent = new Intent(this, MenuActivity.class);
        intent.putExtra("response", res);
        intent.putExtra("secret", secret);
        intent.putExtra("name", n);
        intent.putExtra("surname", s);
        intent.putExtra("patronymic", p);
        intent.putExtra("cdek", c);
        intent.putExtra("addr", a);
        intent.putExtra("phone", phone);

        startActivity(intent);
        this.finish();
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mCPasswordView.setError(null);
        mAddrView.setError(null);
        //mCDEKView.setError(null);
        mSurnameView.setError(null);
        mNameView.setError(null);
        mPatronymicView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String confirm = mCPasswordView.getText().toString();
        String addr = mAddrView.getText().toString();
        //String cdek = mCDEKView.getText().toString();
        String surname = mSurnameView.getText().toString();
        String name = mNameView.getText().toString();
        String patronymic = mPatronymicView.getText().toString();


        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(confirm) && !isPasswordValid(confirm)) {
            mCPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mCPasswordView;
            cancel = true;
        }

        //Confirm password and password
        if (!password.equals(confirm))
        {
            mPasswordView.setError("Пароли не совпадают!");
            mCPasswordView.setError("Пароли не совпадают!");
            focusView = mPasswordView;
            cancel = true;
        }

        //Check main params
        if (addr.isEmpty())
        {
            mAddrView.setError("Обязательное поле");
            cancel = true;
        }

        if (surname.isEmpty())
        {
            mSurnameView.setError("Обязательное поле");
            cancel = true;
        }

        if (name.isEmpty())
        {
            mNameView.setError("Обязательное поле");
            cancel = true;
        }

        if (patronymic.isEmpty())
        {
            mPatronymicView.setError("Обязательное поле");
            cancel = true;
        }
        //-------

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }


        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserRegTask(email, password, addr, "", surname, name, patronymic);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        if (email.length()!=11&&Integer.valueOf(email.substring(0,1))!=9)
        {
            return false;
        }
        return true;
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
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
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(RegActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserRegTask extends AsyncTask<Void, Void, Boolean> {

        private String response;

        private final String mEmail;
        private final String mPassword;
        private final String mAddr;
        private final String mCDEK;
        private final String mSurname;
        private final String mName;
        private final String mPatronymic;

        public String GetResponse()
        {
            return response;
        }

        UserRegTask(String email, String password, String addr, String cdek, String surname, String name, String patronymic) {
            mEmail = email;
            mPassword = password;
            mAddr = addr;
            mCDEK = cdek;
            mSurname = surname;
            mName = name;
            mPatronymic = patronymic;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                Thread.sleep(2000);
                URL myURL = new URL(getString(R.string.server_ip) + "reg");
                //HttpURLConnection urlConnection = (HttpURLConnection) myURL.openConnection();
                HttpURLConnection conn = (HttpURLConnection) myURL.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                Uri.Builder builder = new Uri.Builder()
                        //.appendQueryParameter("key", "reg")
                        .appendQueryParameter("login", mEmail)
                        .appendQueryParameter("pass", mPassword)
                        .appendQueryParameter("addr", mAddr)
                        .appendQueryParameter("cdek", mCDEK)
                        .appendQueryParameter("surname", mSurname)
                        .appendQueryParameter("name", mName)
                        .appendQueryParameter("patronymic", mPatronymic);
                String query = builder.build().getEncodedQuery();

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();

                conn.connect();

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuffer resp = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    resp.append(inputLine);
                }
                in.close();

                response = resp.toString();
                // Simulate network access
            } catch (InterruptedException e) {
                return false;
            } catch (MalformedURLException e) {
                response = e.getMessage();
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                response = e.getMessage();
                e.printStackTrace();
            } catch (ProtocolException e) {
                response = e.getMessage();
                e.printStackTrace();
            } catch (IOException e) {
                response = e.getMessage();
                e.printStackTrace();
            }

            for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mEmail)) {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                //Intent intent = new Intent (this, ShopActivity.class);
                int answer = 0;
                String secret = "";
                String name = "";
                String sur = "";
                String pat = "";
                String cdek = "";
                String addr = "";
                String phone = "";
                //TODO: append check response
                try {
                    JSONObject dataJsonObj = new JSONObject(response);
                    int code = dataJsonObj.getInt("Code");
                    if (code == 200)
                    {
                        answer = dataJsonObj.getInt("UserID");
                        //secret = dataJsonObj.getString("SecretCode");
                        name = dataJsonObj.getString("Name");
                        sur = dataJsonObj.getString("Surname");
                        pat = dataJsonObj.getString("Patronymic");
                        cdek = dataJsonObj.getString("Cdek");
                        addr = dataJsonObj.getString("Addr");
                        phone = dataJsonObj.getString("Number");
                    }
                    else
                    {
                        Toast toast = Toast.makeText(getApplicationContext(),
                                dataJsonObj.getString("Error"),
                                Toast.LENGTH_SHORT);
                        toast.show();
                        return;
                    }
                } catch (JSONException e) {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Ошибка на стороне сервера",
                            Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }

                OpenShop(answer, secret, name, sur, pat, cdek, addr, phone);
                //finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

