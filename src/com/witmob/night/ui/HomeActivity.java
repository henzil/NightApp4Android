package com.witmob.night.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.witmob.night.R;
import com.witmob.night.ui.fragment.BaseNightFragment;
import com.witmob.night.ui.util.FragmentUtil;

public class HomeActivity extends BaseNightActivity {

	private View setting, fav, chartList;

	private int currentTag = -1;
	
	private Fragment currentFragment = null;

	@Override
	protected void initBaseData(Bundle savedInstanceState) {
		super.initBaseData(savedInstanceState);
		if (savedInstanceState != null) {
			currentTag = savedInstanceState.getInt("currentTag", -1);
		}
	}
	
	@Override
	protected void initData() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void initViews() {
		setContentView(R.layout.activity_home);
		setting = findViewById(R.id.setting);
		fav = findViewById(R.id.fav);
		chartList = findViewById(R.id.chartList);
	}

	@Override
	protected void initWidgetActions() {
		setting.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				changeFragment(FragmentUtil.SETTING_FRAGMENT_TYPE);
			}
		});

		fav.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				changeFragment(FragmentUtil.FAV_FRAGMENT_TYPE);
			}
		});

		chartList.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				changeFragment(FragmentUtil.CHART_LIST_FRAGMENT_TYPE);
			}
		});
		initFragment();
	}

	private void initFragment() {
		FragmentManager manager = getFragmentManager();
		// Clear all back stack.
		int backStackCount = manager.getBackStackEntryCount();
		for (int i = 0; i < backStackCount; i++) {
			// Get the back stack fragment id.
			int backStackId = manager.getBackStackEntryAt(i).getId();
			manager.popBackStack(backStackId, FragmentManager.POP_BACK_STACK_INCLUSIVE);
		} /* end of for */
		// Log.e("tag", "manager = " +
		// manager.findFragmentById(R.id.contentLayout));
		if (manager.findFragmentById(R.id.content) != null) {
			FragmentTransaction removeFt = manager.beginTransaction();
			// Log.e("tag", "执行一次删除");
			removeFt.remove(manager.findFragmentById(R.id.content));
			removeFt.commit();// 提交
		}
		changeFragment(currentTag);
	}

	private void changeFragment(int index) {
		FragmentManager manager = getFragmentManager();
		FragmentTransaction ft = manager.beginTransaction();
		if (currentTag != index) {
			BaseNightFragment subViewFragment = null;
			if (manager.findFragmentByTag("" + index) != null) {
				subViewFragment = (BaseNightFragment) manager.findFragmentByTag("" + index);
				ft.hide(currentFragment).show(subViewFragment).commit();
				currentTag = index;
				currentFragment = subViewFragment;
			} else {
				subViewFragment = FragmentUtil.fragmentByIndex(index);
				if (subViewFragment != null) {
					Bundle bundle = new Bundle();
					subViewFragment.setArguments(bundle);
					ft.hide(currentFragment).add(R.id.content, subViewFragment, "" + index).commit();
					currentTag = index;
					currentFragment = subViewFragment;
				}
			}
		} else {
			BaseNightFragment subViewFragment = null;
			subViewFragment = FragmentUtil.fragmentByIndex(index);
			if (subViewFragment != null) {
				Bundle bundle = new Bundle();
				subViewFragment.setArguments(bundle);
				ft.replace(R.id.content, subViewFragment, "" + index);
				ft.commit();// 提交
				currentTag = index;
				currentFragment = subViewFragment;
			}
		}
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("currentTag", currentTag);
	}

}
