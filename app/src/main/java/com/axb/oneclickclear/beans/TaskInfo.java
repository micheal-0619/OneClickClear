package com.axb.oneclickclear.beans;

import android.graphics.drawable.Drawable;

/**
 * Created by Yi-Jing on 2016/9/11.
 * 进程信息的业务bean
 */

public class TaskInfo {
    public Drawable icon;
    public String name;
    public String packageName;
    public long memSize;
    /**
     * true 用户进程
     * false 系统进程
     */
    public boolean userTask;

    @Override
    public String toString() {
        return "TaskInfo{" +
                "icon=" + icon +
                ", name='" + name + '\'' +
                ", packageName='" + packageName + '\'' +
                ", memSize=" + memSize +
                ", userTask=" + userTask +
                '}';
    }
}
