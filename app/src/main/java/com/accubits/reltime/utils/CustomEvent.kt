package com.accubits.reltime.utils

import android.view.View
import smartadapter.Position
import smartadapter.SmartRecyclerAdapter
import smartadapter.viewevent.model.ViewEvent
import smartadapter.viewholder.SmartViewHolder

class CustomEvent(
    adapter: SmartRecyclerAdapter,
    viewHolder: SmartViewHolder<*>,
    position: Position,
    view: View
) : ViewEvent(adapter, viewHolder, position, view)