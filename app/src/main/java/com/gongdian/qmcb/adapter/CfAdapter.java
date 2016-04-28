package com.gongdian.qmcb.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gongdian.qmcb.R;
import com.gongdian.qmcb.model.Cf1;
import com.gongdian.qmcb.model.Cf2;

import java.util.List;


/**
 * 名称：Cf1ListAdapter
 * 描述：文章对象自定义Adapter例子
 */
public class CfAdapter extends BaseAdapter{

	private Context mContext;
    //列表展现的数据
    private List<Object> mList;

   /**
    * 构造方法
    * @param context
    * @param list 列表展现的数据
    */
    public CfAdapter(Context context, List<Object> list){
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.cf_list, parent, false);
            //使用减少findView的次数
            holder = new ViewHolder();
            holder.title = ((TextView) convertView.findViewById(R.id.title)) ;
            holder.fen = ((TextView) convertView.findViewById(R.id.fen));
            holder.qian = ((TextView) convertView.findViewById(R.id.qian));
            //设置标记
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        //获取该行数据
        if(mList.get(position) instanceof Cf1){
            holder.title.setText(((Cf1)mList.get(position)).getTitle());
            holder.fen.setText(((Cf1)mList.get(position)).getFen());
            holder.qian.setText(((Cf1)mList.get(position)).getQian());
        }
        if(mList.get(position) instanceof Cf2){
            holder.title.setText(((Cf2)mList.get(position)).getTitle());
            holder.fen.setText(((Cf2)mList.get(position)).getFen());
            holder.qian.setText(((Cf2)mList.get(position)).getQian());
        }
          
          return convertView;
    }
    
    /**
	 * ViewHolder类
	 */
	static class ViewHolder {
        TextView title;
		TextView fen;
		TextView qian;
	}
}
