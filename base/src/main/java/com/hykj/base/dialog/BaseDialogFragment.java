package com.hykj.base.dialog;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.hykj.base.R;

/**
 * 基础类
 */
public class BaseDialogFragment extends DialogFragment {

    protected Object mTagEx;

    public Object getTagEx() {
        return mTagEx;
    }

    public void setTag(Object tagEx) {
        this.mTagEx = tagEx;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new Dialog(getActivity(), R.style.CustomDialog);
    }
}
