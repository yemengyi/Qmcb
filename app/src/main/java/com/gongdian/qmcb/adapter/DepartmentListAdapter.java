package com.gongdian.qmcb.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gongdian.qmcb.R;
import com.gongdian.qmcb.model.Department;

import java.util.List;


public class DepartmentListAdapter extends BaseAdapter{

	private Context mContext;
    //列表展现的数据
    private List<Department> mList;

   /**
    * 构造方法
    * @param context
    * @param list 列表展现的数据
    */
    public DepartmentListAdapter(Context context, List<Department> list){
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
	          convertView = LayoutInflater.from(mContext).inflate(R.layout.department_item_list, parent, false);
	          //使用减少findView的次数
			  holder = new ViewHolder();
			  holder.itemsText = ((TextView) convertView.findViewById(R.id.itemText));
			  holder.fzrxmText = ((TextView) convertView.findViewById(R.id.fzrxm));
			  //设置标记
			  convertView.setTag(holder);
          }else{
        	  holder = (ViewHolder) convertView.getTag();
          }
          
          //获取该行数据
          Department department = mList.get(position);
          
          holder.itemsText.setText(department.getPname());
          holder.fzrxmText.setText(department.getFzrxm());
          return convertView;
    }


    /**
	 * ViewHolder类
	 */
	static class ViewHolder {
		TextView itemsText;
		TextView fzrxmText;
	}
}
