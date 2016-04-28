package com.gongdian.qmcb.adapter;

/**
 * Created by qian-pc on 2/19/16.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ab.util.AbStrUtil;
import com.gongdian.qmcb.R;
import com.gongdian.qmcb.model.Project2;
import com.gongdian.qmcb.model.Project_jd2;

import java.util.ArrayList;
import java.util.List;

/***
 * 数据源
 *
 * @author Administrator
 */
public class SpListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<Project2> groupList;
    private ArrayList<List<Project_jd2>> childList;

    public SpListAdapter(Context context, ArrayList<Project2> groupList, ArrayList<List<Project_jd2>> childList) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.groupList = groupList;
        this.childList = childList;
    }

    // 返回父列表个数
    @Override
    public int getGroupCount() {
        return groupList.size();
    }

    // 返回子列表个数
    @Override
    public int getChildrenCount(int groupPosition) {
        return childList.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return childList.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {

        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupHolder groupHolder = null;
        if (convertView == null) {
            groupHolder = new GroupHolder();
            convertView = inflater.inflate(R.layout.expand_project_group, parent,false);
            groupHolder.title = (TextView) convertView.findViewById(R.id.group_title);
            groupHolder.spNum = (TextView) convertView.findViewById(R.id.spsl);
            groupHolder.imageView = (ImageView) convertView.findViewById(R.id.image);
            convertView.setTag(groupHolder);
        } else {
            groupHolder = (GroupHolder) convertView.getTag();
        }


        Project2 project2 = (Project2) getGroup(groupPosition);

        int spNum = project2.getSPnumber();

        if (spNum > 0) {
            groupHolder.spNum.setVisibility(View.VISIBLE);
            groupHolder.spNum.setText(spNum + "");
        } else {
            groupHolder.spNum.setVisibility(View.INVISIBLE);
        }

        groupHolder.title.setText(project2.getTitle());

        if (isExpanded)
            groupHolder.imageView.setImageResource(R.drawable.expanded);
        else
            groupHolder.imageView.setImageResource(R.drawable.collapse);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        ChildHolder childHolder = null;
        if (convertView == null) {
            childHolder = new ChildHolder();
            convertView = inflater.inflate(R.layout.expand_project_sp_child, null);
            childHolder.child_title = (TextView) convertView.findViewById(R.id.child_title);
            childHolder.sfgs = (TextView) convertView.findViewById(R.id.sfgs);
            childHolder.sp = (TextView) convertView.findViewById(R.id.sp);
            childHolder.qsp = (TextView) convertView.findViewById(R.id.qsp);
            convertView.setTag(childHolder);
        } else {
            childHolder = (ChildHolder) convertView.getTag();
        }

        childHolder.child_title.setText(((Project_jd2) getChild(groupPosition, childPosition)).getTitle());
        String sfgs = ((Project_jd2) getChild(groupPosition, childPosition)).getSfgs();
        String yxbz = ((Project_jd2) getChild(groupPosition, childPosition)).getYxbz();

        switch (yxbz) {
            case "0":
                childHolder.qsp.setVisibility(View.VISIBLE);
                childHolder.sp.setVisibility(View.INVISIBLE);
                childHolder.sfgs.setVisibility(View.INVISIBLE);
                break;
            case "1":
                childHolder.qsp.setVisibility(View.INVISIBLE);
                childHolder.sp.setVisibility(View.VISIBLE);
                if (AbStrUtil.isEquals(sfgs, "1")) {
                    childHolder.sfgs.setVisibility(View.VISIBLE);
                } else {
                    childHolder.sfgs.setVisibility(View.INVISIBLE);
                }
                break;

            case "2":
                childHolder.qsp.setVisibility(View.INVISIBLE);
                childHolder.sp.setText("废");
                childHolder.sp.setVisibility(View.VISIBLE);
                childHolder.sfgs.setVisibility(View.INVISIBLE);
                break;
        }


        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    static class GroupHolder {
        TextView title;
        TextView spNum;
        ImageView imageView;
    }

    static class ChildHolder {
        TextView child_title;
        TextView sfgs;
        TextView sp;
        TextView qsp;
    }
}