package com.ftt.plurk;

import android.os.Message;
import android.os.Handler;

import android.webkit.WebView;
import android.widget.TextView;
import android.view.LayoutInflater;

import org.json.JSONArray;
import org.json.JSONObject;


import android.util.Log;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class resAdapter extends BaseAdapter{
	private ArrayList<View> mPlurks;
	private Context mContext;
	private SQLiteDatabase db;
	private LayoutInflater inflater;
	private Long plurk_id;

	private static class ViewHolder{
		WebView avatar;
		WebView content;
		TextView responses;
		TextView time;
	}
	public resAdapter(Context c,final Long pid){
		mContext = c;
		plurk_id = pid;
		inflater = LayoutInflater.from(mContext);
		mPlurks = new ArrayList<View>();
		db = engine.db;
		/*try{
			setResponses(plurk_id);
		}
		catch(Exception e){
			Log.d("DPLURK","setResponse failed");
			Log.d("DPLURK",e.getMessage());
			Log.d("DPLURK",e.toString());
		
		}
		Thread thread = new Thread(new Runnable(){
			public void run(){
		engine.getPlurkResponses(plurk_id,0);
		db = engine.db;
		setResponses(plurk_id);		
			}
		});
		thread.start();*/

		final Handler h = new Handler() {
			public void handleMessage(Message msg) {
				if (msg.what == 0x1234){
					//clearresponses();
					setResponses(plurk_id);
				}
			}
		};
		setResponses(plurk_id);		
		/*Thread thread = new Thread(new Runnable(){
			public void run(){
				Log.d("DPLURK","getplurkresponses");
				engine.getPlurkResponses(plurk_id,0);
				Log.d("DPLURK","setResponses");
				Message m = new Message();
				m.what = 0x1234;
				h.sendMessage(m);

			}
		});
		thread.start();*/
	}
	public void refresh(){
		final Handler h = new Handler() {
			public void handleMessage(Message msg) {
				if (msg.what == 0x1234){
					//clearresponses();
					setResponses(plurk_id);
					Log.d("DPLURK","go remove()");
					plurk.unreadAdapter.remove(plurk_id);
				}
			}
		};
		Thread thread = new Thread(new Runnable(){
			public void run(){
				Log.d("DPLURK","getplurkresponses");
				engine.getPlurkResponses(plurk_id,0);
				Log.d("DPLURK","setResponses");
				Message m = new Message();
				m.what = 0x1234;
				h.sendMessage(m);

			}
		});
		thread.start();
	}

	public int getCount(){
		return mPlurks.size();
	}

	public Object getItem(int position){
		//return position;
		return mPlurks.get(position);
	}

	public long getItemId(int position){
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent){
		Log.d("DPLURK",""+position);
		return mPlurks.get(position);
	}

	public View getResponseView(String content,String time,int responses,String avatar){
		View view = inflater.inflate(R.layout.rplurk_list,null);

		ViewHolder holder = new ViewHolder();
		holder.avatar    = (WebView) view.findViewById(R.id.avatar);
		holder.content   = (WebView) view.findViewById(R.id.content);
		holder.time      = (TextView) view.findViewById(R.id.time);
		
		holder.avatar.loadUrl(avatar);
		holder.time.setText(time);
		holder.content.loadData("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />"+content, "text/html", "utf-8");
		view.setFocusable(true);

		return view;
		
	}
	public void clearResponses(){
		Log.d("DPLURK","clearResponses()");
		try{
			mPlurks.clear();
			notifyDataSetChanged();
		}
		catch(Exception e){
			Log.d("DPLURK",e.getMessage());
			Log.d("DPLURK",e.toString());
		}
	}
	public void addResponses(String content,String time,int responses,String avatar,Long plurk_id){
		Log.d("DPLURK","addResponses()");
		try{
			//mPlurks.add(plurkAdapter.getPlurkView(content,time,responses,avatar,plurk_id,mContext));
			mPlurks.add(getResponseView(content,time,responses,avatar));
			notifyDataSetChanged();
		}
		catch(Exception e){
			Log.d("DPLURK",e.getMessage());
			Log.d("DPLURK",e.toString());
		}
	}	
	public void setResponses(Long plurk_id){
		try{
			Log.d("DPLURK","db opened");
			Cursor res = db.rawQuery("SELECT json FROM responses WHERE plurk_id="+plurk_id,null);
			Log.d("DPLURK","SELECTED");
			db.execSQL("DELETE FROM unread WHERE plurk_id="+plurk_id);
			res.moveToNext();
			JSONObject json = new JSONObject(res.getString(0));
			JSONArray responses = json.getJSONArray("responses");
			JSONObject friends = json.getJSONObject("friends");
			JSONObject itr;
			Log.d("DPLURK","START addResponses");
			String avatar,content,posted;
			int owner_id,avatar_id;
			for(int x=mPlurks.size();x<responses.length();x++){
				itr = responses.getJSONObject(x);
				owner_id = itr.getInt("user_id");
				content = itr.getString("content");
				posted = itr.getString("posted");
				if(friends.getJSONObject(""+owner_id).getInt("has_profile_image")==0)
					avatar = "http://www.plurk.com/static/default_small.gif";
				else if(friends.getJSONObject(""+owner_id).isNull("avatar")||friends.getJSONObject(""+owner_id).getInt("avatar")==0)
						avatar = "http://avatars.plurk.com/"+owner_id+"-medium.gif";
				else{
					avatar_id = friends.getJSONObject(""+owner_id).getInt("avatar");
					avatar = "http://avatars.plurk.com/"+owner_id+"-medium"+avatar_id+".gif";
				}
				Log.d("DPLURK",content);
				addResponses(content,posted,0,avatar,0l);
			}
			Log.d("DPLURK","done!");
		}
		catch(Exception e){
			Log.d("DPLURK","READ FROM DB FAILED");
			Log.d("DPLURK",e.getMessage());
			Log.d("DPLURK",e.toString());
		}
	}
}
