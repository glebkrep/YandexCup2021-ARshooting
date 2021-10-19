package com.glebkrep.yandexcup.arshooting

import android.app.Application
import com.glebkrep.yandexcup.arshooting.utils.SharePreferences

class MainApp : Application(){
    override fun onCreate() {
        super.onCreate()
        SharePreferences.init(this)
    }

}