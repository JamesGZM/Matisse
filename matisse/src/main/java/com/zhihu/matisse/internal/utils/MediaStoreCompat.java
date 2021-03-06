/*
 * Copyright 2017 Zhihu Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zhihu.matisse.internal.utils;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.fragment.app.Fragment;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;

public class MediaStoreCompat {

    private final WeakReference<Activity> mContext;
    private final WeakReference<Fragment> mFragment;
    private String authority;
    private Uri photoUri = null;
    private File mTempImageFile = null;

    public MediaStoreCompat(Activity activity) {
        mContext = new WeakReference<>(activity);
        mFragment = null;
    }

    public MediaStoreCompat(Activity activity, Fragment fragment) {
        mContext = new WeakReference<>(activity);
        mFragment = new WeakReference<>(fragment);
    }

    /**
     * Checks whether the device has a camera feature or not.
     *
     * @param context a context to check for camera feature.
     * @return true if the device has a camera feature. false otherwise.
     */
    public static boolean hasCameraFeature(Context context) {
        PackageManager pm = context.getApplicationContext().getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public void dispatchCaptureIntent(Context context, int requestCode) {

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photoUri = null;
        mTempImageFile = null;

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            photoUri = createImageUri();
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            cameraIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            if (mFragment != null) {
                mFragment.get().startActivityForResult(cameraIntent, requestCode);
            } else {
                mContext.get().startActivityForResult(cameraIntent, requestCode);
            }
            return;
        }

        createCameraTempImageFile();
        if (mTempImageFile != null && mTempImageFile.exists()) {

            Uri imageUri;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                imageUri = FileProvider.getUriForFile(context, authority, mTempImageFile);
            } else {
                imageUri = Uri.fromFile(mTempImageFile);
            }

            cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //对目标应用临时授权该Uri所代表的文件
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);//将拍取的照片保存到指定URI
            if (mFragment != null) {
                mFragment.get().startActivityForResult(cameraIntent, requestCode);
            } else {
                mContext.get().startActivityForResult(cameraIntent, requestCode);
            }
        }

    }

    /**
     * 创建图片地址uri,用于保存拍照后的照片 Android 10以后使用这种方法
     */
    private Uri createImageUri() {
        String status = Environment.getExternalStorageState();
        // 判断是否有SD卡,优先使用SD卡存储,当没有SD卡时使用手机存储
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            return mContext.get().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    new ContentValues());
        } else {
            return mContext.get().getContentResolver().insert(MediaStore.Images.Media.INTERNAL_CONTENT_URI,
                    new ContentValues());
        }
    }


    private void createCameraTempImageFile() {
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if (null == dir) {
            dir = new File(Environment.getExternalStorageDirectory(),
                    File.separator + "DCIM" + File.separator + "Camera" + File.separator);
        }
        if (!dir.isDirectory()) {
            if (!dir.mkdirs()) {
                dir = mContext.get().getExternalFilesDir(null);
                if (null == dir || !dir.exists()) {
                    dir = mContext.get().getFilesDir();
                    if (null == dir || !dir.exists()) {
                        String cacheDirPath =
                                File.separator + "data" + File.separator + "data" + File.separator + mContext.get().getPackageName() + File.separator + "cache" + File.separator;
                        dir = new File(cacheDirPath);
                        if (!dir.exists()) {
                            dir.mkdirs();
                        }
                    }
                }
            }
        }

        try {
            mTempImageFile = File.createTempFile("IMG", ".jpg", dir);
        } catch (IOException e) {
            e.printStackTrace();
            mTempImageFile = null;
        }

    }

    public Uri getCurrentPhotoUri() {
        return photoUri;
    }

    public File getTempImageFile() {
        return mTempImageFile;
    }

//    public String getCurrentPhotoPath() {
//
//        if (photoUri != null) {
//            Cursor cursor = mContext.get().getContentResolver().query(photoUri, null, null, null, null);
//            if (cursor == null) {
//                return null;
//            }
//            String path = null;
//            if (cursor.moveToFirst()) {
//                path = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
//
//            }
//            cursor.close();
//            return path;
//        }
//        return mTempImageFile.getAbsolutePath();
//    }
}
