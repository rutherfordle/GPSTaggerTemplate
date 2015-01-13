package com.example.tagger;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
public class Menu extends Activity {
	EditText tag1, tag2, tag3;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu);
		init();
	}
	private void init() {
		tag1 = (EditText) findViewById(R.id.tag1);
		tag2 = (EditText) findViewById(R.id.tag2);
	    tag3 = (EditText) findViewById(R.id.tag3);
		read();
	}

	public void save(View view) {
		String s1 = tag1.getText().toString();
		String s2 = tag2.getText().toString();
		String s3 = tag3.getText().toString();
		
		if (s1 != null)
			PreferenceConnector.writeString(this, PreferenceConnector.tag1,
					s1);
		if (s2 != null)
			PreferenceConnector.writeString(this, PreferenceConnector.tag2,
					s2);
		if (s3 != null)
			PreferenceConnector.writeString(this, PreferenceConnector.tag3,
					s3);
		finish();
	}

	public void reset(View view) {
		PreferenceConnector.getEditor(this).remove(PreferenceConnector.tag1)
				.commit();
		PreferenceConnector.getEditor(this).remove(PreferenceConnector.tag2)
				.commit();
		PreferenceConnector.getEditor(this).remove(PreferenceConnector.tag3)
				.commit();
		read();
	}

	private void read() {
		tag1.setText(PreferenceConnector.readString(this,
				PreferenceConnector.tag1, null));
		tag2.setText(PreferenceConnector.readString(this,
				PreferenceConnector.tag2, null));
		tag3.setText(PreferenceConnector.readString(this,
				PreferenceConnector.tag3, null));
	}
}