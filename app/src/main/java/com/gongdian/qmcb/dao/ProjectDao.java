package com.gongdian.qmcb.dao;

import android.content.Context;

import com.ab.db.orm.dao.AbDBDaoImpl;
import com.gongdian.qmcb.model.Project;

/**
 * Created by qian-pc on 1/18/16.
 */


public class ProjectDao extends AbDBDaoImpl<Project> {
    public ProjectDao(Context context) {
        super(new DBSDHelper(context),Project.class);
    }
}

