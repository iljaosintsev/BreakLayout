package com.turlir.example;

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

import com.turlir.breaklayout.BreakLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;

public class SettingsDialog extends DialogFragment {

    private static final int[] MODELS = new int[] {
            BreakLayout.MODE_RIGHT,
            BreakLayout.MODE_LEFT,
            BreakLayout.MODE_CENTER,
            BreakLayout.MODE_EDGE,
            BreakLayout.MODE_AS_IS,
    };

    private static final String
            ARG_CURRENT_MODEL = "ARG_CURRENT_MODEL",
            BUNDLE_CURRENT_MODEL = "BUNDLE_CURRENT_MODEL";

    @BindView(R.id.settings_child)
    TextView tvCount;

    @BindView(R.id.settings_spinner)
    Spinner spinModes;

    private Callback mCallback;
    private final CountChangedListener mCountListener = new CountChangedListener() {
        @Override
        public void onCountChanged() {
            tvCount.setText(String.valueOf(getCurrentMode().getCount()));
        }
    };

    private Model mCurrent;
    private int mIndex = -1;

    public static SettingsDialog newInstance(Model current) {
        Bundle args = new Bundle();
        args.putParcelable(ARG_CURRENT_MODEL, current);
        SettingsDialog instance = new SettingsDialog();
        instance.setArguments(args);
        return instance;
    }

    @Override
    public Dialog onCreateDialog(Bundle state) {
        // restore state
        if (state != null) {
            mCurrent = state.getParcelable(BUNDLE_CURRENT_MODEL);
        } else {
            mCurrent = getArguments().getParcelable(ARG_CURRENT_MODEL);
        }
        // select index current model
        for (int i = 0; i < MODELS.length && mCurrent != null; i++) {
            if (MODELS[i] == mCurrent.getId()) { // id
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
                        mCallback.newMode(mCurrent);
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(BUNDLE_CURRENT_MODEL, mCurrent);
        super.onSaveInstanceState(outState);
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
        @BreakLayout.Modes int id = MODELS[mIndex];
        int count = mCurrent.getCount();
        mCurrent = new Model(id, count);
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
        String[] array = new String[MODELS.length];
        for (int i = 0; i < array.length; i++) {
            array[i] = Model.MAPPER.map(MODELS[i]);
        }
        ArrayAdapter adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, array);
        spinModes.setAdapter(adapter);
        spinModes.setSelection(mIndex);
        mCountListener.onCountChanged();
    }

    private Model getCurrentMode() {
        return mCurrent;
    }

    public interface Callback {
        void newMode(Model m);
    }

    private interface CountChangedListener {
        void onCountChanged();
    }

}
