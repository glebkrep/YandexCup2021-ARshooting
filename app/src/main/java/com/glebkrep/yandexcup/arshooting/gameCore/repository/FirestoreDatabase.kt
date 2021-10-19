package com.glebkrep.yandexcup.arshooting.gameCore.repository

import com.glebkrep.yandexcup.arshooting.gameCore.data.SessionItem
import com.glebkrep.yandexcup.arshooting.gameCore.data.SessionState
import com.glebkrep.yandexcup.arshooting.utils.Debug
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object FirestoreDatabase {
    val db:FirebaseFirestore
    init {
        db = Firebase.firestore

    }

    fun createSessionWithYourself(sessionUid: String,playerName:String,playerUdid:String){
        val user = hashMapOf(
            "deadTimestamp" to 0,
            "location" to GeoPoint(0.0, 0.0),
            "name" to playerName,
            "udid" to playerUdid
        )

        val session = hashMapOf(
            "hostUdid" to playerUdid,
            "players" to listOf(user),
            "state" to SessionState.IN_LOBBY.int
        )


        db.collection("sessions")
            .document(sessionUid)
            .set(session)
            .addOnSuccessListener { documentReference ->
                Debug.log("Successfully created session")
            }
            .addOnFailureListener { e ->
                Debug.log("Error creating session ${e}")
            }
    }

    fun addYourselfToSession(sessionUid:String,playerName:String,playerUdid:String) {
        val user = hashMapOf(
            "deadTimestamp" to 0,
            "location" to GeoPoint(0.0, 0.0),
            "name" to playerName,
            "udid" to playerUdid
        )


        db.collection("sessions/${sessionUid}/players")
            .add(user)
            .addOnSuccessListener { documentReference ->
                Debug.log("Successfully added myself to session")
            }
            .addOnFailureListener { e ->
                Debug.log("Error adding myself to session ${e}")
            }

    }

    fun setSessionState(sessionUid: String,state: SessionState){
        db.collection("sessions")
            .document(sessionUid)
            .update("state",state.int)
            .addOnSuccessListener { documentReference ->
                Debug.log("Successfully updated session state")
            }
            .addOnFailureListener { e ->
                Debug.log("Error updating session state ${e}")
            }
    }

    fun startListeningForSessions(onSessionListUpdate:(Map<String,SessionItem>)->(Unit)){
        val docRef = db.document("sessions")
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Debug.log("Listen failed. $e")
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists() && snapshot.data!=null) {
                Debug.log("Current data: ${snapshot.data}")
                val sessionList = snapshot.data as Map<String,SessionItem>
                onSessionListUpdate.invoke(sessionList)
            } else {
                Debug.log("Current data: null")
            }
        }
    }

    fun startListeningForPlayersAndSessionState(sessionUid: String,onSessionUpdated:(SessionItem)->(Unit)){
        val docRef = db.document("sessions/${sessionUid}")
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Debug.log("Listen failed. $e")
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {

                Debug.log(snapshot.data?.keys)
                val sessionItem = snapshot.toObject(SessionItem::class.java)?:return@addSnapshotListener
                Debug.log("Current data: ${sessionItem}")
                onSessionUpdated.invoke(sessionItem)
            } else {
                Debug.log("Current data: null")
            }
        }
    }


}