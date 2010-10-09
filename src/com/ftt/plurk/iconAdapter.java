package com.ftt.plurk;

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

public class iconAdapter extends BaseAdapter{
	//private ArrayList<View> mPlurks;
	private ArrayList<Long> mPlurk_id;
	private Context mContext;
	private String type;
	private static LayoutInflater inflater;
	public static SQLiteDatabase db;
	public iconAdapter(Context c){
		mContext = c;
		if(inflater==null)	inflater = LayoutInflater.from(mContext);
	}
	public int getCount(){
		return 30;
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
			convertView = inflater.inflate(R.layout.icon_list,null);
			/*convertView.setOnClickListener(new View.OnClickListener() {
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
			});*/
		ImageView h_icon = (ImageView) convertView.findViewById(R.id.icon);
				TextView h_text = (TextView) convertView.findViewById(R.id.text);
		h_text.setText("position:"+position);
		switch(position){
			case 0:
				h_icon.setImageResource(R.drawable.i0);
				break;
			case 1:
				h_icon.setImageResource(R.drawable.i1);
				break;
			case 2:
				h_icon.setImageResource(R.drawable.i2);
				break;
			case 3:
				h_icon.setImageResource(R.drawable.i3);
				break;
			case 4:
				h_icon.setImageResource(R.drawable.i4);
				break;
			case 5:
				h_icon.setImageResource(R.drawable.i5);
				break;
			case 6:
				h_icon.setImageResource(R.drawable.i6);
				break;
			case 7:
				h_icon.setImageResource(R.drawable.i7);
				break;
			case 8:
				h_icon.setImageResource(R.drawable.i8);
				break;
			case 9:
				h_icon.setImageResource(R.drawable.i9);
				break;
			case 10:
				h_icon.setImageResource(R.drawable.i10);
				break;
			case 11:
				h_icon.setImageResource(R.drawable.i11);
				break;
			case 12:
				h_icon.setImageResource(R.drawable.i12);
				break;
			case 13:
				h_icon.setImageResource(R.drawable.i13);
				break;
			case 14:
				h_icon.setImageResource(R.drawable.i14);
				break;
			case 15:
				h_icon.setImageResource(R.drawable.i15);
				break;
			case 16:
				h_icon.setImageResource(R.drawable.i16);
				break;
			case 17:
				h_icon.setImageResource(R.drawable.i17);
				break;
			case 18:
				h_icon.setImageResource(R.drawable.i18);
				break;
			case 19:
				h_icon.setImageResource(R.drawable.i19);
				break;
			case 20:
				h_icon.setImageResource(R.drawable.i20);
				break;
			case 21:
				h_icon.setImageResource(R.drawable.i21);
				break;
			case 22:
				h_icon.setImageResource(R.drawable.i22);
				break;
			case 23:
				h_icon.setImageResource(R.drawable.i23);
				break;
			case 24:
				h_icon.setImageResource(R.drawable.i24);
				break;
			case 25:
				h_icon.setImageResource(R.drawable.i25);
				break;
			case 26:
				h_icon.setImageResource(R.drawable.i26);
				break;
			case 27:
				h_icon.setImageResource(R.drawable.i27);
				break;
			case 28:
				h_icon.setImageResource(R.drawable.i28);
				break;
			case 29:
				h_icon.setImageResource(R.drawable.i29);
				break;
			case 30:
				h_icon.setImageResource(R.drawable.i30);
				break;
		
		}
		//convertView.setClickable(false);
		return convertView;
	}

	public void refresh(){
		Log.d("DPLURK","SetChanged refresh()");
		notifyDataSetChanged();
	}
}
