package com.example.countrydemo.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 */
public class DBCountryManager {
	private static final String ASSETS_NAME = "country_code.db";
	private static final String DB_NAME = "country_code.db";
	private static final String TABLE_NAME = "country";

	private static final String CODE = "code";
	private static final String COUNTRY = "country";
	
	private static final String PINYIN = "pinyin";
	private static final String SHORT_PINYIN = "shortPinyin";

	private static final int BUFFER_SIZE = 1024;
	private String DB_PATH;
	private Context mContext;

	public DBCountryManager(Context context) {
		this.mContext = context;
		DB_PATH = File.separator + "data"
				+ Environment.getDataDirectory().getAbsolutePath()
				+ File.separator + context.getPackageName() + File.separator
				+ "databases" + File.separator;
		copyDBFile();
	}

	@SuppressWarnings("ResultOfMethodCallIgnored")
	public void copyDBFile() {
		File dir = new File(DB_PATH);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		File dbFile = new File(DB_PATH + DB_NAME);
		if (!dbFile.exists()) {
			InputStream is;
			OutputStream os;
			try {
				is = mContext.getResources().getAssets().open(ASSETS_NAME);
				os = new FileOutputStream(dbFile);
				byte[] buffer = new byte[BUFFER_SIZE];
				int length;
				while ((length = is.read(buffer, 0, buffer.length)) > 0) {
					os.write(buffer, 0, length);
				}
				os.flush();
				os.close();
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 读取所有国家
	 * @return
	 */
	public List<Country> getAllCountry() {
		SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(DB_PATH
				+ DB_NAME, null);
		Cursor cursor = db.rawQuery("select * from " + TABLE_NAME, null);
		List<Country> result = new ArrayList<Country>();
		Country country;
		while (cursor.moveToNext()) {
			String code = cursor.getString(cursor.getColumnIndex(CODE));
			String countryName = cursor.getString(cursor.getColumnIndex(COUNTRY));
			String pinyin = cursor.getString(cursor.getColumnIndex(PINYIN));
			String shortPinyin = cursor.getString(cursor.getColumnIndex(SHORT_PINYIN));
			country = new Country(code, countryName, pinyin, shortPinyin);
			result.add(country);
		}
		cursor.close();
		db.close();
		Collections.sort(result, new CityComparator());
		return result;
	}

	/**
	 * 通过名字或者拼音搜索。或者区号，或者拼音缩写。
	 * @param keyword
	 * @return
	 */
	public List<Country> searchCountry(final String keyword) {
		SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(DB_PATH
				+ DB_NAME, null);
		StringBuffer sb = new StringBuffer();
		sb.append("select * from ").append(TABLE_NAME);
		sb.append(" where ").append(CODE).append(" like \"%").append(keyword).append("%\"");
		sb.append(" or ").append(COUNTRY).append(" like \"%").append(keyword).append("%\"");
		sb.append(" or ").append(PINYIN).append(" like \"").append(keyword).append("%\"");
		sb.append(" or ").append(SHORT_PINYIN).append(" like \"").append(keyword).append("%\"");
		String sql = sb.toString();
	
		
		Cursor cursor = db.rawQuery(sql, null);
		List<Country> result = new ArrayList<Country>();
		Country country;
		while (cursor.moveToNext()) {
			String code = cursor.getString(cursor.getColumnIndex(CODE));
			String countryName = cursor.getString(cursor.getColumnIndex(COUNTRY));
			String pinyin = cursor.getString(cursor.getColumnIndex(PINYIN));
			String shortPinyin = cursor.getString(cursor.getColumnIndex(SHORT_PINYIN));
			country = new Country(code, countryName, pinyin, shortPinyin);
			result.add(country);
		}
		cursor.close();
		db.close();
		Collections.sort(result, new CityComparator());
		return result;
	}

	/**
	 * a-z排序
	 */
	private class CityComparator implements Comparator<Country> {
		@Override
		public int compare(Country lhs, Country rhs) {
			String a = lhs.getPinyin().substring(0, 1);
			String b = rhs.getPinyin().substring(0, 1);
			return a.compareTo(b);
		}
	}

	public static class Country implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = -9171926034228959985L;
		public String id;
		public String code;
		public String country;
		
		public String pinyin;
		public String shortPinyin;

		public Country() {
			
		}

		public Country(String code, String country) {
			this.code = code;
			this.country = country;
//			this.initials = initials;
		}

		public Country(String code, String country, String pinyin,
				String shortPinyin) {
			this.code = code;
			this.country = country;
			this.pinyin = pinyin;
			this.shortPinyin = shortPinyin;
		}

		public String getPinyin() {
			return pinyin;
		}

		public void setPinyin(String pinyin) {
			this.pinyin = pinyin;
		}

		public String getShortPinyin() {
			return shortPinyin;
		}

		public void setShortPinyin(String shortPinyin) {
			this.shortPinyin = shortPinyin;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getCountry() {
			return country;
		}

		public void setCountry(String country) {
			this.country = country;
		}

	}
}
