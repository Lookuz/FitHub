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
    private static final String MESSAGE_TYPE = "Message Type: ";
    private static final int TYPE_NEW_POST = 1;

    private WarningCallBack parent;
    private int type;

    private Button ok;
    private Button cancel;
    private TextView message;

    // TODO: change type to Enum
    public static WarningDialog newInstance(String warningMessage, int type,
                                            WarningCallBack parent) {
         Bundle args = new Bundle();
         args.putString(WARNING_MESSAGE, warningMessage);
         args.putInt(MESSAGE_TYPE, type);
         WarningDialog fragment = new WarningDialog();
         fragment.parent = parent;
         fragment.setArguments(args);
         return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.type = getArguments().getInt(MESSAGE_TYPE);
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
     * @param view
     */
    private void initView(View view) {
        this.cancel = view.findViewById(R.id.warning_cancel);
        this.ok = view.findViewById(R.id.warning_ok);
        this.message = view.findViewById(R.id.warning_message);

        this.message.setText(this.getArguments().getString(WARNING_MESSAGE));
        if (this.type == TYPE_NEW_POST) {
            this.cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    parent.onCallBack(false);
                    dismiss();
                }
            });

            this.ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    parent.onCallBack(true);
                    dismiss();
                }
            });
        }
    }
}