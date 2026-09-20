
package com.evancharlton.mileage;

import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public abstract class BaseActivity extends AppCompatActivity {
    protected Toolbar initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        int paddingLeft = toolbar.getPaddingLeft();
        int paddingRight = toolbar.getPaddingRight();
        int paddingBottom = toolbar.getPaddingBottom();
        ViewCompat.setOnApplyWindowInsetsListener(toolbar, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(
                    WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout());
            v.setPadding(paddingLeft, insets.top, paddingRight, paddingBottom);
            return windowInsets;
        });

        return toolbar;
    }

    /**
     * Pads {@code view} for the bottom system bar inset, for views pinned to the bottom edge of
     * the screen (e.g. a submit button or bottom navigation bar) that would otherwise be
     * obscured by the gesture/button navigation bar under edge-to-edge display.
     */
    protected static void applyBottomInset(View view) {
        int paddingLeft = view.getPaddingLeft();
        int paddingTop = view.getPaddingTop();
        int paddingRight = view.getPaddingRight();
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(
                    WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout());
            v.setPadding(paddingLeft, paddingTop, paddingRight, insets.bottom);
            return windowInsets;
        });
    }
}
