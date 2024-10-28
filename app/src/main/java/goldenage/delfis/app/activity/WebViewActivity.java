package goldenage.delfis.app.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import goldenage.delfis.app.R;
import goldenage.delfis.app.model.response.User;
import goldenage.delfis.app.activity.navbar.ConfigActivity;

public class WebViewActivity extends AppCompatActivity {
    private User user;
    private WebView webView;
    private ProgressBar progressBar;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        String url = getIntent().getStringExtra("url");
        if (url == null) {
            Intent intent = new Intent(WebViewActivity.this, ConfigActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
            finish();
            return;
        }

        webView = findViewById(R.id.webView);
        progressBar = findViewById(R.id.progressBar);

        renderizarInfo();

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);

        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                if (progress < 100 && progressBar.getVisibility() == ProgressBar.GONE)
                    progressBar.setVisibility(ProgressBar.VISIBLE);

                progressBar.setProgress(progress);

                if (progress == 100)
                    progressBar.setVisibility(ProgressBar.GONE);
            }
        });

        webView.loadUrl(url);
    }

    @Override
    protected void onResume() {
        super.onResume();
        renderizarInfo();
    }

    private void renderizarInfo() {
        user = (User) getIntent().getSerializableExtra("user");
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
            Intent intent = new Intent(WebViewActivity.this, RestrictAreaActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
            finish();
        }
    }
}