package com.zhihu.matisse.internal.callback;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

/**
 * @author JamesGZM
 * @description:
 * @date :3/6/21 3:22 PM
 */
public class FragmentResult {

    private static final String TAG = "com.zhihu.matisse";

    private FragmentResult() {
    }

    public static ResultFragment get(FragmentActivity activity) {
        return new FragmentResult().getHolderFragment(activity.getSupportFragmentManager());
    }

    public static ResultFragment get(Fragment fragment) {
        return new FragmentResult().getHolderFragment(fragment.getChildFragmentManager());
    }

    private ResultFragment getHolderFragment(FragmentManager fragmentManager) {
        ResultFragment resultFragment = (ResultFragment) fragmentManager.findFragmentByTag(TAG);
        if (resultFragment == null) {
            resultFragment = new ResultFragment();
            fragmentManager
                    .beginTransaction()
                    .add(resultFragment, TAG)
                    .commitAllowingStateLoss();
            fragmentManager.executePendingTransactions();
        }
        return resultFragment;
    }

}
