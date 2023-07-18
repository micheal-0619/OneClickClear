package com.axb.oneclickclear;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.axb.oneclickclear.adapter.MainAdapter;
import com.axb.oneclickclear.fragment.ClearCacheFragment;
import com.axb.oneclickclear.interfaces.MainInterface;
import com.axb.oneclickclear.utils.Constants;

import java.util.Stack;

public class MainActivity extends FragmentActivity implements MainInterface {

    private FragmentManager fm = getSupportFragmentManager();
    private Stack<String> titles = new Stack<>();
    private TextView tvTitle;

    private final static String[] names = {
            "缓存清理"
    };

    private final static int[] ids = {
            R.drawable.sysoptimize
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initComponent();
    }

    /**
     * 跳页至某Fragment
     *
     * @param ID Tag of the Fragment
     */
    @Override
    public void callFragment(int ID) {
        Fragment fragment = null;
        String tag = null;
        String title = "";
        switch (ID) {
            case Constants.CLEAR_CACHE_FRAG:
                title = "缓存清理";
                fragment = new ClearCacheFragment();
                tag = "CLEAR_CACHE";
                break;
        }
        titles.push(title);
        tvTitle.setText(title);

        FragmentTransaction transaction = fm.beginTransaction();
        transaction.add(R.id.fl_container, fragment, tag);
        transaction.addToBackStack(title);
        transaction.commitAllowingStateLoss();

    }

    /**
     * Clear all fragments in stack
     */
    @Override
    public void clearAllFragments() {
        for (int i = 0; i < fm.getBackStackEntryCount(); i++) {
            fm.popBackStack();
            titles.pop();
        }
        titles.push("功能列表");
    }

    private void initComponent() {
        GridView list_home = (GridView) findViewById(R.id.list_home);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvTitle.setText("功能列表");
        titles.push("功能列表");
        MainAdapter adapter = new MainAdapter(this, names, ids);
        list_home.setAdapter(adapter);
        list_home.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                switch (position) {

                    case 0://缓存清理
                        clearCache();
                        break;
                    default:
                        break;
                }

            }
        });
    }

    private void clearCache() {
        callFragment(Constants.CLEAR_CACHE_FRAG);
    }

}