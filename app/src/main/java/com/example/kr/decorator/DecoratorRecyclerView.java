package com.example.kr.decorator;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class DecoratorRecyclerView extends RecyclerView.ItemDecoration
{
    private int space;

    public DecoratorRecyclerView(int space)
    {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state)
    {
        outRect.bottom = space;
        if (parent.getChildAdapterPosition(view) == 0)
        {
            outRect.top = space;
        }
        if (parent.getChildAdapterPosition(view) == 45)
        {
            outRect.bottom = 50;
        }
    }
}
