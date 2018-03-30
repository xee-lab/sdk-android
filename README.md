# Xee Android SDK V4

[![](https://jitpack.io/v/xee-lab/sdk-android.svg?style=flat-square)](https://jitpack.io/#xee-lab/sdk-android)
![Language kotlin](https://img.shields.io/badge/language-kotlin-05A8F3.svg?style=flat-square)
[![Build Status](https://img.shields.io/travis/xee-lab/sdk-android.svg?branch=master&style=flat-square)](https://travis-ci.org/xee-lab/sdk-android)
![license Apache](https://img.shields.io/badge/license-Apache_2-9D9D9D.svg?style=flat-square)

#### The new Xee Android SDK making easier the usage of our new [API V4](https://dev.xee.com/v4-openapi) on [Android devices](https://developer.android.com)

## Table of contents

- [Requirements](#requirements)
- [Example project](#example-project)
- [Installation](#installation)
- [Setup](#setup)
- [Usage](#usage)
- [Authentication](#authentication)
	- [Create an account](#create-an-account)
	- [Authenticate the user](#authenticate-the-user)
	- [Disconnect the user](#disconnect-the-user)
- [Vehicles](#vehicles)
	- [Associate vehicle](#associate-vehicle)
	- [Retrieve users's vehicles](#retrieve-userss-vehicles)
	- [Disassociate vehicle](#disassociate-vehicle)
	- [Retrieve vehicle](#retrieve-vehicle)
	- [Update vehicle](#update-vehicle)
	- [Retrieve vehicle status](#retrieve-vehicle-status)
- [Users](#users)
	- [Retrieve an user](#retrieve-an-user)
	- [Update an user](#update-an-user)
- [Signals](#advanced-usage)
	- [Retrieve accelerometers](#retrieve-accelerometers)
	- [Retrieves device data](#retrieves-device-data)
	- [Retrieve locations](#retrieve-locations)
	- [Retrieve signals](#retrieve-signals)
- [Privacies](#privacies)
	- [Stop a privacy](#stop-a-privacy)
	- [Retrieve privacies](#retrieve-privacies)
	- [Creates a new privacy](#creates-a-new-privacy)
- [Trips](#trips)
	- [Retrieve trip](#retrieve-trip)
	- [Retrieve trip locations](#retrieve-trip-locations)
	- [Retrieve trip signals](#retrieve-trip-signals)
	- [Retrieve vehicle trips](#retrieve-vehicle-trips)
- [Authorizations](#authorizations)
	- [Revoke authorization](#revoke-authorization)
	- [Retrieve users's authorizations](#retrieve-userss-authorizations)
- [Sign-In button](#sign-in-button)
- [Documentation](#documentation)
- [Issues](#issues)
- [Author](#author)
- [License](#license)

## Requirements

This SDK works for all devices with an Android version >= [16](https://developer.android.com/reference/android/os/Build.VERSION_CODES.html#JELLY_BEAN)

## Example project

We provide a [demo app](app) that shows how the SDK might be used

## Installation

Our SDK is built over [jitpack.io](https://jitpack.io).

In order to use this SDK, please do the following:

Add this to your root project **build.gradle**

```gradle
allprojects {
    repositories {
        maven {
            url "https://jitpack.io"
        }
    }
}
```

Then just add each module your need to the dependencies in the **build.gradle** file.

```gradle
dependencies {
	compile 'com.github.xee-lab.sdk-android:sdk-core:4.0.4'
	compile 'com.github.xee-lab.sdk-android:sdk-api:4.0.4'
}
```

If you just need to sign in with Xee, you can just use:

```groovy
compile 'com.github.xee-lab.sdk-android:sdk-core:4.0.4'
```

If you need to use our api, just only use the `sdk-api` *(no need to add the `sdk-core` line because it's a direct dependency of `sdk-api`)*

```groovy
compile 'com.github.xee-lab.sdk-android:sdk-api:4.0.4'
```

## Setup

Once the SDK is installed, create an application on our developer space to get credentials, see [how to create an app](https://github.com/xee-lab/xee-api-docs/tree/master/setup).

Then initialize the SDK following these steps:

1. Create a `OAuth2Client` with your credentials information and scopes

	```kotlin
	val oAuth2Client = OAuth2Client.Builder()
	                .clientId("your_client_id")
	                .clientSecret("your_client_secret")
	                .redirectUri("your_redirect_uri") // optional
	                .scopes(Arrays.asList("scope1", "scopes2", "..."))
	                .build()
	```

2. Create a `XeeEnv` with your credentials information and link your `OAuth2Client`
	
	```kotlin
	val xeeEnv = XeeEnv(context, oAuthClient)
	```

3. Use this `XeeEnv` to create an instance of each module you need

	```kotlin
	val xeeAuth = XeeAuth(xeeEnv)
	val xeeApi = XeeApi(xeeEnv)
	```
	
	> Note that you can enable SDK logs (please disable them for release) for each module you want with a second parameter like this:
	>
	```kotlin
	val xeeApi = XeeApi(xeeEnv, true) // enable SDK logs
	```
	> You can also set the timeout for requests:
	>
	```kotlin
	val xeeApi = XeeApi(xeeEnv, true, 30000) // set timeout in ms
	```

Now you're ready to use the SDK!

## Usage

Here are some examples of commons methods you might use.

> Note that we'll keep this **SDK up to date** to provide you **all the endpoints** availables on the [4rd version of the API](https://dev.xee.com/v4-openapi)

#### Also, the SDK use [RxJava](https://github.com/ReactiveX/RxJava) which makes easier and more flexible requests to our API.

## Authentication

Please see the [authentication flow](https://dev.xee.com/v4-openapi/#section/Authentication) for explanations

### Create an account

```kotlin
xeeAuth.register(object : RegistrationCallback {
	override fun onCanceled() {
		'your code'
	}

	override fun onError(error: Throwable) {
		'your code'
	}

	override fun onRegistered() {
		'your code'
	}

	override fun onLoggedAfterRegistration() {
		'your code'
	}
})
```

### Authenticate the user

```kotlin
xeeAuth.connect(object : AuthenticationCallback {
	override fun onError(error: Throwable) {                   
		'your code'
	}

	override fun onSuccess() {
		'your code'
	}
})
```

### Disconnect the user

```kotlin
xeeAuth.disconnect(object : DisconnectCallback {
	override fun onCompleted() {
		'your code'
	}
})
```

## Vehicles

Everything about your vehicles

### Associate vehicle

Set a vehicle for an user with a specified device_id and pin code

```kotlin
xeeApi.associateVehicle("id", "pin")
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe({ vehicle -> 'your code' }, { error -> 'your code' })
```

### Retrieve users's vehicles

Returns vehicles corresponding to specified user id (me is also acceptable)

```kotlin
xeeApi.getUserVehicles("user_id") // no parameters corresponds to "me" by default
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe({ vehicles -> 'your code' }, { error -> 'your code' })
```

### Dissociate vehicle

Delete the pairing between a vehicle and a device

```kotlin
xeeApi.dissociateVehicle("vehicle_id")
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe({ 'your code' }) { error -> 'your code' }
```

### Retrieve vehicle

Returns a vehicle corresponding to specified id

```kotlin
xeeApi.getVehicle("vehicle_id")
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe({ vehicles -> 'your code' }, { error -> 'your code' })
```

### Update vehicle

Update a vehicle with a specified ID

```kotlin
xeeApi.updateVehicle("vehicle_id", vehicle_to_update)
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe({ vehicleUpdated -> 'your code' }, { error -> 'your code' })
```

### Retrieve vehicle status

Returns the vehicle status of the vehicle

```kotlin
xeeApi.getVehicleStatus("vehicle_id")
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe({ status -> 'your code' }, { error -> 'your code' })
```

## Users

Access to your profile

### Retrieve an user

Returns a user corresponding to specified id, me is also acceptable

```kotlin
xeeApi.getUser("user_id") // no parameters corresponds to "me" by default
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe({ user -> 'your code' }, { error -> 'your code' })
```

### Update user

Update the current user

```kotlin
xeeApi.updateUser()
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe({ userUpdated -> 'your code' }, { error -> 'your code' })
```

## Signals

Signals of Vehicles (CAN signals, GPS and Accelerometer)

### Retrieve accelerometers

Retrieves the accelerometers data of a vehicle in a given date range

```kotlin
xeeApi.getVehicleAccelerometers("vehicle_id")
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe({ accelerometers -> 'your code' }, { error -> 'your code' })
```

### Retrieve device data

Retrieves the device data of a vehicle in a given time range

```kotlin
xeeApi.getVehicleDeviceData("vehicle_id")
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe({ data -> 'your code' }, { error -> 'your code' })
```

### Retrieve locations

Retrieves the locations of a vehicle in a given date range

```kotlin
xeeApi.getVehicleLocations("vehicle_id", "from", "to", "limit")
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe({ locations -> 'your code' }, { error -> 'your code' })
```

### Retrieve signals

Retrieves signals for a vehicle in a given date range

```kotlin
xeeApi.getVehicleSignals("vehicle_id", "from", "to", "limit")
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe({ signals -> 'your code' }, { error -> 'your code' })
```

You can also filter by signals you want

```kotlin
xeeApi.getVehicleSignals("vehicle_id", "from", "to", "BatteryVoltage,LockSts", "limit")
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe({ signals -> 'your code' }, { error -> 'your code' })
```

## Privacies

Operations about privacies

### Stop a privacy

Stop an existing privacy session

```kotlin
xeeApi.disablePrivacy("privacy_id")
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe({ privacyStopped -> 'your code' }, { error -> 'your code' })
```

### Retrieve privacies

Returns vehicles privacies between 2 dates inclusive

```kotlin
xeeApi.getVehiclePrivacies("vehicle_id", "from", "to", "limit")
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe({ privacies -> 'your code' }, { error -> 'your code' })
```

### Creates a new privacy

Creates a new privacy session on this vehicle

```kotlin
xeeApi.enablePrivacy("vehicle_id")
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe({ privacy -> 'your code' }, { error -> 'your code' })
```

## Trips

Access to the trips of the vehicles

### Retrieve trip

Returns trip corresponding to specified vehicle id

```kotlin
xeeApi.getTrip("trip_id")
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe({ trip -> 'your code' }, { error -> 'your code' })
```

### Retrieve trip locations

Returns trips locations by redirecting to the signals api with the good date range

```kotlin
xeeApi.getTripLocations("trip_id", "from", "to", "limit")
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe({ locations -> 'your code' }, { error -> 'your code' })
```

### Retrieve trip signals

Returns trips signals by redirecting to the signals api with the good date range

```kotlin
xeeApi.getTripSignals("trip_id", "from", "to", "limit")
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe({ signals -> 'your code' }, { error -> 'your code' })
```

### Retrieve vehicle trips

Returns trips corresponding to specified vehicle id. Request by date is inclusive. For example if a trip starts from 15:00 and ends at 16:00. A request from 15:15 to 15:45 will return this trip.

```kotlin
xeeApi.getVehicleTrips("vehicle_id")
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe({ trips -> 'your code' }, { error -> 'your code' })
```

## Authorizations

Manage OAuth authorizations

### Revoke authorization

Revokes the specified authorization

```kotlin
xeeApi.revokeAuthorization("authorization_id")
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe({ 'your code'}) { error -> 'your code'}
```

### Retrieve users's authorizations

Returns authorizations corresponding to specified user id

```kotlin
xeeApi.getAuthorizations("user_id") // no parameters corresponds to "me" by default
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe({ authorizations -> 'your code' }, { error -> 'your code' })
```


## Sign-In button

Use the Sign-In button to sign in with Xee. Three themes and three sizes are provided 

- **size:** icon, normal, large
- **theme:** grey, green, white

![XeeSDK buttons](images/sign_in_buttons.png)

Use the Sign-In & Sign-Up buttons in layout file

```xml
<com.xee.sdk.core.auth.SignInButton
    android:id="@+id/sign_in_button"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:signInBtnSize="normal"
    app:signInBtnTheme="grey"
    />

<com.xee.sdk.core.auth.SignUpButton
    android:id="@+id/sign_up_button"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:signUpBtnSize="normal"
    app:signUpBtnTheme="grey"
    />
```

Then set listeners for your action

```kotlin
signInBtn.setOnClickListener {
	xeeAuth.connect(object : AuthenticationCallback {
		override fun onError(error: Throwable) {
			'your code'
		}

		override fun onSuccess() {
			'your code'        
		}
	})
}

signUpBtn.setOnClickListener {
	xeeAuth.register(object : RegistrationCallback {
		override fun onCanceled() {
			'your code'
		}
		
		override fun onError(error: Throwable) {
			'your code'
		}

		override fun onRegistered() {
			'your code'        
		}
		
		override fun onLoggedAfterRegistration() {
			'your code'
		}
	})
}
```

## Documentation

You can generate documentation of SDK by using [Dokka tool](https://github.com/Kotlin/dokka).  Just execute the command below:

```shell
./gradlew dokka
```
> Note that the output documentation can be found in **javadoc** folder of each module of the SDK

## Issues

We're working hard to provide you an *issue free SDK*, but we're just humans so [we can do mistakes](http://i.giphy.com/RFDXes97gboYg.gif).  
If you find something, feel free to [fill an issue](https://github.com/xee-lab/sdk-android/issues) or/and **fork** the repository to fix it !sdk-android

## Author

[Xee](http://www.xee.com) • [jcholin@xee.com](mailto:jcholin@xee.com)

## License

XeeSDK is available under the Apache License 2.0. See the [LICENSE](LICENSE) file for more info.

