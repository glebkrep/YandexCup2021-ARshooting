package com.glebkrep.yandexcup.arshooting.ar.model

import com.google.android.gms.maps.model.LatLng
import com.google.ar.sceneform.math.Vector3
import com.google.maps.android.ktx.utils.sphericalHeading
import kotlin.math.cos
import kotlin.math.sin

/**
 * A model describing details about a Place (location, name, type, etc.).
 */
data class Player(
    val udid: String,
    val name: String,
    val deadTimeStamp: Long = 0,
    val location: Geometry
) {
    override fun equals(other: Any?): Boolean {
        if (other !is Player) {
            return false
        }
        return this.udid == other.udid
    }

    override fun hashCode(): Int {
        return this.udid.hashCode()
    }
}

fun Player.getPositionVector(azimuth: Float, latLng: LatLng): Vector3 {
    val placeLatLng = this.location.location.latLng
    val heading = latLng.sphericalHeading(placeLatLng)
    val r = -2f
    val x = r * sin(azimuth + heading).toFloat()
    val y = 1f
    val z = r * cos(azimuth + heading).toFloat()
    return Vector3(x, y, z)
}

data class Geometry(
    val location: GeometryLocation
)

data class GeometryLocation(
    val lat: Double,
    val lng: Double
) {
    val latLng: LatLng
        get() = LatLng(lat, lng)
}
