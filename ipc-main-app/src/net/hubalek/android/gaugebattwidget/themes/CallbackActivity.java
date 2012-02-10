package net.hubalek.android.gaugebattwidget.themes;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Test callback activity.
 */
public class CallbackActivity extends Activity {

    /**
     * Notifies and finishes immediately.
     *
     * @param savedInstanceState ignored.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toast.makeText(this, "Callback called...", Toast.LENGTH_LONG).show();
        finish();
    }
}
