package dk.itu.moapd.x9.s25137.data.dependencyInjection

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/* Code adapted from the Make It So repository, found at https://github.com/FirebaseExtended/make-it-so-android
 * Its original license is attached below.

 * Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0
 */

@HiltAndroidApp
class X9HiltApplication : Application() {}