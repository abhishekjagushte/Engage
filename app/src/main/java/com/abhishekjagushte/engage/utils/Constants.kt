package com.abhishekjagushte.engage.utils

object Constants {
    const val FIREBASE_USERS_COLLECTION: String = "users"
    const val NOT_INITIATED = "NOT INITIATED"
    const val INITIATED = "INITIATED"
    const val FIREBASE_CHANGE_COMPLETE = "FIREBASE_COMPLETE"
    const val FIREBASE_CHANGE_FAILED = "FIREBASE_FAILED"
    const val LOCAL_DB_CHANGE_INITIATED = "LOCAL_DB_CHANGE_INITIATED"
    const val LOCAL_DB_SUCCESS = "LOCAL_DB_SUCCESS"
    const val LOCAL_DB_FAILED = "LOCAL_DB_FAILED"

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

 */

