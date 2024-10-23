package goldenage.delfis.app.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import goldenage.delfis.app.R;
import goldenage.delfis.app.model.response.User;
import goldenage.delfis.app.ui.activity.navbar.ConfigActivity;
import goldenage.delfis.app.ui.activity.navbar.HomeActivity;
import goldenage.delfis.app.ui.activity.navbar.StoreActivity;

public class RestrictAreaActivity extends AppCompatActivity {
    private User user;
    private WebView webView;
    private ProgressBar progressBar;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restrict_area);

        webView = findViewById(R.id.webView);
        progressBar = findViewById(R.id.progressBar);
        user = (User) getIntent().getSerializableExtra("user");

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);

        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                if(progress < 100 && progressBar.getVisibility() == ProgressBar.GONE)
                    progressBar.setVisibility(ProgressBar.VISIBLE);

                progressBar.setProgress(progress);

                if(progress == 100)
                    progressBar.setVisibility(ProgressBar.GONE);
            }
        });

        webView.loadUrl("https://delfis-project.github.io/delfis-restrict-area");
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
            Intent intent = new Intent(RestrictAreaActivity.this, ConfigActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
            finish();
        }
    }
}