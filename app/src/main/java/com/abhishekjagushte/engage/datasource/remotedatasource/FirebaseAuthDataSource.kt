package com.abhishekjagushte.engage.datasource.remotedatasource

import com.abhishekjagushte.engage.network.Profile
import com.abhishekjagushte.engage.utils.Constants
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject

class FirebaseAuthDataSource @Inject constructor(
    private val mAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
){
    fun signUp(email: String, password: String): Task<AuthResult> {
        return mAuth.createUserWithEmailAndPassword(email, password)
    }

    fun login(email: String, password: String): Task<AuthResult> {
        return mAuth.signInWithEmailAndPassword(email, password)
    }

    fun getCurrentUserUID():String{
        return mAuth.currentUser!!.uid
    }

    fun getCurrentUser(): FirebaseUser? {
        return mAuth.currentUser
    }

    fun setNameAndUsername(name: String, username: String, notificationChannelID: String): Pair<Task<Void>, Profile> {

        val id = getCurrentUserUID()

        val profile = Profile(
            name = name,
            username = username,
            id = id,
            joinTimeStamp = Date(),
            notificationChannelID = notificationChannelID
        )

        return Pair(firestore.collection(Constants.FIREBASE_USERS_COLLECTION).document(username).set(profile), profile)
    }

}




/*
suspend fun firebaseAddDataSignUp(name: String, username: String, token: String): Profile?{
        val uid = mAuth.currentUser!!.uid

        val profile = Profile(
            id = uid,
            name = name,
            username = username,
            joinTimeStamp = Date(),
            notificationChannelID = token
        )

        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("users").document(profile.username).set(profile).await()
        return profile
    }

    suspend fun addDataLocalSignUp(myself: Contact, email: String, password: String) {
        withContext(Dispatchers.IO) {
            database.databaseDao.insertMeinContacts(myself)
            database.databaseDao.insertCredentials(UserData(email, password))
        }
    }


    //TODO: Complete this function and add the required in login fragment
    suspend fun fireBaseGetDataSignIn(uid: String): Profile?{
        val firestore = FirebaseFirestore.getInstance()

        val query = firestore.collection("users").whereEqualTo("id", uid)

        //Trying out the await extension function
        val result = query.get().await()

        if(result!=null){
            val doc = result.documents.get(0)
            val profile = doc.toObject(Profile::class.java)
            return profile
        }
        else{
            return null
        }
    }

    suspend fun addDataLocalSignIn(profile: Profile, email: String, password: String) {
        withContext(Dispatchers.IO) {
            val firestore = FirebaseFirestore.getInstance()
            val myself = profile.convertDomainObject(0)
            database.databaseDao.insertMeinContacts(myself)
            database.databaseDao.insertCredentials(UserData(email, password))

            //TODO: Add the user connections on a background Thread
//            for(contact in profile.contacts){
//                firestore.collection("users").document(contact).addSnapshotListener {
//                        documentSnapshot, firebaseFirestoreException ->
//
//                    val conn = documentSnapshot?.toObject(Profile::class.java).convertDomainObject(1)
//                }
//            }
        }
    }
 */
