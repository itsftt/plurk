package com.ftt.plurk;

import android.view.Window;

import android.content.Intent;

import android.util.Log;

import android.os.Message;
import android.os.Handler;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import android.app.Activity;
import com.ftt.plurk.R;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

//public class plurk extends ListActivity
public class plurk extends Activity
	implements OnItemClickListener
{
	/** Called when the activity is first created. */
	public static plurkAdapter mAdapter,unreadAdapter,OnScreenAdapter;;
	public static iconAdapter iAdapter;
	private ListView plurkListView;
	private Button old;
    private Button newp;
    private Button all;
    private Button unread;
    private Button b_plurk;
    private Button drop;
	private Context c;
	private final Handler h = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 0x1234){
				//clearresponses();
				Log.d("DPLURK","add to list");
				mAdapter.clearPlurks();
				unreadAdapter.clearPlurks();
				Log.d("DPLURK","list clear");
				unreadAdapter.setPlurks();
				mAdapter.setPlurks();
				Log.d("DPLURK","getCount()"+unreadAdapter.getCount());
				Log.d("DPLURK","done");
			}
 		}
	};
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list);
		//requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		//setProgressBarIndeterminateVisibility(false);
		//setProgressBarIndeterminateVisibility(true);
		c = this;
		try{
			Log.d("DPLURK","init db");
			engine.init(c);
		}
		catch(Exception e){
			Log.d("DPLURK","init db failed");
			Log.d("DPLURK",e.getMessage());
			Log.d("DPLURK",e.toString());
		}
		/*getListView().setEmptyView(findViewById(R.id.empty));

		mAdapter = new plurkAdapter(this);
		setListAdapter(mAdapter);*/
		
		findViews();
		setAdapters();
		try{
			mAdapter.setPlurks();
			new Thread(new Runnable(){
				public void run(){
					engine.login();
					Log.d("DPLURK","logged in");
					Message m = new Message();
					m.what = 0x1234;
					Log.d("DPLURK","start get plurks");
					engine.getUnreadPlurks();
					engine.getPlurks("new");
					h.sendMessage(m);
					while(!engine.getPlurks("new")){
						Log.d("DPLURK","getPlurks(new) failed retry");
					}
					/*if(engine.getPlurks("new")==true){
						h.sendMessage(m);
					}
					else
						Log.d("DPLURK","get plurks failed");*/
	
				}
			}).start();
			Log.d("DPLURK","add to list");
		}
		catch(Exception e){
			Log.d("DPLURK","QQ");
		}
		/*try{
			unreadAdapter.setPlurks();
		}
		catch (Exception e){
			Log.d("DPLURK","QQQQ");
			Log.d("DPLURK",e.getMessage());
			Log.d("DPLURK",e.toString());
		
		}*/
		//plurkListView.setAdapter(unreadAdapter);
		//engine test = new engine(this);
		//mAdapter.addPlurk("content","time",0);
		setListeners();

	}
	public final void findViews(){
		old     = (Button) findViewById(R.id.old);
		newp    = (Button) findViewById(R.id.newp);
		all     = (Button) findViewById(R.id.all);
		unread  = (Button) findViewById(R.id.unread);
		b_plurk = (Button) findViewById(R.id.plurk);
		drop    = (Button) findViewById(R.id.drop);
		plurkListView = (ListView) findViewById(R.id.list);
	}
	public final void setListeners(){
		b_plurk.setOnClickListener(new View.OnClickListener(){
		public void onClick(View v){
			Intent intent = new Intent();
			intent.setClass(plurk.this, post.class);
			Bundle bundle = new Bundle();
			bundle.putLong("plurk_id",0l);
			intent.putExtras(bundle);
			startActivity(intent);
		} });

		unread.setOnClickListener(new View.OnClickListener(){
		public void onClick(View v){
			plurkListView.setAdapter(unreadAdapter);
			OnScreenAdapter = unreadAdapter;
			//plurkListView.setAdapter(iAdapter);
		} });

		drop.setOnClickListener(new View.OnClickListener(){
		public void onClick(View v){
			engine.drop_table();
		} });

		/*Button init = (Button) findViewById(R.id.init);
		init.setOnClickListener(new View.OnClickListener(){
		public void onClick(View v){
			Log.d("DPLURK","engine init");
			try{
				engine.init(c);
			}
			catch(Exception e){
			
			}
		} });*/

		all.setOnClickListener(new View.OnClickListener(){
		public void onClick(View v){
			try{
				plurkListView.setAdapter(mAdapter);
				OnScreenAdapter = mAdapter;
			}
			catch(Exception e){
			
			}
		} });

		newp.setOnClickListener(new View.OnClickListener(){
		public void onClick(View v){
			new Thread(new Runnable(){
				public void run(){
					//engine.login();
					//Log.d("DPLURK","logged in");
					Log.d("DPLURK","start get plurks");
					if(engine.getPlurks("new")==true&&engine.getUnreadPlurks()==true){
						Log.d("DPLURK","get plurks done");
						Message m = new Message();
						m.what = 0x1234;
						h.sendMessage(m);
					}
					else
						Log.d("DPLURK","get plurks failed");
	
				}
			}).start();
			/*try{
				Log.d("DPLURK","engine get plurk");
				if(engine.getPlurks("new")==true){
					Log.d("DPLURK","add to list");
					mAdapter.clearPlurks();
					Log.d("DPLURK","list clear");
					mAdapter.setPlurks();
				}
			}
			catch(Exception e){
				Log.d("DPLURK","update failed Q_Q");
			}*/
		} });

		old.setOnClickListener(new View.OnClickListener(){
		public void onClick(View v){
			try{
				mAdapter.clearPlurks();
				Log.d("DPLURK","engine get plurk");
				engine.getPlurks("old");
				Log.d("DPLURK","add to list");
				mAdapter.setPlurks();
			}
			catch(Exception e){
				Log.d("DPLURK","update failed Q_Q");
			}
		} });
	
	
	}
	public final void setAdapters(){
	
		mAdapter = new plurkAdapter(this,"plurks");
		unreadAdapter = new plurkAdapter(this,"unread");
		iAdapter = new iconAdapter(this);
		plurkListView.setItemsCanFocus(true);
		plurkListView.setClickable(true);
		plurkListView.setOnCreateContextMenuListener(this);
		plurkListView.setOnItemClickListener(this);
		plurkListView.setAdapter(mAdapter);
		OnScreenAdapter = mAdapter;
	}
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Log.d("DPLURK","CLICK "+position+","+id);
		Log.d("DPLURK",((Long)view.getTag()).toString());
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		intent.setClass(this, resList.class);
		bundle.putLong("plurk_id",(Long)(view.getTag()));
		intent.putExtras(bundle);
		startActivity(intent);
		//view.performClick();
		/*final Long plurk_id = view.getTag();
		Log.d("DPLURK","cicked:"+position);
		Intent intent = new Intent();
		Log.d("DPLURK","1");
		Bundle bundle = new Bundle();
		Log.d("DPLURK","2");
		intent.setClass(this, resList.class);
		Log.d("DPLURK","3");
		try{
			//bundle.putLong("plurk_id",(Long)(view.getTag()));
			bundle.putLong("plurk_id",(Long)(OnScreenAdapter.getPlurkId(position)));
		}
		catch(Exception e){
			Log.d("DPLURK",e.getMessage());
			Log.d("DPLURK",e.toString());
		}
		Log.d("DPLURK","4");
		intent.putExtras(bundle);
		Log.d("DPLURK","5");
		startActivity(intent);
		Log.d("DPLURK","6");*/
	}

	@Override
	public void onResume()
	{
		super.onResume();
		//mAdapter.refresh();
		//unreadAdapter.refresh();
	}
}
