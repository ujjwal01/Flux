package com.ujjwal.webbrowser;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
//import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
  
public class MainActivity extends Activity implements OnClickListener{
    final Activity activity = this;
    private Button go;
    private WebView ourBrowser;
    EditText url;
    private static final String TAG = "Main";
    private static final String DEFAULT_URL = "https://www.google.com";
    @SuppressLint("SetJavaScriptEnabled") @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.getWindow().requestFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.activity_main);
        ourBrowser = (WebView) findViewById(R.id.wvBrowser);
        ourBrowser = BrowserSettings(ourBrowser);
        go = (Button)findViewById(R.id.bgo);
        Button forward = (Button)findViewById(R.id.bfwd);
        Button back = (Button)findViewById(R.id.bback);
        Button refresh = (Button)findViewById(R.id.bref);
        Button clearhistory = (Button)findViewById(R.id.bhis);
        url = (EditText)findViewById(R.id.etUrm);
        url.setSingleLine(true);
        /*url.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				url.setCursorVisible(true);
				return false;
			}
		});
        ourBrowser.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					url.setCursorVisible(false);
					return false;
				}
			});*/
        go.setOnClickListener(this);
        forward.setOnClickListener(this);
        back.setOnClickListener(this);
        refresh.setOnClickListener(this);
        clearhistory.setOnClickListener(this);
        
         
        ourBrowser.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress)
            {
                activity.setTitle("Loading...");
                activity.setProgress(progress * 100);
                url.setText(ourBrowser.getUrl());
                go.setText("X");
  
                if(progress == 100){
	                    activity.setTitle("");
	                	url.setText(ourBrowser.getUrl());
	                	go.setText("Go");
	                }
            }
        });
  
        ourBrowser.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)
            {
            	String search = url.getText().toString().replace(" ", "+");
            	search = SearchLink(search);
            	ourBrowser.loadUrl(search);
            	Log.e(TAG, "Error: " + description);
                Context activity = view.getContext();
				Toast.makeText(activity, "Error: " + description, Toast.LENGTH_SHORT).show();
            }
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                view.loadUrl(url);
                return true;
            }
        });
         
        try{
        	ourBrowser.loadUrl(DEFAULT_URL);
        }catch(Exception e){
        	e.printStackTrace();
        }
    }

        

    @Override
	protected void onRestart() {
		super.onRestart();
		if(ourBrowser.canGoBack()){
			ourBrowser.goBack();
		}else{
			ourBrowser.loadUrl(getWebsiteLink());
		}
	}



	@Override
	protected void onResume() {
		super.onResume();
		if(ourBrowser.canGoBack()){
			ourBrowser.goBack();
		}else{
			ourBrowser.loadUrl(getWebsiteLink());
		}
	}

	

	@Override
	public void onBackPressed() {
		//super.onBackPressed();
		if(ourBrowser.canGoBack()){
			ourBrowser.goBack();
		}else{
			AlertDialog.Builder alert= new AlertDialog.Builder(this);
			alert.setTitle("Exitting...")
			.setMessage("Do you want to clear cache?")
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
				
			    public void onClick(DialogInterface dialog, int whichButton) {
			    	ourBrowser.clearCache(true);
					moveTaskToBack(true);
			        Toast.makeText(MainActivity.this, "Cache Cleared", Toast.LENGTH_SHORT).show();
			        
			    }})
			 .setNegativeButton(android.R.string.no,  new DialogInterface.OnClickListener() {
	             public void onClick(DialogInterface dialog, int id) {

	                 dialog.cancel();
	     			moveTaskToBack(true);
	             }}).create().show();

		}
	}


	public void onClick(View view){	
		switch (view.getId()) {
		case R.id.bgo:
						if(go.getText().equals("Go")){
						String theWebSite = getWebsiteLink();
						ourBrowser.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
						ourBrowser.loadUrl(theWebSite);
						InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(url.getApplicationWindowToken(), 0);
						//ourBrowser.focusableViewAvailable(ourBrowser);
						}
						else if(go.getText().equals("X")){
							ourBrowser.stopLoading();
							go.setText("Go");
						}
						break;
		case R.id.bfwd:
						if(ourBrowser.canGoForward()){
							ourBrowser.goForward();
							url.setText(ourBrowser.getUrl());
						}
						break;
		case R.id.bback:
						if(ourBrowser.canGoBack()){
							ourBrowser.goBack();
							url.setText(ourBrowser.getUrl());
						}
						break;
		case R.id.bref:
						ourBrowser.reload();
						url.setText(ourBrowser.getUrl());
						break;
		case R.id.bhis:
						ourBrowser.clearHistory();
		}
    }

	private String getWebsiteLink() {
		String theWebSite = url.getText().toString().trim();
		go.setText("X");
		if(theWebSite.contains(" ")||(!theWebSite.contains("."))){
			
			theWebSite.replace(" ", "+");
			theWebSite = SearchLink(theWebSite);
		}else
		if(!theWebSite.contains("http")){
				theWebSite = "http://"+theWebSite;
		}
		return theWebSite;
	}



	//@SuppressWarnings("deprecation")
	@SuppressLint("SetJavaScriptEnabled")
		private WebView BrowserSettings(WebView ourBrowser) {
		    	WebSettings settings = ourBrowser.getSettings();
		    	settings.setJavaScriptEnabled(true);
		    	settings.setLoadWithOverviewMode(true);
		    	settings.setLoadWithOverviewMode(true);
		    	settings.setBuiltInZoomControls(true);
		        settings.setSupportMultipleWindows(true);
		        //ourBrowser.setFocusable(true);
		        //ourBrowser.setClickable(true);
		        //settings.setLightTouchEnabled(true);
		        return ourBrowser;
		        
	}
	
	 private String SearchLink(String search) {
			// TODO Auto-generated method stub
			return "http://www.google.com/search?q="+search+"&sourceid=ie7&rls=com.microsoft:en-US&ie=utf8&oe=utf8";
		}

}
