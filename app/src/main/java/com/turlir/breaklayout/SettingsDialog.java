package com.turlir.breaklayout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.turlir.breaklayout.layout.Mode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;

public class SettingsDialog extends DialogFragment {

    private static final String
            ALL = "ALL",
            CURRENT = "CURRENT";

    @BindView(R.id.settings_child)
    TextView tvCount;

    @BindView(R.id.settings_spinner)
    Spinner spinModes;

    private Callback mCallback;
    private CountChangedListener mCountListener;

    private Mode[] mAllModes;
    private Mode mCurrentMode;
    private int mIndex = -1;

    public static SettingsDialog newInstance(Mode[] allModes, Mode current) {
        Bundle args = new Bundle();
        args.putParcelableArray(ALL, allModes);
        args.putParcelable(CURRENT, current);
        SettingsDialog instance = new SettingsDialog();
        instance.setArguments(args);
        return instance;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mAllModes = (Mode[]) getArguments().getParcelableArray(ALL);
        mCurrentMode = getArguments().getParcelable(CURRENT);
        for (int i = 0; i < mAllModes.length && mCurrentMode != null; i++) {
            if (mAllModes[i].equals(mCurrentMode)) { // id
                mIndex = i;
                break;
            }
        }
        // build dialog
        return new AlertDialog.Builder(getActivity())
                .setIcon(android.R.drawable.ic_menu_edit)
                .setTitle(R.string.dialog_settings)
                .setView(createView())
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mCallback.newMode(mCurrentMode);
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof Callback) {
            mCallback = (Callback) activity;
        } else {
            throw new IllegalArgumentException("activity must implement callback");
        }
    }

    @OnClick(R.id.settings_btn_plus)
    public void plus() {
        getCurrentMode().inc();
        mCountListener.onCountChanged();
    }

    @OnClick(R.id.settings_btn_minus)
    public void minus() {
        getCurrentMode().dec();
        mCountListener.onCountChanged();
    }

    @OnItemSelected(R.id.settings_spinner)
    public void modeSelected(int position) {
        mIndex = position;
        int id = mAllModes[mIndex].getId();
        int count = mCurrentMode.getCount();
        mCurrentMode = new Mode(id, count);
    }

    private View createView() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        @SuppressLint("InflateParams")
        View root = inflater.inflate(R.layout.dialog_settings, null);
        ButterKnife.bind(this, root);
        bindView();
        return root;
    }

    private void bindView() {
        ArrayAdapter adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, mAllModes);
        spinModes.setAdapter(adapter);
        spinModes.setSelection(mIndex);
        mCountListener = new CountChangedListener() {
            @Override
            public void onCountChanged() {
                tvCount.setText(String.valueOf(getCurrentMode().getCount()));
            }
        };
        mCountListener.onCountChanged();
    }

    private Mode getCurrentMode() {
        return mCurrentMode;
    }

    public interface Callback {
        void newMode(Mode m);
    }

    private interface CountChangedListener {
        void onCountChanged();
    }

}
