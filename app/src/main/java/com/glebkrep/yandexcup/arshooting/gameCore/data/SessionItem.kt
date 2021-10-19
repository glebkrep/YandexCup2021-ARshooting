package com.glebkrep.yandexcup.arshooting.gameCore.data

import com.glebkrep.yandexcup.arshooting.ar.model.Geometry
import com.glebkrep.yandexcup.arshooting.ar.model.GeometryLocation
import com.glebkrep.yandexcup.arshooting.ar.model.Player
import com.google.firebase.firestore.GeoPoint

data class SessionItem(
    val hostUdid:String="",
    val players:List<PlayerItem> = listOf(),
    val state:Int=0
){
}

data class PlayerItem(
    val deadTimestamp:Long=0,
    val location:GeoPoint=GeoPoint(0.0,0.0),
    val name:String="",
    val udid:String=""
)

fun PlayerItem.toPlayer():Player{
    return Player(
        udid = this.udid,
        name = this.name,
        deadTimeStamp = this.deadTimestamp,
        location = Geometry(GeometryLocation(this.location.latitude,this.location.longitude))
    )
}