package jp.co.yaz.active_test;

import java.io.IOException;
import java.net.HttpURLConnection;

import android.accounts.AccountManagerCallback;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
// com.sun.jersey.api.client.ClientResponse;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import static com.google.android.gms.common.GoogleApiAvailability.getInstance;
import static com.google.android.gms.common.Scopes.PLUS_LOGIN;


public class MainActivity extends Activity {

    public static final String TAG = "LoginActivty";

    /* RequestCode for resolutions involving sign-in */
    private static final int RC_SIGN_IN = 1;

    /* Is there a ConnectionResult resolution in progress? */
    private boolean mIsResolving = false;

    /* Should we automatically resolve ConnectionResults when possible? */
    private boolean mShouldResolve = false;

    private GoogleApiClient mGoogleApiClient;
    private DefaultHttpClient httpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                onSignInClicked();
            }

        });

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(mConnectionCallbacks)
                .addOnConnectionFailedListener(mOnConnectionFailedListener)
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PROFILE))
                .addScope(new Scope(Scopes.EMAIL))
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    private void onSignInClicked(){
        mShouldResolve = true;
        mGoogleApiClient.connect();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult:" + requestCode + ":" + resultCode + ":" + data);

        if (requestCode == RC_SIGN_IN) {
            // If the error resolution was not successful we should not resolve further.
            if (resultCode != RESULT_OK) {
                mShouldResolve = false;
            }

            mIsResolving = false;
            mGoogleApiClient.connect();
        }
    }

    GoogleApiClient.ConnectionCallbacks mConnectionCallbacks = new GoogleApiClient.ConnectionCallbacks() {

        @Override
        public void onConnected(Bundle bundle) {
            Log.d(TAG, "onConnected:" + bundle);
            mShouldResolve = false;
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                String accountName = Plus.AccountApi.getAccountName(mGoogleApiClient);
                Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
                String personName = currentPerson.getDisplayName();
//                String personPhoto = currentPerson.getImage().getUrl();
//                String personGooglePlusProfile = currentPerson.getUrl();
                Toast.makeText(MainActivity.this, "Signed in as " + personName, Toast.LENGTH_LONG).show();
                new GetAccessTokenAsync(accountName).execute();
            }
        }

        @Override
        public void onConnectionSuspended(int i) {

        }

    };

    GoogleApiClient.OnConnectionFailedListener mOnConnectionFailedListener = new GoogleApiClient.OnConnectionFailedListener() {

        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            Log.d(TAG, "onConnectionFailed:" + connectionResult);

            if (!mIsResolving && mShouldResolve) {
                if (connectionResult.hasResolution()) {
                    try {
                        connectionResult.startResolutionForResult(MainActivity.this, RC_SIGN_IN);
                        mIsResolving = true;
                    } catch (IntentSender.SendIntentException e) {
                        Log.e(TAG, "Could not resolve ConnectionResult.", e);
                        mIsResolving = false;
                        mGoogleApiClient.connect();
                    }
                } else {
                    // Could not resolve the connection result, show the user an
                    // error dialog.
                    showErrorDialog(connectionResult);
                }
            }
        }

    };

    private void showErrorDialog(ConnectionResult connectionResult) {
        GoogleApiAvailability apiAvailability = getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, RC_SIGN_IN,
                        new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                mShouldResolve = false;
                            }
                        }).show();
            } else {
                Log.w(TAG, "Google Play Services Error:" + connectionResult);
                String errorString = apiAvailability.getErrorString(resultCode);
                Toast.makeText(this, errorString, Toast.LENGTH_SHORT).show();

                mShouldResolve = false;
            }
        }
    }

    public class GetAccessTokenAsync extends AsyncTask<String, Integer, String>{

        String accountName;

        public GetAccessTokenAsync(String accountName){
            this.accountName = accountName;
        }

        @Override
        protected String doInBackground(String... strings) {
            String token = null;
            String token2 = null;
            try{
                Log.e(TAG, accountName);
                token = GoogleAuthUtil.getToken(MainActivity.this, accountName,
                     //   "oauth2:https://www.google.com/accounts/OAuthLogin");  //これにすると強制終了
                        "oauth2:https://www.googleapis.com/auth/userinfo.profile");
              //  String uu="https://www.googleapis.com/oauth2/v1/tokeninfo?access_token="+ token;
               // AccountManager.get(getApplicationContext()).invalidateAuthToken("com.google", null);
                Object lO = new DefaultHttpClient();
                Log.e(TAG, "HERE1");
               HttpGet getUbertoken =new HttpGet("https://accounts.google.com/OAuthLogin?source=ChromiumBrowser&issueuberauth=1");
               // HttpGet lH =new HttpGet("https://www.googleapis.com/oauth2/v1/tokeninfo?access_token="+ token);
                getUbertoken.addHeader("Authorization", "OAuth " + token);
                Log.e(TAG, "HERE2");
              //  HttpResponse res=httpClient.execute(getUbertoken);
                lO = ((DefaultHttpClient)lO).execute(getUbertoken);
               // Log.e(TAG, res.toString());
                token2 = EntityUtils.toString(((HttpResponse)lO).getEntity(), "UTF-8");

                Log.e(TAG, token2);
                ///LoginActivty﹕ Error=badauth こう吐いてくれます

            }catch (Exception e){
                e.printStackTrace();
            }

            return token;
        }

        @Override
        protected void onPostExecute(String toke) {
//            Toast.makeText(LoginActivity.this, s, Toast.LENGTH_LONG).show();
//            PlaylistUpdates.ACCESS_TOKEN = s;

            Log.e(TAG, toke);

           // Uri uri = Uri.parse("https://play.google.com/store/account?access_token="+ toke);
            //Uri uri = Uri.parse("https://www.googleapis.com/oauth2/v1/userinfo?access_token="+ toke);
            Uri uri = Uri.parse("https://accounts.google.com/MergeSession?source=ChromiumBrowser&uberauth="+toke
                    +"&continue=https%3A%2F%2Fplay.google.com%2Fstore%2Faccount");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);//アクセストークン取得してるから飛べるはず。。。
            startActivity(intent);
            finish();
        }
    }

}



