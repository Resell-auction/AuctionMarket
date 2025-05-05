package com.example.auctionmarket.domain.coupon.event;

import java.net.HttpURLConnection;
import java.net.URL;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class CouponExpireLambda implements RequestHandler<Object, String> {

	private static final String API_URL = "https://d7ec-1-239-92-30.ngrok-free.app/v1/coupons/expire";
	//   private static final OkHttpClient client = new OkHttpClient();

	@Override
	public String handleRequest(Object input, Context context) {
		try {
			URL url = new URL(API_URL);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.connect();

			int responseCode = conn.getResponseCode();
			return "Response code: " + responseCode;
		} catch (Exception e) {
			return "Exception: " + e.getMessage();
		}
	}
}
