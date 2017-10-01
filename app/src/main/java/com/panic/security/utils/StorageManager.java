package com.panic.security.utils;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by david on 10/1/17.
 */

public class StorageManager {

    private static final String profileImageDir = "profile_images";

    public static void saveProfileImage(final String uid, final Activity activity) {
        FirebaseDAO.getInstance().getProfileImageInBytes(uid, new DataCallback<byte[]>() {
            @Override
            public void onDataReceive(byte[] data) {
                if(data == null){
                    return;
                }
                saveProfileImage(uid, data, activity);
            }
        });
    }

    public static void saveProfileImage(String uid, byte[] bytes, Activity activity) {
        ContextWrapper cw = new ContextWrapper(activity.getApplicationContext());
        File directory = cw.getDir (profileImageDir, Context.MODE_PRIVATE);

        Bitmap bitmapImage = ImageConverter.getMarkerBitmap(
                BitmapFactory.decodeByteArray (bytes, 0, bytes.length)
        );
        // path to /data/data/yourapp/app_data/imageDir

        // Create imageDir
        File mypath = new File (directory, uid);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Bitmap loadProfileImage(String uid, Activity activity) {
        Bitmap b = null;
        try {
            ContextWrapper cw = new ContextWrapper(activity.getApplicationContext());
            File directory = cw.getDir (profileImageDir, Context.MODE_PRIVATE);

            File f = new File(directory, uid);
            b = BitmapFactory.decodeStream (new FileInputStream (f));

        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return b;
    }
}
