package com.glebkrep.yandexcup.arshooting.gameCore.data

enum class SessionState(val int: Int) {
    IN_LOBBY(0),
    PLAYING(1),
    GAME_FINISHED(2)
}

fun sessionStateFromInt(int: Int): SessionState {
    return when (int) {
        0 -> SessionState.IN_LOBBY
        1 -> SessionState.PLAYING
        else -> SessionState.GAME_FINISHED
    }
}