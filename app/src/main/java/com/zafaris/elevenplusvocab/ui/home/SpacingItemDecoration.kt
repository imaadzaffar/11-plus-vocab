package com.zafaris.elevenplusvocab.ui.home

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class SpacingItemDecoration(
		private val spacing: Int,
		private val spanCount: Int,
		private val includeEdge: Boolean
) : RecyclerView.ItemDecoration() {

	override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
		val position = parent.getChildAdapterPosition(view)
		val column = position % spanCount

		if (includeEdge) {
			outRect.left = spacing - column * spacing / spanCount // spacing - column * ((1f / spanCount) * spacing)
			outRect.right = (column + 1) * spacing / spanCount // (column + 1) * ((1f / spanCount) * spacing)

			if (position < spanCount) { // top edge
				outRect.top = spacing
			}
			outRect.bottom = spacing // item bottom
		} else {
			outRect.left = column * spacing / spanCount // column * ((1f / spanCount) * spacing)
			outRect.right = spacing - (column + 1) * spacing / spanCount // spacing - (column + 1) * ((1f /    spanCount) * spacing)
			if (position >= spanCount) {
				outRect.top = spacing // item top
			}
		}
	}
}