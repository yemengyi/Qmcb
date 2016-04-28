package com.gongdian.qmcb.dao;

import android.content.Context;

import com.ab.db.orm.AbDBHelper;
import com.gongdian.qmcb.model.Project;
import com.gongdian.qmcb.model.Project_jd;
import com.gongdian.qmcb.model.Project_ry;
import com.gongdian.qmcb.model.Project_dw;
import com.gongdian.qmcb.model.Project_photo;
import com.gongdian.qmcb.utils.Constant;

public class DBInsideHelper extends AbDBHelper {
	// 数据库名
	private static final String DBNAME = "weian.db";
    
    // 当前数据库的版本
	private static final int DBVERSION = Constant.DBVERSION;
	// 要初始化的表
	private static final Class<?>[] clazz = { Project.class,Project_dw.class,Project_ry.class, Project_photo.class, Project_jd.class};

	public DBInsideHelper(Context context) {
		super(context, DBNAME, null, DBVERSION, clazz);
	}

}



