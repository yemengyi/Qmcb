package com.gongdian.qmcb.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ab.image.AbImageLoader;
import com.ab.util.AbFileUtil;
import com.ab.util.AbLogUtil;
import com.ab.util.AbStrUtil;
import com.ab.util.AbToastUtil;
import com.gongdian.qmcb.R;
import com.gongdian.qmcb.model.Users;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.File;
import java.util.List;


public class UsersListAdapter extends BaseAdapter {

    private Context mContext;
    //列表展现的数据
    private List<Users> mList;
    AbImageLoader abImageLoader = null;
    private File SAVE_DIR = null;

    /**
     * 构造方法
     *
     * @param context
     * @param list    列表展现的数据
     */
    public UsersListAdapter(Context context, List<Users> list) {
        mContext = context;
        mList = list;
        abImageLoader = AbImageLoader.getInstance(context);
        String saveDir = AbFileUtil.getImageDownloadDir(context);
        if (AbStrUtil.isEmpty(saveDir)) {
            AbToastUtil.showToast(context, "存储卡不存在");
        } else {
            SAVE_DIR = new File(saveDir);
        }
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.users_item_list, parent, false);
            //使用减少findView的次数
            holder = new ViewHolder();
            holder.head = ((RoundedImageView) convertView.findViewById(R.id.head));
            /**100为圆,<100为椭圆*/
            holder.head.setCornerRadius(100);
            holder.head.setOval(true);
            holder.department = ((TextView) convertView.findViewById(R.id.partment));
            holder.user_id = ((TextView) convertView.findViewById(R.id.user_id));
            holder.user_name = ((TextView) convertView.findViewById(R.id.user_name));
            holder.user_phone = ((TextView) convertView.findViewById(R.id.user_photo));
            //设置标记
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //获取该行数据
        Users Users = mList.get(position);
        /**圆角图片 没搞定*/
//        if (AbStrUtil.isEmpty(Users.getHeadurl())) {
//            holder.head.setImageResource(R.drawable.ic_sex_male);
//        }else {
//            String url = Users.getHeadurl();
//            int start = url.lastIndexOf("/");
//            String filename = SAVE_DIR + url.substring(start,url.length());
//            File file = new File(filename);
//            if(!file.exists()){
//                Bitmap bitmap = AbFileUtil.getBitmapFromURL(url, 60, 60);
//                AbFileUtil.writeBitmapToSD(filename, bitmap, true);
//            }
//            holder.head.setImageURI(Uri.fromFile(file));
//        }
        if (AbStrUtil.isEmpty(Users.getHeadurl())) {
            holder.head.setImageResource(R.drawable.ic_sex_male);
        }else {
            abImageLoader.display(holder.head, Users.getHeadurl());
        }
        AbLogUtil.d("xxxx","user list");

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
        RoundedImageView head;
        TextView department;
        TextView user_name;
        TextView user_phone;
        TextView user_id;
    }



}
