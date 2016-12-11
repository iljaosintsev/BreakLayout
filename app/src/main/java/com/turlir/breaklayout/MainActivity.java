package com.turlir.breaklayout;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.turlir.breaklayout.layout.BreakLayout;
import com.turlir.breaklayout.layout.Mode;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements SettingsDialog.Callback {

    @BindView(R.id.target)
    BreakLayout target;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Toolbar t = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(t);
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
        Mode current = new Mode(target.getMode(), target.getChildCount());
        SettingsDialog frg = SettingsDialog.newInstance(Mode.MODES, current);
        frg.show(getFragmentManager(), null);
    }

    @Override
    public void newMode(Mode n) {
        int m = n.getId();
        int c = n.getCount();
        // old algorithm
        int tmp = target.getMode();
        target.setMode(m);
        if (c > target.getChildCount()) {
            View childAt = target.getChildAt(0);
            for (int i = 0; i < c - target.getChildCount(); i++) {
                View v = new View(getApplicationContext());
                v.setLayoutParams(childAt.getLayoutParams());
                v.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
                target.addView(v);
            }
        } else if (c < target.getChildCount()) {
            for (int i = target.getChildCount() - 1; i >= c; i--) {
                target.removeViewAt(i);
            }
        } else if(tmp != m) {
            target.requestLayout();
        }
    }

}
