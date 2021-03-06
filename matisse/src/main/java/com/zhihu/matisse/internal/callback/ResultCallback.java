package com.zhihu.matisse.internal.callback;

import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

/**
 * @author JamesGZM
 * @description: 选择回调
 * @date :3/6/21 3:11 PM
 */
public interface ResultCallback {

    /**
     * 选择结果回调
     *
     * @param uris  uri地址
     * @param paths 地址
     */
    void onResult(List<Uri> uris, List<String> paths);
}
