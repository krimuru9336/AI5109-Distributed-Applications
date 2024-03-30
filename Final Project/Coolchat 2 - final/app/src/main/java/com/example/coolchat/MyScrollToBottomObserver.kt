package com.example.coolchat

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MyScrollToBottomObserver(
    private val recycler: RecyclerView,
    private val adapter: FriendlyMessageAdapter,
    private val manager: LinearLayoutManager
) : RecyclerView.AdapterDataObserver() {
    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
        super.onItemRangeInserted(positionStart, itemCount)
        val count = adapter.itemCount
        val lastVisiblePosition = manager.findLastCompletelyVisibleItemPosition()
        // If the recycler view is initially being loaded or the
        // user is at the bottom of the list, scroll to the bottom
        // of the list to show the newly added message.
        val loading = lastVisiblePosition == -1
        val atBottom = positionStart >= count - 1 && lastVisiblePosition == positionStart - 1
        if (loading || atBottom) {
            recycler.scrollToPosition(positionStart)
        }
    }
}