# Common
-renamesourcefileattribute SourceFile
-keepattributes EnclosingMethod, Signature, Exceptions, SourceFile, LineNumberTable
-keepattributes *Annotation*
-keep public class * extends java.lang.Exception


# Jackson
-keepnames class com.fasterxml.jackson.** { *; }
-dontwarn com.fasterxml.jackson.databind.**
-keep class org.codehaus.** { *; }
-keepclassmembers public final enum org.codehaus.jackson.annotate.JsonAutoDetect$Visibility { public static final org.codehaus.jackson.annotate.JsonAutoDetect$Visibility *; }
-keep @interface com.fasterxml.jackson.**
-dontwarn org.w3c.**


# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}


# Retrofit
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions
-dontwarn com.squareup.**
-dontwarn okio.**
-dontwarn okhttp3.**
-dontwarn java.nio.**
-dontwarn org.codehaus.mojo.**


# Dagger 2
-keep class com.google.errorprone.annotations.** { *; }
-dontwarn com.google.errorprone.annotations.**