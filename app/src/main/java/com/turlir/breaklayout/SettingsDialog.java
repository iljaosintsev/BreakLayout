package com.turlir.breaklayout;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.turlir.breaklayout.layout.BreakLayout;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsDialog extends DialogFragment {

    @BindView(R.id.settings_child)
    TextView count;

    @BindView(R.id.settings_spinner)
    Spinner mode;

    private Callback mCallback;
    private int mCount, mMode;

    private static final Map<String, Integer> sMap;
    private static final String[] sKeys;
    private static final Integer[] sValue;
    static {
        sMap = new LinkedHashMap<>();
        sMap.put("RIGHT", BreakLayout.MODE_RIGHT);
        sMap.put("LEFT", BreakLayout.MODE_LEFT);
        sMap.put("CENTER", BreakLayout.MODE_CENTER);
        sMap.put("EDGE", BreakLayout.MODE_EDGE);
        sMap.put("AS_IS", BreakLayout.MODE_AS_IS);

        sKeys = sMap.keySet().toArray(new String[sMap.size()]);
        sValue = sMap.values().toArray(new Integer[sMap.size()]);
    }

    public static SettingsDialog newInstance(int c, int m) {
        Bundle args = new Bundle();
        args.putInt("C", c);
        args.putInt("M", m);
        SettingsDialog settingsDialog = new SettingsDialog();
        settingsDialog.setArguments(args);
        return settingsDialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setIcon(android.R.drawable.ic_menu_edit)
                .setTitle("Settings")
                .setView(resolveView())
                .create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof Callback) {
            mCallback = (Callback) activity;
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        mCallback.newValue(mCount, getPosition());
    }

    private View resolveView() {
        LayoutInflater inflater = LayoutInflater.from(getActivity().getApplicationContext());
        View root = inflater.inflate(R.layout.dialog_settings, null);
        ButterKnife.bind(this, root);
        mCount = getArguments().getInt("C");
        mMode = getArguments().getInt("M");
        ArrayAdapter<String> mAdapter = new ArrayAdapter<>(getActivity().getApplicationContext(),
                android.R.layout.simple_list_item_1, sKeys);
        mode.setAdapter(mAdapter);
        mode.setSelection(getPosition());
        mode.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mMode = position;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
        update();
        return root;
    }


    @OnClick(R.id.settings_btn_plus)
    public void plus() {
        mCount++;
        mCount = Math.min(mCount, 10);
        update();
    }

    @OnClick(R.id.settings_btn_minus)
    public void minus() {
        mCount--;
        mCount = Math.max(mCount, 1);
        update();
    }

    private void update() {
        count.setText(String.valueOf(mCount));
    }

    private int getPosition() {
        return Arrays.binarySearch(sValue, mMode);
    }

    public interface Callback {
        void newValue(int c, int m);
    }

}
