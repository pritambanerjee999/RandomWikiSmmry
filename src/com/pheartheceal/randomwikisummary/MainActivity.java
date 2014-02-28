package com.pheartheceal.randomwikisummary;

import java.util.concurrent.ExecutionException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.pheartheceal.randomwikisummary.R;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

	public static final String WIKI_TEXT = "wikitxt";
	public TextView summaryText;
	protected WikiDownloader WD;
	protected TextSummarizerTask summarizer;
	String wikiUrl;
	Button more;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		summaryText = (TextView) findViewById(R.id.textView1);
		summaryText.setMovementMethod(new ScrollingMovementMethod());
		wikiUrl = "http://en.m.wikipedia.org/wiki/Main_Page";
		more = (Button) findViewById(R.id.moreButton);

	}
	
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		
		summaryText.setText(savedInstanceState.getString(WIKI_TEXT));
		super.onRestoreInstanceState(savedInstanceState);
	}
	
	public void onSaveInstanceState(Bundle savedInstanceState) {
	    // Save the user's current text state
	    savedInstanceState.putString(WIKI_TEXT, summaryText.getText().toString());
	    
	    // Always call the superclass so it can save the view hierarchy state
	    super.onSaveInstanceState(savedInstanceState);
	}
	
	public void showMore(View view) {
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse(wikiUrl));
		startActivity(i);
	}

	public void nextArticle(View view) {
		
		// change the text view
		WD = new WikiDownloader();
		WD.execute("");
	}

	private class WikiDownloader extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			summaryText.setText("Loading...");
		}

		@Override
		protected String doInBackground(String... params) {

			Document doc = null;

			try {
				doc = Jsoup
						.connect("http://en.wikipedia.org/wiki/Special:Random")
						.userAgent(
								"Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
						.referrer("http://www.google.com").get();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				return "Download failed.";
			}
			
			wikiUrl = doc.baseUri();

			Elements tables = doc.getElementsByTag("table");

			for (Element t : tables) {
				t.remove();
			}

			return doc.getElementsByTag("p").text();
		}

		protected void onPostExecute(String result) {
			summarizer = new TextSummarizerTask();
			summarizer.execute(result);
			try {
				summaryText.setText(summarizer.get());
				more.setVisibility(View.VISIBLE);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				summaryText.setText("Parsing Error.");
				//e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				summaryText.setText("Parsing Error.");
				//e.printStackTrace();
			} catch (Exception e) {
				summaryText.setText("Parsing Error.");
			}
		}

	}
}
