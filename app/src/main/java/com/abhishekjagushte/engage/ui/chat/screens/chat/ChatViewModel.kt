package com.abhishekjagushte.engage.ui.chat.screens.chat

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.abhishekjagushte.engage.database.views.MessageView
import com.abhishekjagushte.engage.database.entities.Conversation
import com.abhishekjagushte.engage.repository.DataRepository
import com.abhishekjagushte.engage.ui.chat.screens.chat.fragments.chatscreen.ChatDataItem
import com.abhishekjagushte.engage.utils.Constants
import kotlinx.coroutines.*
import javax.inject.Inject

enum class ChatState{
    NEW, EXISTING
}

enum class ChatType{
    CHAT_TYPE_121, CHAT_TYPE_M2M
}

class ChatViewModel @Inject constructor(
    private val dataRepository: DataRepository
) : ViewModel(){

    private var _conversationID = MutableLiveData<String>()
    val conversationID : LiveData<String>
        get() = _conversationID

    private var _conversation = MutableLiveData<Conversation>()
    val conversation : LiveData<Conversation>
        get() = _conversation


    private var myUsername: String?=null//set up in setupScreen

    private val TAG = "ChatViewModel"

    var _chatState = MutableLiveData<ChatState>()
    val chatState: LiveData<ChatState>
        get() = _chatState//Set initially as existing as this will be in most of the cases

    var _chatType = MutableLiveData<ChatType>()
    val chatType: LiveData<ChatType>
        get() = _chatType//Set initially as existing as this will be in most of the cases


    val viewModelJob = Job()
    val viewModelScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    init {
        _chatState.value = ChatState.NEW
    }

    fun setupScreen(conversationID: String){
        _conversationID.value = conversationID
        //this is to set senderid and receiverid in messages
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val conversation = dataRepository.getConversation(conversationID)
                myUsername = dataRepository.getMydetails()!!.username
                if (conversation != null){

                    if(conversation.type == Constants.CONVERSATION_TYPE_121) {
                        _chatType.postValue(ChatType.CHAT_TYPE_121)
                        //TODO : setUI121(conversation)
                        Log.d(TAG, "setupScreen: Type 121")
                    }
                    else{
                        _chatType.postValue(ChatType.CHAT_TYPE_M2M)
                        //TODO : setUIM2M(conversation)
                        Log.d(TAG, "setupScreen: Type M2M")
                    }

                    //sets the conversation
                    _conversation.postValue(conversation)

                    _chatState.postValue(ChatState.EXISTING)
                }
                else{
                    //The chat will always be 121 if not present in db
                    _chatType.postValue(ChatType.CHAT_TYPE_121)
                    _chatState.postValue(ChatState.NEW)

                    val contact = dataRepository.getContact(conversationID)

                    val con = Conversation(
                        conversationID = conversationID,
                        name = contact?.nickname?: conversationID,
                        type = Constants.CONVERSATION_TYPE_121,
                        active = Constants.CONVERSATION_ACTIVE_NO)

                    if(contact!=null){
                        _conversation.postValue(con)
                    }
                    else{
                        TODO("Handle if contact not found")
                    }

                    //TODO : setUINew(conversationID)
                    Log.d(TAG, "setupScreen: type NOT DEFINED")
                }
            }
        }
    }


    fun setUI121(conversation: Conversation){
        TODO("Set the conversation name etc on the page")
    }

    fun setUIM2M(conversation: Conversation){
        TODO("Set the conversation name etc on the page")
    }

    fun setUINew(username: String){
        TODO("Set the screen for a new chat")
    }

    //If a conversationID already exists then it will be fetched while login and will be fetched automatically
    //this funct is called only when there isn't a conID present already so i can create a new one over here
    // and then make a converstaion by sending the conversationid from here in firebase

    private fun createNewConversation121(username: String){
        dataRepository.addConversation121(username)
    }

    fun getChatsAll(): LiveData<PagedList<ChatDataItem>> {
        val factory: DataSource.Factory<Int, MessageView> = dataRepository.getChats(conversationID.value!!)

        val convertedFactory = factory.map{
            return@map when(it.type){
                0 -> ChatDataItem.MyTextMessage(it)
                else -> ChatDataItem.OtherTextMessage(it)
            }
        }

        val pagedListBuilder: LivePagedListBuilder<Int, ChatDataItem>
            = LivePagedListBuilder<Int, ChatDataItem>(convertedFactory, 50)

        return pagedListBuilder.build()
    }

    fun sendTextMessage121(message: String){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                //new is only possible when chat type is 121
                if(chatState.value == ChatState.NEW){
                    //createNewChat121(username!!) //suspend function sets the conversationID

                    //conversationID is username
                    createNewConversation121(conversationID.value!!)

                    Log.d(TAG, "sendMessage: The conversationID created and it is ${conversationID}")
                    _chatState.postValue(ChatState.EXISTING)
                    dataRepository.saveTextMessage121Local(
                        message,
                        conversationID.value!!,
                        myUsername!!,
                        conversationID.value!! //For 121 username = conversationID
                    )
                }
                else {
                    Log.d(TAG, "sendTextMessage121: sent")
                    //sets the livedata for chats once the conversationID is initialized
                    dataRepository.saveTextMessage121Local(
                        message,
                        conversationID.value!!,
                        myUsername!!,
                        conversationID.value!!
                    )
                }
            }
        }
    }

    fun sendTextMessageM2M(message: String, replyToId: String?) {
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                Log.d(TAG, "sendTextMessageM2M: ${conversationID.value}")
                dataRepository.saveTextMessageM2M(message, conversationID.value!!, replyToId)
            }
        }
    }

    fun markMessagesRead() {
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                dataRepository.markMessagesRead(conversationID.value!!)
            }
        }
    }
}


/*

    private fun getConversationIDFromUsername(username: String?) {
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                //Sets the myUsername here
                myUsername = dataRepository.getMydetails()!!.username
                val conID = dataRepository.getConversationIDFromUsername(username = username!!)
                if(conID == null){
                    _chatState.postValue(ChatState.NEW)
                    Log.d(TAG, "getConversationIDFromUsername: Worked")
                }
                else{
                    conversationID = conID
                    _chatState.postValue(ChatState.EXISTING)
                }
            }
        }
    }

private fun createNewChat121(username: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                val u = dataRepository.getMydetails()!!.username

                dataRepository.createNewChat121(hashMapOf(
                    "myID" to u,
                    "otherID" to username
                )).addOnCompleteListener {
                    if(it.isSuccessful)
                    {
                        val res = (it.result!!.data as HashMap<*, *>).get("conversationID")
                        Log.d(TAG, "createTestNewChat: $res is the new conversationID")
                    }
                }
            }
        }
    }


     //TODO tested with jio and wifi the delay is not that high enough to switch to listener
    fun setChatListener(conversationID: String){
        listener = dataRepository.setChatListener(conversationID).addSnapshotListener{ snapshot, exception ->
            if(exception!=null){
                Log.d(TAG, "setChatListener: Error")
                return@addSnapshotListener
            }

            snapshot?.let {
                for(doc in snapshot.documents) {
                    Log.d(TAG, "setChatListener: ${doc.data}")
                }
            }
        }
    }


 */