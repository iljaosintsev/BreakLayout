package com.turlir.example;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.turlir.breaklayout.BreakLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends Activity implements SettingsDialog.Callback {

    private static final int[] MODELS = new int[] {
            BreakLayout.MODE_RIGHT,
            BreakLayout.MODE_LEFT,
            BreakLayout.MODE_CENTER,
            BreakLayout.MODE_EDGE,
            BreakLayout.MODE_AS_IS,
    };

    private static final String BUNDLE_MODEL = "BUNDLE_MODEL";

    @BindView(R.id.target)
    BreakLayout target;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        if (state != null) {
            newMode((Model) state.getParcelable(BUNDLE_MODEL));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Model m = new Model(target.getMode(), target.getChildCount());
        outState.putParcelable(BUNDLE_MODEL, m);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.settings) {
            openSettingsDialog();
        }
        return false;
    }

    private void openSettingsDialog() {
        Model current = new Model(target.getMode(), target.getChildCount());
        SettingsDialog frg = SettingsDialog.newInstance(MODELS, current);
        frg.show(getFragmentManager(), null);
    }

    @Override
    public void newMode(Model n) {
        int m = n.getId();
        int c = n.getCount();
        // old algorithm
        int tmp = target.getMode();
        if (c > target.getChildCount()) {
            View childAt = target.getChildAt(0);
            for (int i = 0; i <= c - target.getChildCount(); i++) {
                View v = new View(getApplicationContext());
                v.setLayoutParams(childAt.getLayoutParams());
                v.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                target.addView(v);
            }
        } else if (c < target.getChildCount()) {
            for (int i = target.getChildCount() - 1; i >= c; i--) {
                target.removeViewAt(i);
            }
        }
        if (tmp != m) {
            target.setMode(m);
        }
    }

}
