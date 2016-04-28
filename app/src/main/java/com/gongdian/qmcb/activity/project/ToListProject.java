package com.gongdian.qmcb.activity.project;

/**
 * Created by qian-pc on 2/16/16.
 * 将工程单按施工单位分开
 */

import com.ab.util.AbStrUtil;
import com.gongdian.qmcb.model.Project;
import com.gongdian.qmcb.model.Project_dw;

import java.util.ArrayList;
import java.util.List;

public class ToListProject {
    private List<Project> mProjects;
    private String menu_id;
    private String pid;

    public ToListProject(List<Project> projects, String menu_id, String pid) {
        mProjects = projects;
        this.menu_id = menu_id;
        this.pid = pid;
    }

    public List<Project> toList() {
        List<Project> outProjects = new ArrayList<>();

        if (AbStrUtil.isEquals(menu_id, "201")) {
            return mProjects;
        }

        for (int i = 0; i < mProjects.size(); i++) {
            Project project = mProjects.get(i);
            List<Project_dw> project_dws = project.getProject_dw();
            if (project_dws == null) {
                continue;
            }
            for (int j = 0; j < project_dws.size(); j++) {
                Project outProject = project;
                switch (menu_id) {
                    case "202": //勘查 -- 施工单位
                        if (AbStrUtil.isEquals(project_dws.get(j).getPid(), pid)) {
                            outProject.setDz(project_dws.get(j).getDz());
                            outProject.setFzr(project_dws.get(j).getFzr());
                            outProject.setFzrxm(project_dws.get(j).getFzrxm());
                            outProject.setPid(project_dws.get(j).getPid());
                            outProject.setDw(project_dws.get(j).getPname());
                            outProjects.add(outProject);
                        }
                        break;
                    case "203": //开工 --勘查之后
                        if (AbStrUtil.isEquals(project_dws.get(j).getFlag(), "2") && AbStrUtil.isEquals(outProject.getXk_pid(),pid)) {
                            outProject.setDz(project_dws.get(j).getDz());
                            outProject.setFzr(project_dws.get(j).getFzr());
                            outProject.setFzrxm(project_dws.get(j).getFzrxm());
                            outProject.setPid(project_dws.get(j).getPid());
                            outProject.setDw(project_dws.get(j).getPname());
                            outProjects.add(outProject);
                        }
                        break;
                    case "206": //完工
                        if (AbStrUtil.isEquals(project_dws.get(j).getFlag(), "3") && AbStrUtil.isEquals(outProject.getXk_pid(),pid)) {
                            outProject.setDz(project_dws.get(j).getDz());
                            outProject.setFzr(project_dws.get(j).getFzr());
                            outProject.setFzrxm(project_dws.get(j).getFzrxm());
                            outProject.setPid(project_dws.get(j).getPid());
                            outProject.setDw(project_dws.get(j).getPname());
                            outProjects.add(outProject);
                        }
                        break;
                    case "204": //到岗--筛选已经开工未完工的project_dw
                        if (AbStrUtil.isEquals(project_dws.get(j).getFlag(), "3")) {
                            outProject.setDz(project_dws.get(j).getDz());
                            outProject.setFzr(project_dws.get(j).getFzr());
                            outProject.setFzrxm(project_dws.get(j).getFzrxm());
                            outProject.setPid(project_dws.get(j).getPid());
                            outProject.setDw(project_dws.get(j).getPname());
                            outProjects.add(outProject);
                        }
                        break;

                    case "205": //监督--筛选已经开工的project_dw
                        if (AbStrUtil.isEquals(project_dws.get(j).getFlag(), "3") || AbStrUtil.isEquals(project_dws.get(j).getFlag(), "4")) {
                            outProject.setDz(project_dws.get(j).getDz());
                            outProject.setFzr(project_dws.get(j).getFzr());
                            outProject.setFzrxm(project_dws.get(j).getFzrxm());
                            outProject.setPid(project_dws.get(j).getPid());
                            outProject.setDw(project_dws.get(j).getPname());
                            outProjects.add(outProject);
                        }
                        break;


                }

            }

        }

        return outProjects;
    }

}
