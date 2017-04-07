package com.example.lkbwei.freeOrder.Tools;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;

import java.io.File;

/**
 * Created by lkbwei on 2017/3/6.
 */

public class LoadImage  {

    /**
     * 获取存储临时图片的本地地址
     * @param context 上下文
     * @return Uri
     * @since 1.0
     */
    public static Uri getTempFileDir(Context context){
        File file = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);//temp file
        File tempFile = new File(file,"temp.jpg");

        return Uri.fromFile(tempFile);
    }

    /**
     * 获取存储头像图片的本地地址
     * @param context 上下文
     * @return Uri
     * @since 1.0
     */
    public static Uri getUserImage(Context context){
        File file = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File tempFile = new File(file,"my.jpg");

        return Uri.fromFile(tempFile);
    }

    /**
     * 获取存储头像图片的本地File
     * @param context 上下文
     * @return Uri
     * @since 1.0
     */
    public static File getUserImageFile(Context context){
        File file = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return new File(file,"my.jpg");
    }

    /**
     * 获取存储临时图片的本地地址String
     * @param context 上下文
     * @return Uri
     * @since 1.0
     */
    public static String getImageFileString(Context context){
        File file = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);//temp file
        File tempFile = new File(file,"temp.jpg");
        return tempFile.getPath();
    }

    /**
     * 创建跳转Intent
     * @param context 上下文
     * @param view 视图
     * @param uri Uri
     * @return Intent
     * @since 1.0
     */
    public static Intent getImage(Context context, View view,Uri uri){
        Uri temp = uri;

        int width = view.getWidth();
        int height = view.getHeight();

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
        intent.setType("image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", width/height);
        intent.putExtra("aspectY", 1);
//        intent.putExtra("outputX", width);
//        intent.putExtra("outputY", height);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, temp);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        return intent;
    }

    /**
     * 创建跳转相机Intent
     * @param context 上下文
     * @return Intent
     * @since 1.0
     */
    public static Intent openCamera(Context context){
        Uri temp = getTempFileDir(context);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//action is capture
        intent.putExtra(MediaStore.EXTRA_OUTPUT, temp);

        return intent;
    }

    /**
     * 创建跳转Intent
     * @param context 上下文
     * @param view 视图
     * @param imageUri Uri
     * @return Intent
     * @since 1.0
     */
    public static Intent getFromCamera(Context context,View view,Uri imageUri){
        Uri uri = imageUri;

        int width = view.getWidth();
        int height = view.getHeight();

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", width/height);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", width);
        intent.putExtra("outputY", height);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection

        return intent;
    }
}
