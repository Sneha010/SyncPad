package com.nearby.syncpad;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.instant.runtimepermission.MPermission;
import com.instant.runtimepermission.PermissionRequest;
import com.nearby.syncpad.models.Participant;
import com.nearby.syncpad.storedata.ProfileStore;
import com.nearby.syncpad.util.GeneralUtils;
import com.nearby.syncpad.util.ImageUtility;
import com.rey.material.widget.Button;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
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

    @BindView(R.id.myOrg)
    EditText myOrg;

    @BindView(R.id.myOrgTIL)
    TextInputLayout myOrgTIL;

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

    private Bitmap mProfileBitmap;

    @Inject
    FirebaseAuth mAuth;

    @Inject
    ProfileStore mProfileStore;

    @Inject
    ImageUtility mImageUtility;

    private FirebaseAuth.AuthStateListener mAuthListener;

    Unbinder binder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myprofile_layout);
        binder =  ButterKnife.bind(this);

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
            }
        });

        tvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
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
                    mProfileStore.clearProfileData();
                    startActivity(new Intent(MyProfileActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };
    }

    //To show the earlier saved data filled in the form fields
    private void initializeFieldsWithSavedData() {

        Participant participant = mProfileStore.getMyProfile();

        if (participant != null) {

            myName.setText(participant.getName());
            myEmailId.setText(participant.getEmailAddress());

            if(!GeneralUtils.isEmpty(participant.getRole()))
                myRole.setText(participant.getRole());
            if(!GeneralUtils.isEmpty(participant.getOrganisation()))
                myOrg.setText(participant.getOrganisation());

            if(!GeneralUtils.isEmpty(participant.getImageBytes())){
                mProfileBitmap = mImageUtility.getBitmapFromImageBytes(participant.getImageBytes());
                ivPhoto.setImageBitmap(mProfileBitmap);
            }


        }
    }

    //Click on save button on profile
    public void saveProfileData() {

        if (TextUtils.isEmpty(myName.getText().toString())) {
            myNameTIL.setErrorEnabled(true);
            myNameTIL.setError("Cannot be empty");
            return;
        }
        if (TextUtils.isEmpty(myOrg.getText().toString())) {
            myOrgTIL.setErrorEnabled(true);
            myOrgTIL.setError("Cannot be empty");
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

        String profileImageBytes = "";
        if (mProfileBitmap != null) {

            profileImageBytes = mImageUtility.getProfileImageBytes(mProfileBitmap);

            mProfileBitmap.recycle();

        }

        Participant participant = new Participant();
        participant.setName(myName.getText().toString());
        participant.setRole(myRole.getText().toString());
        participant.setEmailAddress(myEmailId.getText().toString());
        participant.setImageBytes(profileImageBytes);
        participant.setOrganisation(myOrg.getText().toString());

        mProfileStore.saveProfile(participant);

        finish();

    }

    final static int CAMERA_RESULT = 0;
    private static final int GALLERY_INTENT_CALLED = 1;
    private static final int GALLERY_KITKAT_INTENT_CALLED = 2;
    private File photo;

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        try {

            Uri selectedImage = null;
            String alertMsg = null;

            switch (requestCode) {

                case CAMERA_RESULT:

                    new UpdatePicTask().execute(photo.getAbsolutePath(),
                            getString(R.string.me_please_click_image));


                    break;
                case GALLERY_INTENT_CALLED:

                    if (data != null) {
                        selectedImage = data.getData();
                        new UpdatePicTask().execute(
                                mImageUtility.getRealPathFromURI(selectedImage),
                                getString(R.string.me_please_select_image));
                    } else {
                        alertMsg = getString(R.string.me_please_select_image);
                    }

                    break;
                case GALLERY_KITKAT_INTENT_CALLED:
                    if (data != null) {

                        selectedImage = data.getData();

                        new UpdatePicTask().execute(
                                mImageUtility.getRealPathFromURIForKitkat(selectedImage),
                                getString(R.string.me_please_select_image));
                    } else {
                        alertMsg = getString(R.string.me_please_select_image);
                    }

                    break;
            }
            if (!(isFinishing())) {

                Toast.makeText(this, alertMsg, Toast.LENGTH_SHORT).show();
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
                    mProfileBitmap = mImageUtility.grabImage(imgPath);
                    mProfileBitmap = mProfileBitmap.copy(Bitmap.Config.ARGB_8888, true);
                }


                if (mProfileBitmap == null) {
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

                } else {

                    ivPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    ivPhoto.setImageBitmap(mProfileBitmap);

                }
            }
        }
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

            final CharSequence[] items = {getString(R.string.gallery), getString(R.string.take_photo)};
            Dialog selectionDialog;
            AlertDialog.Builder builder = new AlertDialog.Builder(MyProfileActivity.this);
            builder.setTitle("Via");
            builder.setItems(items, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int via) {
                    performAction(via);
                }
            });
            selectionDialog = builder.create();
            selectionDialog.show();

        }

        return true;

    }

    private void performAction(int via) {
        switch (via) {
            case 0: {

                PermissionRequest.inside(this)
                        .withRequestId("Gallery")
                        .forPermissions(new MPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE,
                                "Application wants to read your image for profile picture"))
                        .request(new PermissionRequest.GrantPermissionListener() {
                            @Override
                            public void grantedPermission(List<String> permissions) {

                                startGallery();
                            }

                            @Override
                            public void rejected(List<String> permissions) {

                                // Do nothing or show error

                            }
                        });


                break;
            }

            case 1:
                startCamera();
        }
    }

    private void startGallery() {
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
    }

    private void startCamera() {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        binder.unbind();
    }
}
