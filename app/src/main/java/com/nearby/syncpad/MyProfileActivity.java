package com.nearby.syncpad;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.instant.runtimepermission.MPermission;
import com.instant.runtimepermission.PermissionRequest;
import com.nearby.syncpad.models.Participant;
import com.nearby.syncpad.storedata.ProfileStore;
import com.nearby.syncpad.util.GeneralUtils;
import com.nearby.syncpad.util.ImageUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class MyProfileActivity extends BaseActivity {


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

    private Bitmap mProfileBitmap;

    @Inject
    FirebaseAuth mAuth;

    @Inject
    ProfileStore mProfileStore;

    @Inject
    ImageUtils mImageUtility;

    private FirebaseAuth.AuthStateListener mAuthListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myprofile_layout);
        mUnbinder = ButterKnife.bind(this);

        ((SyncPadApplication) getApplication()).getMyApplicationComponent().inject(this);

        setUpFirebaseLogout();
        setToolbar();
        initializeFieldsWithSavedData();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    private void setToolbar() {

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.my_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchMainActivity();
            }
        });


    }

    private void setUpFirebaseLogout() {

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    mProfileStore.clearProfileData();
                    mProfileStore.firstLaunchDone(false);
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

            if (!GeneralUtils.isEmpty(participant.getRole()))
                myRole.setText(participant.getRole());
            if (!GeneralUtils.isEmpty(participant.getOrganisation()))
                myOrg.setText(participant.getOrganisation());

            if (!GeneralUtils.isEmpty(participant.getImageBytes())) {
                mProfileBitmap = mImageUtility.getBitmapFromImageBytes(participant.getImageBytes());
                ivPhoto.setImageBitmap(mProfileBitmap);
            }


        }
    }

    @OnClick(R.id.tvLogout)
    public void logOut(View view) {
        mAuth.signOut();
    }

    //Click on save button on profile
    @OnClick(R.id.buttonSave)
    public void saveProfileData(View view) {

        if (TextUtils.isEmpty(myName.getText().toString())) {
            myNameTIL.setErrorEnabled(true);
            myNameTIL.setError(getString(R.string.cannot_be_empty));
            return;
        }
        if (TextUtils.isEmpty(myOrg.getText().toString())) {
            myOrgTIL.setErrorEnabled(true);
            myOrgTIL.setError(getString(R.string.cannot_be_empty));
            return;
        }
        if (TextUtils.isEmpty(myRole.getText().toString())) {
            myRoleTIL.setErrorEnabled(true);
            myRoleTIL.setError(getString(R.string.cannot_be_empty));
            return;
        }

        if (TextUtils.isEmpty(myEmailId.getText().toString())) {
            myEmailTIL.setErrorEnabled(true);
            myEmailTIL.setError(getString(R.string.cannot_be_empty));
            return;
        }

        String profileImageBytes = "";
        if (mProfileBitmap != null) {

            profileImageBytes = mImageUtility.getProfileImageBytes(mProfileBitmap);

            mProfileBitmap.recycle();

        } else {

            mProfileBitmap = BitmapFactory.decodeResource(getResources(),
                    R.drawable.default_user);
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

        launchMainActivity();

    }

    private void launchMainActivity() {

        if (!mProfileStore.isFirstLaunchDone()) {
            mProfileStore.firstLaunchDone(true);
            startActivity(new Intent(MyProfileActivity.this, MainActivity.class));
        }
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
            if (!(isFinishing()) && !GeneralUtils.isEmpty(alertMsg)) {

                GeneralUtils.displayCustomToast(this, alertMsg);
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

                    if (!GeneralUtils.isEmpty(MyProfileActivity.this))
                        GeneralUtils.displayCustomToast(MyProfileActivity.this, errMessage);

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

    private void performAction(final int via) {

        PermissionRequest.inside(this)
                .withRequestId(getString(R.string.gallery))
                .forPermissions(new MPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
                        getString(R.string.photo_permission)))
                .request(new PermissionRequest.GrantPermissionListener() {
                    @Override
                    public void grantedPermission(List<String> permissions) {

                        switch (via) {
                            case 0: {


                                startGallery();
                                break;
                            }

                            case 1:
                                startCamera();
                        }
                    }

                    @Override
                    public void rejected(List<String> permissions) {

                        // Do nothing or show error

                    }
                });

    }

    private void startGallery() {
        if (Build.VERSION.SDK_INT < 20) {
            Intent intent = new Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            intent.setType(getString(R.string.image_path));

            startActivityForResult(Intent.createChooser(intent,
                    getString(R.string.select_pic)), GALLERY_INTENT_CALLED);
        } else {
            //For kitkat and above versions..
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType(getString(R.string.image_path));
            //intent.setType("image/jpeg");
            startActivityForResult(intent, GALLERY_KITKAT_INTENT_CALLED);
        }
    }

    private void startCamera() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyyMMdd-HHmmss");
        if (GeneralUtils.checkSDCard()) {
            photo = new File(Environment.getExternalStorageDirectory(),
                    dateFormat.format(new Date()) + getString(R.string.jpeg_ext));
            Intent cameraintent = new Intent(
                    "android.media.action.IMAGE_CAPTURE");
            cameraintent.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(photo));
            startActivityForResult(cameraintent, CAMERA_RESULT);
        } else {

            GeneralUtils.displayCustomToast(this,
                    this.getResources().getString(R.string.no_storage));
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

}
