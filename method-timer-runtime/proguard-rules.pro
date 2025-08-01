# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Keep MethodTimer class and methods to prevent obfuscation
-keep class com.methodtimer.MethodTimer {
    public static *;
}

# Keep method signatures for proper instrumentation
-keepnames class * {
    *;
}