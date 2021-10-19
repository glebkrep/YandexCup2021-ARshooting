package com.glebkrep.yandexcup.arshooting.ar

import android.content.Context
import android.widget.TextView
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.rendering.ViewRenderable
import com.glebkrep.yandexcup.arshooting.R
import com.glebkrep.yandexcup.arshooting.ar.model.Player

class PlaceNode(
    val context: Context,
    val player: Player?
) : Node() {

    private var placeRenderable: ViewRenderable? = null
    private var textViewPlace: TextView? = null

    override fun onActivate() {
        super.onActivate()

        if (scene == null) {
            return
        }

        if (placeRenderable != null) {
            return
        }

        ViewRenderable.builder()
            .setView(context, R.layout.place_view)
            .build()
            .thenAccept { renderable ->
                setRenderable(renderable)
                placeRenderable = renderable

                player?.let {
                    textViewPlace = renderable.view.findViewById(R.id.placeName)
                    textViewPlace?.text = it.name
                }
            }
    }

//    fun showInfoWindow() {
//        // Show text
//        textViewPlace?.let {
//            it.visibility = if (it.visibility == View.VISIBLE) View.GONE else View.VISIBLE
//        }
//
//        // Hide text for other nodes
//        this.parent?.children?.filter {
//            it is PlaceNode && it != this
//        }?.forEach {
//            (it as PlaceNode).textViewPlace?.visibility = View.GONE
//        }
//    }
}