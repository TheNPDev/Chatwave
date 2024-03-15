package com.example.chatwave

import android.content.ContentValues.TAG
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import com.example.chatwave.data.ChatData
import com.example.chatwave.data.Event
import com.example.chatwave.data.USER_NODE
import com.example.chatwave.data.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CWViewModel @Inject constructor(
    val auth: FirebaseAuth,
    var db : FirebaseFirestore,
    val storage : FirebaseStorage
): ViewModel() {

    var inProcess = mutableStateOf(false)
    var inProcessChat = mutableStateOf(false)
    var signin = mutableStateOf(false)
    val eventMuableState = mutableStateOf<Event<String>?>(null)
    val userData = mutableStateOf<UserData?>(null)
    val chats = mutableStateOf<List<ChatData>>(listOf())

    init {
        val currentUser = auth.currentUser
//        Log.e("CHATTT", "User ID: ${currentUser?.uid}, Email: ${currentUser?.email}")
        if (currentUser != null){
            signin.value = true
        }


        currentUser?.uid?.let {
            getUserData(it)
        }
    }

    fun signUp(name: String,number: String,email:String,password:String){

        inProcess.value = true
        if(name.isEmpty() || number.isEmpty() || email.isEmpty() || password.isEmpty()){
            handleException(customMessage = "Please fill all fields")
            return
        }



        db.collection(USER_NODE).whereEqualTo("number",number).get().addOnSuccessListener {
            if(it.isEmpty){
                auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener{
                    inProcess.value = true
                    if(it.isSuccessful){
                        signin.value = true
                        createOrUpdateProfile(name,number)
                    }
                    else{
                        handleException(it.exception,"Sign up failed")
                    }
                }
            }
            else{
                handleException(customMessage = "Number already exists")
                inProcess.value = false
            }
        }
    }

    fun Login(email: String,password: String){

        if(email.isEmpty() or password.isEmpty()){
            handleException(customMessage = "Please fill all fields")
            return
        }
        else{
            inProcess.value = true
            auth.signInWithEmailAndPassword(email,password).addOnCompleteListener {
                if (it.isSuccessful){
                    signin.value = true
                    inProcess.value = false
                    auth.currentUser?.uid?.let {
                        getUserData(it)
                    }
                }
                else{
                    handleException(it.exception,"Login failed")
                }
            }
        }

    }


    fun createOrUpdateProfile(name:String? = null,number: String? = null,imageUrl:String?= null){

        val uid = auth.currentUser?.uid
        Log.e("CHATTT", "User ID: ${uid}")
        val userData = UserData(
            userId = uid,
            name = name?: userData.value?.name,
            number = number?: userData.value?.number,
            imageUrl = imageUrl?: userData.value?.imageUrl
        )
        uid?.let {
            inProcess.value = true
            db.collection(USER_NODE).document(uid).get().addOnSuccessListener {
                if(it.exists()){
                    //Update user data
                    db.collection(USER_NODE).document(uid).set(userData).addOnSuccessListener {
                        inProcess.value = false
                        getUserData(uid)
                    }.addOnFailureListener { exception ->
                        handleException(exception, "Failed to update user data")
                    }
                } else{
                    db.collection(USER_NODE).document(uid).set(userData).addOnSuccessListener {
                        inProcess.value = false
                        getUserData(uid)
                    }.addOnFailureListener { exception ->
                        handleException(exception, "Failed to create user data")
                    }
                }
            }
                .addOnFailureListener{
                    handleException(it,"Cannot retrieve user")
                }

        }

    }

    private fun getUserData(uid: String) {
        inProcess.value = true
        db.collection(USER_NODE).document(uid).addSnapshotListener { value, error ->
            if(error != null){
                handleException(error,"Cannot retrive user")
            }
            if(value != null){
                var user = value.toObject<UserData>()
                userData.value = user
                inProcess.value = false
            }
        }
    }

    fun handleException(exception:Exception? = null,customMessage:String = ""){

        Log.e("ChatWaveApp","Chatwave exception: ",exception)
        exception?.printStackTrace()
        val errorMsg = exception?.localizedMessage?:""
        val message = if (customMessage.isNullOrEmpty()) errorMsg else customMessage

        eventMuableState.value = Event(message)
        inProcess.value = false
    }

    fun uploadProfileImage(uri: Uri){
        uploadImage(uri){
            createOrUpdateProfile(imageUrl = it.toString())
        }
    }

    fun uploadImage(uri: Uri, onSuccess: (Uri)-> Unit){
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
        eventMuableState.value = Event("Logged Out")
    }

    fun onAddChat(it: String) {

    }


}

