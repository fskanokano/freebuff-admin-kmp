# Proguard rules for Freebuff Admin

# Keep Ktor
-keep class io.ktor.** { *; }
-keepclassmembers class io.ktor.** { volatile <fields>; }

# Keep kotlinx.serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keepclassmembers @kotlinx.serialization.Serializable class com.freebuff.admin.** {
    *** Companion;
}
-keepclasseswithmembers class com.freebuff.admin.**$$serializer {
    *** INSTANCE;
}

# Keep KMP
-keep class kotlin.Metadata { *; }
-keepclassmembers class kotlin.Metadata {
    public <methods>;
}
