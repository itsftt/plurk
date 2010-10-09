package com.ftt.plurk;

import java.util.HashSet;
import java.util.Set;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.BufferedInputStream;
import java.net.URL;
import java.net.URLConnection;
import java.io.IOException;
import java.net.MalformedURLException;


import android.util.Log;
import android.os.Message;
import android.os.Handler;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.ImageView;
import android.net.Uri;
import java.util.ArrayList;
import android.view.LayoutInflater;
import android.webkit.WebView;
import java.util.HashMap;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import com.ftt.plurk.R;

import android.content.Intent;

public class plurkAdapter extends BaseAdapter{
	private static class ViewHolder{
		WebView avatar;
		WebView content;
		TextView responses;
		TextView time;
	}
	//private ArrayList<View> mPlurks;
	private ArrayList<Long> mPlurk_id;
	private ArrayList<String> mContent;
	private ArrayList<String> mAvatar;
	private ArrayList<Integer> mResponses;
	private ArrayList<String> mTime;
	private Context mContext;
	private String type;
	private static LayoutInflater inflater;
	public static HashMap<Long, Bitmap> avatarPool;
	public static SQLiteDatabase db;
	public Set<Long> ViewOnScreen;
	private final Handler h = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 0x1234){
				//clearresponses();
				Log.d("DPLURK","SetChanged Handler");
				notifyDataSetChanged();
			}
		}
	};
	public plurkAdapter(Context c,String t){
		mContext = c;
		type = t;
		avatarPool = plurks2db.avatarPool;
		db = plurks2db.db;
		ViewOnScreen = new HashSet<Long>();
		if(inflater==null)	inflater = LayoutInflater.from(mContext);
		//mPlurks = new ArrayList<View>();
		mPlurk_id = new ArrayList<Long>();
		mContent = new ArrayList<String>();
		mAvatar = new ArrayList<String>();
		mResponses = new ArrayList<Integer>();
		mTime = new ArrayList<String>();
	}
	public boolean getAvatar(final Long plurk_id, final String url){
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

			avatarPool.put(plurk_id, avatar);

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
		Log.d("DPLURK","plurk_id:"+plurk_id+" url:"+url+" done!");
		return true;
		//return avatar;
	}
	public void remove(Long plurk_id){
		Log.d("DPLURK","remove() plurk_id:"+plurk_id);
		Log.d("DPLURK","length:"+mPlurk_id.size());
		for(int x=0;x<mPlurk_id.size();x++){
			Log.d("DPLURK",mPlurk_id.get(x)+":"+plurk_id);
			if(plurk_id.equals(mPlurk_id.get(x))){
				mPlurk_id.remove(x);
				mContent.remove(x);
				mAvatar.remove(x);
				mResponses.remove(x);
				mTime.remove(x);
				break;
			}
		}
		Log.d("DPLURK","length:"+mPlurk_id.size());
	}
	public int getCount(){
		return mTime.size();
		//return mPlurks.size();
	}

	public Object getItem(int position){
		return position;
		//return mPlurks.get(position);
	}

	public long getItemId(int position){
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent){
		Log.d("DPLURK",type+":"+position);
		if(convertView == null){
			convertView = inflater.inflate(R.layout.plurk_list_xx,null);
			convertView.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					v.performLongClick();
					Log.d("DPLURK","cicked");
					Intent intent = new Intent();
					Bundle bundle = new Bundle();
					intent.setClass(mContext, resList.class);
					bundle.putLong("plurk_id",(Long)(v.getTag()));
					intent.putExtras(bundle);
					mContext.startActivity(intent);
				}
			});
		}
		else if(convertView.getTag()==mPlurk_id.get(position)){
			Long plurk_id = mPlurk_id.get(position);
			if(avatarPool.containsKey(plurk_id)&&avatarPool.get(plurk_id)!=null){
				modifyPlurkViewImage(position,convertView);
			}
			return convertView;
		}
		
		try{	
			String content = mContent.get(position);
			String time = mTime.get(position);
			int responses = mResponses.get(position);
			String avatar = mAvatar.get(position);
			Long plurk_id = mPlurk_id.get(position);

			ImageView h_avatar;
			convertView.setTag(plurk_id);
			TextView h_content = (TextView) convertView.findViewById(R.id.content);
			ViewHolder holder = new ViewHolder();
			h_avatar    = (ImageView) convertView.findViewById(R.id.avatar);
			//holder.content   = (WebView) convertView.findViewById(R.id.content);
			holder.responses = (TextView) convertView.findViewById(R.id.responses);
			holder.time      = (TextView) convertView.findViewById(R.id.time);
			
			if(avatarPool.containsKey(plurk_id)&&avatarPool.get(plurk_id)!=null)
				h_avatar.setImageBitmap(avatarPool.get(plurk_id));
			else{
				h_avatar.setImageResource(R.drawable.avatar_unknown);
			}
			holder.responses.setText(""+responses);
			holder.time.setText(time);
			h_content.setText(content);
			//holder.content.loadData("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />"+content, "text/html", "utf-8");
			//holder.content.loadData("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" /> qqqq", "text/html", "utf-8");
		}
		catch(Exception e){
			Log.d("DPLURK",e.getMessage());
			Log.d("DPLURK",e.toString());
		}
		return convertView;
	}


	public void clearPlurks(){
		//mPlurks.clear();
		mPlurk_id.clear();
		mContent.clear();
		mAvatar.clear();
		mResponses.clear();
		mTime.clear();
		
				Log.d("DPLURK","SetChanged clearPlurks()");
		notifyDataSetChanged();
	}
	public void refresh(){
				Log.d("DPLURK","SetChanged refresh()");
		notifyDataSetChanged();
	}
	public void modifyPlurkViewImage(final int position,View view){
		Long plurk_id = mPlurk_id.get(position);
		ImageView h_avatar = (ImageView) view.findViewById(R.id.avatar);
		h_avatar.setImageBitmap(avatarPool.get(plurk_id));
	}
	public void modifyPlurkViewByPosition(final int position,View view){
		modifyPlurkView(view,mContent.get(position),mTime.get(position),mResponses.get(position),mAvatar.get(position),mPlurk_id.get(position));
	}
	public void modifyPlurkView(View view,final String content,final String time,final int responses,final String avatar,final Long plurk_id){
		view.setTag(plurk_id);
		ViewHolder holder = new ViewHolder();
		ImageView h_avatar = (ImageView) view.findViewById(R.id.avatar);
		//holder.avatar    = (WebView) view.findViewById(R.id.avatar);
		holder.content   = (WebView) view.findViewById(R.id.content);
		holder.responses = (TextView) view.findViewById(R.id.responses);
		holder.time      = (TextView) view.findViewById(R.id.time);
		
		//holder.avatar.loadUrl(avatar);
		//h_avatar.setImageURI(Uri.parse(avatar));
		if(avatarPool.containsKey(plurk_id)&&avatarPool.get(plurk_id)!=null)
			h_avatar.setImageBitmap(avatarPool.get(plurk_id));
		else{
			h_avatar.setImageResource(R.drawable.avatar_unknown);
			new Thread(){
				public void run(){
					if(getAvatar(plurk_id,avatar)&&ViewOnScreen.contains(plurk_id)){
						Message m = new Message();
						m.what = 0x1234;
						h.sendMessage(m);
					}
				}
			
			}.start();
		}
		holder.responses.setText(""+responses);
		holder.time.setText(time);
		holder.content.loadData("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />"+content, "text/html", "utf-8");
	}
	public View getPlurkViewByPosition(int position){
		return getPlurkView(mContent.get(position),mTime.get(position),mResponses.get(position),mAvatar.get(position),mPlurk_id.get(position),mContext);
	}
	public View getPlurkView(String content,String time,int responses,final String avatar,final Long plurk_id,final Context context){
		View view = inflater.inflate(R.layout.plurk_list_x,null);
		ImageView h_avatar;
		view.setTag(plurk_id);
		ViewHolder holder = new ViewHolder();
		//holder.avatar    = (WebView) view.findViewById(R.id.avatar);
		h_avatar    = (ImageView) view.findViewById(R.id.avatar);
		holder.content   = (WebView) view.findViewById(R.id.content);
		holder.responses = (TextView) view.findViewById(R.id.responses);
		holder.time      = (TextView) view.findViewById(R.id.time);
		
		if(avatarPool.containsKey(plurk_id)&&avatarPool.get(plurk_id)!=null)
			h_avatar.setImageBitmap(avatarPool.get(plurk_id));
		else{
			h_avatar.setImageResource(R.drawable.avatar_unknown);
			new Thread(){
				public void run(){
					if(getAvatar(plurk_id,avatar)&&ViewOnScreen.contains(plurk_id)){
						Message m = new Message();
						m.what = 0x1234;
						h.sendMessage(m);
					}
				}
			
			}.start();
		}
		//h_avatar.setImageBitmap(plurks2db.getAvatar(plurk_id,avatar));
		//h_avatar.setImageURI(Uri.parse(avatar));
		//holder.avatar.loadUrl(avatar);
		holder.responses.setText(""+responses);
		holder.time.setText(time);
		holder.content.loadData("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />"+content, "text/html", "utf-8");
		//view.setClickable(true);
		//view.setFocusable(true);
		return view;
	}
	public void addPlurk(String content,String time,int responses,String avatar,Long plurk_id){
		//mPlurks.add(getPlurkView(content,time,responses,avatar,plurk_id,mContext));
		//Log.d("DPLURK","add #"+mTime.size());
		mPlurk_id.add(plurk_id);
		mContent.add(content);
		mAvatar.add(avatar);
		mResponses.add(responses);
		mTime.add(time);
	}	
	public void orz(final Long plurk_id, final String avatar){
		getAvatar(plurk_id,avatar);
	}
	public void setPlurks(){
		Log.d("DPLURK","setPlurks()");
		try{
			Log.d("DPLURK","SELECT content,posted,response_count,avatar,plurk_id FROM "+type+" ORDER BY plurk_id DESC");
			Cursor res = db.rawQuery("SELECT content,posted,response_count,avatar,plurk_id FROM "+type+" ORDER BY plurk_id DESC",null);
			Log.d("DPLURK","SELECTED");
			int content,posted,response_count,avatar,plurk_id;
			content = res.getColumnIndex("content");
			posted = res.getColumnIndex("posted");
			avatar = res.getColumnIndex("avatar");
			response_count = res.getColumnIndex("response_count");
			plurk_id = res.getColumnIndex("plurk_id");
			res.moveToNext();
			Log.d("DPLURK","START addPlurks:"+res.getCount());
			for(int x=res.getCount();x>0;x--){
				addPlurk(res.getString(content),res.getString(posted),res.getInt(response_count),res.getString(avatar),res.getLong(plurk_id));
				res.moveToNext();
			}
			Log.d("DPLURK","SetChanged setPlurk()");
			//notifyDataSetChanged();
		}
		catch(Exception e){
			Log.d("DPLURK","READ FROM DB FAILED");
			Log.d("DPLURK",e.getMessage());
			Log.d("DPLURK",e.toString());
		}
	}
}
