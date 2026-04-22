import java.util.Properties

/** build.gradle.kts — подробные пояснения к программе (файл с инструкциями для печати конкретной детали). if Graidle 3D printer*/
plugins {
    alias(libs.plugins.android.application)  // Подключаем AGP, Настройка задачи для AGP: В блоке android { ... } вы даете указания главному прорабу: какую версию SDK использовать, как подписывать приложение и какой функционал включить.
    alias(libs.plugins.kotlin.compose)    // Подключаем Compose Compiler Plugin
}

//@Suppress("HardcodedStringLiteral")
val proguardAndroidOptimize: String = "proguard-android-optimize.txt"

//@Suppress("HardcodedStringLiteral")
val proguardRulesPro: String = "proguard-rules.pro"

//@Suppress("HardcodedStringLiteral")
val composeUiTextGoogleFonts: String = "androidx.compose.ui:ui-text-google-fonts:1.6.1"

android {  // ... настройки AGP ...
    namespace = "com.github.misham72.communalpayments"
    compileSdk = 37

    // Загружаем свойства из файла keystore.properties. Блок загрузки, относятся только к вашему ключу подписи.
    val keystoreProperties = Properties()
    val keystorePropertiesFile = rootProject.file("keystore.properties")
    if (keystorePropertiesFile.exists()) {
        keystoreProperties.load(keystorePropertiesFile.inputStream())
    }

    // Добавляем конфигурацию подписи, относятся только к вашему ключу подписи.
    signingConfigs {
        create("release") {
            storeFile = file(keystoreProperties["storeFile"] as String)
            storePassword = keystoreProperties["storePassword"] as String
            keyAlias = keystoreProperties["keyAlias"] as String
            keyPassword = keystoreProperties["keyPassword"] as String
        }
    }
    defaultConfig {
        applicationId = "com.github.misham72.communalpayments" // Это уникальный идентификатор
        // вашего приложения в системе Android и в магазинах приложений (Google Play, RuStore).
        minSdk = 24  // Минимальная версия Android, на которой будет работать приложение.
        targetSdk = 36  //Версия Android, под которую вы оптимизировали приложение.
        versionCode = 12
        versionName = "2.4.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"  // Стандартный раннер для тестов на устройстве/эмуляторе. Если вы не пишете тесты, эта строка всё равно нужна для корректной работы
    }
    // Мы видим блок buildTypes, который определяет конфигурации для отладочной (debug) и релизной (release) сборок приложения.
    buildTypes {
        debug { // Debug подписываем тем же ключом, что и Release. debug — это встроенный тип сборки, который используется по умолчанию при нажатии кнопки Run (зелёный треугольник) в Android Studio.
            signingConfig = signingConfigs.getByName("release")  // — здесь мы принудительно назначаем отладочной сборке ту же подпись, что и у релизной. Это делается для удобства тестирования:
        }
        release {
            isMinifyEnabled = false  // — отключает минификацию (сжатие и обфускацию) кода. В вашем случае это false, потому что вы, вероятно, не хотите усложнять отладку или столкнуться с проблемами обфускации на учебном проекте.
            proguardFiles(
                getDefaultProguardFile(proguardAndroidOptimize), proguardRulesPro  // — указывает файлы справилами для ProGuard/R8.
            )
            signingConfig = signingConfigs.getByName("release")  // Без этой строки APK будет неподписанным или подписанным отладочным ключом по умолчанию, что недопустимо для публикации.
        }
    }

    compileOptions {  // заботится о совместимости языка
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures { //  он включает поддержку Jetpack Compose в проекте. Это необходимо, чтобы использовать @Composable функции и всю экосистему Compose.
        compose = true  //  Явная команда прорабу включить поддержку Compose Gradle подключает компилятор Compose (androidx.compose.compiler), который обрабатывает аннотации @Composable. Становятся доступны специальные инструменты для предпросмотра
    }
}

kotlin {
    compilerOptions {// — управляет генерацией байт-кода Kotlin.
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
    }
}
dependencies {   // это список библиотек, которые вы подключаете к своему проекту. Каждая строка добавляет определённый готовый код, чтобы вам не приходилось писать всё с нуля.
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)  // — связка Compose с жизненным циклом и ViewModel.
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))  // — специальный "набор версий", чтобы все библиотеки Compose были совместимы друг с другом.
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.runtime)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)   //— инструменты для отладки Compose в режиме разработки
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    implementation(libs.androidx.datastore.preferences)
    implementation(composeUiTextGoogleFonts)
    implementation(libs.androidx.lifecycle.viewmodel.compose)  // — связка Compose с жизненным циклом и ViewModel.
    implementation(libs.androidx.compose.material.icons.core)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.appcompat)
}