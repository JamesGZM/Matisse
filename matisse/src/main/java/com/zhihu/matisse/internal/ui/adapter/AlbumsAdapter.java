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
package com.zhihu.matisse.internal.ui.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;


import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.R;
import com.zhihu.matisse.internal.entity.Album;
import com.zhihu.matisse.internal.utils.PathUtils;

public class AlbumsAdapter extends CursorAdapter {

    private final Drawable mPlaceholder;
    private float mDensity;
    private final int SIZE = 40; //dp

    public AlbumsAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);

        TypedArray ta = context.getTheme().obtainStyledAttributes(
                new int[]{R.attr.album_thumbnail_placeholder});
        mPlaceholder = ta.getDrawable(0);
        ta.recycle();

        mDensity = context.getResources().getDisplayMetrics().density;
    }

    public AlbumsAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);

        TypedArray ta = context.getTheme().obtainStyledAttributes(
                new int[]{R.attr.album_thumbnail_placeholder});
        mPlaceholder = ta.getDrawable(0);
        ta.recycle();

        mDensity = context.getResources().getDisplayMetrics().density;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.album_list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Album album = Album.valueOf(cursor);
        ((TextView) view.findViewById(R.id.album_name)).setText(album.getDisplayName(context));
        ((TextView) view.findViewById(R.id.album_media_count)).setText(String.valueOf(album.getCount()));

        // do not need to load animated Gif
        SimpleDraweeView albumCover = view.findViewById(R.id.album_cover);

        albumCover.getHierarchy().setPlaceholderImage(mPlaceholder);

        if (MimeType.isVideo(album.mimeType) && android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Bitmap bitmap = PathUtils.getVideoThumbnailBitmap(album.getCoverUri(), context, (int) (SIZE * mDensity), (int) (SIZE * mDensity));
            if (bitmap != null) {
                albumCover.getHierarchy().setImage(new BitmapDrawable(context.getResources(), bitmap), 1, true);
                return;
            }
        }

        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(album.getCoverUri())
                .setResizeOptions(new ResizeOptions((int) (SIZE * mDensity), (int) (SIZE * mDensity)))
                .build();
        DraweeController newController = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setOldController(albumCover.getController())
                .build();

        albumCover.setController(newController);


    }
}
