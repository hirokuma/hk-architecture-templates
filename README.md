Architecture starter template (single module)
==================

This template is compatible with the latest **stable** version of Android Studio.

## Screenshots
![Screenshot](https://github.com/android/architecture-templates/raw/main/screenshots.png)

## Features

* Room Database
* Hilt
* ViewModel, read+write
* UI in Compose, list + write (Material3)
* Navigation
* Repository and data source
* Kotlin Coroutines and Flow
* Unit tests
* UI tests using fake data with Hilt

## Usage

1. Clone this branch

```
git clone https://github.com/hirokuma/hk-architecture-templates.git --branch hk/agp-8.6 [project_dir_name]
```

2. Run the customizer script:

```
./customizer.sh your.package.name AppName
```

Where `your.package.name` is your app ID (should be lowercase).

## BLE Service

1. Create `your.package.name.ble.XxxService` (change `XxxSerivce` to your Service name)
2. Implement `XxxService` by referring to `BleServiceBase.kt`
3. Remove files
  * `your/package/name`
    * `ble/`
      * `LbsService.kt`
      * `LpsService`
    * `ui/screens/`
      * `LbsView.kt`
      * `LpsView.kt`
4. Create `your/package/name/screens/XxxView.kt` and implement UI components
5. Edit implementation
  * `your/package/name/ui/Navigation.kt`
    * Find `// TODO Add BLE service classes`
      * add your Service class instance
      * add `services` map
  * `your/package/name/ui/screens/ConnectedScreen.kt`
    * Find `// TODO Add service view`
    * Remove `LbsView()` and `LpsView()`
    * Add your Views

# License

Now in Android is distributed under the terms of the Apache License (Version 2.0). See the
[license](LICENSE) for more information.
