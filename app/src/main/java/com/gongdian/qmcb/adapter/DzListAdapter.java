package com.gongdian.qmcb.adapter;

import android.content.Context;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.ab.model.AbResult;
import com.ab.soap.AbSoapParams;
import com.ab.util.AbJsonUtil;
import com.ab.util.AbStrUtil;
import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.gongdian.qmcb.R;
import com.gongdian.qmcb.activity.project.AddProjectActivity;
import com.gongdian.qmcb.model.Project_dw;
import com.gongdian.qmcb.model.Users;
import com.gongdian.qmcb.model.UsersListResult;
import com.gongdian.qmcb.utils.Constant;
import com.gongdian.qmcb.utils.MsgUtil;
import com.gongdian.qmcb.utils.WebServiceUntils;

import java.util.List;


/**
 * 名称：Project_dwListAdapter
 * 描述：文章对象自定义Adapter例子
 */
public class DzListAdapter extends BaseAdapter {

    private Context mContext;
    //列表展现的数据
    private List<Project_dw> mList;

    /**
     * 构造方法
     *
     * @param context
     * @param list    列表展现的数据
     */
    public DzListAdapter(Context context, List<Project_dw> list) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.dz_item_list, parent, false);
            holder = new ViewHolder();
            holder.dwText = ((TextView) convertView.findViewById(R.id.dw));
            holder.fzrText = ((EditText) convertView.findViewById(R.id.fzr));
            holder.fzrxmText = ((TextView) convertView.findViewById(R.id.fzrxm));
            holder.dzText = ((EditText) convertView.findViewById(R.id.dz));
            //设置标记
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //获取该行数据
        final Project_dw mProject_dw = mList.get(position);
        holder.dwText.setText(mProject_dw.getPname());
        holder.dzText.setText(mProject_dw.getDz());
        holder.fzrText.setText(mProject_dw.getFzr());
        holder.fzrxmText.setText(mProject_dw.getFzrxm());

        holder.fzrxmText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.fzrxmText.setInputType(InputType.TYPE_NULL);//关闭软键盘
                AbSoapParams params = new AbSoapParams();
                params.put("pid", mProject_dw.getPid());
                WebServiceUntils.call(mContext, Constant.GET_users_by_pid, params, 10000, false, "", new WebServiceUntils.webServiceCallBack() {
                    @Override
                    public void callback(Boolean aBoolean, String result1) {
                        if (aBoolean) {
                            AbResult result = new AbResult(result1);
                            if (result.getResultCode() > 0) {
                                //成功
                                UsersListResult UsersListResult = AbJsonUtil.fromJson(result1, UsersListResult.class);
                                List<Users> UserssList = UsersListResult.getItems();
                                if (UserssList != null && UserssList.size() > 0) {
                                    initView(UserssList, holder);
                                } else {
                                    MsgUtil.sendMsgTop((AddProjectActivity) mContext, Constant.MSG_ALERT, "该部门内没有人员,请联系管理员添加");
                                }
                            }
                        }
                    }
                });
            }
        });

//        holder.fzrxmText.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                holder.fzrxmText.setInputType(InputType.TYPE_NULL);//关闭软键盘
//                AbSoapParams params = new AbSoapParams();
//                params.put("pid", mProject_dw.getPid());
//                WebServiceUntils.call(mContext, Constant.GET_users_by_pid, params, 10000, false, "", new WebServiceUntils.webServiceCallBack() {
//                    @Override
//                    public void callback(Boolean aBoolean, String result1) {
//                        if (aBoolean) {
//                            AbResult result = new AbResult(result1);
//                            if (result.getResultCode() > 0) {
//                                //成功
//                                UsersListResult UsersListResult = AbJsonUtil.fromJson(result1, UsersListResult.class);
//                                List<Users> UserssList = UsersListResult.getItems();
//                                if (UserssList != null && UserssList.size() > 0) {
//                                    initView(UserssList, holder);
//                                } else {
//                                    MsgUtil.sendMsgTop((AddProjectActivity) mContext, Constant.MSG_ALERT, "该部门内没有人员,请联系管理员添加");
//                                }
//                            }
//                        }
//                    }
//                });
//                return false;
//            }
//        });

        return convertView;
    }

    /**
     * ViewHolder类
     */
    public static class ViewHolder {
        public TextView dwText;
        public TextView fzrxmText;
        public EditText fzrText;
        public EditText dzText;
    }

    private void initView(final List<Users> mList_ry,final ViewHolder holder){
        View mView = LayoutInflater.from(mContext).inflate(R.layout.dialog_choose2, null);
        ListView listView = (ListView) mView.findViewById(R.id.list);
        UserChooseOneAdapter mUserChooseAdapter = new UserChooseOneAdapter(mContext, mList_ry);
        listView.setAdapter(mUserChooseAdapter);
        AlertView mAlertViewExt = new AlertView("请选择", null, "取消", null, new String[]{"完成"}, mContext, AlertView.Style.Alert, new OnItemClickListener() {
            @Override
            public void onItemClick(Object o, int position) {
                //判断是否是拓展窗口View，而且点击的是非取消按钮
                if ( position != AlertView.CANCELPOSITION) {
                    for (int i = 0; i < mList_ry.size(); i++) {
                        if (mList_ry.get(i).isChoose()) {
                            if (AbStrUtil.isEmpty(mList_ry.get(i).getId())||mList_ry.get(i).getId().equals("null")) {
                                MsgUtil.sendMsgTop((AddProjectActivity)mContext,Constant.MSG_ALERT,"该部门内没有人员,请联系管理员添加");
                            }else {
                                holder.fzrText.setText(mList_ry.get(i).getId());
                                holder.fzrxmText.setText(mList_ry.get(i).getUname());
                            }
                            break;
                        }
                    }

                    return;
                }
            }
        });
        mAlertViewExt.addExtView(mView);
        mAlertViewExt.show();
    }
}
