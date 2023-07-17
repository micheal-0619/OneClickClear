package com.axb.oneclickclear.fragment;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.axb.oneclickclear.R;

public class ClearCacheFragment extends Fragment {
    private static final String TAG = "ClearCacheFragment";

    private TextView tvStatus;
    private ProgressBar pb;
    private LinearLayout layoutContent;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_clear_cache, container, false);
        tvStatus = (TextView) view.findViewById(R.id.tv_status);
        pb = (ProgressBar) view.findViewById(R.id.pb);
        layoutContent = (LinearLayout) view.findViewById(R.id.ll_content);

        return view;
    }

}
