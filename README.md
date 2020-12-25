Permissions-Android-KTX
=======================
[![Release](https://jitpack.io/v/adwardstark/permissions-android-ktx.svg)](https://jitpack.io/#adwardstark/permissions-android-ktx)

This light-weight library provides extension functions to existing Runtime-Permissions API available on android to reduce boilerplate-code and simply some commonly used APIs. It also uses kotlin-coroutines to remove dependency on callbacks and provides lifecycle-aware permission-requests.

## Compatibility
----------------
Permissions-Android-KTX requires Android [Jellybean](https://developer.android.com/about/versions/jelly-bean) (API level 16) or higher.

## How to
---------
To add this library in your project follow these steps:
 * **Step 1.** Add the JitPack repository to your root `build.gradle` file.
    ```gradle
    allprojects {
      repositories {
        ...
        maven { url 'https://jitpack.io' }
      }
    }
    ```

 * **Step 2.** Add the following dependency to your module `build.gradle` file.
    ```gradle
    dependencies {
      implementation 'com.github.adwardstark:permissions-android-ktx:{latest_version}'
    }
    ```

## List of Extension Functions
------------------------------

These are the extensions that are available within this library:

 * **hasPermission(permission: String)** : Returns true or false if the given permission is granted or denied.
    ```kotlin
    if(hasPermission(permission)) {
        // Granted
    } else {
        // Denied
    }
    ```
 * **permissionSafeScope(permission: String)** : This block will only execute if the given permission is already granted. Use this to safe-guard your logic that requires specific permission.
    ```kotlin
    permissionSafeScope(Manifest.permission.PERMISSION_NAME) {
        Log.i(TAG, "->Executed-successfully")
    }
    ```
 * **permissionSafeScope(permission: String, onGrant={}, onDeny={})** : These block will be executed on the basis of whether the given permission is granted or denied.
    ```kotlin
    permissionSafeScope(Manifest.permission.PERMISSION_NAME,
            onGrant = {
                Log.i(TAG, "->onGrant() Executed-successfully")
            },
            onDeny = {
                Log.i(TAG, "->onDeny() Executed-successfully")
            }
    )
    ```
 * **permissionSafeScope(permission: String, onGrant={}, onDeny={}, onNeeded={})** : These block will be executed on the basis of whether the given permission is granted, denied or if it is already denied once.
    ```kotlin
    permissionSafeScope(Manifest.permission.PERMISSION_NAME,
            onGrant = {
                Log.i(TAG, "->onGrant() Executed-successfully")
            },
            onDeny = {
                Log.i(TAG, "->onDeny() Executed-successfully")
            },
            onNeeded = {
                // This block will only run if the given permission need
                // an additional rational to be shown to the user.
                Log.i(TAG, "->onNeeded() Executed-successfully")
            })
    ```
 * **singlePermissionResult {}** : Returns true or false if the requested permission is granted or denied.
    ```kotlin
    private val singlePermissionRequest
            = singlePermissionResult { isGranted ->
                if(isGranted) {
                    Log.i(TAG, "->Permission-granted")
                } else {
                    Log.i(TAG,"->Permission-denied")
                }
            }
    
    // And initiate request using singlePermissionRequest.launch(lManifest.permission.PERMISSION_NAME)
    ```
 * **multiPermissionResult {}** : Returns list of permissions and their status whether granted or denied.
    ```kotlin
    private val multiPermissionRequest
            = multiPermissionResult { permissions ->
                permissions.forEach { permission ->
                    Log.i(TAG, "${permission.key} is ${permission.value}")
                }
            }
    
    // And initiate request using multiPermissionRequest.launch(listOfPermissions)
    ```
 * **requestSinglePermissionOnStart(permission: String)** : Returns true or false if the given permission is granted or denied, This method is `lifecycle-aware` and should only be called inside the `onStart()` of an activity.
    ```kotlin
    override fun onStart() {
        super.onStart()
        requestSinglePermissionOnStart(Manifest.permission.PERMISSION_NAME) { isGranted ->
            if(isGranted) {
                Log.i(TAG, "->Permission-granted")
            } else {
                Log.i(TAG,"->Permission-denied")
            }
        }
    }
    ```
 * **requestMultiPermissionOnStart(permissions: Array<String>)** : Returns list of permissions and their status whether granted or denied. This method is `lifecycle-aware` and should only be called inside the `onStart()` of an activity.
    ```kotlin
    override fun onStart() {
        super.onStart()
        requestMultiPermissionOnStart(listOfPermissions) { permissions ->
            permissions.forEach { permission ->
                Log.i(TAG, "${permission.key} is ${permission.value}")
            }
        }
    }
    ```