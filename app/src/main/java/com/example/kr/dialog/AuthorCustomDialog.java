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

public class AuthorCustomDialog extends DialogFragment  {
    public AuthorCustomDialog() {
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
        View root =  inflater.inflate(R.layout.dialog_author, container, false);

        TextView authorLinkTextView = root.findViewById(R.id.profile_link_textview);
        TextView projectLinkTextView = root.findViewById(R.id.link_textview);

        authorLinkTextView.setText(
                Html.fromHtml(
                        "<a href=\"https://github.com/DocZier\">"+ authorLinkTextView.getText().toString() + "</a> "));
        authorLinkTextView.setMovementMethod(LinkMovementMethod.getInstance());

        projectLinkTextView.setText(
                Html.fromHtml(
                        "<a href=\"https://github.com/DocZier/Project_ISoHd\">"+ projectLinkTextView.getText().toString() +"</a> "));
        projectLinkTextView.setMovementMethod(LinkMovementMethod.getInstance());

        return root;
    }
}