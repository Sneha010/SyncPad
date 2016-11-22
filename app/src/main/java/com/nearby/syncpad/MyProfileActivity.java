package com.nearby.syncpad;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.nearby.syncpad.storedata.ProfileStore;
import com.nearby.syncpad.util.GeneralUtils;
import com.rey.material.widget.Button;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class MyProfileActivity extends AppCompatActivity {


    @BindView(R.id.ivPhoto)
    ImageView ivPhoto;
    @BindView(R.id.iv_support_bg)
    View ivSupportBg;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.collapsingToolbar)
    CollapsingToolbarLayout collapsingToolbar;
    @BindView(R.id.app_bar)
    AppBarLayout appBar;
    @BindView(R.id.myName)
    EditText myName;
    @BindView(R.id.myNameTIL)
    TextInputLayout myNameTIL;
    @BindView(R.id.myRole)
    EditText myRole;
    @BindView(R.id.myRoleTIL)
    TextInputLayout myRoleTIL;
    @BindView(R.id.myEmailId)
    EditText myEmailId;
    @BindView(R.id.myEmailTIL)
    TextInputLayout myEmailTIL;
    @BindView(R.id.buttonSave)
    Button buttonSave;
    @BindView(R.id.tvLogout)
    TextView tvLogout;


    @Inject
    FirebaseAuth mAuth;

    private FirebaseAuth.AuthStateListener mAuthListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myprofile_layout);
        ButterKnife.bind(this);

        ((SyncPadApplication) getApplication()).getMyApplicationComponent().inject(this);

        setUpFirebaseLogout();
        init();
        initializeFieldsWithSavedData();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    private void init() {

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfileData();
                finish();
            }
        });

        tvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
            }
        });

        ivPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCameraAndSavePhoto(v);
            }
        });
    }

    private void setUpFirebaseLogout() {
        //get firebase auth instance
        //mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(MyProfileActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };
    }

    //To show the earlier saved data filled in the form fields
    private void initializeFieldsWithSavedData() {
        myName.setText(ProfileStore.getUserName(this));
        myRole.setText(ProfileStore.getUserRole(this));
        myEmailId.setText(ProfileStore.getEmailAddress(this));

        if (ProfileStore.getImagePath(this) != null) {
            new UpdatePicTask().execute(ProfileStore.getImagePath(this), "");
        }
    }


    //Click on save button on profile
    public void saveProfileData() {

        if (TextUtils.isEmpty(myName.getText().toString())) {
            myNameTIL.setErrorEnabled(true);
            myNameTIL.setError("Cannot be empty");
            return;
        }

        if (TextUtils.isEmpty(myRole.getText().toString())) {
            myRoleTIL.setErrorEnabled(true);
            myRoleTIL.setError("Cannot be empty");
            return;
        }

        if (TextUtils.isEmpty(myEmailId.getText().toString())) {
            myEmailTIL.setErrorEnabled(true);
            myEmailTIL.setError("Cannot be empty");
            return;
        }


        //Saving the profile of user to publish it to other nearby devices
        ProfileStore.saveUserName(this, myName.getText().toString());
        ProfileStore.saveEmailKey(this, myEmailId.getText().toString());
        ProfileStore.saveUserRole(this, myRole.getText().toString());

        //Go back after saving the data
        getFragmentManager().popBackStack();

    }

    public void openCameraAndSavePhoto(View v) {

        uploadPhoto(v);

    }

    public void uploadPhoto(View v) {

        if (GeneralUtils.checkSDCard()) {
            registerForContextMenu(v);
            this.openContextMenu(v);
        } else {
            if (!this.isFinishing()) {

                Toast.makeText(this, "SD card not available.", Toast.LENGTH_LONG).show();

            }
        }
    }

    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.profileImage) {
            menu.setHeaderTitle(getString(R.string.select_action));
            String[] menuItems = {getString(R.string.gallery),
                    getString(R.string.take_photo)};
            for (int i = 0; i < menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }

    final static int CAMERA_RESULT = 0;
    private static final int GALLERY_INTENT_CALLED = 1;
    private static final int GALLERY_KITKAT_INTENT_CALLED = 2;
    private File photo;
    private Bitmap uploadBitmap;

    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case 0: {

                if (Build.VERSION.SDK_INT < 20) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                    intent.setType("image/*");

                    startActivityForResult(Intent.createChooser(intent,
                            getString(R.string.select_pic)), GALLERY_INTENT_CALLED);
                } else {
                    //For kitkat and above versions..
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("image/*");
                    //intent.setType("image/jpeg");
                    startActivityForResult(intent, GALLERY_KITKAT_INTENT_CALLED);
                }
                break;
            }

            case 1: {
                SimpleDateFormat dateFormat = new SimpleDateFormat(
                        "yyyyMMdd-HHmmss");
                if (GeneralUtils.checkSDCard()) {
                    photo = new File(Environment.getExternalStorageDirectory(),
                            dateFormat.format(new Date()) + ".jpg");
                    Intent cameraintent = new Intent(
                            "android.media.action.IMAGE_CAPTURE");
                    cameraintent.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(photo));
                    startActivityForResult(cameraintent, CAMERA_RESULT);
                } else {

                    Toast.makeText(this,
                            this.getResources().getString(R.string.no_storage), Toast.LENGTH_SHORT).show();
                }

            }
        }
        return true;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        try {

            String alertMsg = null;
            if (requestCode == CAMERA_RESULT && resultCode == RESULT_OK) {
                new UpdatePicTask().execute(photo.getAbsolutePath(),
                        getString(R.string.me_please_click_image));
            } else {
                if (data != null) {

                    Uri selectedImage = null;

                    if (requestCode == GALLERY_INTENT_CALLED) {
                        selectedImage = data.getData();
                        new UpdatePicTask().execute(
                                getRealPathFromURI(selectedImage),
                                getString(R.string.me_please_select_image));
                        new UpdatePicTask().execute(
                                getRealPathFromURI(selectedImage),
                                getString(R.string.me_please_select_image));

                    } else if (requestCode == GALLERY_KITKAT_INTENT_CALLED) {
                        selectedImage = data.getData();
                      /* final int takeFlags = data.getFlags()
                                & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        // Check for the freshest data.
                        getContentResolver().takePersistableUriPermission(selectedImage, takeFlags);*/


                        new UpdatePicTask().execute(
                                getRealPathFromURIForKitkat(selectedImage),
                                getString(R.string.me_please_select_image));

                    }


                } else {
                    if (requestCode == GALLERY_INTENT_CALLED || requestCode == GALLERY_KITKAT_INTENT_CALLED) {
                        alertMsg = getString(R.string.me_please_select_image);
                    } else if (requestCode == CAMERA_RESULT) {
                        alertMsg = getString(R.string.me_please_click_image);
                    }
                    if (!(isFinishing())) {

                        Toast.makeText(this, alertMsg, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } catch (OutOfMemoryError e) {
            ivPhoto.setImageBitmap(null);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class UpdatePicTask extends AsyncTask<String, Void, Void> {
        private boolean err = false;
        private String errMessage = "";
        private String imgPath;


        @Override
        protected Void doInBackground(String... params) {
            // TODO Auto-generated method stub
            try {
                imgPath = params[0];
                errMessage = params[1];

                if (imgPath != null) {
                    uploadBitmap = grabImage(imgPath);
                    uploadBitmap = uploadBitmap.copy(Bitmap.Config.ARGB_8888, true);
                }


                if (uploadBitmap == null) {
                    err = true;
                }
            } catch (Exception e) {
                err = true;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void params) {
            if (!isFinishing()) {

                if (err) {

                    Toast.makeText(MyProfileActivity.this, errMessage, Toast.LENGTH_SHORT).show();
                    ProfileStore.saveImagePath(MyProfileActivity.this, null);

                } else {

                    ivPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    ivPhoto.setImageBitmap(uploadBitmap);
                    ProfileStore.saveImagePath(MyProfileActivity.this, imgPath);

                }
            }
        }
    }

    public Bitmap grabImage(String path) throws FileNotFoundException,
            IOException {
        Bitmap bitmap = null;
        bitmap = GeneralUtils.decodeSampledBitmapFromPath(path, 250, 400);
        return bitmap;
    }

    public String getRealPathFromURI(Uri contentUri) {
        int column_index = 0;
        Cursor cursor = null;
        if (contentUri != null) {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = managedQuery(contentUri, proj, null, null, null);
            if (cursor == null)
                return null;
            column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
        }
        return cursor.getString(column_index);
    }

    public String getRealPathFromURIForKitkat(Uri contentUri) {
        String id = contentUri.getLastPathSegment().split(":")[1];
        final String[] imageColumns = {MediaStore.Images.Media.DATA};
        final String imageOrderBy = null;

        Uri uri = getUri();
        String selectedImagePath = "path";

        Cursor imageCursor = managedQuery(uri, imageColumns,
                MediaStore.Images.Media._ID + "=" + id, null, imageOrderBy);

        if (imageCursor.moveToFirst()) {
            selectedImagePath = imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DATA));
        }

        return selectedImagePath;
    }

    // By using this method get the Uri of Internal/External Storage for Media
    private Uri getUri() {
        String state = Environment.getExternalStorageState();
        if (!state.equalsIgnoreCase(Environment.MEDIA_MOUNTED))
            return MediaStore.Images.Media.INTERNAL_CONTENT_URI;

        return MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.camera, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.camera) {

            ImageButton cameraButton = (ImageButton) item.getActionView();

            cameraButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    openCameraAndSavePhoto(v);
                }
            });


        }

        return true;

    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
