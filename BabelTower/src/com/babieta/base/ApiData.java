package com.babieta.base;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class ApiData {
	private static String ret = "";

	public static String httpGet(String url) {
		System.out.println(url);
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet();
		HttpResponse response;
		StringBuffer sBuffer = new StringBuffer();
		try {
			request.setURI(new URI(url));
			response = client.execute(request);
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response
					.getEntity().getContent()));
			String line = "";
			while ((line = bufferedReader.readLine()) != null) {
				sBuffer.append(line);
			}
			bufferedReader.close();
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return "";
		}
		System.out.println(sBuffer.toString());
		return sBuffer.toString();
	}

}
