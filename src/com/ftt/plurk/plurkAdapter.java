package com.ftt.plurk;
import android.widget.ListView;


import java.io.InputStream;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.SpannableStringBuilder;

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




import java.util.Iterator;

public class plurkAdapter extends BaseAdapter{
	private static class ViewHolder{
		ImageView avatar;
		TextView content;
		TextView responses;
		TextView time;
	}
	private static class PlurkItem{
		Long plurk_id;
		String content;
		String avatar;
		int responses;
		String time;
	}
	private ArrayList<PlurkItem> plurkItems;
	private Context mContext;
	private String type;
	private static LayoutInflater inflater;
	public static HashMap<Long, Bitmap> avatarPool;
	public static HashMap<String, Drawable> imagePool;
	public static SQLiteDatabase db;
	private final Handler h = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 0x1234){
				Log.d("DPLURK","SetChanged Handler");
				notifyDataSetChanged();
			}
		}
	};
	public plurkAdapter(Context c,String t){
		mContext = c;
		type = t;
		avatarPool = engine.avatarPool;
		db = engine.db;
		if(inflater==null)	inflater = LayoutInflater.from(mContext);
		if(imagePool==null)	imagePool = new HashMap<String, Drawable>();
		plurkItems = new ArrayList<PlurkItem>();
	}
	public void remove(Long plurk_id){
		Log.d("DPLURK","remove() plurk_id:"+plurk_id);
		Log.d("DPLURK","length:"+plurkItems.size());
		for(int x=0;x<plurkItems.size();x++){
			Log.d("DPLURK",plurkItems.get(x).plurk_id+":"+plurk_id);
			if(plurk_id.equals(plurkItems.get(x).plurk_id)){
				plurkItems.remove(x);
				Log.d("DPLURK","SetChanged remove()");
				notifyDataSetChanged();
				break;
			}
		}
		Log.d("DPLURK","length:"+plurkItems.size());
	}
	public int getCount(){
		return plurkItems.size();
	}

	public Object getItem(int position){
		return position;
	}

	public long getItemId(int position){
		return position;
	}

	public View getView(final int position,View convertView,final ViewGroup parent){
		//Log.d("D PLURK",type+":"+position);
		if(convertView == null){
			convertView = inflater.inflate(R.layout.plurk_list_xx,null);
			/*convertView.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					//v.performLongClick();
					Log.d("DPLURK","position:"+position);
					//((ListView)parent).performItemClick(parent, position, getItemId(position));
					Log.d("DPLURK","cicked");
					Intent intent = new Intent();
					Bundle bundle = new Bundle();
					intent.setClass(mContext, resList.class);
					bundle.putLong("plurk_id",(Long)(v.getTag()));
					intent.putExtras(bundle);
					mContext.startActivity(intent);
				}
			});*/
			//convertView.setClickable(true);
		}
		else if(plurkItems.get(position).plurk_id.equals(convertView.getTag())){
			final Long plurk_id = (Long) convertView.getTag();
			if(avatarPool.containsKey(plurk_id)&&avatarPool.get(plurk_id)!=null){
				ImageView h_avatar = (ImageView) convertView.findViewById(R.id.avatar);
				h_avatar.setImageBitmap(avatarPool.get(plurk_id));
			}
			return convertView;
		}
		else{
		}
		try{	
			final String content = plurkItems.get(position).content;
			final String time = plurkItems.get(position).time;
			final int responses = plurkItems.get(position).responses;
			final String avatar = plurkItems.get(position).avatar;
			final Long plurk_id = plurkItems.get(position).plurk_id;

			convertView.setTag(plurk_id);
			ViewHolder holder = new ViewHolder();
			holder.avatar    = (ImageView) convertView.findViewById(R.id.avatar);
			holder.content   = (TextView) convertView.findViewById(R.id.content);
			holder.responses = (TextView) convertView.findViewById(R.id.responses);
			holder.time      = (TextView) convertView.findViewById(R.id.time);
			
			holder.responses.setText(""+responses);
			holder.time.setText(time);
			//holder.content.setText(content);
			holder.content.setText(
				(SpannableStringBuilder) Html.fromHtml(content, new Html.ImageGetter() {
					public Drawable getDrawable(final String source) {
						if (imagePool.containsKey(source)) 
							return imagePool.get(source);
						else {
							new Thread(){
								public void run(){
									try{
										URLConnection conn = new URL(source).openConnection();
										conn.connect();
										InputStream is = conn.getInputStream();
										Bitmap bmp = BitmapFactory.decodeStream(is);
										Drawable d = new BitmapDrawable(bmp);
										d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
										is.close();
										imagePool.put(source, d);
										Message m = new Message();
										m.what = 0x1234;
										h.sendMessage(m);
									}
									catch (Exception e) {
									}
								}
							}.start();
							return null;
						}
					}
				}, null),
				TextView.BufferType.SPANNABLE);


			if(avatarPool.containsKey(plurk_id)&&avatarPool.get(plurk_id)!=null)
				holder.avatar.setImageBitmap(avatarPool.get(plurk_id));
			else{
				holder.avatar.setImageResource(R.drawable.avatar_unknown);
			}
		}
		catch(Exception e){
			Log.d("DPLURK",e.getMessage());
			Log.d("DPLURK",e.toString());
		}
		return convertView;
	}

	public void clearPlurks(){
		plurkItems.clear();
		
		Log.d("DPLURK","SetChanged clearPlurks()");
		notifyDataSetChanged();
	}
	public void refresh(){
		Log.d("DPLURK","SetChanged refresh()");
		notifyDataSetChanged();
	}
	public void addPlurk(final String content,final String time,final int responses,final String avatar,final Long plurk_id){
		PlurkItem i = new PlurkItem();
		i.content = content;
		i.time = time;
		i.responses = responses;
		i.avatar = avatar;
		i.plurk_id = plurk_id;
		plurkItems.add(i);
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
			res.moveToFirst();
			Log.d("DPLURK","START addPlurks:"+res.getCount());
			for(int x=res.getCount();x>0;x--){
				addPlurk(res.getString(content),res.getString(posted),res.getInt(response_count),res.getString(avatar),res.getLong(plurk_id));

				res.moveToNext();
			}
			new Thread(){
				public void run(){
					Log.d("DPLURK","SELECT content,posted,response_count,avatar,plurk_id FROM "+type+" ORDER BY plurk_id DESC");
					Cursor res = db.rawQuery("SELECT content,posted,response_count,avatar,plurk_id FROM "+type+" ORDER BY plurk_id DESC",null);
					Log.d("DPLURK","SELECTED");
					int content = res.getColumnIndex("content");
					int posted = res.getColumnIndex("posted");
					int avatar = res.getColumnIndex("avatar");
					int response_count = res.getColumnIndex("response_count");
					int plurk_id = res.getColumnIndex("plurk_id");
					res.moveToFirst();
					for(int x=res.getCount();x>0;x--){
						engine.getAvatar(res.getLong(plurk_id),res.getString(avatar));
						if(x%20==1){
							Message m = new Message();
							m.what = 0x1234;
							h.sendMessage(m);
							Log.d("DPLURK","SetChanged setPlurk()");
						}
						res.moveToNext();
					}
				}
			}.start();
			notifyDataSetChanged();
		}
		catch(Exception e){
			Log.d("DPLURK","READ FROM DB FAILED");
			Log.d("DPLURK",e.getMessage());
			Log.d("DPLURK",e.toString());
		}
	}
}
