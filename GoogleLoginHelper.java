package candra.me.googleloginexample.helper;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
/**
 * Created by candra on 18-Apr-16.
 */
public class GoogleLoginHelper implements OnConnectionFailedListener{

    FragmentActivity context;
    OnRetrieveDataListener onRetrieveDataListener;

    public static final int RC_SIGN_IN = 0;
    private static final String TAG = GoogleLoginHelper.class.getSimpleName();

    private GoogleApiClient mGoogleApiClient;

    public GoogleLoginHelper(FragmentActivity context, OnRetrieveDataListener onRetrieveDataListener) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.onRetrieveDataListener = onRetrieveDataListener;
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.PLUS_LOGIN))
                .requestEmail()
                .requestProfile()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .enableAutoManage(context, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, options)
                .addApi(Plus.API)
                .build();
    }

    public void start(){
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            getProfileInformation(result);
        } else {
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    getProfileInformation(googleSignInResult);
                }
            });
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // TODO Auto-generated method stub
        Log.d(TAG, "onConnectionFailed:" + result);
    }

    public void setOnActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            getProfileInformation(result);
        }
    }

    /**
     * Fetching user's information name, email, profile pic
     * */
    private void getProfileInformation(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();

            String id = "";
            String name = "";
            String email = "";
            String image_url = "";

            try {
                id = acct.getId();
                name = acct.getDisplayName();
                email = acct.getEmail();
                image_url = acct.getPhotoUrl().toString();
                onRetrieveDataListener.onRetrieveData(id, name, email, image_url);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * Sign-in into google
     * */
    public void signInWithGplus() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        context.startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    /**
     * Sign-out from google
     * */
    public void signOutFromGplus() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        Log.e(TAG, "Logout");
                    }
                });
    }

    /**
     * Revoking access from google
     * */
    public void revokeGplusAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        Log.e(TAG, "User access revoked!");
                    }
                });
    }

    public interface OnRetrieveDataListener{
        void onRetrieveData(String id, String name, String email, String image_url);
    }

}
