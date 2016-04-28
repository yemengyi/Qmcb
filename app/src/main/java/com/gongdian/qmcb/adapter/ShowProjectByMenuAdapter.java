package com.gongdian.qmcb.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ab.util.AbStrUtil;
import com.gongdian.qmcb.R;
import com.gongdian.qmcb.model.Project;

import java.util.List;


public class ShowProjectByMenuAdapter extends BaseAdapter {

    private Context mContext;
    //列表展现的数据
    private List<Project> mList;

    /**
     * 构造方法
     *
     * @param context
     * @param list    列表展现的数据
     */
    public ShowProjectByMenuAdapter(Context context, List<Project> list) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.project_item_list, parent, false);
            //使用减少findView的次数
            holder = new ViewHolder();
            holder.pro_id = ((TextView) convertView.findViewById(R.id.pro_id));
            holder.nr = ((TextView) convertView.findViewById(R.id.nr));
            holder.mc = ((TextView) convertView.findViewById(R.id.mc));
            holder.kssj = ((TextView) convertView.findViewById(R.id.kssj));
            holder.dz = ((TextView) convertView.findViewById(R.id.dz));
            holder.createuername = ((TextView) convertView.findViewById(R.id.createuername));
            holder.dw = ((TextView) convertView.findViewById(R.id.dw));
            holder.ry = ((TextView) convertView.findViewById(R.id.ry));
            holder.xk = ((TextView) convertView.findViewById(R.id.xk));
            //设置标记
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //获取该行数据
        Project mProject = mList.get(position);

        holder.pro_id.setText(mProject.getId() + " (" + mProject.getYf() + ")");
        holder.nr.setText(mProject.getNr());
        holder.mc.setText(mProject.getMc());
        String jssj = mProject.getJssj();
        String kssj = mProject.getKssj();
        String s = kssj.substring(kssj.indexOf("-") + 1) + " ~ " + jssj.substring(jssj.indexOf("-") + 1);
        holder.kssj.setText(s);
        holder.dz.setText(mProject.getDz());
        String cn = mProject.getCreateusername() + " " +  mProject.getCreatetime();
        holder.createuername.setText(cn);
        holder.dw.setText(mProject.getDw());
        holder.ry.setText(mProject.getRy());
        holder.xk.setText(mProject.getXkdw());
        if (!AbStrUtil.isEmpty(mProject.getFzrxm())) {
            holder.dw.setText(mProject.getDw() + " (" + mProject.getFzrxm() + ")");
        } else {
            holder.dw.setText(mProject.getDw() + " (" + mProject.getFzrs() + ")" );
        }
        return convertView;
    }


    /**
     * ViewHolder类
     */
    static class ViewHolder {
        TextView pro_id;
        TextView nr;
        TextView mc;
        TextView kssj;
        TextView dz;
        TextView createuername;
        TextView dw;
        TextView ry;
        TextView xk;
    }
}
