package com.abhishekjagushte.engage.utils

object Constants {

    const val PROFILE_PHOTO_FILENAME: String = "profile_img.jpg"
    const val THUMBNAIL_PREFIX = "thumb_"
    const val MESSAGE_ID: String = "MESSAGE_ID"
    const val SEARCH_TIME_DELAY: Long = 500L
    const val FIREBASE_USERS_COLLECTION: String = "users"
    const val FIREBASE_CONNECTION_REQUEST_COLLECTION = "connection-requests"
    const val NOT_INITIATED = "NOT INITIATED"
    const val INITIATED = "INITIATED"
    const val FIREBASE_CHANGE_COMPLETE = "FIREBASE_COMPLETE"
    const val FIREBASE_CHANGE_FAILED = "FIREBASE_FAILED"
    const val LOCAL_DB_CHANGE_INITIATED = "LOCAL_DB_CHANGE_INITIATED"
    const val LOCAL_DB_SUCCESS = "LOCAL_DB_SUCCESS"
    const val LOCAL_DB_FAILED = "LOCAL_DB_FAILED"

    //Contacts Handling
    const val SEND_FR_TYPE = 1
    const val ACCEPT_FR_TYPE = 2

    //Contacts constants
    const val CONTACTS_ME = 0
    const val CONTACTS_CONFIRMED = 1
    const val CONTACTS_REQUESTED = 2
    const val CONTACTS_PENDING = 3
    const val CONTACTS_UNKNOWN = 4

    //Room Constants
    const val FIREBASE_USER_ID_FIELD_NAME = "id"
    const val USER_DATA_TABLE_NAME = "user_data"

    //Recyclerview constants
    const val RECYCLERVIEW_TYPE_HEADER = 1
    const val RECYCLERVIEW_TYPE_SEARCH_RESULT = 2
    const val HEADER_ID_RECYCLERVIEW = "#&(##@^#@^#@^#@@*^#"

    //Chat List Constants
    const val CHATLIST_STATUS_NORMAL = 1
    const val CHATLIST_STATUS_MUTED = 2
    const val CHATLIST_STATUS_BLOCKED = 3
    const val CHATLIST_TYPE_121 = 1
    const val CHATLIST_TYPE_M2M = 2

    //SearchData types
    const val SEARCHDATA_CONTACT = 1
    const val SEARCHDATA_CONVERSATION = 2

    //Argument Constants
    const val ARGUMENT_NAME = "name"
    const val ARGUMENT_USERNAME = "username"

    ///////////////////////////////////////////////////////////////////////////
    // Chats
    ///////////////////////////////////////////////////////////////////////////
    const val TYPE_MY_MSG = 0
    const val TYPE_OTHER_MSG = 1
    const val TYPE_NOTICE: Int = 2

    const val STATUS_NOT_SENT = 0
    const val STATUS_SENT_BUT_NOT_DELIVERED = 1
    const val STATUS_SENT_BUT_NOT_READ = 2
    const val STATUS_SENT_AND_READ = 3
    const val STATUS_RECEIVED_BUT_NOT_READ = 4
    const val STATUS_RECEIVED_AND_READ_BY_ME = 5
    const val STATUS_UPLOADING = 6
    const val STATUS_UPLOAD_FAILED = 7
    const val STATUS_RECEIVED_MEDIA_NOT_DOWNLOADED = 8
    const val STATUS_EVENT_RECEIVED = 1

    const val NEEDS_PUSH_YES = 1
    const val NEEDS_PUSH_NO = 2

    const val DELETED_NO = 1
    const val DELETED_YES = 2


    ///////////////////////////////////////////////////////////////////////////
    // Events
    const val REMINDER_STATUS_ACTIVE = 1
    const val REMINDER_STATUS_INACTIVE = 2

    const val EVENT_MINE = 1
    const val EVENT_OTHER = 2

    const val EVENT_TYPE_REMINDER = 1
    const val EVENT_TYPE_POLL= 2
    ///////////////////////////////////////////////////////////////////////////


    //MIME Types
    const val MIME_TYPE_TEXT = "text/plain"
    const val MIME_TYPE_IMAGE_JPG = "image/jpeg"

    //Conversation types
    const val CONVERSATION_TYPE_121: Int = 1
    const val CONVERSATION_TYPE_M2M = 2

    const val CONVERSATION_ACTIVE_YES = 1
    const val CONVERSATION_ACTIVE_NO = 2
    const val CONVERSATION_ACTIVE_NO_NEEDS_PUSH = 3

    const val CONVERSATION_ID_INITIAL = "NONE"

    const val CONVERSATION_NEEDS_PUSH_YES = 1
    const val CONVERSATION_NEEDS_PUSH_NO = 0

    //ContactList Fragment Modes
    const val CONTACT_LIST_MODE_SELECTION = 1 //selection for adding participants - on click select
    const val CONTACT_LIST_MODE_NORMAL = 2 //Normal displaying - only click and on click


    const val I_CREATED_GROUP = "You created this group"



    //Data transfer constatnts
    const val IMAGE_KEY: String = "image"
    const val IMAGE_URI_KEY = "image_uri"
    const val CONVERSATION_INFO_KEY = "conversationInfo"

    //Permissions
    const val WRITE_PERMISSION_REQUEST_CODE = 100
    // Request code for creating a PDF document.
    const val CREATE_FILE = 1

    //Storage Paths
    const val IMAGE_SENT_STORAGE_PATH = "/Engage/Media/Engage Images/Sent"
    const val IMAGE_RECEIVED_STORAGE_PATH = "/Engage/Media/Engage Images/Received"
    const val PROFILE_PHOTO_LOCAL_STORAGE_PATH = "/Engage/dp"
    const val THUMBNAIL_DIRECTORY_PATH = "/thumbs"
    const val PROFILE_PHOTO_PATH_CLOUD = "users/"
    const val PROFILE_PHOTO_PATH_OTHERS_LOACL = "/Engage/Profiles/dp"


    //Firebase cloud storage paths
    const val MEDIA_121_IMAGES = "media121/Images"
    const val MEDIA_M2M_ROOT = "groups/"
    const val MEDIA_M2M_IMAGES = "Images/"

    //Chat State String
    const val CHAT_STATE_NEW = "NEW"
    const val CHAT_STATE_EXISTING = "EXISTING"

    const val CONVERSATION_ID = "conversationID"
    const val TITLE = "title"
    const val DESCRIPTION = "description"
    const val TIME = "time"
    const val REMINDER_TIME = "reminderTime"
    const val EVENT_ID: String = "eventID"

}

//Contacts
/*
    Since the contacts will contain the unknown numbers only that are common in groups every time i will search
    and the result is of TYPE 5, it will be common from a group

    The firebase document reference in FireStore will be username and not the uid. This saves some unnecessary reads
    while sending fcm to users

    The suggested contacts will form a new table contacts_suggested with a parameter score
    on basis of which they can be displayed

    Nickname is added in Contacts table which allows custom names for people to save
 */


//Searching
/*
    The data class holder for search results from firebase as well as the local database is now
    defined in search package in main ui

    Anything that doesn't need to be displayed will go in extras in SearchData

 */

