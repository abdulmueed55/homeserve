# HomeServ Android App

Kotlin + XML + SQLite Home Services Marketplace app.

## Demo accounts
- Admin: phone `0000`, password `admin123`
- Customer: phone `03000000001`, password `123456`
- Service Provider: phone `03000000002`, password `123456`

## Build
Open the `HomeServ` folder in Android Studio and sync Gradle. Required versions are already set: Gradle 8.9 wrapper properties, AGP 8.5.2, Kotlin 1.9.10, compileSdk/targetSdk 34, minSdk 24.

## File list
- `README.md`
- `app/build.gradle`
- `app/proguard-rules.pro`
- `app/src/main/AndroidManifest.xml`
- `app/src/main/java/com/example/homeserv/adapters/BookingAdapter.kt`
- `app/src/main/java/com/example/homeserv/adapters/OfferAdapter.kt`
- `app/src/main/java/com/example/homeserv/data/Models.kt`
- `app/src/main/java/com/example/homeserv/db/DBHelper.kt`
- `app/src/main/java/com/example/homeserv/ui/AddProviderActivity.kt`
- `app/src/main/java/com/example/homeserv/ui/AuthActivity.kt`
- `app/src/main/java/com/example/homeserv/ui/BaseNav.kt`
- `app/src/main/java/com/example/homeserv/ui/BookingConfirmationActivity.kt`
- `app/src/main/java/com/example/homeserv/ui/DashboardActivity.kt`
- `app/src/main/java/com/example/homeserv/ui/MyBookingsActivity.kt`
- `app/src/main/java/com/example/homeserv/ui/PostOfferActivity.kt`
- `app/src/main/java/com/example/homeserv/ui/ProfileActivity.kt`
- `app/src/main/java/com/example/homeserv/ui/SessionManager.kt`
- `app/src/main/java/com/example/homeserv/ui/SplashActivity.kt`
- `app/src/main/res/drawable/bg_badge_green.xml`
- `app/src/main/res/drawable/bg_card_light.xml`
- `app/src/main/res/drawable/bg_status.xml`
- `app/src/main/res/drawable/ic_add.xml`
- `app/src/main/res/drawable/ic_bookings.xml`
- `app/src/main/res/drawable/ic_home.xml`
- `app/src/main/res/drawable/ic_logo_home.xml`
- `app/src/main/res/drawable/ic_profile.xml`
- `app/src/main/res/layout/activity_add_provider.xml`
- `app/src/main/res/layout/activity_auth.xml`
- `app/src/main/res/layout/activity_booking_confirmation.xml`
- `app/src/main/res/layout/activity_dashboard.xml`
- `app/src/main/res/layout/activity_my_bookings.xml`
- `app/src/main/res/layout/activity_post_offer.xml`
- `app/src/main/res/layout/activity_profile.xml`
- `app/src/main/res/layout/activity_splash.xml`
- `app/src/main/res/layout/include_stat_bookings.xml`
- `app/src/main/res/layout/include_stat_offers.xml`
- `app/src/main/res/layout/include_stat_providers.xml`
- `app/src/main/res/layout/item_booking.xml`
- `app/src/main/res/layout/item_offer.xml`
- `app/src/main/res/menu/bottom_nav_menu.xml`
- `app/src/main/res/mipmap-hdpi/ic_launcher.png`
- `app/src/main/res/mipmap-hdpi/ic_launcher_round.png`
- `app/src/main/res/mipmap-mdpi/ic_launcher.png`
- `app/src/main/res/mipmap-mdpi/ic_launcher_round.png`
- `app/src/main/res/mipmap-xhdpi/ic_launcher.png`
- `app/src/main/res/mipmap-xhdpi/ic_launcher_round.png`
- `app/src/main/res/mipmap-xxhdpi/ic_launcher.png`
- `app/src/main/res/mipmap-xxhdpi/ic_launcher_round.png`
- `app/src/main/res/mipmap-xxxhdpi/ic_launcher.png`
- `app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.png`
- `app/src/main/res/values/colors.xml`
- `app/src/main/res/values/strings.xml`
- `app/src/main/res/values/themes.xml`
- `build.gradle`
- `gradle.properties`
- `gradle/wrapper/gradle-wrapper.properties`
- `settings.gradle`