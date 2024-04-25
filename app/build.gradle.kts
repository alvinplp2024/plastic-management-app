plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.pplastic_management_system"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.pplastic_management_system"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-auth:22.3.1")
    implementation("com.google.firebase:firebase-database:20.3.1")
    implementation("com.google.firebase:firebase-firestore:24.11.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")



    implementation ("com.google.android.gms:play-services-maps:18.2.0")
    implementation ("com.google.android.gms:play-services-location:21.2.0")
    implementation ("androidx.navigation:navigation-fragment:2.7.7")
    implementation ("androidx.navigation:navigation-ui:2.7.7")
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")

    //cardview
    implementation ("androidx.cardview:cardview:1.0.0")


    //stk push libs
    implementation ("com.jakewharton:butterknife:10.1.0")
    //annotationProcessor ("com.jakewharton:butterknife-compiler:10.1.0")
    implementation ("com.jakewharton.timber:timber:4.7.1")

    //  implementation ("com.github.jumadeveloper:networkmanager:0.0.2")
    //implementation ("cn.pedant.sweetalert:library:1.3")
    implementation ("com.squareup.retrofit2:retrofit:2.5.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.5.0")
    implementation ("com.squareup.okhttp3:okhttp:3.12.13")
    implementation ("com.squareup.okhttp3:logging-interceptor:3.12.0")
    implementation ("com.google.code.gson:gson:2.10.1")
    implementation ("com.squareup.okio:okio:2.1.0")
    implementation ("javax.annotation:javax.annotation-api:1.3.2")


    //pdf implementation
    implementation ("com.itextpdf:itext7-core:7.1.16")
}