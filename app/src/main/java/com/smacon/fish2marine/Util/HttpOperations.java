package com.smacon.fish2marine.Util;

/**
 * Created by anju on 23/02/17.
 */

import android.content.Context;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HttpOperations {

    public final String SERVER_URL = "http://dev1.fish2marine.com/rest/";


    public Context ctx;

    public HttpOperations(Context ctx) {
        this.ctx = ctx;
    }

    enum APIS {
        API_RESEND_OTP("V1/resendotp","POST"),
        API_FORGOT_PASSWORD("V1/forgotpass","POST"),
        API_RESETPASSWORD("V1/resetpass","POST"),
        API_MY_REFERRALS("V1/myreferrals","POST"),
        API_REFERRAL_INVITE("V1/myreferralsinvite","POST"),
        API_ORDER_DETAILS("V1/orderview","GET"),
        API_MY_ORDERLIST("V1/myorderhistory", "POST"),
        API_GOTO_PAYMENT("V1/getpaymentdata", "GET"),
        API_PLACEORDER("V1/carts", "PUT"),
        API_SHIPPING_INFORMATION("V1/carts", "POST"),
        API_PAYMENT_METHOD("V1/setcheckoutdata", "POST"),
        API_SHIPPING_METHOD("retail_english/V1/carts", "POST"),
        API_APPLY_REWARDS("V1/reward", "POST"),
        API_APPLY_COUPON("V1/applygiftcard", "POST"),
        API_MY_REWARDS("V1/myrewards", "POST"),
        API_DELETE_ADDRESS("V1/deleteaddress", "GET"),
        API_MY_PROFILE("V1/myaccount", "GET"),
        API_EDIT_PROFILE("V1/editaccount", "POST"),
        API_MY_ADDRESS("V1/addresslist", "GET"),
        API_ADD_ADDRESS("V1/addaddress", "POST"),
        API_DELETE_CART("V1/deletecart", "POST"),
        API_MY_CART("V1/listcart", "GET"),
        API_CUTTYPE_LIST("V1/cuttypes", "GET"),
        API_PRODUCT_LIST_BY_CATEGORY("V1/productlistbycatid", "GET"),
        API_PRODUCT_DESCRIPTION("V1/productdata","GET"),
        API_USER_REGISTRATION("V1/registration", "POST"),
        API_ADDTOCART("V1/addtocart", "POST"),
        API_UPDATECART("V1/updatecart", "POST"),
        API_USERLOCATION("V1/userlocation", "POST"),
        API_LOGIN("V1/login", "POST"),
        API_SOCIAL_LOGIN("V1/sociallogin", "POST"),
        API_DASHBOARD("V1/dashboard", "GET");

        private final String api_name;
        private final String method;

        private APIS(final String text, String method) {
            this.api_name = text;
            this.method = method;
        }

        @Override
        public String toString() {
            return api_name;
        }
    }

    //Forgot Password
    public StringBuilder doForgotpassword(final String resetdata,final String type) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("resetdata", resetdata);
        params.put("type", type);
        return sendRequest(params, APIS.API_FORGOT_PASSWORD,"","");
    }

    //Resend OTP
    public StringBuilder doResendOTP(final String customerId,final String resetdata,final String type) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("customerId", customerId);
        params.put("resetdata", resetdata);
        params.put("type", type);
        return sendRequest(params, APIS.API_RESEND_OTP,"","");
    }

    //OTP Reset Password
    public StringBuilder doResetpassword(final String email,final String customerid,final String otp,final String newpass) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("email", email);
        params.put("customerid", customerid);
        params.put("otp", otp);
        params.put("newpass", newpass);
        return sendRequest(params, APIS.API_RESETPASSWORD,"","");
    }

    //My Referrals
    public StringBuilder doMyReferrals(final String cid) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("cid", cid);
        return sendRequest(params, APIS.API_MY_REFERRALS,"","");
    }

    //Referral invite
    public StringBuilder doReferralInvite(final String cid,final String name,final String email,final String message) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("cid", cid);
        params.put("name", name);
        params.put("email", email);
        params.put("message", message);
        return sendRequest(params, APIS.API_REFERRAL_INVITE,"","");
    }


    //Order Details
    public StringBuilder doOrderDetails(final String id) {
        HashMap<String, String> params = new HashMap<String, String>();
        //   params.put("order_id", order_id);
        return sendRequest(params, APIS.API_ORDER_DETAILS,"/"+id,"");
    }

    //Go To Payment
    public StringBuilder doGoToPayment(final String id) {
        HashMap<String, String> params = new HashMap<String, String>();

        return sendRequest(params, APIS.API_GOTO_PAYMENT,"/"+id,"");
    }

    //Place Order
    public StringBuilder doPlaceOrder(final String quote_id) {
        HashMap<String, String> params = new HashMap<String, String>();
        //   params.put("order_id", order_id);
        return sendRequest(params, APIS.API_PLACEORDER,"/"+quote_id+"/order","");
    }

    //Payment Methods
    public StringBuilder doPaymentMethod(final String customer_id,final String paymentmethod) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("cid", customer_id);
        params.put("paymentmethod", paymentmethod);
        return sendRequest(params, APIS.API_PAYMENT_METHOD,"","");
    }

    //Shipping Methods
    public StringBuilder doShippingMethod(final String address_id,final String cart_id) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("addressId", address_id);
        params.put("cart_id", cart_id);
        return sendRequest(params, APIS.API_SHIPPING_METHOD,"/"+cart_id+"/estimate-shipping-methods-by-address-id","");
    }

    //Shipping Information
    public StringBuilder doShippingInformation(final String address,final String cart_id) {
        HashMap<String, String> params = new HashMap<String, String>();
        // params.put("addressInformation", address);
        params.put("cart_id", cart_id);
        return sendRequest(params, APIS.API_SHIPPING_INFORMATION,"/"+cart_id+"/shipping-information",address);
    }

    //My Reward Points
    public StringBuilder doMyRewardsList(final String customer_id) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("cid", customer_id);
        return sendRequest(params, APIS.API_MY_REWARDS,"","");
    }

    //My Orders
    public StringBuilder doMyOrderList(final String customer_id) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("id", customer_id);
        params.put("mode", "1");
        return sendRequest(params, APIS.API_MY_ORDERLIST,"","");
    }

    //Apply Discount Coupons
    public StringBuilder doApplyCoupon(final String customer_id,final String couponcode) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("cid", customer_id);
        params.put("couponcode", couponcode);
        return sendRequest(params, APIS.API_APPLY_COUPON,"","");
    }

    //Apply Reward Points
    public StringBuilder doApplyRewardPoints(final String customer_id,final String points) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("cid", customer_id);
        params.put("points", points);
        return sendRequest(params, APIS.API_APPLY_REWARDS,"","");
    }

    //Edit Profile
    public StringBuilder doEditProfile(final String customerid,final String firstname, final String lastname, final String email,
                                       final String mobile,final String resetpassword,final String oldpassword,
                                       final String newpassword) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("cid", customerid);
        params.put("firstname", firstname);
        params.put("lastname", lastname);
        params.put("email", email);
        params.put("mob", mobile);
        params.put("resetpassword", resetpassword);
        params.put("oldpassword", oldpassword);
        params.put("newpassword", newpassword);
        return sendRequest(params, APIS.API_EDIT_PROFILE, "","");
    }


    // Add Address
    public StringBuilder doAddAddress(final String customerid,final String firstname, final String lastname, final String company,
                                      final String street1,final String street2,final String city,final String postcode,
                                      final String phone,final String country,final String state,final String defaultship,final String defaultbill,
                                      final String mode,final String addressid) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("cid", customerid);
        params.put("fname", firstname);
        params.put("lastname", lastname);
        params.put("company", company);
        params.put("street1", street1);
        params.put("street2", street2);
        params.put("city", city);
        params.put("postcode", postcode);
        params.put("telephone", phone);
        params.put("country", country);
        params.put("state", state);
        params.put("defaultship", defaultship);
        params.put("defaultbill", defaultbill);
        params.put("mode", mode);
        params.put("addressid", addressid);
        return sendRequest(params, APIS.API_ADD_ADDRESS, "","");
    }

    public StringBuilder doMyProfile(final String customer_id) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("customer_id", customer_id);
        return sendRequest(params, APIS.API_MY_PROFILE,"/"+customer_id,"");
    }

    //Delete Address
    public StringBuilder doDeleteAddress(final String address_id) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("addressid", address_id);
        return sendRequest(params, APIS.API_DELETE_ADDRESS,"/"+address_id,"");
    }

    public StringBuilder doDeleteCart(final String customer_id,final String item_id) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("cid", customer_id);
        params.put("itemid", item_id);
        return sendRequest(params, APIS.API_DELETE_CART,"","");
    }

    //Address List
    public StringBuilder doMyAddressList(final String customer_id) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("cid", customer_id);
        return sendRequest(params, APIS.API_MY_ADDRESS,"/"+customer_id,"");
    }

    //Cart List
    public StringBuilder doMyCartList(final String customer_id) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("customer_id", customer_id);
        return sendRequest(params, APIS.API_MY_CART,"/"+customer_id,"");
    }

    public StringBuilder doCutTypesList(final String product_id) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("product_id", product_id);
        return sendRequest(params, APIS.API_CUTTYPE_LIST,"/"+product_id,"");
    }

    //Add To Cart
    public StringBuilder doAddToCart(final String customerid, final String productid, final String cuttype,final String qty) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("customerid", customerid);
        params.put("productid", productid);
        params.put("cuttype", cuttype);
        params.put("qty", qty);
        return sendRequest(params, APIS.API_ADDTOCART, "","");
    }

    //Update Cart
    public StringBuilder doUpdateCart(final String customerid, final String itemid, final String cuttype,final String qty) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("cid", customerid);
        params.put("itemid", itemid);
        params.put("cuttype", cuttype);
        params.put("qty", qty);
        return sendRequest(params, APIS.API_UPDATECART, "","");
    }


    public StringBuilder doProductDescription(final String product_id) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("product_id", product_id);
        return sendRequest(params, APIS.API_PRODUCT_DESCRIPTION,"/"+product_id,"");
    }
    public StringBuilder doProductList(final String category_id) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("category_id", category_id);
        return sendRequest(params, APIS.API_PRODUCT_LIST_BY_CATEGORY,"/"+category_id+"/page/1/del/23","");
    }
    public StringBuilder doLocation(final String latitude,final String longitude,String customer_id,String location) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("lat", latitude);
        params.put("long", longitude);
        params.put("customer_id", customer_id);
        params.put("location", location);
        return sendRequest(params, APIS.API_USERLOCATION, "","");
    }
    public StringBuilder doDashboard(final String delivery_centerId) {
        HashMap<String, String> params = new HashMap<String, String>();
        return sendRequest(params, APIS.API_DASHBOARD,"/"+delivery_centerId,"");
    }

    //Social Login
    public StringBuilder doSocialLogin(final String email,final String mobile,final String first_name,
                                       final String last_name,final String userid,final String loginmode) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("email", email);
        params.put("mobile", mobile);
        params.put("first_name", first_name);
        params.put("last_name", last_name);
        params.put("userid", userid);
        params.put("loginmode", loginmode);
        return sendRequest(params, APIS.API_SOCIAL_LOGIN, "","");
    }

    //Login
    public StringBuilder doLogin(final String username,final String password) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("logindata", username);
        params.put("pass", password);
        return sendRequest(params, APIS.API_LOGIN, "","");
    }
    // Registration
    public StringBuilder doUserRegistration(final String firstname, final String lastname, final String mobile,final String email,final String password) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("firstname", firstname);
        params.put("lastname", lastname);
        params.put("mobile", mobile);
        params.put("email", email);
        params.put("pass", password);
        return sendRequest(params, APIS.API_USER_REGISTRATION, "","");
    }

    public StringBuilder sendRequest(HashMap<String, String> params, APIS api, String URL, String ShippingParams) {
        // Create a new HttpClient and Post Header
        final HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 15000);
        // 1. create HttpClient
        HttpClient httpclient = new DefaultHttpClient(httpParams);
        try {
            // Add your data
            if (api.method.equals("GET")) {
                try {
                    HttpGet httpget = new HttpGet(SERVER_URL + api.toString()+URL.replaceAll(" ","%20").trim());
                    //httpget.getParams().setParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, Boolean.FALSE);
                    httpget.addHeader("Authorization","Bearer iuqvlwox8dthqva2a376meum68qjimda");
                    Log.d("111111111",SERVER_URL+api.toString()+URL.replaceAll(" ","%20").trim());
                    HttpResponse response = httpclient.execute(httpget);
                    return readStream(response.getEntity().getContent());
                } catch (HttpHostConnectException e) {
                    e.printStackTrace();
                }
            }
            else if (api.method.equals("POST")){
                // Add your data

                try {
                    // 2. make POST request to the given URL
                    HttpPost httppost = new HttpPost(SERVER_URL + api.toString()+URL.replaceAll(" ","%20").trim());
                    httppost.addHeader("Authorization","Bearer iuqvlwox8dthqva2a376meum68qjimda");
                    httppost.addHeader("Content-type", "application/json; charset=utf-8");
                    httppost.getParams().setParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, Boolean.FALSE);
                    String json = "";
                    // 3. build jsonObject
                    JSONObject jsonObject = new JSONObject();
                    //List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                    for (String key : params.keySet()) {
                        //nameValuePairs.add(new BasicNameValuePair(key, params.get(key)));
                        jsonObject.accumulate(key,params.get(key));
                    }
                    if(URL.contains("shipping-information")){
                        json=ShippingParams;
                        Log.d("11111111json",json);
                    }
                    else {
                        // 4. convert JSONObject to JSON to String
                        json = jsonObject.toString();
                        Log.d("11111111json",json);
                    }
                    // 5. set json to StringEntity
                    StringEntity se = new StringEntity(json);

                    // 6. set httpPost Entity
                    httppost.setEntity(se);
                    //httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    Log.d("111111111",SERVER_URL+api.toString()+URL.replaceAll(" ","%20").trim());
                    HttpResponse response = httpclient.execute(httppost);
                    return readStream(response.getEntity().getContent());
                } catch (HttpHostConnectException e) {
                    e.printStackTrace();
                }

            }
            else if (api.method.equals("PUT")){
                // Add your data
                try {
                    // 2. make POST request to the given URL
                    HttpPut httpput = new HttpPut(SERVER_URL + api.toString()+URL.replaceAll(" ","%20").trim());
                    httpput.addHeader("Authorization","Bearer iuqvlwox8dthqva2a376meum68qjimda");
                    httpput.addHeader("Content-type", "application/json; charset=utf-8");
                    httpput.getParams().setParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, Boolean.FALSE);
                    String json = "";
                    // 3. build jsonObject
                    JSONObject jsonObject = new JSONObject();
                    //List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                    for (String key : params.keySet()) {
                        //nameValuePairs.add(new BasicNameValuePair(key, params.get(key)));
                        jsonObject.accumulate(key,params.get(key));
                    }
                    if(URL.contains("shipping-information")){
                        json=ShippingParams;
                        Log.d("11111111json",json);
                    }
                    else {
                        // 4. convert JSONObject to JSON to String
                        json = jsonObject.toString();
                        Log.d("11111111json",json);
                    }
                    // 5. set json to StringEntity
                    StringEntity se = new StringEntity(json);

                    // 6. set httpPost Entity
                    httpput.setEntity(se);
                    //httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    Log.d("111111111",SERVER_URL+api.toString()+URL.replaceAll(" ","%20").trim());
                    HttpResponse response = httpclient.execute(httpput);
                    return readStream(response.getEntity().getContent());
                } catch (HttpHostConnectException e) {
                    e.printStackTrace();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public StringBuilder readStream(InputStream in) throws Exception {
        StringBuilder stringBuilder = null;

        if (in == null) {
            return null;
        }

        BufferedReader inReader = new BufferedReader(new InputStreamReader(in));
        stringBuilder = new StringBuilder();
        String line = null;

        while ((line = inReader.readLine()) != null) {
            stringBuilder.append(line + "\n");
        }
        in.close();
        return stringBuilder;
    }
}



