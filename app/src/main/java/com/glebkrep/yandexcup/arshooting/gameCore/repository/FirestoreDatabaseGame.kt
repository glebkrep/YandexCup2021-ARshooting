package com.glebkrep.yandexcup.arshooting.gameCore.repository

import android.location.Location
import com.glebkrep.yandexcup.arshooting.gameCore.data.PlayerItem
import com.glebkrep.yandexcup.arshooting.gameCore.data.SessionItem
import com.glebkrep.yandexcup.arshooting.utils.Debug
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object FirestoreDatabaseGame {
    val db: FirebaseFirestore

    init {
        db = Firebase.firestore

    }

    fun writeYourLocation(sessionId: String, mLocation: Location, udid: String) {
        Debug.log("GAME.LOCATION_sessionId :$sessionId")
        val sessionDocRef = db.document("sessions/${sessionId}")
        db.runTransaction { transaction ->
            Debug.log("TRANSACTION")
            val sessionSnapshot = transaction.get(sessionDocRef)
            val session = sessionSnapshot.toObject(SessionItem::class.java) ?: return@runTransaction
            Debug.log("session:${session.players.map { it.toString() }}")
            val myPlayer = session.players.first() { it.udid == udid }
            val myPlayerHashMap = hashMapOf(
                "deadTimestamp" to myPlayer.deadTimestamp,
                "location" to myPlayer.location,
                "name" to myPlayer.name,
                "udid" to myPlayer.udid
            )
            val myPlayerNewHashMap = hashMapOf(
                "deadTimestamp" to myPlayer.deadTimestamp,
                "location" to GeoPoint(mLocation.latitude, mLocation.longitude),
                "name" to myPlayer.name,
                "udid" to myPlayer.udid
            )

            transaction.update(
                sessionDocRef,
                "players",
                FieldValue.arrayRemove(myPlayerHashMap)
            )
            transaction.update(
                sessionDocRef,
                "players",
                FieldValue.arrayUnion(myPlayerNewHashMap)
            )
        }.addOnSuccessListener {
        }.addOnFailureListener {
            Debug.log("Error updating gps player ${it}")

        }
    }

    fun getPlayersDataUpdates(sessionId: String, onLocationsUpdated: (List<PlayerItem>) -> (Unit)) {
        val sessionDocRef = db.collection("sessions").document(sessionId)
        sessionDocRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Debug.log("update listen failed ${e}")
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                val sessionItem =
                    snapshot.toObject(SessionItem::class.java) ?: return@addSnapshotListener
//                Debug.log("Current data: ${sessionItem}")
                onLocationsUpdated.invoke(sessionItem.players)
            } else {
                Debug.log("empty data")
            }
        }
    }

    fun shotPlayer(sessionId: String, shotPlayerUdid: String) {
        val sessionDocRef = db.collection("sessions").document(sessionId)
        db.runTransaction { transaction ->
            val sessionSnapshot = transaction.get(sessionDocRef)
            val session = sessionSnapshot.toObject(SessionItem::class.java) ?: return@runTransaction
            val killedPlayer = session.players.first() { it.udid == shotPlayerUdid }
            val killedPlayerHashMap = hashMapOf(
                "deadTimestamp" to killedPlayer.deadTimestamp,
                "location" to killedPlayer.location,
                "name" to killedPlayer.name,
                "udid" to killedPlayer.udid
            )
            val newKilledPlayerHashMap = hashMapOf(
                "deadTimestamp" to System.currentTimeMillis(),
                "location" to killedPlayer.location,
                "name" to killedPlayer.name,
                "udid" to killedPlayer.udid
            )

            transaction.update(
                sessionDocRef,
                "players",
                FieldValue.arrayRemove(killedPlayerHashMap)
            )
            transaction.update(
                sessionDocRef,
                "players",
                FieldValue.arrayUnion(newKilledPlayerHashMap)
            )
        }.addOnSuccessListener {

        }.addOnFailureListener {
            Debug.log("Error killing player ${it}")

        }
    }


}