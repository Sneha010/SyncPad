package com.nearby.syncpad;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.nearby.syncpad.data.MeetingNotesLoader;
import com.nearby.syncpad.util.GeneralUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.nearby.syncpad.R.id.tvNotes;

/**
 * Created by Sneha Khadatare on 11/20/2016.
 */

public class MeetingDetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "MeetingDetailsActivity";

    @BindView(tvNotes)
    TextView mTvNotes;

    @BindView(R.id.tvParticipant)
    TextView mTvParticipant;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    private Unbinder mUnbinder;
    private Cursor mCursor;
    private String mItemId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.meetings_details_layout);
        mUnbinder = ButterKnife.bind(this);




        if (savedInstanceState == null) {
            if (getIntent() != null) {
               //mId = ItemsContract.Items.getItemId(getIntent().getData());
                mItemId = getIntent().getExtras().getString("item_id");
            }
        }
        getLoaderManager().initLoader(0, null, this);

        initialise();
        displayMeetingDetails();
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    public void initialise() {

        setSupportActionBar(mToolbar);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                supportFinishAfterTransition();
            }
        });


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

    }

    private void displayMeetingDetails() {

        //TODO display meeting using bean

        if (mCursor != null) {

            getSupportActionBar().setTitle(mCursor.getString(MeetingNotesLoader.Query.MEETING_NAME));

            if (!GeneralUtils.isEmpty(mCursor.getString(MeetingNotesLoader.Query.MEETING_NOTES)))
                mTvNotes.setText(mCursor.getString(MeetingNotesLoader.Query.MEETING_NOTES));
            if (!GeneralUtils.isEmpty(mCursor.getString(MeetingNotesLoader.Query.MEETING_PARTICIPANTS)))
            mTvParticipant.setText(mCursor.getString(MeetingNotesLoader.Query.MEETING_PARTICIPANTS));

        }

    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return MeetingNotesLoader.newInstanceForItemId(this, mItemId);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        mCursor = cursor;
        if (mCursor != null && !mCursor.moveToFirst()) {
            Log.e(TAG, "Error reading item detail cursor");
            mCursor.close();
            mCursor = null;
        }
        displayMeetingDetails();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursor = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }
}
