package com.gongdian.qmcb.activity.profile;

import android.app.Fragment;
import android.os.Bundle;
import android.view.View;

import com.ab.activity.AbActivity;
import com.ab.view.sliding.AbSlidingTabView;
import com.ab.view.titlebar.AbTitleBar;
import com.gongdian.qmcb.R;
import com.gongdian.qmcb.fragment.FragmentPublicity;
import com.gongdian.qmcb.utils.Constant;
import com.gongdian.qmcb.utils.MyApplication;

import java.util.ArrayList;
import java.util.List;

public class PublicityActivity extends AbActivity {
	
	private MyApplication application;
	private AbSlidingTabView mAbSlidingTabView;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.tab_top);
		application = (MyApplication) abApplication;

		AbTitleBar mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setTitleText("违规公示");
		mAbTitleBar.setLogo(R.drawable.title_back_n);
		mAbTitleBar.setTitleBarBackground(R.color.colorPrimaryDark);
		mAbTitleBar.setTitleTextMargin(20, 0, 0, 0);
		mAbTitleBar.setLogoOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
				overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
			}
		});
		
        //AbSlidingTabView2这个类包含了另外的一种效果，和AbSlidingTabView是不同的
		mAbSlidingTabView = (AbSlidingTabView) findViewById(R.id.mAbSlidingTabView);
		
		//如果里面的页面列表不能下载原因：
		//Fragment里面用的AbTaskQueue,由于有多个tab，顺序下载有延迟，还没下载好就被缓存了。改成用AbTaskPool，就ok了。
		//或者setOffscreenPageLimit(0)
		
		//缓存数量
		mAbSlidingTabView.getViewPager().setOffscreenPageLimit(2);
		
		//禁止滑动
		/*mAbSlidingTabView.getViewPager().setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
			
		});*/

		FragmentPublicity page1 = FragmentPublicity.newInstance(Constant.Cf1);
		FragmentPublicity page2 = FragmentPublicity.newInstance(Constant.Cf2);
		
		List<Fragment> mFragments = new ArrayList<>();
		mFragments.add(page1);
		mFragments.add(page2);

		List<String> tabTexts = new ArrayList<>();
		tabTexts.add("施工单位");
		tabTexts.add("工程负责人");

		//设置样式
		mAbSlidingTabView.setTabTextColor(R.color.gray);
		mAbSlidingTabView.setTabSelectColor(R.color.gray_dark);
		mAbSlidingTabView.setTabBackgroundResource(R.drawable.tab_bg);
		mAbSlidingTabView.setTabLayoutBackgroundResource(R.drawable.slide_top);
		//演示增加一组
		mAbSlidingTabView.addItemViews(tabTexts, mFragments);
		
		mAbSlidingTabView.setTabPadding(20, 8, 20, 8);
		
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
	}

}
