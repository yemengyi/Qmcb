package com.gongdian.qmcb.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.gongdian.qmcb.R;
import com.gongdian.qmcb.model.Dxtz;

import java.util.List;


public class DxtzAdapter extends BaseAdapter {

    private Context mContext;
    //列表展现的数据
    private List<Dxtz> mList;

    /**
     * 构造方法
     *
     * @param context
     * @param list    列表展现的数据
     */
    public DxtzAdapter(Context context, List<Dxtz> list) {
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            //使用自定义的list_items作为Layout
            convertView = LayoutInflater.from(mContext).inflate(R.layout.dxtz_item_list, parent, false);
            //使用减少findView的次数
            holder = new ViewHolder();
            holder.itemsText = ((TextView) convertView.findViewById(R.id.itemText));
            holder.dxnr = ((EditText) convertView.findViewById(R.id.dxnr));
            holder.itemsCheck = (CheckBox) convertView.findViewById(R.id.mSliderBtn);
            //设置标记
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //获取该行数据
        final Dxtz dxtz = mList.get(position);

        holder.itemsText.setText("至:" + dxtz.getPname() + " " + dxtz.getXm());
        holder.dxnr.setText(dxtz.getDxnr());

        //设置开关的监听
        holder.itemsCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                if (arg1) {
                    dxtz.setChoose(true);
                } else {
                    dxtz.setChoose(false);
                }
            }
        });

        if (dxtz.isChoose()) {
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
        EditText dxnr;
        CheckBox itemsCheck;
    }
}
