package com.glebkrep.yandexcup.arshooting.gameCore.repository

import com.glebkrep.yandexcup.arshooting.gameCore.data.SessionItem

object FirestoreDatabaseResults {
    fun getSessionData(sessionId: String, gotSessionItem: (SessionItem) -> (Unit)) {
        val collectionRef = FirestoreDatabaseHome.db.collection("sessions")
        collectionRef.document(sessionId).get().addOnCompleteListener { it ->
            if (it.isSuccessful) {
                val result = it.result.toObject(SessionItem::class.java) ?: SessionItem()
                gotSessionItem.invoke(result)
            }
        }
    }
}