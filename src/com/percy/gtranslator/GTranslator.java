package com.percy.gtranslator;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.impl.cookie.DateParseException;

import android.R.integer;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewManager;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Spinner;

public class GTranslator extends Activity implements OnClickListener
{
	/** Called when the activity is first created. */
	private static final boolean	bAtOffice		= false;

	private TextView				textViewTranslate;
	private Button					buttonTranslate;
	private EditText				editTextTranslate;

	private TextView				promptTextView;
	private Spinner					usedTranslatorSpinner;
	private static Spinner			fromSpinner;
	private static Spinner			toSpinner;
	private Button					translateButton;
	private Button					clearButton;
	private Button					readDatabaseButton;
	private Button					readFileButton;
	private static TextView			translatedTextView;
	private static EditText			toTranslateEditText;

	private static DBAdapter		dbAdapter;

	public static Context			context;
	public static ProgressDialog	progressDialog;

	private static final String[]	mCountries		=
													{ "en", "zh-CN", "it",
			"zh_TW", "ja", "de", "el", "ko", "ko", "ru", "th", "fr", "fr" };

	private static final String[]	translateWeb	=
													{ "google", "yahoo",
			"baidu"								};
	private List<String>			allcountries;

	private ArrayAdapter<String>	fromSpinnerAdapter;

	private ArrayAdapter<String>	translateWebAdapter;
	private List<String>			allTranslateWeb;

	private static String			fromString;
	private static String			toString;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		setTheme(android.R.style.Theme_NoTitleBar);

		this.dbAdapter = new DBAdapter(this);
		this.dbAdapter.open();
		GTranslator.context = this;

		promptTextView = (TextView) findViewById(R.id.promptTextView);
		usedTranslatorSpinner = (Spinner) findViewById(R.id.usedTranslatorSpinner);
		fromSpinner = (Spinner) findViewById(R.id.fromSpinner);
		toSpinner = (Spinner) findViewById(R.id.toSpinner);
		translateButton = (Button) findViewById(R.id.translateButton);
		clearButton = (Button) findViewById(R.id.clearButton);
		readDatabaseButton = (Button) findViewById(R.id.readDatabaseButton);
		readFileButton = (Button) findViewById(R.id.readFileButton);
		translatedTextView = (TextView) findViewById(R.id.translatedTextView);
		toTranslateEditText = (EditText) findViewById(R.id.toTranslateEditText);

		toTranslateEditText.setCursorVisible(true);
		toTranslateEditText.setVerticalScrollBarEnabled(true);

		translateButton.setOnClickListener(this);
		clearButton.setOnClickListener(this);
		readDatabaseButton.setOnClickListener(this);
		readFileButton.setOnClickListener(this);

		allcountries = new ArrayList<String>();

		for (int i = 0; i < mCountries.length; i++)
		{
			allcountries.add(mCountries[i]);
		}

		fromSpinnerAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, allcountries);// simple_spinner_item
		fromSpinnerAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);// simple_spinner_dropdown_item
		fromSpinner.setAdapter(fromSpinnerAdapter);
		fromSpinner.setSelection(0);
		toSpinner.setAdapter(fromSpinnerAdapter);
		toSpinner.setSelection(1);

		allTranslateWeb = new ArrayList<String>();
		for (int i = 0; i < translateWeb.length; i++)
		{
			allTranslateWeb.add(translateWeb[i]);
		}

		translateWebAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, allTranslateWeb);
		translateWebAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		usedTranslatorSpinner.setAdapter(translateWebAdapter);

		listItemFromDatabase();
	}

	@Override
	public void onClick(View v)
	{
		int id = v.getId();
		if (id == R.id.translateButton)
		{
			ProcessDialogThread dialogThread = new ProcessDialogThread();
			dialogThread.execute(null);
			/*
			 * translatedTextView.setText("Connecting..."); String
			 * toTranslateTextString = toTranslateEditText.getText()
			 * .toString(); String tempString =
			 * toTranslateTextString.replace(" ", "%20"); fromString =
			 * mCountries[fromSpinner.getSelectedItemPosition()]; toString =
			 * mCountries[toSpinner.getSelectedItemPosition()]; String
			 * queryString = tempString + "&langpair=" + fromString + "%7C" +
			 * toString;
			 */
			// String queryString = tempString + "&langpair=en%7Czh-CN";
			// textViewTranslate.setText("queryString:"+toTranslateTextString+":"+'\n');
			/*
			 * String rawData = getRawData(queryString); if (null != rawData) {
			 * String parsedDataString = getData(rawData); if (null ==
			 * parsedDataString || "" == parsedDataString) {
			 * translatedTextView.setText("Not found"); } else {
			 * translatedTextView.setText(parsedDataString);
			 * dbAdapter.insertItem(toTranslateTextString, parsedDataString);
			 * 
			 * FileAccess.writeFile(this, toTranslateTextString + ":" +
			 * parsedDataString); }
			 * 
			 * } else { translatedTextView.setText("Translate failed!"); }
			 */
		} else if (id == R.id.clearButton)
		{
			toTranslateEditText.setText("");
			translatedTextView.setText("");
		} else if (id == R.id.readDatabaseButton)
		{
			translatedTextView.setText("Search history(Read Database):" + '\n');
			Cursor mCursor = this.dbAdapter.getAllItem();
			if (mCursor.moveToFirst())
			{
				do
				{
					int index = mCursor
							.getColumnIndex(DBAdapter.TOTRANSLATETEXT);
					String fromTextString = mCursor.getString(index);
					index = mCursor.getColumnIndex(DBAdapter.TRANSLATEDTEXT);
					String toTextString = mCursor.getString(index);
					translatedTextView
							.append("**********************************" + '\n');
					translatedTextView.append(fromTextString + ":"
							+ toTextString + '\n');

				} while (mCursor.moveToNext());
				translatedTextView
						.append("**********************************" + '\n');
			}

		} else if (id == R.id.readFileButton)
		{

			translatedTextView.setText("Search history(Read File):" + '\n');
			String readTextString = FileAccess.readFile(this, "test.txt");
			translatedTextView.append("" + readTextString);

		}
	}

	@Override
	protected void onDestroy()
	{
		// TODO Auto-generated method stub

		super.onDestroy();
		dbAdapter.close();
	}

	public static String getData(String dataString)
	{
		int index1 = dataString.indexOf(",");
		String string1 = dataString.substring(1, index1);
		String string2 = string1.replace("\"", "");

		int index2 = string2.lastIndexOf(":");
		String string3 = string2.substring(index2);
		String string4 = string3.replace("}", "");
		String string5 = string4.replace(":", "");
		return string5;
	}

	public static String getRawData(String string)
	{

		String dataString = null;
		// String queryString = "hello%20world&langpair=en%7Czh-CN";
		String queryString = string;
		try
		{
			URL url;
			if (bAtOffice)
			{
				url = new URL("http://10.85.40.153:8000/a.xml");
			} else
			{
				url = new URL(
						"http://ajax.googleapis.com/ajax/services/language/translate?v=1.0&q="
								+ queryString);
			}

			URLConnection conn = url.openConnection();
			conn.connect();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));

			byte buffer[] = new byte[1024];

			// bis.r
			String readerString;
			while ((readerString = reader.readLine()) != null)
			{
				dataString += readerString;
			}

			reader.close();
			// is.close();

		} catch (IOException e)
		{
			System.out.print("Net work error");
		}
		return dataString;

	}

	public void createTable(String tableName, String[] columnNames)
			throws DateParseException
	{
		StringBuffer sql = new StringBuffer("Create table if not exists");
		sql.append(tableName);
		sql.append("(");
		int length = columnNames.length - 1;
		for (int i = 0; i <= length; i++)
		{
			sql.append(columnNames[i]);
			sql.append("varchar");
		}
		sql.append(");");

	}

	public void listItemFromDatabase()
	{

		translatedTextView.append("Search history:" + '\n');
		Cursor mCursor = this.dbAdapter.getAllItem();
		if (mCursor.moveToFirst())
		{
			do
			{
				int index = mCursor.getColumnIndex(DBAdapter.TOTRANSLATETEXT);
				String fromTextString = mCursor.getString(index);
				index = mCursor.getColumnIndex(DBAdapter.TRANSLATEDTEXT);
				String toTextString = mCursor.getString(index);
				translatedTextView
						.append("**********************************" + '\n');
				translatedTextView.append(fromTextString + ":" + toTextString
						+ '\n');

			} while (mCursor.moveToNext());
			translatedTextView
					.append("**********************************" + '\n');
		}

		translatedTextView
				.append("#################Read File###################" + '\n');
		String readTextString = FileAccess.readFile(this, "test.txt");
		translatedTextView.append("" + readTextString);

	}

	private static class ProcessDialogThread extends
			AsyncTask<String, Void, String>
	{

		@Override
		protected String doInBackground(String... params)
		{
			// TODO Auto-generated method stub
			String toTranslateTextString = toTranslateEditText.getText()
					.toString();
			String tempString = toTranslateTextString.replace(" ", "%20");
			fromString = mCountries[fromSpinner.getSelectedItemPosition()];
			toString = mCountries[toSpinner.getSelectedItemPosition()];
			String queryString = tempString + "&langpair=" + fromString + "%7C"
					+ toString;
			// String queryString = tempString + "&langpair=en%7Czh-CN";
			// textViewTranslate.setText("queryString:"+toTranslateTextString+":"+'\n');

			return getRawData(queryString);

		}

		protected void onPreExecute()
		{
			showDialog();
			translatedTextView.setText("Connecting...");

		}

		protected void onPostExecute(String rawData)
		{
			dismissDialog();
			String toTranslateTextString = toTranslateEditText.getText()
					.toString();
			if (null != rawData)
			{
				String parsedDataString = getData(rawData);
				if (null == parsedDataString || "" == parsedDataString)
				{
					translatedTextView.setText("Not found");
				} else
				{
					translatedTextView.setText(parsedDataString);
					dbAdapter.insertItem(toTranslateTextString,
							parsedDataString);

					FileAccess.writeFile(GTranslator.context,
							toTranslateTextString + ":" + parsedDataString);
				}

			} else
			{
				translatedTextView.setText("Translate failed!");
			}

		}

		public void showDialog()
		{
			progressDialog = new ProgressDialog(GTranslator.context);
			progressDialog.setTitle("Searching");
			progressDialog.setMessage("Please waiting...");
			progressDialog.show();

		}

		public void dismissDialog()
		{
			progressDialog.dismiss();
		}

	}

}