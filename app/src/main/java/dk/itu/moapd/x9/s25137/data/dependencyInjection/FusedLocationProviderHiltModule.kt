package dk.itu.moapd.x9.s25137.data.dependencyInjection

import android.content.Context
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object fusedLocationProviderHiltModule {
    @Provides
    fun fusedLocationProviderClient(
        @ApplicationContext context: Context
    ) = LocationServices.getFusedLocationProviderClient(context)
}