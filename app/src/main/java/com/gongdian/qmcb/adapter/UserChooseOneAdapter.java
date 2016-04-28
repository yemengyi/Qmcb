package com.gongdian.qmcb.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.ab.view.sliding.AbSlidingButton;
import com.gongdian.qmcb.R;
import com.gongdian.qmcb.model.Users;

import java.util.List;


public class UserChooseOneAdapter extends BaseAdapter{

	private Context mContext;
    //列表展现的数据
    private List<Users> mList;

   /**
    * 构造方法
    * @param context
    * @param list 列表展现的数据
    */
    public UserChooseOneAdapter(Context context, List<Users> list){
    	 mContext = context;
    	 mList = list;
    }
    
    @Override
    public int getCount() {
    	if(mList==null){
    		return 0;
    	}
        return mList.size();
    }
    
    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position){
      return position;
    }
    
    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
    	  final ViewHolder holder;
          if(convertView == null){
	          //使用自定义的list_items作为Layout
	          convertView = LayoutInflater.from(mContext).inflate(R.layout.item_choose_list, parent, false);
	          //使用减少findView的次数
			  holder = new ViewHolder();
			  holder.itemsText = ((TextView) convertView.findViewById(R.id.itemText));
              holder.itemsCheck = (AbSlidingButton) convertView.findViewById(R.id.mSliderBtn);

              //设置标记
			  convertView.setTag(holder);
          }else{
        	  holder = (ViewHolder) convertView.getTag();
          }
          
          //获取该行数据
          final Users Users = (Users)mList.get(position);
          
          holder.itemsText.setText(Users.getUname());

        //设置开关显示所用的图片
        holder.itemsCheck.setImageResource(R.drawable.btn_bottom, R.drawable.btn_frame, R.drawable.btn_mask, R.drawable.btn_unpressed, R.drawable.btn_pressed);

        //设置开关的监听
        holder.itemsCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {

                if (arg1) {
                   Users.setChoose(true);

                    /**通过一个循环让,其他position设置为未选中*/
                    for(int i=0;i<mList.size();i++) {
                        if (mList.get(i).isChoose()&&i!=position) {
                            mList.get(i).setChoose(false);
                        }
                    }
                    notifyDataSetChanged();
                } else {
                   Users.setChoose(false);
                }
            }
        });

        if (Users.isChoose()) {
            holder.itemsCheck.setChecked(true);
        } else {
            holder.itemsCheck.setChecked(false);
        }

        return convertView;
    }


    /**
	 * ViewHolder类
	 */
	static class ViewHolder {
		TextView itemsText;
        AbSlidingButton itemsCheck;
	}
}
