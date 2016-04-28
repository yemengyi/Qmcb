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

import com.gongdian.qmcb.R;
import com.gongdian.qmcb.model.Project2;
import com.gongdian.qmcb.model.Project_dw2;
import com.gongdian.qmcb.model.Project_jd2;

import java.util.ArrayList;
import java.util.List;

/***
 * 数据源
 *
 * @author Administrator
 */
public class MyexpandableListAdapter extends BaseExpandableListAdapter{
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<Project2> groupList;
    private ArrayList<List<Project_dw2>> childList;

    public MyexpandableListAdapter(Context context, ArrayList<Project2> groupList, ArrayList<List<Project_dw2>> childList) {
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
            convertView = inflater.inflate(R.layout.expand_project_group,null);
            groupHolder.title = (TextView) convertView.findViewById(R.id.group_title);
            groupHolder.imageView = (ImageView) convertView.findViewById(R.id.image);
            convertView.setTag(groupHolder);
        } else {
            groupHolder = (GroupHolder) convertView.getTag();
        }
        Project2 project2 =  (Project2)getGroup(groupPosition);

        groupHolder.title.setText(project2.getTitle());

        if (isExpanded)// ture is Expanded or false is not isExpanded
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
            convertView = inflater.inflate(R.layout.expand_project_child, null);

            childHolder.child_title = (TextView) convertView.findViewById(R.id.child_title);
            childHolder.btn_202 = (TextView) convertView.findViewById(R.id.btn202);
            childHolder.btn_203 = (TextView) convertView.findViewById(R.id.btn203);
            childHolder.btn_204 = (TextView) convertView.findViewById(R.id.btn204);
            childHolder.btn_205 = (TextView) convertView.findViewById(R.id.btn205);
            childHolder.btn_206 = (TextView) convertView.findViewById(R.id.btn206);

            convertView.setTag(childHolder);
        } else {
            childHolder = (ChildHolder) convertView.getTag();
        }

        final List<Project_jd2> mProject_menu= ((Project_dw2)getChild(groupPosition,childPosition)).getProject_jd();
        Project2 project2 = groupList.get(groupPosition);


        childHolder.btn_202.setBackgroundResource(R.drawable.btn_red);
        childHolder.btn_203.setBackgroundResource(R.drawable.btn_red);
        childHolder.btn_204.setBackgroundResource(R.drawable.btn_red);
        childHolder.btn_205.setBackgroundResource(R.drawable.btn_red);
        childHolder.btn_206.setBackgroundResource(R.drawable.btn_red);

        String menu_id;
        if(mProject_menu!=null) {
            for (int i = 0; i < mProject_menu.size(); i++) {
                menu_id = mProject_menu.get(i).getMenu_id();
                switch (menu_id) {
                    case "202":
                        childHolder.btn_202.setBackgroundResource(R.drawable.btn_green);
                        break;
                    case "203":
                        childHolder.btn_203.setBackgroundResource(R.drawable.btn_green);
                        break;
                    case "204":
                        childHolder.btn_204.setBackgroundResource(R.drawable.btn_green);
                        break;
                    case "205":
                        childHolder.btn_205.setBackgroundResource(R.drawable.btn_green);
                        break;
                    case "206":
                        childHolder.btn_206.setBackgroundResource(R.drawable.btn_green);
                        break;
                }
            }
        }
        String s = ((Project_dw2) getChild(groupPosition,childPosition)).getTitle() + "\n许可部门:"+project2.getXkdw() + "\n到岗到位:" + project2.getRy();

        childHolder.child_title.setText(s);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    static class GroupHolder {
        TextView title;
        ImageView imageView;
    }

    static class ChildHolder {
        TextView child_title;
        TextView btn_202;
        TextView btn_203;
        TextView btn_204;
        TextView btn_205;
        TextView btn_206;
    }
}