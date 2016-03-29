package com.example.countrydemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import com.example.countrydemo.adapter.CityListAdapter;
import com.example.countrydemo.utils.DBCountryManager;
import com.example.countrydemo.utils.DBCountryManager.Country;
import com.example.countrydemo.view.MyLetterListView;

/**
 * 选择国家
 */
public class CountryListActivity extends FragmentActivity implements
		View.OnClickListener {
	
	public static final String COUNTRY_NAME = "countryName";
	public static final String COUNTRY_CODE = "countryCode";
	
	private ListView mListView;
	private MyLetterListView mLetterBar;
	private EditText searchBox;
	private ImageView clearBtn;
	private ImageView backBtn;
	private ViewGroup emptyView;

	private CityListAdapter mCityAdapter;
	
	private List<Country> mAllCities;
	private DBCountryManager dbManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_country_list);
		initData();
		initView();
	}

	private void initData() {
		dbManager = new DBCountryManager(this);
		dbManager.copyDBFile();
		mAllCities = dbManager.getAllCountry();
		mCityAdapter = new CityListAdapter(this, mAllCities);
		mCityAdapter
				.setOnCountryClickListener(new CityListAdapter.OnCountryClickListener() {
					@Override
					public void onCountryClick(String name,String code) {
						back(name,code);
					}
				});
	}

	private void initView() {
		mListView = (ListView) findViewById(R.id.city_listview_all_city);
		mListView.setAdapter(mCityAdapter);

		TextView overlay = (TextView) findViewById(R.id.city_tv_letter_overlay);
		mLetterBar = (MyLetterListView) findViewById(R.id.city_side_letter_bar);
		mLetterBar.setOverlay(overlay);
		mLetterBar
				.setOnLetterChangedListener(new MyLetterListView.OnLetterChangedListener() {
					@Override
					public void onLetterChanged(String letter) {
						int position = mCityAdapter.getLetterPosition(letter);
						mListView.setSelection(position);
					}
				});

		searchBox = (EditText) findViewById(R.id.city_et_search);
		searchBox.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				String keyword = s.toString();
				if (TextUtils.isEmpty(keyword)) {
					clearBtn.setVisibility(View.GONE);
					emptyView.setVisibility(View.GONE);
					
					mListView.setVisibility(View.VISIBLE);
					List<Country> result = dbManager.getAllCountry();
					mCityAdapter.updateCountry(result);
				} else {
					List<Country> result = dbManager.searchCountry(keyword);
					if (result == null || result.isEmpty()) {
//						mCityAdapter.updateCountry(result);
						emptyView.setVisibility(View.VISIBLE);
						mListView.setVisibility(View.GONE);
					} else {
						emptyView.setVisibility(View.GONE);
						mListView.setVisibility(View.VISIBLE);
						mCityAdapter.updateCountry(result);
					}
					
				}
			}
		});

		emptyView = (ViewGroup) findViewById(R.id.city_view_empty);
		clearBtn = (ImageView) findViewById(R.id.city_iv_search_clear);

		clearBtn.setOnClickListener(this);

	}

	private void back(String countryName,String code) {
//		hideKeyboard();
		Intent intent = getIntent();
		intent.putExtra(COUNTRY_NAME, countryName);
		intent.putExtra(COUNTRY_CODE, code);
		setResult(RESULT_OK, intent);
		finish();
		
//		Toast.makeText(this, countryName, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.city_iv_search_clear:
			searchBox.setText("");
			clearBtn.setVisibility(View.GONE);
			emptyView.setVisibility(View.GONE);
//			hideKeyboard();
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}



}
