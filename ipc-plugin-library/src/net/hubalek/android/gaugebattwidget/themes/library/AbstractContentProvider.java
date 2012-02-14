package net.hubalek.android.gaugebattwidget.themes.library;

/**
 */
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import static net.hubalek.android.gaugebattwidget.themes.library.Constants.*;


/**
 * Providers weather icons.
 */
public abstract class AbstractContentProvider extends ContentProvider {

    /**
     * Dummy implementation doing nothing.
     */
    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    /**
     * Dummy implementation doing nothing.
     */
    @Override
    public String getType(Uri uri) {
        return DEFAULT_CONTENT_TYPE;
    }

    /**
     * Dummy implementation doing nothing.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    /**
     * Dummy implementation doing nothing.
     */
    @Override
    public boolean onCreate() {
        return false;
    }

    /**
     * Returns file from assets.
     */
    @Override
    public AssetFileDescriptor openAssetFile(Uri uri, String mode) throws FileNotFoundException {

//        Log.d(Constants.LOG_TAG, "AbstractContentProvider.openAssetFile(" + uri + "," + mode + " called...");

        if (uri.getPathSegments().size() > 1 && uri.getPathSegments().get(0).equals(CONTENT_PROVIDER_PATH_ELEMENT_ICONS)) {

            String icon_name = uri.getPathSegments().get(1);

            if (icon_name.equals(CONTENT_PROVIDER_PATH_ELEMENT_SAMPLE_ICON_0)) {
                icon_name = DEFAULT_SAMPLE_ICON_NAME_0;
            } else if (icon_name.equals(CONTENT_PROVIDER_PATH_ELEMENT_SAMPLE_ICON_25)) {
                icon_name = DEFAULT_SAMPLE_ICON_NAME_25;
            } else if (icon_name.equals(CONTENT_PROVIDER_PATH_ELEMENT_SAMPLE_ICON_50)) {
                icon_name = DEFAULT_SAMPLE_ICON_NAME_50;
            } else if (icon_name.equals(CONTENT_PROVIDER_PATH_ELEMENT_SAMPLE_ICON_75)) {
                icon_name = DEFAULT_SAMPLE_ICON_NAME_75;
            } else if (icon_name.equals(CONTENT_PROVIDER_PATH_ELEMENT_SAMPLE_ICON_100)) {
                icon_name = DEFAULT_SAMPLE_ICON_NAME_100;
            }

            try {
                final AssetFileDescriptor fileDescriptor = getContext().getAssets().openFd(icon_name + ICON_FILE_EXTENSION);
//                Log.d(Constants.LOG_TAG, "fileDescriptor=" + fileDescriptor);
                return fileDescriptor;
            } catch (IOException e) {
                Log.e(Constants.LOG_TAG, "ERROR: " + e);
                throw new FileNotFoundException("Error loading file ");
            }
        } else {
            Log.w(Constants.LOG_TAG, "Path segments.size() < 2");
            return null;
        }
    }


    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings1, String s1) {
        final List<String> pathSegments = uri.getPathSegments();

        // handling url in form content://my.provider.com/info
        if (pathSegments.size() > 0 && pathSegments.get(0).equals(CONTENT_PROVIDER_PATH_ELEMENT_INFO)) {

            Resources resources = getContext().getResources();

            MatrixCursor retVal = new MatrixCursor(new String[]{
                    CONTENT_PROVIDER_CURSOR_COLUMN_AUTHOR,
                    CONTENT_PROVIDER_CURSOR_COLUMN_EMAIL,
                    CONTENT_PROVIDER_CURSOR_COLUMN_URL,
                    CONTENT_PROVIDER_CURSOR_COLUMN_DESCRIPTION,

                    CONTENT_PROVIDER_CURSOR_COLUMN_DONATE_CURRENCY,
                    CONTENT_PROVIDER_CURSOR_COLUMN_DONATE_AMOUNT,
                    CONTENT_PROVIDER_CURSOR_COLUMN_DONATE_EMAIL
            });
            retVal.addRow(new String[]{
                    resources.getString(getAuthorResourceId()),
                    resources.getString(getAuthorUrlResourceId()),
                    resources.getString(getAuthorEmailResourceId()),
                    resources.getString(getDescriptionResourceId()),

                    getDonationCurrencyResourceId() > 0 ? resources.getString(getDonationCurrencyResourceId()) : "",
                    getDonationAmountResourceId() > 0 ? resources.getString(getDonationAmountResourceId()) : "0.0",
                    getDonationEmailResourceId() > 0 ? resources.getString(getDonationEmailResourceId()) : "",
            }
            );

            return retVal;
        }
        return null;
    }

    protected abstract int getAuthorEmailResourceId();

    protected abstract int getDonationEmailResourceId();

    protected abstract int getDonationAmountResourceId();

    protected abstract int getDonationCurrencyResourceId();

    protected abstract int getDescriptionResourceId();

    protected abstract int getAuthorUrlResourceId();

    protected abstract int getAuthorResourceId();

    /**
     * Dummy implementation doing nothing.
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}
