package com.ftt.plurk;

import android.os.Message;
import android.os.Handler;

import android.util.Log;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.content.Intent;

import android.os.Bundle;
import android.view.View;

import android.widget.TextView;
import android.webkit.WebView;
import android.widget.Button;

import android.widget.ListView;

import android.app.Activity;
import com.ftt.plurk.R;

//public class plurk extends ListActivity
public class resList extends Activity
{
	/** Called when the activity is first created. */
	private SQLiteDatabase db;
	private resAdapter mAdapter;
	private ListView plurkListView;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.res_list);
		final Long plurk_id = this.getIntent().getExtras().getLong("plurk_id");
		//inflater = LayoutInflater.from(this);
		//View mPlurk = inflater.inflate(R.layout.plurk_list,null);
		View mPlurk = findViewById(R.id.mPlurk);

		Log.d("DPLURK","plurk_id:"+plurk_id.toString());
		db = engine.db;
		Log.d("DPLURK","db opened");
		Cursor res;
		res = db.rawQuery("SELECT content,posted,response_count,avatar FROM plurks WHERE plurk_id="+plurk_id,null);
		if(res.getCount()==0)
			res = db.rawQuery("SELECT content,posted,response_count,avatar FROM unread WHERE plurk_id="+plurk_id,null);
		Log.d("DPLURK","query done");
		Log.d("DPLURK","count:"+res.getCount());
		int content,posted,response_count,avatar;
		content = res.getColumnIndex("content");
		posted = res.getColumnIndex("posted");
		avatar = res.getColumnIndex("avatar");
		response_count = res.getColumnIndex("response_count");
		res.moveToNext();
		setView(mPlurk,res.getString(content),res.getString(posted),res.getInt(response_count),res.getString(avatar));

		mAdapter = new resAdapter(this,plurk_id);
		plurkListView = (ListView) findViewById(R.id.rlist);
		plurkListView.setItemsCanFocus(true);
		plurkListView.setClickable(false);
		plurkListView.setOnCreateContextMenuListener(this);
		plurkListView.setAdapter(mAdapter);
		//plurk.unreadAdapter.refresh();
		
		final Handler h = new Handler() {
			public void handleMessage(Message msg) {
				if (msg.what == 0x1234){
					//clearresponses();
					mAdapter.setResponses(plurk_id);
					Log.d("DPLURK","go remove()");
					plurk.unreadAdapter.remove(plurk_id);
				}
			}
		};
		new Thread(new Runnable(){
			public void run(){
				Log.d("DPLURK","getplurkresponses");
				engine.getPlurkResponses(plurk_id,0);
				Log.d("DPLURK","setResponses");
				Message m = new Message();
				m.what = 0x1234;
				h.sendMessage(m);

			}
		}).start();
		Button b_plurk = (Button) findViewById(R.id.plurk);
		b_plurk.setOnClickListener(new View.OnClickListener(){
		public void onClick(View v){
			Intent intent = new Intent();
			intent.setClass(resList.this, post.class);
			Bundle bundle = new Bundle();
			bundle.putLong("plurk_id",plurk_id);
			intent.putExtras(bundle);
			startActivity(intent);
		} });
		Log.d("DPLURK","done!");
	}
	@Override
	protected void onResume(){
		super.onResume();
		Log.d("DPLURK","onResume");
		mAdapter.refresh();

	}
	private static class ViewHolder{
		WebView avatar;
		WebView content;
		TextView responses;
		TextView time;
	}
	public void setView(View view,String content,String time,int responses,String avatar){
		Log.d("DPLURK",content+":"+time+":"+responses+":"+avatar);
		ViewHolder holder = new ViewHolder();
		holder.avatar = (WebView) view.findViewById(R.id.avatar);
		holder.content = (WebView) view.findViewById(R.id.content);
		holder.responses = (TextView) view.findViewById(R.id.responses);
		holder.time = (TextView) view.findViewById(R.id.time);
		Log.d("DPLURK","holder ok");
		
		holder.avatar.loadUrl(avatar);
		holder.responses.setText(""+responses);
		holder.time.setText(time);
		holder.content.loadData("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />"+content, "text/html", "utf-8");

	}	
}
