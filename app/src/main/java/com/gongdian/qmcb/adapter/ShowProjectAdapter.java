package com.gongdian.qmcb.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gongdian.qmcb.R;
import com.gongdian.qmcb.model.Users;

import java.util.List;


public class ShowProjectAdapter extends BaseAdapter{

	private Context mContext;
    //列表展现的数据
    private List<Users> mList;

   /**
    * 构造方法
    * @param context
    * @param list 列表展现的数据
    */
    public ShowProjectAdapter(Context context, List<Users> list){
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
    public View getView(int position, View convertView, ViewGroup parent){
    	  final ViewHolder holder;
          if(convertView == null){
	          //使用自定义的list_items作为Layout
	          convertView = LayoutInflater.from(mContext).inflate(R.layout.users_item_list, parent, false);
	          //使用减少findView的次数
			  holder = new ViewHolder();
              holder.department = ((TextView) convertView.findViewById(R.id.partment));
			  holder.user_id = ((TextView) convertView.findViewById(R.id.user_id));
			  holder.user_name = ((TextView) convertView.findViewById(R.id.user_name));
			  holder.user_phone = ((TextView) convertView.findViewById(R.id.user_photo));
			  //设置标记
			  convertView.setTag(holder);
          }else{
        	  holder = (ViewHolder) convertView.getTag();
          }
          
          //获取该行数据
          Users Users = (Users)mList.get(position);
          holder.department.setText(Users.getPname());
          holder.user_id.setText(Users.getUids());
          holder.user_name.setText(Users.getUname());
          holder.user_phone.setText(Users.getPcode());
          return convertView;
    }


    /**
	 * ViewHolder类
	 */
	static class ViewHolder {
        TextView department;
		TextView user_name;
		TextView user_phone;
		TextView user_id;
	}
}
