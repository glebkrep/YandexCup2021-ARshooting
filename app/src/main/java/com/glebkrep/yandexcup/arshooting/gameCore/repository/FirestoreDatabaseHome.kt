package com.glebkrep.yandexcup.arshooting.gameCore.repository

import com.glebkrep.yandexcup.arshooting.gameCore.data.SessionItem
import com.glebkrep.yandexcup.arshooting.gameCore.data.SessionState
import com.glebkrep.yandexcup.arshooting.utils.Debug
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object FirestoreDatabaseHome {
    val db: FirebaseFirestore

    init {
        db = Firebase.firestore

    }

    fun createSessionWithYourself(sessionUid: String, playerName: String, playerUdid: String) {
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
            }
            .addOnFailureListener { e ->
                Debug.log("Error creating session ${e}")
            }
    }

    fun addYourselfToSession(sessionUid: String, playerName: String, playerUdid: String) {
        val user = hashMapOf(
            "deadTimestamp" to 0,
            "location" to GeoPoint(0.0, 0.0),
            "name" to playerName,
            "udid" to playerUdid
        )
        db.collection("sessions")
            .document(sessionUid)
            .update("players", FieldValue.arrayUnion(user))
            .addOnSuccessListener { documentReference ->
            }
            .addOnFailureListener { e ->
                Debug.log("Error adding myself to session ${e}")
            }

    }

    fun setSessionState(sessionUid: String, state: SessionState) {
        db.collection("sessions")
            .document(sessionUid)
            .update("state", state.int)
            .addOnSuccessListener { documentReference ->
            }
            .addOnFailureListener { e ->
                Debug.log("Error updating session state ${e}")
            }
    }

    fun startListeningForSessions(onSessionListUpdate: (Map<String, SessionItem>) -> (Unit)) {
        val collectionRef = db.collection("sessions")
        collectionRef.get().addOnCompleteListener { it ->
            if (it.isSuccessful) {
                val querySnapshot = it.result
                val documents = querySnapshot.documents
                val sessionsMap = documents.map { mapItem ->
                    mapItem.id to (mapItem.toObject(SessionItem::class.java) ?: SessionItem())
                }.toMap()
                onSessionListUpdate.invoke(sessionsMap)

            }
        }
    }

    fun startListeningForPlayersAndSessionState(
        sessionUid: String,
        onSessionUpdated: (SessionItem) -> (Unit)
    ) {
        val docRef = db.document("sessions/${sessionUid}")
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Debug.log("Listen failed. $e")
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {

                val sessionItem =
                    snapshot.toObject(SessionItem::class.java) ?: return@addSnapshotListener
                Debug.log("Current data: ${sessionItem}")
                onSessionUpdated.invoke(sessionItem)
            } else {
                Debug.log("Current data: null")
            }
        }
    }


}