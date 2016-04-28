package com.gongdian.qmcb.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gongdian.qmcb.R;
import com.gongdian.qmcb.model.Menu;

import java.util.List;


public class MenuListAdapter extends BaseAdapter {

    private Context mContext;
    //列表展现的数据
    private List<Menu> mList;
    //图片下载器
    // private AbImageLoader mAbImageLoader = null;

    /**
     * 构造方法
     *
     * @param context
     * @param list    列表展现的数据
     */
    public MenuListAdapter(Context context, List<Menu> list) {
        mContext = context;
        mList = list;
        //图片下载器
        //mAbImageLoader = AbImageLoader.getInstance(context);
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.menu_item_list, parent, false);
            //使用减少findView的次数
            holder = new ViewHolder();
            holder.itemsText = ((TextView) convertView.findViewById(R.id.itemText));
            holder.taskText = ((TextView) convertView.findViewById(R.id.task));
            //设置标记
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //获取该行数据
        Menu menu = mList.get(position);

//        /**根据menu,替换左侧图片*/
//        Drawable drawable = null;
//        switch (menu.getMenu()){
//            case Constant.MENU1:
//                drawable= mContext.getResources().getDrawable(R.drawable.icon_qunliao);
//                break;
//            case Constant.MENU2:
//                break;
//            case Constant.MENU3:
//                break;
//            case Constant.MENU4:
//                break;
//            case Constant.MENU5:
//                break;
//            case Constant.MENU6:
//                break;
//            case Constant.MENU7:
//                break;
//            case Constant.MENU8:
//                break;
//            default:
//                drawable= mContext.getResources().getDrawable(R.drawable.icon_me_collect);
//                break;
//        }
//
//        /**这一步必须要做,否则不会显示.*/
////        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
//        holder.itemsText.setCompoundDrawables(drawable, null, null, null);

        //根据imageUrl下载图片
        holder.itemsText.setText(menu.getMenu());
        int task = 0 ;
        task = menu.getTask();
        if (task>0) {
            holder.taskText.setVisibility(View.VISIBLE);
            holder.taskText.setText(String.valueOf(task));
        }else {
            holder.taskText.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }


    /**
     * ViewHolder类
     */
    static class ViewHolder {
        TextView itemsText;
        TextView taskText;
    }
}
