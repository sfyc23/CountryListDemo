package com.example.countrydemo.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import com.example.countrydemo.R;
import com.example.countrydemo.utils.DBCountryManager.Country;

public class CityListAdapter extends BaseAdapter {
	private static final int VIEW_TYPE_COUNT = 2;

	private Context mContext;
	private LayoutInflater inflater;
	private List<Country> mCities;
	private HashMap<String, Integer> letterIndexes;
	private String[] sections;
	private OnCountryClickListener onCountryClickListener;

	public CityListAdapter(Context mContext, List<Country> mCities) {
		this.mContext = mContext;
		this.mCities = mCities;
		this.inflater = LayoutInflater.from(mContext);
		if (mCities == null) {
			mCities = new ArrayList<Country>();
		}
		int size = mCities.size();
		letterIndexes = new HashMap<String, Integer>();
		sections = new String[size];
		for (int index = 0; index < size; index++) {
			// 当前城市拼音首字母
			String currentLetter = getFirstLetter(mCities.get(index)
					.getPinyin().substring(0, 1));
			// 上个首字母，如果不存在设为""
			String previousLetter = index >= 1 ? getFirstLetter(mCities.get(
					index - 1).getPinyin().substring(0,1)) : "";
			if (!TextUtils.equals(currentLetter, previousLetter)) {
				letterIndexes.put(currentLetter, index);
				sections[index] = currentLetter;
			}
		}
	}

	/**
	 * 获取字母索引的位置
	 */
	public int getLetterPosition(String letter) {
		Integer integer = letterIndexes.get(letter);
		return integer == null ? -1 : integer;
	}

	// @Override
	// public int getViewTypeCount() {
	// return VIEW_TYPE_COUNT;
	// }

	// @Override
	// public int getItemViewType(int position) {
	// return position < VIEW_TYPE_COUNT - 1 ? position : VIEW_TYPE_COUNT - 1;
	// }

	@Override
	public int getCount() {
		return mCities == null ? 0 : mCities.size();
	}

	@Override
	public Country getItem(int position) {
		return mCities == null ? null : mCities.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View view, ViewGroup parent) {
		CountryViewHolder holder;
		if (view == null) {
			view = inflater
					.inflate(R.layout.list_item_country_list, parent, false);
			holder = new CountryViewHolder();
			holder.letterTv = (TextView) view
					.findViewById(R.id.item_country_tv_letter);
			holder.nameTv = (TextView) view
					.findViewById(R.id.item_country_tv_name);
			holder.codeTv = (TextView) view
					.findViewById(R.id.item_country_tv_code);
			holder.selectView = view
					.findViewById(R.id.item_country_ll);
			view.setTag(holder);
		} else {
			holder = (CountryViewHolder) view.getTag();
		}
		Country country = mCities.get(position);
		
		final String countryName = country.getCountry();
		final String code = country.getCode();
		
		holder.nameTv.setText(countryName);
		holder.codeTv.setText("+"+code);
		
		String currentLetter = getFirstLetter(country.getPinyin());
		String previousLetter = position >= 1 ? getFirstLetter(mCities.get(
				position - 1).getPinyin()) : "";
		if (!TextUtils.equals(currentLetter, previousLetter)) {
			holder.letterTv.setVisibility(View.VISIBLE);
			holder.letterTv.setText(currentLetter);
		} else {
			holder.letterTv.setVisibility(View.GONE);
		}
		holder.selectView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (onCountryClickListener != null) {
					onCountryClickListener.onCountryClick(countryName,code);
				}
			}
		});
		return view;
	}

	public static class CountryViewHolder {
		TextView letterTv;
		TextView nameTv;
		TextView codeTv;
		View selectView;
	}

	public void setOnCountryClickListener(OnCountryClickListener listener) {
		this.onCountryClickListener = listener;
	}

	public interface OnCountryClickListener {
		void onCountryClick(String countryName,String code);
	}

	public void updateCountry(List<Country> cities) {
		mCities = cities;
		notifyDataSetChanged();
	}

	public static String getFirstLetter(String pinyin) {
		// if (TextUtils.isEmpty(pinyin))
		// return "热门";
		// String c = pinyin.substring(0, 1);
		// Pattern pattern = Pattern.compile("^[A-Za-z]+$");
		// if (pattern.matcher(c).matches()) {
		// return c.toUpperCase();
		// } else if ("0".equals(c)) {
		// return "热门";
		// }
		// return "热门";
		if (TextUtils.isEmpty(pinyin))
			return "";
		String c = pinyin.substring(0, 1);
		Pattern pattern = Pattern.compile("^[A-Za-z]+$");
		if (pattern.matcher(c).matches()) {
			return c.toUpperCase();
		} else if ("0".equals(c)) {
			return "";
		}
		return "";
	}
}
