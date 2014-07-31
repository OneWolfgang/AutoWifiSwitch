# AutoWifiSwitch

A free Android app which connects to the best wifi connection possible. Useful for locations which have multiple extenders with different SSIDs.

Please note: This app will only attempt to connect to networks which you have connected to before.

## Permission Breakdown

* **ACCESS_WIFI_STATE** - Used for determining if wifi is enabled, so we don't attempt to scan whilst its off.
* **CHANGE_WIFI_STATE** - Required for starting a scan of nearby networks.
* **RECEIVE_BOOT_COMPLETED** - Used for starting up the application's wifi scanning service when your device is powered up.
* **QUICKBOOT_POWERON** - The same as above, only for HTC devices using quick boot

## Upcoming Features

* Power-saving mode disables the app, waiting on Android L for APIs, but may hook into vendor specific APIs in the meantime.
* Sound notification when changing network?
* Debugging interface to see network signal strengths and how they compare to other nearby networks, useful for figuring out a good signal difference requirement.
* Better tablet experience.

# Todo

* Fix battery issues (not sure if this is still an issue).

## Credits

* [AndroidAssetStudio](http://romannurik.github.io/AndroidAssetStudio/index.html) for the icons, I'm very bad at graphics myself.
* **Gravity** for doing some testing.

## Donations

The app has a link in the preferences menu for making donations, however we'll give you the link anyway, PayPal donations can be made [here](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=BJQTQKAPZT6VU)