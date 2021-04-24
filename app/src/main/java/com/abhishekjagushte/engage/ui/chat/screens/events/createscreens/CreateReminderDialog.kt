package com.abhishekjagushte.engage.ui.chat.screens.events.createscreens

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CalendarView
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.abhishekjagushte.engage.EngageApplication
import com.abhishekjagushte.engage.R
import com.abhishekjagushte.engage.database.entities.Event
import com.abhishekjagushte.engage.database.entities.jsonmodels.Reminder
import com.abhishekjagushte.engage.repository.DataRepository
import com.abhishekjagushte.engage.utils.Constants
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.android.synthetic.main.fragment_create_reminder_dialog.*
import java.util.*
import javax.inject.Inject


class CreateReminderDialog : BottomSheetDialogFragment() {

    private val TAG = "CreateReminderDialog"

    @Inject
    lateinit var dataRepository: DataRepository

    private var selectedDate: Long =  System.currentTimeMillis()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as EngageApplication).appComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(
            R.layout.fragment_create_reminder_dialog,
            container,
            false
        )

        val args by navArgs<CreateReminderDialogArgs>()
        val conType = args.conversationType
        val conversationID: String? =  args.conversationID

        val calendarView = view.findViewById<CalendarView>(R.id.calendarView)
        calendarView.minDate = System.currentTimeMillis()


        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            run {
                val c = Calendar.getInstance()
                c[year, month] = dayOfMonth
                selectedDate = c.timeInMillis
            }
        }


        view.findViewById<Button>(R.id.create_reminder_button).setOnClickListener {
            val createdTime = System.currentTimeMillis()
            val reminderTime = getInputUTCDate()
            val offset = reminderTime - createdTime
            val title = reminder_text_input.text.toString()
            val description = reminder_description_input.text.toString()
            val reminder = Reminder(
                title,
                description,
                Constants.REMINDER_STATUS_ACTIVE,
                createdTime,
                reminderTime,
                offset
            )
            val event = createReminderEvent(reminder, conType, conversationID)

            Log.d(
                TAG,
                "onCreateView: created = ${createdTime} reminderTime = ${reminder.reminderTime} offset = ${
                    Date(
                        offset
                    )
                }, $offset"
            )
            
            dataRepository.createReminderEvent(event)
            findNavController().navigateUp()
        }

        return view
    }


    private fun createReminderEvent(reminder: Reminder, conType: Int, conversationID: String?): Event {
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val jsonAdapter: JsonAdapter<Reminder> = moshi.adapter(Reminder::class.java)
        val json = jsonAdapter.toJson(reminder)
        return Event(
            eventID = "Will be set later",
            conversationID = conversationID!!,
            type = Constants.EVENT_MINE,
            status = Constants.STATUS_NOT_SENT,
            timeStamp = Date(reminder.createdTime),
            data = json,
            senderID = "set it later (my username)",
            receiverID = if (conType == Constants.CONVERSATION_TYPE_121) conversationID else null,
            deleted = Constants.DELETED_NO,
            lastUpdatedTimestamp = Date(reminder.createdTime),
            event_type = Constants.EVENT_TYPE_REMINDER,
            needs_push = Constants.NEEDS_PUSH_YES,
            conType = conType
        )
    }

    //TODO convert this time to UTC
    private fun getInputUTCDate(): Long{
        val calendarDate = selectedDate
        var time = calendarDate - calendarDate%(24*60*60*1000)
        Log.e(TAG, "getInputUTCDate: Initial Date = $time  Date = ${Date(time)}")
        val hour = timePicker.hour
        val minutes = timePicker.minute
        time += hour * (1000 * 60 * 60) + minutes * (1000 * 60)  //This is a date according to the setter's timezone, convert it as per UTC
        val timeZone =  TimeZone.getDefault()
        time -= timeZone.rawOffset

        Log.e(TAG, "getInputUTCDate: date = ${Date(time)}  hour = $hour minute = $minutes")

        return time
    }

}