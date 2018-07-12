package com.fithub.codekienmee.fithub;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * DialogFragment that pops up with warning if user posts are missing fields.
 */
public class WarningDialog extends DialogFragment {

    private static final String WARNING_MESSAGE = "Warning Message: ";

    private WarningCallBack parent;
    private WarningEnum type;

    private Button cont;
    private Button ok;
    private Button cancel;
    private TextView message;

    // TODO: change type to Enum
    public static WarningDialog newInstance(WarningEnum type, WarningCallBack parent) {
         Bundle args = new Bundle();
         WarningDialog fragment = new WarningDialog();
         fragment.type = type;
         fragment.parent = parent;
         fragment.setArguments(args);
         return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_warning, container, false);
        this.initView(view);

        return view;
    }

    /**
     * Initializes view and widgets.
     */
    private void initView(View view) {
        this.cancel = view.findViewById(R.id.warning_cancel);
        this.cont = view.findViewById(R.id.warning_cont);
        this.message = view.findViewById(R.id.warning_message);
        this.ok = view.findViewById(R.id.warning_ok);

        this.message.setText(getString(this.type.getWarningMessage()));
        switch (this.type) {
            case UNSAVED_POST:
                this.iniOptionalWarning(view);
                break;
            case EMPTY_POST: case EMPTY_FIELDS: case INCORRECT_CRED:
                this.initCompulsoryWarning(view);
                break;
        }
    }

    /**
     * Initializes widgets for Unsaved Post Warning.
     */
    private void iniOptionalWarning(View view) {
        view.findViewById(R.id.warning_empty_post_bar).setVisibility(View.GONE);

        this.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parent.onCallBack(false);
                dismiss();
            }
        });

        this.cont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parent.onCallBack(true);
                dismiss();
            }
        });
    }

    /**
     * Initializes widgets for Empty Post Warning.
     */
    private void initCompulsoryWarning(View view) {
        view.findViewById(R.id.warning_new_post_bar).setVisibility(View.GONE);
        this.ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parent.onCallBack(false);
                dismiss();
            }
        });
    }
}