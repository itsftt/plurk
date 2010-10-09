package com.ftt.plurk;

import android.app.AlertDialog;                                                                                                
import android.app.Dialog;
import android.content.DialogInterface;

import android.util.Log;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.content.Intent;

import android.os.Bundle;
import android.view.View;

import android.widget.TextView;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;

import android.widget.ListView;

import android.app.Activity;
import com.ftt.plurk.R;
import android.widget.Spinner;
import android.widget.ArrayAdapter;

public class post extends Activity
{
	/** Called when the activity is first created. */
	private Button b_plurk,b_icons;
	private EditText e_input;
	private Spinner s_qualifier;
	public static iconAdapter iAdapter;
	public static String[] qualifiers={
		":",
		"loves",
		"likes",
		"shares",
		"gives",
		"hates",
		"wants",
		"wishes",
		"needs",
		"will",
		"hopes",
		"asks",
		"has",
		"was",
		"wonders",
		"feels",
		"thinks",
		"says",
		"is"
	};
	@Override    
	protected Dialog onCreateDialog(int id) {
		return new AlertDialog.Builder(post.this)
			   .setTitle("icons")
			   .setAdapter(iAdapter,new DialogInterface.OnClickListener() {
			   				public void onClick(DialogInterface dialog, int which) {
								//new AlertDialog.Builder(post.this).setMessage("TEST").show();
								e_input.setText(e_input.getText()+(""+which));
							}
						})
			   .create();
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.post);
		final Long plurk_id = this.getIntent().getExtras().getLong("plurk_id");
		findViews();

		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.qualifier, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		s_qualifier.setAdapter(adapter);
		s_qualifier.setSelection(17);
		Log.d("DPLURK","qualifier="+qualifiers[s_qualifier.getSelectedItemPosition()]);
		//Log.d("DPLURK","qualifier="+s_qualifier.getPrompt().toString());
		//s_qualifier.setPrompt("qq");
		//Log.d("DPLURK","qualifier="+s_qualifier.getPrompt().toString());

		if(iAdapter==null)	iAdapter = new iconAdapter(this);

		b_plurk.setOnClickListener(new View.OnClickListener(){
			public void onClick(View view){
				//getSelectedItemPosition
				if(plurk_id==0l)
					engine.plurkAdd(e_input.getText().toString(),qualifiers[s_qualifier.getSelectedItemPosition()]);
				else
					engine.responseAdd(e_input.getText().toString(),qualifiers[s_qualifier.getSelectedItemPosition()],plurk_id);
				finish();
			}
		});

		b_icons.setOnClickListener(new View.OnClickListener(){
			public void onClick(View view){
				showDialog(0);
			}
		});
	}
	public void findViews(){
		b_plurk = (Button)findViewById(R.id.plurk);
		b_icons = (Button)findViewById(R.id.icons);
		e_input = (EditText)findViewById(R.id.input);
		s_qualifier = (Spinner)findViewById(R.id.qualifier);
	}
}
