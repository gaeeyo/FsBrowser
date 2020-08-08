package jp.syoboi.android.fsbrowser;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {

    static final String TAG      = "FsBrowser";
    static final String PREF_URL = "url";

    WebView           mWebView;
    View              mUiPanel;
    EditText            mUrlEdit;
    SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mWebView = findViewById(R.id.web);
        mUiPanel = findViewById(R.id.uiPanel);
        mUrlEdit = findViewById(R.id.url);

        mUrlEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                mWebView.loadUrl(mUrlEdit.getText().toString());
                enterFullScreen();
                return true;
            }
        });

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.setWebChromeClient(new WebChromeClient() {

        });
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                mUrlEdit.setText(url);
            }
        });

        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                Log.v(TAG, "visibility:" + visibility);
                mUiPanel.setVisibility((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) != 0 ?
                        View.INVISIBLE : View.VISIBLE);
            }
        });

        mWebView.loadUrl(mPrefs.getString(PREF_URL, "https://google.com/"));

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    @Override
    public void onBackPressed() {
        if (mWebView != null && mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPrefs.edit().putString(PREF_URL, mWebView.getUrl()).apply();
    }

    void enterFullScreen() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mUrlEdit.getWindowToken(), 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LOW_PROFILE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_IMMERSIVE);
        } else {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
        mUiPanel.setVisibility(View.GONE);
    }

    public void goBack(View v) {
        if (mWebView != null && mWebView.canGoBack()) mWebView.goBack();
    }

    public void goForward(View v) {
        if (mWebView != null && mWebView.canGoForward()) mWebView.goForward();
    }

    public void goFullScreen(View v) {
        enterFullScreen();
    }

    public void reload(View v) {
        if (mWebView != null) mWebView.reload();
    }

}
