package ru.arvalon.camera;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * Example capture foto and video by camera
 * @author arvalon
 */
public class MainActivity extends AppCompatActivity {

    private static final String LOGTAG = "camera.log";

    private static final String FILENAME = "picture.jpg";

    private static final String VIDEOFILENAME = "video.mp4";

    private static final String  authority = ".ru.arvalon.camera.provider";

    private static final int TAKE_PHOTO = 1;
    private static final int PICK_IMAGE = 2;
    private static final int TAKE_VIDEO = 3;

    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        image = findViewById(R.id.image);
    }

    public void takePhoto(View view) {
        takePhotoWrapper();
    }

    private void takePhotoWrapper() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                ) {
            requestPermissions(
                    new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA
                    },
                    TAKE_PHOTO);
        } else {
            takePhoto();
        }
    }

    /** Take foto */
    private void takePhoto() {

        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        if(!dir.isDirectory()){
            dir.mkdirs();
        }

        File file = new File(dir, FILENAME);

        // Intent for foto capture
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        Uri uri = FileProvider.getUriForFile(this,getPackageName()+authority,file);

        i.putExtra(MediaStore.ACTION_IMAGE_CAPTURE, uri);

        startActivityForResult(i, TAKE_PHOTO);
    }

    public void scan(View view) {
        File picDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        scan(picDir);
    }

    /** need description */
    private void scan(File picsDir) {
        if(picsDir != null)
        {
            MediaScannerConnection.scanFile(
                    this,
                    new String[]{
                            picsDir.getAbsolutePath()
                    },
                    null,
                    (path, uri) -> {
                        Log.d(LOGTAG, "Scanned " + path);
                        Log.d(LOGTAG, "Uri: " + uri);
                    }
            );
        }
    }

    public void pickImage(View view) {
        pickImageWrapper();
    }

    /** check permission and pick image from device */
    private void pickImageWrapper() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED
                ) {
            requestPermissions(
                    new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    },
                    PICK_IMAGE);
        } else {
            Intent intent = new Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            );
            startActivityForResult(intent, PICK_IMAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if(requestCode == PICK_IMAGE)
        {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                pickImageWrapper();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == PICK_IMAGE)
        {
            if(resultCode == RESULT_OK)
            {
                if(data != null && data.getData() != null)
                {
                    Uri  uri = data.getData();

                    Picasso.with(this).load(uri).fit().into(image);
                }
            }
        }
        else if(requestCode == TAKE_PHOTO)
        {
            if(resultCode == RESULT_OK)
            {
                File dir = Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

                File file = new File(dir, FILENAME);

                Picasso.with(this).load(file).fit().into(image);
            }
        }
        else if(requestCode == TAKE_VIDEO)
        {
            if(resultCode == RESULT_OK)
            {
                File dir = Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                scan(dir);
            }
        }
    }

    public void takeVideo(View view) {
        takeVideoWrapper();
    }

    /** check permissions */
    private void takeVideoWrapper() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                ) {
            requestPermissions(
                    new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA
                    },
                    TAKE_VIDEO);
        } else {
            takeVideo();
        }
    }

    /** capture video */
    private void takeVideo() {
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        if(!dir.isDirectory())
            dir.mkdirs();

        File file = new File(dir, VIDEOFILENAME);

        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        Uri uri = FileProvider.getUriForFile(this,getPackageName()+authority,file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 30);

        startActivityForResult(intent, TAKE_VIDEO);
    }
}