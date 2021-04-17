package com.abhishekjagushte.engage.ui.main.screens.events

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.abhishekjagushte.engage.database.views.EventView
import com.abhishekjagushte.engage.repository.DataRepository
import com.abhishekjagushte.engage.ui.chat.adapters.EventDataItem
import com.abhishekjagushte.engage.utils.Constants
import java.lang.IllegalStateException
import javax.inject.Inject

class EventsFragmentViewModel @Inject constructor(
    val dataRepository: DataRepository
) : ViewModel() {

    fun getAllEvents(): LiveData<PagedList<EventDataItem>> {
        val factory: DataSource.Factory<Int, EventView> =
            dataRepository.getAllEvents()

        val convertedFactory = factory.map {
            return@map when(it.event_type){
                Constants.EVENT_TYPE_REMINDER -> EventDataItem.ReminderItem(it)
                Constants.EVENT_TYPE_POLL -> EventDataItem.PollItem(it)
                else -> throw IllegalStateException("Event type incorrect")
            }
        }

        val pagedListBuilder: LivePagedListBuilder<Int, EventDataItem> = LivePagedListBuilder(convertedFactory, 20)

        return pagedListBuilder.build()
    }
}
