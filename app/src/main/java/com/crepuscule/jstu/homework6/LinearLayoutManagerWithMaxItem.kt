package com.crepuscule.jstu.homework6

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/*
* 当 item 数量不超过 maxItem 时自动布局，超过时固定高度
* 参考资料 https://juejin.cn/post/6891899635296632846
*/

class LinearLayoutManagerWithMaxItem(context: Context?, maxItem : Int = 100) : LinearLayoutManager(context) {
    private val maxItem : Int = maxItem

    override fun onMeasure(
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State,
        widthSpec: Int,
        heightSpec: Int
    ) {
        if (itemCount <= maxItem) {
            return super.onMeasure(recycler, state, widthSpec, heightSpec)
        } else {
            // 测量一个 item 的宽和高
            val child = recycler.getViewForPosition(0)
            addView(child)
            measureChildWithMargins(child, 0, 0)
            val itemWidth = getDecoratedMeasuredWidth(child)
            val itemHeight = getDecoratedMeasuredHeight(child)
            removeAndRecycleView(child, recycler)

            val widthMode = View.MeasureSpec.getMode(widthSpec)
            val heightMode = View.MeasureSpec.getMode(heightSpec)
            var width = 0
            var height = 0

            if (orientation == HORIZONTAL) {
                height = if (heightMode == View.MeasureSpec.EXACTLY) {
                    View.MeasureSpec.getSize(heightSpec)
                } else {
                    itemHeight
                }
                width = itemWidth * maxItem
            } else {
                width = if (widthMode == View.MeasureSpec.EXACTLY) {
                    View.MeasureSpec.getSize(widthSpec)
                } else {
                    itemWidth
                }
                height = itemHeight * maxItem
            }
            setMeasuredDimension(width, height)
        }
    }

    override fun isAutoMeasureEnabled(): Boolean {
        if (itemCount <= maxItem) return super.isAutoMeasureEnabled()
        return false
    }
}