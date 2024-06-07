package com.example.kr.dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.kr.R;

public class AboutResourcesCustomDialog extends DialogFragment {
    public AboutResourcesCustomDialog() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        View root =  inflater.inflate(R.layout.dialog_resources, container, false);

        TextView projectLinkTextView = root.findViewById(R.id.link_textview);

        projectLinkTextView.setText(
                Html.fromHtml(
                        "<a href=\"https://yandex.ru/legal/maps_termsofuse/\">"+ projectLinkTextView.getText().toString() +"</a> "));
        projectLinkTextView.setMovementMethod(LinkMovementMethod.getInstance());

        return root;
    }
}