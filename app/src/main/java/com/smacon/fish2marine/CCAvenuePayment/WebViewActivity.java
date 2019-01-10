package com.smacon.fish2marine.CCAvenuePayment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.smacon.fish2marine.CCAvenuePayment.utility.AvenuesParams;
import com.smacon.fish2marine.CCAvenuePayment.utility.Constants;
import com.smacon.fish2marine.CCAvenuePayment.utility.RSAUtility;
import com.smacon.fish2marine.CCAvenuePayment.utility.ServiceHandler;
import com.smacon.fish2marine.CCAvenuePayment.utility.ServiceUtility;
import com.smacon.fish2marine.Constants.OrdersConstants;
import com.smacon.fish2marine.R;
import com.smacon.fish2marine.SuccessActivity;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EncodingUtils;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class WebViewActivity extends AppCompatActivity {
	private ProgressDialog dialog;
	Intent mainIntent;
	String html, encVal;
	
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.activity_webview);
		mainIntent = getIntent();
		
		// Calling async task to get display content
		new RenderView().execute();
	}
	
	/**
	 * Async task class to get json by making HTTP call
	 * */
	public class RenderView extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// Showing progress dialog
			dialog = new ProgressDialog(WebViewActivity.this);
			dialog.setMessage("Please wait...");
			dialog.setCancelable(false);
			dialog.show();
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			// Creating service handler class instance
			ServiceHandler sh = new ServiceHandler();
	
			// Making a request to url and getting response
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair(AvenuesParams.ACCESS_CODE, mainIntent.getStringExtra(OrdersConstants.ACCESS_CODE)));
			params.add(new BasicNameValuePair(AvenuesParams.ORDER_NUMBER, mainIntent.getStringExtra(OrdersConstants.ORDER_NUMBER)));
			//params.add(new BasicNameValuePair(AvenuesParams.NAME,mainIntent.getStringExtra(OrdersConstants.NAME)));
			Log.d("11111111","webviewaccess" +mainIntent.getStringExtra(OrdersConstants.ACCESS_CODE));
			Log.d("11111111","webviewordernum" +mainIntent.getStringExtra(OrdersConstants.ORDER_NUMBER));
			Log.d("11111111","webviewrsa" +mainIntent.getStringExtra(OrdersConstants.RSA_URL));
			Log.d("11111111","webviewmerchant" +mainIntent.getStringExtra(OrdersConstants.MERCHANT_ID));
			Log.d("11111111","webviewsucess" +mainIntent.getStringExtra(OrdersConstants.SUCCESS_URL));
			Log.d("11111111","webviewcancel" +mainIntent.getStringExtra(OrdersConstants.CANCEL_URL));

			String vResponse = sh.makeServiceCall(mainIntent.getStringExtra(OrdersConstants.RSA_URL), ServiceHandler.POST, params);
			System.out.println(vResponse);
			Log.d("11111111","webview" +vResponse);
			if(!ServiceUtility.chkNull(vResponse).equals("") 
					&& ServiceUtility.chkNull(vResponse).toString().indexOf("ERROR")==-1){
				StringBuffer vEncVal = new StringBuffer("");
				vEncVal.append(ServiceUtility.addToPostParams(AvenuesParams.AMOUNT, mainIntent.getStringExtra(OrdersConstants.ORDER_AMOUNT)));
				vEncVal.append(ServiceUtility.addToPostParams(AvenuesParams.CURRENCY, mainIntent.getStringExtra(OrdersConstants.CURRENCY)));
				encVal = RSAUtility.encrypt(vEncVal.substring(0,vEncVal.length()-1), vResponse);
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			// Dismiss the progress dialog
			if (dialog.isShowing())
				dialog.dismiss();
			
			@SuppressWarnings("unused")
			class MyJavaScriptInterface
			{
				@JavascriptInterface
			    public void processHTML(String html)
			    {
			        // process the html as needed by the app
			    	String status = null;
			    	if(html.indexOf("Failure")!=-1){
			    		status = "Transaction Declined!";
			    	}else if(html.indexOf("Success")!=-1){
			    		status = "Transaction Successful!";
			    	}else if(html.indexOf("Aborted")!=-1){
			    		status = "Transaction Cancelled!";
			    	}else{
			    		status = "Status Not Known!";
			    	}
			    	//Toast.makeText(getApplicationContext(), status, Toast.LENGTH_SHORT).show();
			    	Intent intent = new Intent(getApplicationContext(),StatusActivity.class);
					intent.putExtra("transStatus", status);
					startActivity(intent);
			    }
			}
			
			final WebView webview = (WebView) findViewById(R.id.webview);
			webview.getSettings().setJavaScriptEnabled(true);
			webview.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");
			webview.setWebViewClient(new WebViewClient(){
				@Override  
	    	    public void onPageFinished(WebView view, String url) {
	    	        super.onPageFinished(webview, url);
	    	        if (url.indexOf("ccavenuepay/ccavenuepay/successmob")!=-1){
						Intent i = new Intent(WebViewActivity.this,SuccessActivity.class);
						i.putExtra("ORDER_ID", mainIntent.getStringExtra(OrdersConstants.ORDER_ID));
						i.putExtra("FROM_CHECKOUT", true);
						startActivity(i);
						/*MainActivity.getInstance().updateCartCount();
						if (ProductsActivity.getInstance() != null)
							ProductsActivity.getInstance().updateCartCount();
						if (ProductDetailsActivity.getInstance() != null)
							ProductDetailsActivity.getInstance().updateCartCount();*/
						finish();
					}
					else if (url.indexOf("ccavenuepay/ccavenuepay/failuremob")!=-1){
						Intent i = new Intent(WebViewActivity.this,SuccessActivity.class);
						i.putExtra("ORDER_ID", mainIntent.getStringExtra(OrdersConstants.ORDER_ID));
						i.putExtra("FROM_CHECKOUT", false);
						startActivity(i);
						/*MainActivity.getInstance().updateCartCount();
						if (ProductsActivity.getInstance() != null)
							ProductsActivity.getInstance().updateCartCount();
						if (ProductDetailsActivity.getInstance() != null)
							ProductDetailsActivity.getInstance().updateCartCount();*/
						finish();
					}
	    	        else if(url.indexOf("/ccavResponseHandler.jsp")!=-1){
	    	        	webview.loadUrl("javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
	    	        }
	    	    }  

	    	    @Override
	    	    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
	    	        Toast.makeText(getApplicationContext(), "Oh no! " + description, Toast.LENGTH_SHORT).show();
	    	    }
			});
			
			/* An instance of this class will be registered as a JavaScript interface */
			StringBuffer params = new StringBuffer();
			params.append(ServiceUtility.addToPostParams(AvenuesParams.ACCESS_CODE,mainIntent.getStringExtra(OrdersConstants.ACCESS_CODE)));
			params.append(ServiceUtility.addToPostParams(AvenuesParams.MERCHANT_ID,mainIntent.getStringExtra(OrdersConstants.MERCHANT_ID)));
			params.append(ServiceUtility.addToPostParams(AvenuesParams.ORDER_NUMBER,mainIntent.getStringExtra(OrdersConstants.ORDER_NUMBER)));
			params.append(ServiceUtility.addToPostParams(AvenuesParams.REDIRECT_URL,mainIntent.getStringExtra(OrdersConstants.SUCCESS_URL)));
			params.append(ServiceUtility.addToPostParams(AvenuesParams.CANCEL_URL,mainIntent.getStringExtra(OrdersConstants.CANCEL_URL)));
			params.append(ServiceUtility.addToPostParams(AvenuesParams.BILLING_NAME,mainIntent.getStringExtra(OrdersConstants.BILLING_NAME)));
			params.append(ServiceUtility.addToPostParams(AvenuesParams.BILLING_ADDRESS,mainIntent.getStringExtra(OrdersConstants.BILLING_ADDRESS)));
			params.append(ServiceUtility.addToPostParams(AvenuesParams.BILLING_ZIP,mainIntent.getStringExtra(OrdersConstants.BILLING_ZIP)));
			params.append(ServiceUtility.addToPostParams(AvenuesParams.BILLING_CITY,mainIntent.getStringExtra(OrdersConstants.BILLING_CITY)));
			params.append(ServiceUtility.addToPostParams(AvenuesParams.BILLING_STATE,mainIntent.getStringExtra(OrdersConstants.BILLING_STATE)));
			params.append(ServiceUtility.addToPostParams(AvenuesParams.BILLING_COUNTRY,mainIntent.getStringExtra(OrdersConstants.BILLING_COUNTRY)));
			params.append(ServiceUtility.addToPostParams(AvenuesParams.BILLING_TEL,mainIntent.getStringExtra(OrdersConstants.BILLING_TEL)));

			params.append(ServiceUtility.addToPostParams(AvenuesParams.ENC_VAL,URLEncoder.encode(encVal)));
			
			String vPostParams = params.substring(0,params.length()-1);
			try {
                String postData = URLEncoder.encode(vPostParams, "UTF-8");
                webview.postUrl(Constants.TRANS_URL, EncodingUtils.getBytes(vPostParams, "UTF-8"));
				Log.d("111111111 vpostparam",postData);
			} catch (Exception e) {
				showToast("Exception occured while opening webview.");
			}
		}
	}
	
	public void showToast(String msg) {
		Toast.makeText(this, "Toast: " + msg, Toast.LENGTH_LONG).show();
	}
} 