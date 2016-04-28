package com.gongdian.qmcb.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.gongdian.qmcb.R;
import com.gongdian.qmcb.activity.photo.ShowPhotoActivity;
import com.gongdian.qmcb.model.Project_jd2;
import com.gongdian.qmcb.model.Project_photo;

import java.util.ArrayList;
import java.util.List;


public class ProjectInfoAdapter extends BaseAdapter {

    private Context mContext;
    //列表展现的数据
    private List<Project_jd2> mList;
    /** The m width. */
    private int mWidth;
    /** The m height. */
    private int mHeight;

    /**
     * 构造方法
     *
     * @param context
     * @param list    列表展现的数据
     */
    public ProjectInfoAdapter(Context context, List<Project_jd2> list, int width, int height) {
        mContext = context;
        mList = list;
        this.mWidth = width;
        this.mHeight = height;
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.project_info_list, parent, false);
            //使用减少findView的次数
            holder = new ViewHolder();
            holder.title = ((TextView) convertView.findViewById(R.id.title));
            holder.gridview = ((GridView) convertView.findViewById(R.id.myGrid));
            holder.projectInfoGridAdapter = new ProjectInfoGridAdapter(mContext,holder.project_photo,mWidth,mHeight);
            holder.gridview.setAdapter(holder.projectInfoGridAdapter);
            holder.gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position2, long id) {
                   Project_photo project_photo = holder.project_photo.get(position2);
                    Intent intent = new Intent();
                    intent.setClass(mContext, ShowPhotoActivity.class);
                    intent.putExtra("filepath", project_photo.getUrl());
                    intent.putExtra("info", project_photo.getInfo());
                    mContext.startActivity(intent);
                }
            });

            //设置标记
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //获取该行数据
        Project_jd2 mProject = mList.get(position);
        holder.title.setText(mProject.getTitle());
        List<Project_photo> project_photo = mProject.getProject_photo();

        holder.project_photo.clear();
        holder.project_photo.addAll(project_photo);
        holder.projectInfoGridAdapter.notifyDataSetChanged();
//        projectInfoGridAdapter = new ProjectInfoGridAdapter(mContext,project_photo,mWidth,mHeight);
//        holder.gridview.setAdapter(projectInfoGridAdapter);
//        projectInfoGridAdapter.notifyDataSetChanged();
        return convertView;
    }


    /**
     * ViewHolder类
     */
     class ViewHolder {
        TextView title;
        GridView gridview;
        ProjectInfoGridAdapter projectInfoGridAdapter;
        List<Project_photo> project_photo = new ArrayList<>();
    }
}
