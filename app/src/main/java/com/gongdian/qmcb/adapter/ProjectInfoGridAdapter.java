package com.gongdian.qmcb.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.ab.cache.image.AbImageBaseCache;
import com.ab.image.AbImageLoader;
import com.ab.util.AbFileUtil;
import com.ab.util.AbImageUtil;
import com.ab.util.AbStrUtil;
import com.gongdian.qmcb.R;
import com.gongdian.qmcb.model.Project_photo;

import java.io.File;
import java.util.List;


public class ProjectInfoGridAdapter extends BaseAdapter {

    private Context mContext;
    //列表展现的数据
    private List<Project_photo> mList;
    private AbImageLoader mAbImageLoader = null;
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
    public ProjectInfoGridAdapter(Context context, List<Project_photo> list, int width, int height) {
        mContext = context;
        mList = list;
        mAbImageLoader = new AbImageLoader(mContext);
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

    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        if(convertView == null){
            holder = new ViewHolder();
            LinearLayout mLinearLayout = new LinearLayout(mContext);
            RelativeLayout mRelativeLayout = new RelativeLayout(mContext);
            ImageView mImageView1 = new ImageView(mContext);
            mImageView1.setScaleType(ImageView.ScaleType.FIT_CENTER);
            holder.mImageView1 = mImageView1;
            LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            lp1.gravity = Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL;
            RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(mWidth,mHeight);
            lp2.addRule(RelativeLayout.CENTER_HORIZONTAL,RelativeLayout.TRUE);
            lp2.addRule(RelativeLayout.CENTER_VERTICAL,RelativeLayout.TRUE);
            mRelativeLayout.addView(mImageView1,lp2);
            mLinearLayout.addView(mRelativeLayout,lp1);

            convertView = mLinearLayout;
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.mImageView1.setImageBitmap(null);

        String imagePath = mList.get(position).getUrl();


        if(!AbStrUtil.isEmpty(imagePath)){
            //从缓存中获取图片，很重要否则会导致页面闪动
            Bitmap bitmap = AbImageBaseCache.getInstance().getBitmap(imagePath);
            //缓存中没有则从网络和SD卡获取
            if(bitmap == null){
                holder.mImageView1.setImageResource(R.drawable.image_loading);
                if(imagePath.indexOf("http://")!=-1){
                    //图片的下载
                    mAbImageLoader.display(holder.mImageView1,imagePath,this.mWidth,this.mHeight);

                }else if(imagePath.indexOf("/")==-1){
                    //在资源文件里索引图片
                    try {
                        int res  = Integer.parseInt(imagePath);
                        holder.mImageView1.setImageDrawable(mContext.getResources().getDrawable(res));
                    } catch (Exception e) {
                        holder.mImageView1.setImageResource(R.drawable.image_error);
                    }
                }else{
                    Bitmap mBitmap = AbFileUtil.getBitmapFromSD(new File(imagePath), AbImageUtil.SCALEIMG, mWidth, mHeight);
                    if(mBitmap!=null){
                        holder.mImageView1.setImageBitmap(mBitmap);
                    }else{
                        // 无图片时显示
                        holder.mImageView1.setImageResource(R.drawable.image_empty);
                    }
                }
            }else{
                //直接显示
                holder.mImageView1.setImageBitmap(bitmap);
            }
        }else{
            // 无图片时显示
            holder.mImageView1.setImageResource(R.drawable.image_empty);
        }

        holder.mImageView1.setAdjustViewBounds(true); //是否保持宽高比。
        return convertView;
    }


    /**
     * ViewHolder类
     */
    static class ViewHolder {
        ImageView mImageView1;
    }
}
