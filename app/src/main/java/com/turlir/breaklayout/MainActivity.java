package com.turlir.breaklayout;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.BoringLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.turlir.breaklayout.layout.BreakLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements SettingsFragment.Callback {

    @BindView(R.id.target)
    BreakLayout target;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
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
        SettingsFragment frg = SettingsFragment.newInstance(target.getChildCount(), target.getMode());
        frg.show(getFragmentManager(), null);
    }

    @Override
    public void newValue(int c, int m) {
        target.setMode(m);
        if (c > target.getChildCount()) {
            View childAt = target.getChildAt(0);
            for (int i = 0; i < c - target.getChildCount(); i++) {
                View v = new View(getApplicationContext());
                v.setLayoutParams(childAt.getLayoutParams());
                v.setBackgroundColor(getColor(R.color.primaryLight));
                target.addView(v);
            }
        } else {
            for (int i = target.getChildCount() - 1; i >= c; i--) {
                target.removeViewAt(i);
            }
        }
    }
}
