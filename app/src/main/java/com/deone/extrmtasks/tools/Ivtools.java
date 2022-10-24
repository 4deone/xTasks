package com.deone.extrmtasks.tools;

import static com.deone.extrmtasks.tools.Constants.CAMERA_REQUEST_CODE;
import static com.deone.extrmtasks.tools.Constants.STORAGE_REQUEST_CODE;
import static com.deone.extrmtasks.tools.Other.isStringEmpty;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.deone.extrmtasks.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class Ivtools {

    private static Context appContext;
    private static Ivtools instance;
    private static String[] cameraPermissions;
    private static String[] storagePermissions;
    private static ActivityResultLauncher<Intent> someActivityResultLauncher ;

    /**
     *
     * @param applicationContext
     * @return
     */
    public static synchronized Ivtools getInstance(Context applicationContext) {
        if (instance == null)
            instance = new Ivtools(applicationContext);
        return instance;
    }

    /**
     *
     * @param applicationContext
     */
    private Ivtools(Context applicationContext) {
        appContext = applicationContext;
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
    }

    /**
     *
     */
    public void requestStoragePermissions() {
        ActivityCompat.requestPermissions((Activity) appContext, storagePermissions, STORAGE_REQUEST_CODE);
    }

    /**
     *
     */
    public void requestCameraPermissions() {
        ActivityCompat.requestPermissions((Activity) appContext, cameraPermissions, CAMERA_REQUEST_CODE);
    }

    /**
     *
     * @return
     */
    public boolean checkStoragePermissions() {
        return ContextCompat.checkSelfPermission(appContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
    }

    /**
     *
     * @return
     */
    public boolean checkCameraPermissions() {
        boolean result = ContextCompat.checkSelfPermission(appContext, Manifest.permission.CAMERA)
                == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(appContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    /**
     *
     */
    public void pickFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        someActivityResultLauncher.launch(galleryIntent);
    }

    /**
     *
     */
    public void pickFromCamera(Uri imageUri) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "User Avatar Title");
        values.put(MediaStore.Images.Media.DESCRIPTION, "User Avatar Description");
        imageUri = appContext.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        someActivityResultLauncher.launch(cameraIntent);
    }

    /**
     *
     * @param someActivityResultLauncher
     */
    public void setSomeActivityResultLauncher(ActivityResultLauncher<Intent> someActivityResultLauncher) {
        Ivtools.someActivityResultLauncher = someActivityResultLauncher;
    }


    /**
     *
     * @param grantResult
     * @return
     */
    public static boolean isWriteStorageAccepted(int grantResult) {
        return grantResult == PackageManager.PERMISSION_GRANTED;
    }

    /**
     *
     * @param grantResult
     * @return
     */
    public static boolean isCameraAccepted(int grantResult) {
        return grantResult == PackageManager.PERMISSION_GRANTED;
    }

    /**
     *
     * @param iv
     * @param pb
     * @param image
     */
    public static void loadingImageWithPath(ImageView iv, ProgressBar pb, int draw, String image) {
        if (isStringEmpty(image))
            pb.setVisibility(View.GONE);
        else
            Picasso.get().load(image).placeholder(draw).into(iv, new Callback() {
                @Override
                public void onSuccess() {
                    pb.setVisibility(View.GONE);
                }

                @Override
                public void onError(Exception e) {
                    Toast.makeText(appContext, appContext.getString(R.string.download_image_error), Toast.LENGTH_SHORT).show();
                }
            });
    }

    /**
     *
     * @param iv
     * @param draw
     * @param image
     */
    public static void loadingImageWithPath(ImageView iv, int draw, String image) {
        if (!isStringEmpty(image))
            Picasso.get().load(image).placeholder(draw).into(iv);
    }

    /**
     *
     * @param requestCode
     * @param grantResults
     * @param imageUri
     */
    public void requestCameraAndGalleryPermissions(int requestCode, int[] grantResults, Uri imageUri) {
        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                if (grantResults.length>0){
                    if (isCameraAccepted(grantResults[0]) && isWriteStorageAccepted(grantResults[1]))
                        this.pickFromCamera(imageUri);
                    else
                        Toast.makeText(appContext, appContext.getString(R.string.enable_camera_storage_permissions), Toast.LENGTH_SHORT).show();
                }
            }
            break;
            case STORAGE_REQUEST_CODE:{
                if (grantResults.length>0){
                    if (isWriteStorageAccepted(grantResults[0]))
                        this.pickFromGallery();
                    else
                        Toast.makeText(appContext, appContext.getString(R.string.enable_storage_permissions), Toast.LENGTH_SHORT).show();
                }
            }
            break;
        }
    }

}
