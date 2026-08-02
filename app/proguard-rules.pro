# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# Keep Hilt generated code
-keep,allowobfuscation,allowshrinking class * extends dagger.hilt.android.lifecycle.HiltViewModel
-keep,allowobfuscation,allowshrinking class * extends androidx.lifecycle.ViewModel

# Moshi
-keepclassmembers class * {
    @com.squareup.moshi.JsonClass <fields>;
}
-keep @com.squareup.moshi.JsonClass class * { *; }

# Room
-keep class * extends androidx.room.RoomDatabase { *; }
-keep @androidx.room.Entity class * { *; }

# Kotlin Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# Lottie
-keep class com.airbnb.lottie.** { *; }
