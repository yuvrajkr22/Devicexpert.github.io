-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.devicexpert.app.**$$serializer { *; }
-keepclassmembers class com.devicexpert.app.** {
    *** Companion;
}
-keepclasseswithmembers class com.devicexpert.app.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep class com.google.crypto.tink.** { *; }
-dontwarn com.google.errorprone.annotations.**
-keep class com.hivemq.client.** { *; }
-dontwarn io.netty.**
-dontwarn org.jctools.**
-dontwarn javax.annotation.**
-dontwarn okhttp3.**
-dontwarn okio.**
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations