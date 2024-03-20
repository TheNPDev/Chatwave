package com.example.chatwave

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import com.example.chatwave.data.CHATS
import com.example.chatwave.data.ChatData
import com.example.chatwave.data.ChatUser
import com.example.chatwave.data.Event
import com.example.chatwave.data.MESSAGE
import com.example.chatwave.data.Message
import com.example.chatwave.data.STATUS
import com.example.chatwave.data.Status
import com.example.chatwave.data.USER_NODE
import com.example.chatwave.data.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Calendar
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CWViewModel @Inject constructor(
    val auth: FirebaseAuth,
    var db: FirebaseFirestore,
    val storage: FirebaseStorage,
) : ViewModel() {

    var inProcess = mutableStateOf(false)
    var inProcessChat = mutableStateOf(false)
    var signin = mutableStateOf(false)
    val eventMuableState = mutableStateOf<Event<String>?>(null)
    val userData = mutableStateOf<UserData?>(null)
    val chats = mutableStateOf<List<ChatData>>(listOf())
    val chatMessages = mutableStateOf<List<Message>>(listOf())
    val inProgressChatMessage = mutableStateOf(false)
    var currentChatMessageListener: ListenerRegistration? = null
    val status = mutableStateOf<List<Status>>(listOf())
    val inProgressStatus = mutableStateOf(false)

    init {
        val currentUser = auth.currentUser
//        Log.e("CHATTT", "User ID: ${currentUser?.uid}, Email: ${currentUser?.email}")
        if (currentUser != null) {
            signin.value = true
        }


        currentUser?.uid?.let {
            getUserData(it)
        }
    }

    fun signUp(name: String, number: String, email: String, password: String) {

        inProcess.value = true
        if (name.isEmpty() || number.isEmpty() || email.isEmpty() || password.isEmpty()) {
            handleException(customMessage = "Please fill all fields")
            return
        }



        db.collection(USER_NODE).whereEqualTo("number", number).get().addOnSuccessListener {
            if (it.isEmpty) {
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                    inProcess.value = true
                    if (it.isSuccessful) {
                        signin.value = true
                        createOrUpdateProfile(name, number)
                    } else {
                        handleException(it.exception, "Sign up failed")
                    }
                }
            } else {
                handleException(customMessage = "Number already exists")
                inProcess.value = false
            }
        }
    }

    fun Login(email: String, password: String) {

        if (email.isEmpty() or password.isEmpty()) {
            handleException(customMessage = "Please fill all fields")
            return
        } else {
            inProcess.value = true
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    signin.value = true
                    inProcess.value = false
                    auth.currentUser?.uid?.let {
                        getUserData(it)
                    }
                } else {
                    handleException(it.exception, "Login failed")
                }
            }
        }

    }


    fun createOrUpdateProfile(
        name: String? = null,
        number: String? = null,
        imageUrl: String? = null,
    ) {

        val uid = auth.currentUser?.uid
        Log.e("CHATTT", "User ID: ${uid}")
        val userData = UserData(
            userId = uid,
            name = name ?: userData.value?.name,
            number = number ?: userData.value?.number,
            imageUrl = imageUrl ?: userData.value?.imageUrl
        )

        uid?.let {
            inProcess.value = true
            db.collection(USER_NODE).document(uid).get().addOnSuccessListener {
                if (it.exists()) {
                    //Update user data
                    db.collection(USER_NODE).document(uid).set(userData).addOnSuccessListener {
                        inProcess.value = false
                        getUserData(uid)
                    }.addOnFailureListener { exception ->
                        handleException(exception, "Failed to update user data")
                    }
                } else {
                    db.collection(USER_NODE).document(uid).set(userData).addOnSuccessListener {
                        inProcess.value = false
                        getUserData(uid)
                    }.addOnFailureListener { exception ->
                        handleException(exception, "Failed to create user data")
                    }
                }
            }
                .addOnFailureListener {
                    handleException(it, "Cannot retrieve user")
                }

        }

    }

    private fun getUserData(uid: String) {
        inProcess.value = true
        db.collection(USER_NODE).document(uid).addSnapshotListener { value, error ->
            if (error != null) {
                handleException(error, "Cannot retrive user")
            }
            if (value != null) {
                var user = value.toObject<UserData>()
                userData.value = user
                inProcess.value = false
                populateChats()
                populateStatuses()
            }
        }
    }

    fun handleException(exception: Exception? = null, customMessage: String = "") {

        Log.e("ChatWaveApp", "Chatwave exception: ", exception)
        exception?.printStackTrace()
        val errorMsg = exception?.localizedMessage ?: ""
        val message = if (customMessage.isNullOrEmpty()) errorMsg else customMessage

        eventMuableState.value = Event(message)
        inProcess.value = false
    }

    fun uploadProfileImage(uri: Uri) {
        uploadImage(uri) {
            createOrUpdateProfile(imageUrl = it.toString())
        }
    }

    fun uploadImage(uri: Uri, onSuccess: (Uri) -> Unit) {
        inProcess.value = true
        val storageRef = storage.reference
        val uuid = UUID.randomUUID()
        val imageRef = storageRef.child("images/$uuid")
        val uploadTask = imageRef.putFile(uri)
        uploadTask.addOnSuccessListener {
            val result = it.metadata?.reference?.downloadUrl

            result?.addOnSuccessListener(onSuccess)
            inProcess.value = false
        }
            .addOnFailureListener { handleException(it) }
    }

    fun logout() {
        auth.signOut()
        signin.value = false
        userData.value = null
        depopulateMessages()
        currentChatMessageListener = null
        eventMuableState.value = Event("Logged Out")
    }

    fun onAddChat(number: String) {

        if (number.isEmpty() or !number.isDigitsOnly()) {
            handleException(customMessage = "Number must be contains digits only")
        } else {
            db.collection(CHATS).where(
                Filter.or(
                    Filter.and(
                        Filter.equalTo("user1.number", number),
                        Filter.equalTo("user2.number", userData.value?.number)
                    ),
                    Filter.and(
                        Filter.equalTo("user1.number", userData.value?.number),
                        Filter.equalTo("user2.number", number)
                    )
                )
            ).get().addOnSuccessListener {
                if (it.isEmpty) {
                    db.collection(USER_NODE).whereEqualTo("number", number).get()
                        .addOnSuccessListener {
                            if (it.isEmpty) {
                                handleException(customMessage = "Number not found")
                            } else {
                                val chatPartner = it.toObjects<UserData>()[0]
                                val id = db.collection(CHATS).document().id
                                val chat = ChatData(
                                    chatId = id,
                                    ChatUser(
                                        userData.value?.userId,
                                        userData.value?.name,
                                        userData.value?.imageUrl,
                                        userData.value?.number
                                    ),
                                    ChatUser(
                                        chatPartner.userId,
                                        chatPartner.name,
                                        chatPartner.imageUrl,
                                        chatPartner.number
                                    )
                                )

                                db.collection(CHATS).document(id).set(chat)
                            }
                        }
                        .addOnFailureListener {
                            handleException(it)
                        }
                }
            }
        }

    }

    fun populateChats() {
        inProcessChat.value = true
        db.collection(CHATS).where(
            Filter.or(
                Filter.equalTo("user1.userId", userData.value?.userId),
                Filter.equalTo("user2.userId", userData.value?.userId)
            )
        ).addSnapshotListener { value, error ->

            if (error != null) {
                handleException(error)

            }
            if (value != null) {
                chats.value = value.documents.mapNotNull {
                    it.toObject<ChatData>()
                }
                inProcessChat.value = false
            }
        }
    }

    fun onSendReply(chatId: String, message: String) {
        val time = Calendar.getInstance().time.toString()
        val msg = Message(
            userData.value?.userId,
            message,
            time
        )

        db.collection(CHATS).document(chatId).collection(MESSAGE).document().set(msg)
    }

    fun populateMessages(chatId: String) {
        inProgressChatMessage.value = true

        currentChatMessageListener = db.collection(CHATS).document(chatId).collection(MESSAGE)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    handleException(error)
                }
                if (value != null) {
                    chatMessages.value = value.documents.mapNotNull {
                        it.toObject<Message>()
                    }.sortedBy { it.timestamp }
                    inProgressChatMessage.value = false
                }


            }
    }

    fun depopulateMessages() {
        chatMessages.value = listOf()
        currentChatMessageListener = null
    }

    fun uploadStatus(uri: Uri) {
        uploadImage(uri) {
            createStatus(it.toString())
        }
    }

    fun createStatus(imageUrl: String) {
        val newStatus = Status(
            ChatUser(
                userData.value?.userId,
                userData.value?.name,
                userData.value?.imageUrl,
                userData.value?.number
            ),
            imageUrl,
            System.currentTimeMillis()

        )

        db.collection(STATUS).document().set(newStatus)
    }

    fun populateStatuses() {
        val timeDelta = 24L * 60 * 60 * 1000
        val cutOff = System.currentTimeMillis() - timeDelta
        inProgressStatus.value = true
        db.collection(CHATS).where(
            Filter.or(
                Filter.equalTo("user1.userId", userData.value?.userId),
                Filter.equalTo("user2.userId", userData.value?.userId)
            )
        ).addSnapshotListener { value, error ->
            if (error != null) {
                handleException(error)
            }
            if (value != null) {
                val currentConnections = arrayListOf(userData.value?.userId)

                val chats = value.toObjects<ChatData>()
                chats.forEach { chat ->
                    if (chat.user1.userId == userData.value?.userId) {
                        currentConnections.add(chat.user2.userId)
                    } else {
                        currentConnections.add(chat.user1.userId)
                    }
                }
                db.collection(STATUS).whereGreaterThan("timestamp",cutOff).whereIn("user.userId", currentConnections)
                    .addSnapshotListener { value, error ->
                        if(error != null){
                            handleException(error)
                        }
                        if(value!= null){
                            val statuses = value.toObjects<Status>()
                            Log.d("populateStatuses", "Statuses: $statuses")
                            status.value = statuses
                            inProgressStatus.value = false
                        }


                    }
            }
        }
    }
}

