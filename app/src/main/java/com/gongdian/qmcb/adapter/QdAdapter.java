package com.gongdian.qmcb.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gongdian.qmcb.R;
import com.gongdian.qmcb.model.Qd;

import java.util.List;


public class QdAdapter extends BaseAdapter {

    private Context mContext;
    //列表展现的数据
    private List<Qd> mList;

    /**
     * 构造方法
     *
     * @param context
     * @param list    列表展现的数据
     */
    public QdAdapter(Context context, Activity activity, List<Qd> list) {
        mContext = context;
        mList = list;
    }

    @Override
    public int getCount() {
        if (mList == null) {
            return 0;
        }
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            //使用自定义的list_items作为Layout
            convertView = LayoutInflater.from(mContext).inflate(R.layout.qd_item_list, parent, false);
            //使用减少findView的次数
            holder = new ViewHolder();
            holder.nr = ((TextView) convertView.findViewById(R.id.nr));

            //设置标记
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //监听


        //获取该行数据
        final Qd qd = mList.get(position);
        holder.nr.setText(qd.getQdsj() + " " + qd.getAddress() + "("+qd.getLocationdescribe()+")");
        return convertView;
    }


    /**
     * ViewHolder类
     */
    static class ViewHolder {
        TextView nr;
    }

}
