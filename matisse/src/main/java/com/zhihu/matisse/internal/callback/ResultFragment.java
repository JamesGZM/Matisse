package com.zhihu.matisse.internal.callback;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.ui.MatisseActivity;

/**
 * @author JamesGZM
 * @description: 代理onActivityResult
 * @date :3/6/21 3:15 PM
 */
public class ResultFragment extends Fragment {

    private static final int HOLDER_SELECT_REQUEST_CODE = 0x44;

    private ResultCallback mSelectCallback;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public void startSelect(ResultCallback callback) {
        mSelectCallback = callback;
        startActivityForResult(new Intent(getActivity(), MatisseActivity.class), HOLDER_SELECT_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Activity.RESULT_OK == resultCode && requestCode == HOLDER_SELECT_REQUEST_CODE) {
            if (mSelectCallback != null) {
                mSelectCallback.onResult(Matisse.obtainResult(data), Matisse.obtainPathResult(data));
            }
        }
    }
}
