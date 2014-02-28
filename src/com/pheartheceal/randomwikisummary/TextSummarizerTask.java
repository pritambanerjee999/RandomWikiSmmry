package com.pheartheceal.randomwikisummary;

import android.os.AsyncTask;

public class TextSummarizerTask extends AsyncTask<String, Void, String> {

	
	
	@Override
	protected String doInBackground(String... params) {

		return TextSummarizer.getSummary(params[0]);
	}

}