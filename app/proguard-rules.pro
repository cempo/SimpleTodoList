# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\dol\AppData\Local\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Picasso
-dontwarn com.squareup.okhttp.**
-dontwarn org.joda.time.**

# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.AppGlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

# Models
-keep class com.rohmr.models.** { *; }
-keep enum com.rohmr.** { *; }

#Play services
-keep class * extends java.util.ListResourceBundle {
    protected Object[][] getContents();
}

-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
    public static final *** NULL;
}

-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
    @com.google.android.gms.common.annotation.KeepName *;
}

-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

-keep class android.support.design.widget.** { *; }
-keep interface android.support.design.widget.** { *; }
-keep public class android.support.** { *; }
-dontwarn android.support.design.**
-dontwarn android.support.**

#FCM
-keepattributes EnclosingMethod
-keepattributes InnerClasses

# For old versions
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**

# Fabric
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception

# Retrofit 2
-dontnote retrofit2.Platform
-dontnote retrofit2.Platform$IOS$MainThreadExecutor
-dontwarn retrofit2.Platform$Java8

-dontwarn retrofit2.**
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class retrofit2.** { *; }
-keep class okhttp3.** { *; }
-keepattributes Signature
-keepattributes Exceptions

-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

#ViewPageIndicator
-dontwarn com.viewpagerindicator.**

#EventBus
-keep class org.greenrobot.** {*;}
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}