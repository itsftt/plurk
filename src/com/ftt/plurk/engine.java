package com.ftt.plurk;

import android.net.Uri;


import android.database.sqlite.SQLiteException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import java.util.Locale;
import android.util.Log;

import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.BufferedInputStream;
import java.net.URL;
import java.net.URLConnection;
import java.io.IOException;
import java.net.MalformedURLException;

public class engine{
	public static SQLiteDatabase db;
	private static Context context;
		private static String API_KEY = "";
		private static String username = "";
		private static String password = "";
	private static HttpClient httpclient;
	public static DateFormat formatter,formattero;
	public static HashMap<Long, Bitmap> avatarPool;
	public static String apiUrl(String uri) {
		return "http://www.plurk.com/API" + uri;
	}
	public static void drop_table(){
		Log.d("DPLURK","DROP TABLE plurks");
		Log.d("DPLURK","DROP TABLE unread");
		db.execSQL("DROP TABLE plurks");
		db.execSQL("DROP TABLE unread");
	}
	public static boolean getAvatar(final Long plurk_id, final String url){
		if(avatarPool.containsKey(plurk_id))
			return false;
			//return avatarPool.get(plurk_id);
		//Log.d("DPLURK","plurk_id:"+plurk_id+" url:"+url);
		avatarPool.put(plurk_id, null);
		Bitmap avatar = null;
		BufferedInputStream bis = null;
		try {
			URL avatarUrl = new URL(url);
			URLConnection conn = avatarUrl.openConnection();
			conn.connect();
			bis = new BufferedInputStream(conn.getInputStream(), 8192);
			avatar = BitmapFactory.decodeStream(bis);

			//Log.d("DPLURK","plurk_id:"+plurk_id+" url:"+url+" done!");
			avatarPool.put(plurk_id, avatar);
			return true;

		} catch (MalformedURLException e) {
			Log.e("DPLURK", "avatar url error: " + url);
		} catch (IOException e) {
			Log.e("DPLURK", "fetching avatar fail! ("+url+")");
		} finally {
			if (bis != null) {
				try {
					bis.close();
				} catch (IOException e) {
				}
			}
		}
		avatarPool.remove(plurk_id);
		return false;
		//return avatar;
	}
	/*public boolean getAvatar(final Long plurk_id, final String url){
		if(avatarPool.containsKey(plurk_id))
			return false;
			//return avatarPool.get(plurk_id);
		Log.d("DPLURK","plurk_id:"+plurk_id+" url:"+url);
		avatarPool.put(plurk_id, null);
		Bitmap avatar = null;
		BufferedInputStream bis = null;
		try {
			URL avatarUrl = new URL(url);
			URLConnection conn = avatarUrl.openConnection();
			conn.connect();
			bis = new BufferedInputStream(conn.getInputStream(), 8192);
			avatar = BitmapFactory.decodeStream(bis);

			Log.d("DPLURK","plurk_id:"+plurk_id+" url:"+url+" done!");
			avatarPool.put(plurk_id, avatar);
			return true;

		} catch (MalformedURLException e) {
			Log.e("DPLURK", "avatar url error: " + url);
		} catch (IOException e) {
			Log.e("DPLURK", "fetching avatar fail! ("+url+")");
		} finally {
			if (bis != null) {
				try {
					bis.close();
				} catch (IOException e) {
				}
			}
		}
		avatarPool.remove(plurk_id);
		return false;
		//return avatar;
	}*/

	public static void login(){
		httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(apiUrl("/Users/login?"+
							"api_key=" + API_KEY + "&" +
							"username=" + username + "&" +
							"password=" + password ));
		ResponseHandler responseHandler = new BasicResponseHandler();
		try{
		      httpclient.execute(httpget, responseHandler);
			Log.d("DPLURK","LOGIN SUCCEED");
		}catch (Exception e){
			Log.d("DPLURK","LOGIN FAILED");
			Log.d("DPLURK",e.getMessage());
			Log.d("DPLURK",e.toString());
		}
	}
	public static void init(Context c) throws Exception{
		context = c;
		String _dbpath = context.getDatabasePath("plurks.db").toString();
		//String _dbpath = "/sdcard/plurks.db";
		avatarPool = new HashMap<Long, Bitmap>();
		Log.d("DPLURK",_dbpath);
		try{
			db = SQLiteDatabase.openDatabase(_dbpath, null, SQLiteDatabase.CREATE_IF_NECESSARY);
			//db = SQLiteDatabase.openOrCreateDatabase(_dbpath, null);
			Log.d("DPLURK","db selected");
			db.setLocale(Locale.TRADITIONAL_CHINESE);
			Log.d("DPLURK","o_O");
		}
		catch (Exception e){
			Log.d("DPLURK",e.getMessage());
			Log.d("DPLURK",e.toString());
		}
		try{
			db.rawQuery("SELECT * FROM plurks LIMIT 1", null);
		}
		catch (Exception e){
			Log.d("DPLURK","creating table plurks");
			db.execSQL("CREATE TABLE plurks (plurk_id INTEGER PRIMARY KEY,owner_id INTEGER,content TEXT,response_count INTEGER,posted TEXT,avatar TEXT);");
			Log.d("DPLURK","create table done");
		}
		try{
			db.rawQuery("SELECT * FROM responses LIMIT 1", null);
		}
		catch (Exception e){
			Log.d("DPLURK","creating table responses");
			db.execSQL("CREATE TABLE responses (plurk_id INTEGER PRIMARY KEY,json TEXT);");
			Log.d("DPLURK","create table done");
		} 
		try{
			db.rawQuery("SELECT * FROM unread LIMIT 1", null);
		}
		catch (Exception e){
			Log.d("DPLURK","creating table unread");
			db.execSQL("CREATE TABLE unread (plurk_id INTEGER PRIMARY KEY,owner_id INTEGER,content TEXT,response_count INTEGER,posted TEXT,avatar TEXT);");
			Log.d("DPLURK","create table done");
		} 
	}
	public static void plurkAdd(String content,String qualifier){
		Log.d("DPLURK","plurkAdd()");
		HttpPost post = new HttpPost(apiUrl("/Timeline/plurkAdd"));
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("api_key",API_KEY));
		params.add(new BasicNameValuePair("content",content));
		params.add(new BasicNameValuePair("qualifier",qualifier));
		ResponseHandler responseHandler = new BasicResponseHandler();
		try{
			post.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
			httpclient.execute(post, responseHandler);
		}
		catch(Exception e){
			Log.d("DPLURK","plurkAdd failed");
		}
	}
	public static void responseAdd(String content,String qualifier,Long plurk_id){
		Log.d("DPLURK","responseAdd()");
		HttpPost post = new HttpPost(apiUrl("/Responses/responseAdd"));
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("api_key",API_KEY));
		params.add(new BasicNameValuePair("plurk_id",plurk_id.toString()));
		params.add(new BasicNameValuePair("content",content));
		params.add(new BasicNameValuePair("qualifier",qualifier));
		ResponseHandler responseHandler = new BasicResponseHandler();
		try{
			post.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
			httpclient.execute(post, responseHandler);
		}
		catch(Exception e){
			Log.d("DPLURK","responseAdd failed");
		}
	}
	public static boolean getPlurks(String type){
		Log.d("DPLURK","start getPlurks()");
		boolean empty=false;
		String _posted;
		int _plurk_id=-1;
		Cursor cur;
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		formatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'",Locale.US);
		formattero = new SimpleDateFormat("yyyy-M-d'T'HH:mm:ss");
		params.add(new BasicNameValuePair("api_key",API_KEY));
		if(type.equals("old")){
			cur = db.rawQuery("SELECT posted FROM plurks ORDER BY plurk_id LIMIT 1",null);
			if(cur.getCount()==0)
				empty = true;
			else{
				cur.moveToNext();
				_posted = cur.getString(0);
				Log.d("DPLURK","_posted:"+_posted);
				try{
					Date date = (Date)formatter.parse(_posted);
					params.add(new BasicNameValuePair("offset",formattero.format(date)));
				}
				catch(Exception e){
					Log.d("DPLURK","date parse error");
					Log.d("DPLURK",e.getMessage());
					Log.d("DPLURK",e.toString());
				
				}
			}
		}
		else if(type.equals("new")){
			cur = db.rawQuery("SELECT plurk_id FROM plurks ORDER BY plurk_id DESC LIMIT 1",null);
			if(cur.getCount()==0)
				empty = true;
			else{
				cur.moveToNext();
				_plurk_id = cur.getInt(0);
			}
		}
		HttpPost post = new HttpPost(apiUrl("/Timeline/getPlurks"));
		ResponseHandler responseHandler = new BasicResponseHandler();
		String res=""; 
		try{
		      post.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
		      res = httpclient.execute(post, responseHandler).toString();
		}
		catch (Exception e){

			Log.d("DPLURK","sql insert exception");
			Log.d("DPLURK",e.getMessage());
			Log.d("DPLURK",e.toString());
			e.printStackTrace();

			Log.d("DPLURK","http request failed");
			return false;
		}
		int plurk_id,owner_id,response_count;
		String content,posted,query,avatar;
		posted = "";
		try{
			while(true==true){
				JSONObject json = new JSONObject(res);
				JSONArray plurks = json.getJSONArray("plurks");
				JSONObject users = json.getJSONObject("plurk_users");
				for(int x=0;x<plurks.length();x++){
					plurk_id = plurks.getJSONObject(x).getInt("plurk_id");
					owner_id = plurks.getJSONObject(x).getInt("owner_id");
					content = plurks.getJSONObject(x).getString("content");
					response_count = plurks.getJSONObject(x).getInt("response_count");
					posted = plurks.getJSONObject(x).getString("posted");
					if(type.equals("new")&&empty==false&&plurk_id==_plurk_id){
						empty = true;
						break;
					}
					if(users.getJSONObject(""+owner_id).getInt("has_profile_image")==0)
						avatar = "http://www.plurk.com/static/default_small.gif";
					else if(users.getJSONObject(""+owner_id).isNull("avatar")||users.getJSONObject(""+owner_id).getInt("avatar")==0)
						avatar = "http://avatars.plurk.com/"+owner_id+"-medium.gif";
					else
						avatar = "http://avatars.plurk.com/"+owner_id+"-medium"+users.getJSONObject(""+owner_id).getInt("avatar")+".gif";
					/*try{
						avatar_id = json.getJSONObject("plurk_users").getJSONObject(""+owner_id).getInt("avatar");
						Log.d("DPLURK",""+avatar_id);
						if(avatar_id>0)
							avatar = "http://avatars.plurk.com/"+owner_id+"-medium"+avatar_id+".gif";
						else
							avatar = "http://avatars.plurk.com/"+owner_id+"-medium.gif";
					}
					catch(Exception ee){
						try{
							avatar = "http://avatars.plurk.com/"+owner_id+"-medium.gif";
						}
						catch(Exception eee){
							avatar = "http://www.plurk.com/static/default_medium.gif"; 
						}
						
					}*/
					query = "INSERT INTO plurks(plurk_id,owner_id,content,response_count,posted,avatar) VALUES ("+plurk_id+","+owner_id+",'"+content.replace("'","&apos;").replace("%","%25")+"',"+response_count+",'"+posted+"','"+avatar+"')";
					//Log.d("DPLURK",query);
					db.execSQL(query);
					//Log.d("DPLURK","insert done");
				}
				if(empty==true||type.equals("old")){
					break;
				}
				post = new HttpPost(apiUrl("/Timeline/getPlurks"));
				params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("api_key",API_KEY));
				Log.d("DPLURK","posted:"+posted);
				Date date = (Date)formatter.parse(posted);
				params.add(new BasicNameValuePair("offset",formattero.format(date)));
				post.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
				res = httpclient.execute(post, responseHandler).toString();
			}
		}
		catch (SQLiteException e){
			Log.d("DPLURK","sql insert exception");
			Log.d("DPLURK",e.getMessage());
			Log.d("DPLURK",e.toString());
			e.printStackTrace();
			return false;
		}
		catch (ParseException e){
			Log.d("DPLURK","date parse error");
			Log.d("DPLURK",e.getMessage());
			Log.d("DPLURK",e.toString());
			e.printStackTrace();
			return false;
		}
		catch (Exception e){
			Log.d("DPLURK","json to db error");
			Log.d("DPLURK",e.getMessage());
			Log.d("DPLURK",e.toString());
			return false;
		}
		return true; 
	}
	public static boolean getUnreadPlurks(){
		Log.d("DPLURK","start getUnreadPlurks()");
		HttpPost post = new HttpPost(apiUrl("/Timeline/getUnreadPlurks"));
		boolean empty=false;
		String _posted;
		int _plurk_id=-1;
		Cursor cur;
		JSONObject json;
		JSONArray plurks;
		JSONObject users;
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("api_key",API_KEY));
		ResponseHandler responseHandler = new BasicResponseHandler();
		String res=""; 
		int plurk_id,owner_id,response_count;
		String content,posted,query,avatar;
		posted = "";
		Log.d("DPLURK","1");
		try{
			post.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
			Log.d("DPLURK","2");
			res = httpclient.execute(post, responseHandler).toString();
			Log.d("DPLURK","3");
			json = new JSONObject(res);
			plurks = json.getJSONArray("plurks");
			users = json.getJSONObject("plurk_users");
			//Log.d("DPLURK","DROP TABLE unread");
			Log.d("DPLURK","4");
			db.execSQL("DELETE FROM unread");
			for(int x=0;x<plurks.length();x++){
				plurk_id = plurks.getJSONObject(x).getInt("plurk_id");
				owner_id = plurks.getJSONObject(x).getInt("owner_id");
				content = plurks.getJSONObject(x).getString("content");
				response_count = plurks.getJSONObject(x).getInt("response_count");
				posted = plurks.getJSONObject(x).getString("posted");
				if(users.getJSONObject(""+owner_id).getInt("has_profile_image")==0)
					avatar = "http://www.plurk.com/static/default_small.gif";
				else if(users.getJSONObject(""+owner_id).isNull("avatar")||users.getJSONObject(""+owner_id).getInt("avatar")==0)
					avatar = "http://avatars.plurk.com/"+owner_id+"-medium.gif";
				else
					avatar = "http://avatars.plurk.com/"+owner_id+"-medium"+users.getJSONObject(""+owner_id).getInt("avatar")+".gif";

				query = "INSERT INTO unread(plurk_id,owner_id,content,response_count,posted,avatar) VALUES ("+plurk_id+","+owner_id+",'"+content.replace("'","&apos;").replace("%","%25")+"',"+response_count+",'"+posted+"','"+avatar+"')";
				//Log.d("DPLURK",query);
				db.execSQL(query);
				Log.d("DPLURK","insert done");
			}
		}
		catch (SQLiteException e){
			Log.d("DPLURK","sql insert exception");
			Log.d("DPLURK",e.getMessage());
			Log.d("DPLURK",e.toString());
			e.printStackTrace();
			return false;
		}
		/*catch (ParseException e){
			Log.d("DPLURK","date parse error");
			Log.d("DPLURK",e.getMessage());
			Log.d("DPLURK",e.toString());
			e.printStackTrace();
			return false;
		}*/
		catch (Exception e){
			Log.d("DPLURK","json to db error qq");
			Log.d("DPLURK",e.getMessage());
			Log.d("DPLURK",e.toString());
			return false;
		}
		return true;
	}
	public static void getPlurkResponses(Long plurk_id,int offset){
		Log.d("DPLURK","getPlurkResponses() plurk_id:"+plurk_id);
		HttpPost post = new HttpPost(apiUrl("/Responses/get"));
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("api_key",API_KEY));
		params.add(new BasicNameValuePair("plurk_id",plurk_id.toString()));
		ResponseHandler responseHandler = new BasicResponseHandler();
		String res,query; 
		try{
			post.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
			res = httpclient.execute(post, responseHandler).toString();
			Cursor cur = db.rawQuery("SELECT json FROM responses WHERE plurk_id="+plurk_id,null);
			if(cur.getCount()==0)
				query = "INSERT INTO responses(plurk_id,json) VALUES ("+plurk_id+",'"+res.replace("'","&apos;").replace("%","%25")+"')";
			else
				query = "UPDATE responses SET json='"+res.replace("'","&apos;").replace("%","%25")+"' WHERE plurk_id="+plurk_id;
			Log.d("DPLURK",query);
			db.execSQL(query);
			Log.d("DPLURK","insert done");
		}
		catch(Exception e){
			Log.d("DPLURK","get responses failed");
			Log.d("DPLURK",e.getMessage());
			Log.d("DPLURK",e.toString());
		}
	}
	public static boolean leave(){
		db.close();
		return true;
	}
}
