package com.kaetter.motorcyclemaintenancelog;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.Window;

public class Main extends FragmentActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main);
		
		

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		return true;
	}

	
	
}