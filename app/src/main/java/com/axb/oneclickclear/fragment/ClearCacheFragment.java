package com.axb.oneclickclear.fragment;

import android.Manifest;
import android.app.usage.StorageStats;
import android.app.usage.StorageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.storage.StorageManager;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;


import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.axb.oneclickclear.R;
import com.axb.oneclickclear.utils.PermissionsUtils;
import com.axb.oneclickclear.utils.SystemInfoUtils;
import com.axb.oneclickclear.utils.UtilLog;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class ClearCacheFragment extends Fragment {
    private static final String TAG = "ClearCacheFragment";

    private TextView tvStatus;
    private ProgressBar pb;
    private LinearLayout layoutContent;
    private PackageManager pm;


    //创建监听权限的接口对象
    PermissionsUtils.IPermissionsResult permissionsResult = new PermissionsUtils.IPermissionsResult() {

        @Override
        public void passPermissions() {
            Toast.makeText(getContext(), "权限通过，可以做其他事情!", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void forbidPermissions() {
            Toast.makeText(getContext(), "权限不通过!", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //就多一个参数this
        PermissionsUtils.getInstance().onRequestPermissionsResult(getActivity(), requestCode, permissions, grantResults);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_clear_cache, container, false);

        //申请权限
        String[] permissions = new String[]{Manifest.permission.DELETE_CACHE_FILES, Manifest.permission.GET_PACKAGE_SIZE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
//        PermissionsUtils.showSystemSetting = false;//是否支持显示系统设置权限设置窗口跳转
        //这里的this不是上下文，是Activity对象！
        PermissionsUtils.getInstance().checkPermissions(getActivity(), permissions, permissionsResult);
        tvStatus = (TextView) view.findViewById(R.id.tv_status);
        pb = (ProgressBar) view.findViewById(R.id.pb);
        layoutContent = (LinearLayout) view.findViewById(R.id.ll_content);
        scanCache();
        return view;
    }

    private void scanCache() {
        pm = getActivity().getPackageManager();
        new Thread() {
            public void run() {
                List<PackageInfo> installedPackages = pm.getInstalledPackages(0);
                //Reflection
                Method[] methods = PackageManager.class.getDeclaredMethods();
                Method getPackageSizeInfo = null;
                for (Method m : methods) {
                    UtilLog.d(TAG, m.getName());
                    if ("getPackageSizeInfo".equals(m.getName())) {
                        getPackageSizeInfo = m;
                    }
                }
                pb.setMax(installedPackages.size());
                for (int i = 0; i < installedPackages.size(); i++) {
                    try {
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                            getPackageSizeInfo.invoke(pm, installedPackages.get(i).packageName, new StatsObserver());
                        } else {
                            Context context = getContext();
                            ApplicationInfo info = pm.getApplicationInfo(installedPackages.get(i).packageName, 0);
                            getAppSize(context, info);
                        }
                        pb.setProgress(i + 1);
                        Thread.sleep(50);

                        if (i == installedPackages.size() - 1) {
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        tvStatus.setText(String.format("扫描完毕！"));
                                    }
                                });
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        }.start();
    }


    private class StatsObserver extends IPackageStatsObserver.Stub {
        @Override
        public void onGetStatsCompleted(final PackageStats pStats, boolean succeeded) throws RemoteException {
            final long cacheSize = pStats.cacheSize;
            long codeSize = pStats.codeSize;
            long dataSize = pStats.dataSize;

            UtilLog.d(TAG, pStats.packageName);
            UtilLog.d(TAG, "cacheSize " + SystemInfoUtils.formatFileSize(cacheSize));
            UtilLog.d(TAG, "codeSize " + SystemInfoUtils.formatFileSize(codeSize));
            UtilLog.d(TAG, "dataSize " + SystemInfoUtils.formatFileSize(dataSize));
            UtilLog.d(TAG, "--------------------------------");
            ClearCache(pStats.packageName, cacheSize);
        }
    }

    private class DataObserver extends IPackageDataObserver.Stub {
        public void onRemoveCompleted(final String packageName, final boolean succeeded) {
            Log.d(TAG, "DataObserver " + succeeded);
        }
    }

    /*
     * Android 8.0以上不能通过反射获取缓存
     * 获取缓存
     * */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getAppSize(Context context, ApplicationInfo info) {

        StorageStatsManager storageStatsManager =
                (StorageStatsManager) context.getSystemService(Context.STORAGE_STATS_SERVICE);
        StorageManager storageManager =
                (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        //ApplicationInfo info = pm.getApplicationInfo(installedPackages.get(i).packageName, 0);
        StorageStats storageStats = null;
        try {
            storageStats = storageStatsManager.queryStatsForUid(info.storageUuid, info.uid);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        long appBytes = storageStats.getAppBytes();
        long cacheBytes = storageStats.getCacheBytes();
        long dataBytes = storageStats.getDataBytes();
        UtilLog.e(TAG, " cacheBytes = " + cacheBytes + "   name= " + info.name);
        if (cacheBytes > 0) {
            String cache = Formatter.formatFileSize(context, cacheBytes);
        }
        ClearCache(info.name, cacheBytes);
    }


    private void ClearCache(String name, long cacheSize) {

        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvStatus.setText(String.format("正在扫描%s", name));
                    if (cacheSize > 0) {
                        View v = View.inflate(getActivity(), R.layout.list_item_cache, null);
                        TextView tv_name = (TextView) v.findViewById(R.id.tv_name);
                        TextView tv_cacheSize = (TextView) v.findViewById(R.id.tv_cache_size);
                        ImageView iv_clear = (ImageView) v.findViewById(R.id.iv_clear);
                        iv_clear.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //Not work, due to lacking android.permission.DELETE_CACHE_FILES
                                //DELETE_CACHE_FILES which allows an application to delete cache files is Not for use by third-party applications.
                                ActivityManagerWrapper.getInstance().removeAllRecentTasks();
                                Toast.makeText(getActivity(), "很抱歉，只有系统级别的应用才能清理缓存", Toast.LENGTH_LONG).show();
                                try {
                                    //Reflection
                                    Method deleteApplicationCacheFiles = PackageManager.class.getMethod("deleteApplicationCacheFiles", String.class, IPackageDataObserver.class);
                                    deleteApplicationCacheFiles.invoke(pm, name, new DataObserver());
                                    Toast.makeText(getActivity(), "清除缓存成功", Toast.LENGTH_LONG).show();
                                } catch (NoSuchMethodException e) {
                                    e.printStackTrace();
                                } catch (InvocationTargetException e) {
                                    e.printStackTrace();
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                        tv_name.setText(name);
                        tv_cacheSize.setText(String.format("缓存大小：%s", SystemInfoUtils.formatFileSize(cacheSize)));
                        layoutContent.addView(v, 0);
                    }
                }
            });
        }
    }


}
