package com.gongdian.qmcb.activity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.ab.activity.AbActivity;
import com.ab.view.titlebar.AbTitleBar;
import com.gongdian.qmcb.R;
import com.gongdian.qmcb.utils.MyApplication;
import com.pgyersdk.feedback.PgyFeedbackShakeManager;

public class AboutActivity extends AbActivity {
	
	private MyApplication application;
    String version = null;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAbContentView(R.layout.activity_about);

        AbTitleBar mAbTitleBar  = this.getTitleBar();
        mAbTitleBar.setTitleText("关于");
        mAbTitleBar.setLogo(R.drawable.title_selector_back);
        mAbTitleBar.setTitleBarBackground(R.color.colorPrimaryDark);
        mAbTitleBar.setTitleTextMargin(20, 0, 0, 0);
        mAbTitleBar.setLogoOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
            }
        });
        //设置AbTitleBar在最上
//        this.setTitleBarOverlay(true);
        application = (MyApplication)abApplication;
        final ProgressBar bar = (ProgressBar)findViewById(R.id.myProgressBar);
        final WebView webView = (WebView)findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    bar.setVisibility(View.INVISIBLE);
                } else {
                    if (View.INVISIBLE == bar.getVisibility()) {
                        bar.setVisibility(View.VISIBLE);
                    }
                    bar.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }

        });


        final String url = "http://www.pgyer.com/qmcbdj";
        webView.loadUrl(url);
    }

    @Override
    protected void onPause() {
        PgyFeedbackShakeManager.unregister();
        super.onPause();
    }

    @Override
    protected void onResume() {
        PgyFeedbackShakeManager.register(AboutActivity.this, false);
        super.onResume();
    }
    
}


