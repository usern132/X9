package dk.itu.moapd.x9.s25137.data.dependencyInjection

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.cdimascio.dotenv.dotenv

/* Code adapted from the Make It So repository, found at https://github.com/FirebaseExtended/make-it-so-android
 * Its original license is attached below.

 * Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0
 */

@Module
@InstallIn(SingletonComponent::class)
object FirebaseHiltModule {
    val DATABASE_URL: String = dotenv {
        directory = "/assets"
        filename = "env"
    }["DATABASE_URL"]

    @Provides
    fun auth(): FirebaseAuth = Firebase.auth

    @Provides
    fun database(): FirebaseDatabase = Firebase.database(DATABASE_URL)
}