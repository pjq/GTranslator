package com.percy.gtranslator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.text.StaticLayout;

public class DBAdapter
{
	public final static String	KEYID			= "_id";
	public final static String	TOTRANSLATETEXT	= "ToTranslateText";
	public final static String	TRANSLATEDTEXT	= "TranslatedText";

	public final static String	TABLENAME		= "TRANSLATOR";
	public final static String	DATABASENAME	= "TRANSLATORDATABASE";
	public final static int		DATABASEVERSION	= 1;

	private static final String	CREATETABLE		= "create table "
														+ TABLENAME
														+ " ("
														+ KEYID
														+ " integer primary key autoincrement,"
														+ TOTRANSLATETEXT
														+ " text not null, "
														+ TRANSLATEDTEXT
														+ " text not null);";
	
    private static final String DATABASE_CREATE =
        "create table titles (_id integer primary key autoincrement, "
        + "isbn text not null, title text not null, "
        + "publisher text not null);";

	private DatabaseHelper		DBHelper;
	private SQLiteDatabase		db;
	private Context				context;

	public DBAdapter(Context context)
	{
		this.context = context;
		this.DBHelper = new DatabaseHelper(context);
	}

	public static class DatabaseHelper extends SQLiteOpenHelper
	{

		// public DatabaseHelper(Context context, String name, CursorFactory
		// factory, int version)
		public DatabaseHelper(Context context)
		{
			super(context, DATABASENAME, null, DATABASEVERSION);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onCreate(SQLiteDatabase db)
		{
			// TODO Auto-generated method stub
			db.execSQL(CREATETABLE);

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
		{
			// TODO Auto-generated method stub

		}

	}

	public void open() throws SQLException
	{
		db = this.DBHelper.getWritableDatabase();
		// return this;
	}

	public void close()
	{
		DBHelper.close();
	}

	public long insertItem(String fromText, String toText)
	{
		ContentValues contentValues = new ContentValues();
		contentValues.put(TOTRANSLATETEXT, fromText);
		contentValues.put(TRANSLATEDTEXT, toText);
		return db.insert(TABLENAME, null, contentValues);
	}

	public Cursor getItem(long id) throws SQLException
	{
		// db.query(TABLENAME, new String[]{}, selection, selectionArgs,
		// groupBy, having, orderBy);
		Cursor mCursor = db.query(TABLENAME, new String[]
		{ TOTRANSLATETEXT, TRANSLATEDTEXT }, KEYID + "=" + id, null, null,
				null, null);

		if (mCursor != null)
		{
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	public Cursor getAllItem() throws SQLException
	{
		Cursor mCursor = db.query(TABLENAME, new String[]
		{ TOTRANSLATETEXT, TRANSLATEDTEXT }, null, null, null, null, null);

		return mCursor;
	}

}
