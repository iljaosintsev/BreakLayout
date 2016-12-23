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

    private static final Model[] MODELS = new Model[]{
            new Model(BreakLayout.MODE_RIGHT),
            new Model(BreakLayout.MODE_LEFT),
            new Model(BreakLayout.MODE_CENTER),
            new Model(BreakLayout.MODE_EDGE),
            new Model(BreakLayout.MODE_AS_IS),
    };

    @BindView(R.id.target)
    BreakLayout target;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
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
        } else if (tmp != m) {
            target.setMode(m);
        }
    }

}
