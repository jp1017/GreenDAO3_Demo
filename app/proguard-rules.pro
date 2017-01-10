# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/jp/Android/Sdk/tools/proguard/proguard-android.txt
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

#异常文件重命名为SourceFile
-renamesourcefileattribute SourceFile
#保留行号
-keepattributes SourceFile,LineNumberTable

# #  ######## greenDao3.0混淆  ##########
# # -------------------------------------------
-dontwarn rx.**
-keep class de.greenrobot.dao.** {*;}
-dontwarn org.greenrobot.greendao.database.**
-keepclassmembers class * extends org.greenrobot.greendao.AbstractDao {
    public static java.lang.String TABLENAME;
}
-keep class **$Properties