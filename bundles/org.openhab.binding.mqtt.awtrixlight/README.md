# MQTT Awtrix 3 Binding

This binding allows you to control Awtrix 3 (formerly Awtrix Light) LED matrix displays via MQTT. The Awtrix 3 is a customizable 32x8 LED matrix display that can show various information like time, weather, notifications and custom text/graphics.

## Supported Things

This binding supports two types of things:

| Thing Type             | Description                                                                                     |
|------------------------|-------------------------------------------------------------------------------------------------|
| `awtrixclock` (Bridge) | Represents an Awtrix 3 display device. Acts as a bridge for apps.                               |
| `awtrixapp`            | Represents an app running on the Awtrix display. Apps can show text, icons, notifications, etc. |

## Prerequisites

- An MQTT broker (the MQTT binding must be installed and a broker configured)
- An Awtrix 3 LED matrix display configured to use MQTT

## Thing Configuration

### Bridge Configuration (`awtrixclock`)

| Parameter             | Description                                                                                                                                | Required | Default  |
|-----------------------|--------------------------------------------------------------------------------------------------------------------------------------------|----------|----------|
| `basetopic`           | The MQTT base topic for the Awtrix device                                                                                                  | Yes      | "awtrix" |
| `appLockTimeout`      | Timeout in seconds before releasing the lock to a selected app and returning to normal app cycle (see App Configuration for more details). | No       | 10       |
| `discoverDefaultApps` | Enable discovery of default apps. Since default apps cannot be controlled by this binding this should usually be disabled.                 | No       | false    |
| `lowBatteryThreshold` | Battery level threshold for low battery warning.                                                                                           | No       | 25       |

### App Configuration (`awtrixapp`)

| Parameter    | Description                        | Required | Default |
|--------------|------------------------------------|----------|---------|
| `appname`    | Name of the app                    | Yes      | -       |
| `useButtons` | Enable button control for this app | No       | false   |

When you enable the button control for an app, you can lock the app to the display by pushing the select button on the clock device. A red indicator will be shown while the app is locked and will start to blink shortly before the lock ends. The lock will last for the appLockTimeout set for the bridge. As long as the app is locked the normal app cycle is disabled and you can control the app by pressing the left and right buttons or the select button on the clock device. Pressing a button while the app is locked will reset the lock timeout to the value set for appLockTimeout. Left and right button presses will emit button events on the clock itself and the selected app. The button events can be used by rules to change the displayed app or perform any other actions (for example change the text color of the app or skip the current song playing on your audio device).

## Channels

### Bridge Channels (`awtrixclock`)

| Channel           | Type                 | Description                                                                                                                                    |
|-------------------|----------------------|------------------------------------------------------------------------------------------------------------------------------------------------|
| `app`             | String               | Currently active app: Will show the name of the app that is currently shown on the display.                                                    |
| `autoBrightness`  | Switch               | Automatic brightness control: The clock will adjust the display brightness automatically based on ambient light.                               |
| `batteryLevel`    | Number               | Battery level: The battery level of the internal battery in percent.                                                                           |
| `brightness`      | Dimmer               | Display brightness: The brightness of the display in percent.                                                                                  |
| `buttonLeft`      | Trigger              | Left button press event: Triggered when the left button is pressed/released (Event PRESSED or RELEASED).                                       |
| `buttonRight`     | Trigger              | Right button press event: Triggered when the right button is pressed/released (Event PRESSED or RELEASED).                                     |
| `buttonSelect`    | Trigger              | Select button press event: Triggered when the select button is pressed/released (Event PRESSED or RELEASED).                                   |
| `display`         | Switch               | Display on/off: Switches the display on or off. The clock will still stay on while the display is off.                                         |
| `humidity`        | Number:Dimensionless | Relative humidity: Relative humidity in percent. For the Ulanzi clock values are usually very inaccurate.                                      |
| `indicator1`      | Switch               | Control first indicator LED: Switches the first indicator LED on or off. The color of the LED will be green but can be customised by using thing actions (you can also use blinking/fading effects).                                                                                                                                                      |
| `indicator2`      | Switch               | Control second indicator LED: Switches the second indicator LED on or off.The color of the LED will be green but can be customised by using thing actions (you can also use blinking/fading effects).                                                                                                                                                 |
| `indicator3`      | Switch               | Control third indicator LED: Switches the third indicator LED on or off. The color of the LED will be green but can be customised by using thing actions (you can also use blinking/fading effects).                                                                                                                                                      |
| `lowBattery`      | Switch               | Low battery warning: Will be switched ON as soon as the battery level drops below the lowBatteryThreshold set for the bridge.                  |
| `lux`             | Number:Illuminance   | Ambient light level: Ambient light level in lux as measured by the built-in light sensor.                                                      |
| `rssi`            | Number:Dimensionless | WiFi signal strength (RSSI): WiFi signal strength (RSSI) in dBm.                                                                               |
| `rtttl`           | String               | Play RTTTL ringtone: Play a ringtone specified in RTTTL format (see https://de.wikipedia.org/wiki/Ring_Tones_Text_Transfer_Language)           |
| `screen`          | String               | Screen image: Allows you to mirror the screen image from the clock. The screen image will be updated automatically when the app changes but can be updated manually by sending a RefreshType command to the channel.                                                                                                                                   |
| `sound`           | String               | Play sound file: The sound file must be available on the clock device in the MELODIES folder. Save a file with a valid RTTTL string (e.g. melody.txt) in this folder and play it by sending a String command to the channel with the filename without file extension (e.g. "melody").                                                                     |
| `temperature`     | Number:Temperature   | Device temperature: Temperature in °C as measured by the built-in temperature sensor. For the Ulanzi clock values are usually very inaccurate. |

### App Channels (`awtrixapp`)

| Channel              | Type                 | Description                                                                                                                                 |
|----------------------|----------------------|---------------------------------------------------------------------------------------------------------------------------------------------|
| `active`             | Switch               | Enable/disable the app: Switches the app on or off. Note that channels of inactive apps will be reset to their default values during a restart of openHAB.                                                                                                                                                                                    |
| `autoscale`          | Switch               | Enable/disable autoscaling for bar and linechart.                                                                                           |
| `background`         | Color                | Sets a background color.                                                                                                                    |
| `bar`                | String               | Shows a bar chart: Send a string with values separated by commas (e.g. "value1,value2,value3"). Only the last 16 values will be displayed.  |
| `blink`              | Number:Time          | Blink text: Blink the text in the specified interval. Ignored if gradientColor or rainbow are set.                                          |
| `center`             | Switch               | Center short text horizontally and disable scrolling.                                                                                       |
| `color`              | Color                | Text, bar or line chart color.                                                                                                              |
| `duration`           | Number:Time          | Display duration in seconds.                                                                                                                |
| `effect`             | String               | Display effect (see https://blueforcer.github.io/awtrix3/#/effects for possible values).                                                    |
| `effectBlend`        | Switch               | Enable smoother effect transitions. Only to be used with effect.                                                                            |
| `effectPalette`      | String               | Color palette for effects (see https://blueforcer.github.io/awtrix3/#/effects for possible values and how to create custom palettes). Only to be used with effect.                                                                                                                                                                                     |
| `effectSpeed`        | Number:Dimensionless | Effect animation speed: Higher means faster (see https://blueforcer.github.io/awtrix3/#/effects). Only to be used with effect.              |
| `fade`               | Number:Time          | Fade text: Fades the text in and out in the specified interval. Ignored if gradientColor or rainbow are set.                                |
| `gradientColor`      | Color                | Secondary color for gradient effects. Use color for setting the primary color.                                                              |
| `icon`               | String               | Icon name to display: Install icons on the clock device first.                                                                              |
| `lifetime`           | Number:Time          | App lifetime: Define how long the app will remain active on the clock.                                                                      |
| `lifetimeMode`       | String               | Lifetime mode: Define if the app should be deleted (Command DELETE) or marked as stale (Command STALE) after lifetime.                      |
| `line`               | String               | Shows a line chart: Send a string with values separated by commas (e.g. "value1,value2,value3"). Only the last 16 values will be displayed. |
| `overlay`            | String               | Enable overlay mode: Shows a weather overlay effect (can be any of clear, snow, rain, drizzle, storm, thunder, frost).                      |
| `progress`           | Number:Dimensionless | Progress value: Shows a progress bar at the bottom of the app with the specified percentage value.                                          |
| `progressBackground` | Color                | Progress bar background color: Background color for the progress bar.                                                                       |
| `progressColor`      | Color                | Progress bar color: Color for the progress bar.                                                                                             |
| `pushIcon`           | String               | Push icon animation (STATIC=Icon doesn't move, PUSHOUT=Icon moves with text and will not appear again, PUSHOUTRETURN=Icon moves with text but appears again when the text starts to scroll again).                                                                                                                                                |
| `rainbow`            | Switch               | Enable rainbow effect: Uses a rainbow effect for the displayed text.                                                                        |
| `reset`              | Switch               | Reset app to default state: All channels will be reset to their default values.                                                             |
| `scrollSpeed`        | Number:Dimensionless | Text scrolling speed: Provide as percentage value. The original speed is 100%. Values above 100% will increase the scrolling speed, values below 100% will decrease it. Setting this value to 0 will disable scrolling completely.                                                                                                                     |
| `text`               | String               | Text to display.                                                                                                                            |
| `textCase`           | Number:Dimensionless | Set text case (0=normal, 1=uppercase, 2=lowercase).                                                                                         |
| `textOffset`         | Number:Dimensionless | Text offset position: Horizontal offset of the text in pixels.                                                                              |
| `topText`            | String               | Draws the text on the top of the display.                                                                                                   |

## Full Example

### Things

```
Bridge mqtt:broker:myBroker [ host="localhost", port=1883 ]
Bridge mqtt:awtrixclock:myBroker:myAwtrix "Living Room Display" (mqtt:broker:myBroker) [ basetopic="awtrix", appLockTimeout=10, lowBatteryThreshold=25 ] {
    Thing awtrixapp clock "Clock App" [ appname="clock", useButtons=true ]
    Thing awtrixapp weather "Weather App" [ appname="weather" ]
    Thing awtrixapp calendar "Calendar App" [ appname="calendar" ]
    Thing awtrixapp custom "Custom App" [ appname="custom" ]
}
```

### Items

```
// Bridge items (Living Room Display)
Group gAwtrix "Living Room Awtrix Display" <screen>
Dimmer Display_Brightness "Brightness [%d %%]" (gAwtrix) { channel="mqtt:awtrixclock:myBroker:myAwtrix:brightness" }
Switch Display_Power "Power" (gAwtrix) { channel="mqtt:awtrixclock:myBroker:myAwtrix:power" }
Switch Display_Screen "Screen" (gAwtrix) { channel="mqtt:awtrixclock:myBroker:myAwtrix:display" }
Switch Display_Sound "Sound" (gAwtrix) { channel="mqtt:awtrixclock:myBroker:myAwtrix:sound" }
Switch Display_AutoBrightness "Auto Brightness" (gAwtrix) { channel="mqtt:awtrixclock:myBroker:myAwtrix:autoBrightness" }
Number:Temperature Display_Temperature "Temperature [%.1f °C]" (gAwtrix) { channel="mqtt:awtrixclock:myBroker:myAwtrix:temperature" }
Number:Dimensionless Display_Humidity "Humidity [%d %%]" (gAwtrix) { channel="mqtt:awtrixclock:myBroker:myAwtrix:humidity" }
Number Display_Battery "Battery Level [%d %%]" (gAwtrix) { channel="mqtt:awtrixclock:myBroker:myAwtrix:batteryLevel" }
Switch Display_LowBattery "Low Battery" (gAwtrix) { channel="mqtt:awtrixclock:myBroker:myAwtrix:lowBattery" }
Number:Dimensionless Display_WiFi "WiFi Signal [%d %%]" (gAwtrix) { channel="mqtt:awtrixclock:myBroker:myAwtrix:rssi" }
String Display_CurrentApp "Active App [%s]" (gAwtrix) { channel="mqtt:awtrixclock:myBroker:myAwtrix:app" }

// Clock App items
Group gAwtrixClock "Clock App"
Switch Clock_Active "Clock Active" (gAwtrixClock) { channel="mqtt:awtrixapp:myBroker:myAwtrix:clock:active" }
String Clock_Text "Clock Text" (gAwtrixClock) { channel="mqtt:awtrixapp:myBroker:myAwtrix:clock:text" }
Color Clock_Color "Clock Color" (gAwtrixClock) { channel="mqtt:awtrixapp:myBroker:myAwtrix:clock:color" }
Number Clock_Duration "Clock Duration" (gAwtrixClock) { channel="mqtt:awtrixapp:myBroker:myAwtrix:clock:duration" }

// Weather App items
Group gAwtrixWeather "Weather App"
Switch Weather_Active "Weather Active" (gAwtrixWeather) { channel="mqtt:awtrixapp:myBroker:myAwtrix:weather:active" }
String Weather_Text "Weather Text" (gAwtrixWeather) { channel="mqtt:awtrixapp:myBroker:myAwtrix:weather:text" }
String Weather_Icon "Weather Icon" (gAwtrixWeather) { channel="mqtt:awtrixapp:myBroker:myAwtrix:weather:icon" }
Color Weather_Color "Weather Color" (gAwtrixWeather) { channel="mqtt:awtrixapp:myBroker:myAwtrix:weather:color" }
Switch Weather_Rainbow "Weather Rainbow Effect" (gAwtrixWeather) { channel="mqtt:awtrixapp:myBroker:myAwtrix:weather:rainbow" }

// Custom App items with advanced features
Switch Custom_Active "Custom App Active" (gAwtrix) { channel="mqtt:awtrixapp:myBroker:myAwtrix:custom:active" }
String Custom_Text "Custom Text" (gAwtrix) { channel="mqtt:awtrixapp:myBroker:myAwtrix:custom:text" }
String Custom_Icon "Custom Icon" (gAwtrix) { channel="mqtt:awtrixapp:myBroker:myAwtrix:custom:icon" }
Color Custom_Color "Text Color" (gAwtrix) { channel="mqtt:awtrixapp:myBroker:myAwtrix:custom:color" }
Color Custom_Background "Background Color" (gAwtrix) { channel="mqtt:awtrixapp:myBroker:myAwtrix:custom:background" }
Number:Dimensionless Custom_ScrollSpeed "Scroll Speed" (gAwtrix) { channel="mqtt:awtrixapp:myBroker:myAwtrix:custom:scrollSpeed" }
Switch Custom_Center "Center Text" (gAwtrix) { channel="mqtt:awtrixapp:myBroker:myAwtrix:custom:center" }
Number:Dimensionless Custom_Progress "Progress [%d %%]" (gAwtrix) { channel="mqtt:awtrixapp:myBroker:myAwtrix:custom:progress" }
Color Custom_ProgressColor "Progress Color" (gAwtrix) { channel="mqtt:awtrixapp:myBroker:myAwtrix:custom:progressColor" }
```

### Sitemap

```

sitemap awtrix label="Awtrix Display" {
    Frame label="Display Control" {
        Switch item=Display_Power
        Slider item=Display_Brightness
        Switch item=Display_Screen
        Switch item=Display_Sound
        Switch item=Display_AutoBrightness
        Text item=Display_Temperature
        Text item=Display_Humidity
        Text item=Display_Battery visibility=[Display_LowBattery==ON]
        Text item=Display_WiFi
        Text item=Display_CurrentApp
    }
    
    Frame label="Clock App" {
        Switch item=Clock_Active
        Text item=Clock_Text
        Colorpicker item=Clock_Color
        Slider item=Clock_Duration
    }
    
    Frame label="Weather App" {
        Switch item=Weather_Active
        Text item=Weather_Text
        Text item=Weather_Icon
        Colorpicker item=Weather_Color
        Switch item=Weather_Rainbow
    }
    
    Frame label="Custom App" {
        Switch item=Custom_Active
        Text item=Custom_Text
        Text item=Custom_Icon
        Colorpicker item=Custom_Color
        Colorpicker item=Custom_Background
        Slider item=Custom_ScrollSpeed
        Switch item=Custom_Center
        Slider item=Custom_Progress
        Colorpicker item=Custom_ProgressColor
    }
}

```

## Discovery

The binding can automatically discover Awtrix devices that publish their status to the configured MQTT broker. Once a device is discovered, it will appear in the inbox. Default Awtrix apps can also be discovered if `discoverDefaultApps` is enabled on the bridge. This is however not recommended as the default apps cannot be controlled via this binding.

## Actions

The binding provides various actions that can be used in rules to control the Awtrix display. To use these actions, you need to import them in your rules.

Rules DSL:

```
val awtrixActions = getActions("mqtt.awtrixlight", "mqtt:awtrixclock:myBroker:myAwtrix")
```

JS Scripting:

```javascript
var awtrixActions = actions.thingActions("mqtt.awtrixlight", "mqtt:awtrixclock:myBroker:myAwtrix");
```

### Indicator Control

Control the three indicator LEDs on the Awtrix display (JS Scripting):

```javascript
// Blink indicator 1 in red for 1 second
awtrixActions.blinkIndicator(1, [255,0,0], 1000)

// Fade indicator 2 to blue over 2 seconds
awtrixActions.fadeIndicator(2, [0,0,255], 2000)

// Turn on indicator 3 in green
awtrixActions.activateIndicator(3, [0,255,0])

// Turn off indicator 1
awtrixActions.deactivateIndicator(1)
```

### Device Control

Control basic device functions:

```javascript
// Reboot the device
awtrixActions.reboot()

// Put device to sleep for 60 seconds
awtrixActions.sleep(60)

// Perform firmware upgrade
awtrixActions.upgrade()
```

### Sound Control

Play sounds and melodies:

```javascript
// Play a predefined sound file (without extension)
awtrixActions.playSound("notification")

// Play an RTTTL melody
awtrixActions.playRtttl("Indiana:d=4,o=5,b=250:e,8p,8f,8g,8p,1c6,8p.,d,8p,8e,1f,p.,g,8p,8a,8b,8p,1f6,p,a,8p,8b,2c6,2d6,2e6,e,8p,8f,8g,8p,1c6,p,d6,8p,8e6,1f.6,g,8p,8g,e.6,8p,d6,8p,8g,e.6,8p,d6,8p,8g,f.6,8p,e6,8p,8d6,2c6")
```

### Notifications

Display notifications on the screen:

```
// Show simple notification with icon
awtrixActions.showNotification("Hello World", "alert")

// Show custom notification with advanced options
val params = newHashMap(
    'text' -> 'Custom Message',
    'icon' -> 'warning',
    'color' -> [255,165,0],  // Orange color
    'rainbow' -> true,
    'duration' -> 10
)
awtrixActions.showCustomNotification(
    params,     // Notification parameters
    false,      // hold: Keep notification until manually cleared
    true,       // wakeUp: Wake up from screen saver
    true,       // stack: Add to notification stack
    "Indiana:d=4,o=5,b=250:e,8p,8f,8g,8p,1c6,8p.,d,8p,8e,1f,p.,g,8p,8a,8b,8p,1f6,p,a,8p,8b,2c6,2d6,2e6,e,8p,8f,8g,8p,1c6,p,d6,8p,8e6,1f.6,g,8p,8g,e.6,8p,d6,8p,8g,e.6,8p,d6,8p,8g,f.6,8p,e6,8p,8d6,2c6", // RTTTL sound to play (not both sound and rtttl)
    "alert",    // Sound file to play (not both sound and rtttl)
    false       // loopSound: Loop the sound until manually stopped
)
```

#### Custom Notification Parameters

The action method parameters:

| Parameter | Type | Description |
|-----------|------|-------------|
| `hold` | Boolean | Keep notification until manually cleared |
| `wakeUp` | Boolean | Wake up from screen saver |
| `stack` | Boolean | Add to notification stack |
| `rtttl` | String | RTTTL melody to play |
| `sound` | String | Sound file to play (without extension) |
| `loopSound` | Boolean | Loop the sound |
| `params` | Map | Notification parameters |

The `showCustomNotification` action accepts all app channels as shown above as parameters in the params map.

### Rule Examples

Here are some example rules demonstrating various features:

```

rule "Battery Status Indicator Demo"
when
    Item Display_Battery changed
then
    if (Display_Battery.state <= 20) {
        // Show low battery warning with blinking red indicator
        getActions("mqtt.awtrixlight", "mqtt:awtrixclock:myBroker:myAwtrix").blinkIndicator(1, [255,0,0], 500)
        getActions("mqtt.awtrixlight", "mqtt:awtrixclock:myBroker:myAwtrix").showNotification("Low Battery!", "battery-alert")
    } else if (Display_Battery.state <= 50) {
        // Show yellow indicator for medium battery
        getActions("mqtt.awtrixlight", "mqtt:awtrixclock:myBroker:myAwtrix").setIndicator(1, [255,255,0])
    } else {
        // Show green indicator for good battery
        getActions("mqtt.awtrixlight", "mqtt:awtrixclock:myBroker:myAwtrix").setIndicator(1, [0,255,0])
    }
end

rule "Door Bell Demo"
when
    Item Doorbell_Button changed to ON
then
    // Play sound and show notification
    getActions("mqtt.awtrixlight", "mqtt:awtrixclock:myBroker:myAwtrix").playRtttl("doorbell:d=4,o=6,b=100:8e,8g,8e,8c")
    
    var params = newHashMap(
        'text' -> "Doorbell",
        'icon' -> "bell-ring",
        'color' -> [0,255,255],  // Cyan color
        'pushIcon' -> "PUSHOUT",
        'center' -> true
    )
    getActions("mqtt.awtrixlight", "mqtt:awtrixclock:myBroker:myAwtrix").showCustomNotification(
        params, false, true, true, "", "", false
    )
end

rule "Progress Bar Demo"
when
    Item Washing_Machine_Progress changed
then
    var progress = (Washing_Machine_Progress.state as Number).intValue
    
    // Update custom app with progress bar
    Custom_Text.sendCommand("Washing")
    Custom_Icon.sendCommand("washing-machine")
    Custom_Progress.sendCommand(progress)
    Custom_ProgressColor.sendCommand("0,255,0")  // Green progress bar
    
    if (progress == 100) {
        // Play sound when done
        getActions("mqtt.awtrixlight", "mqtt:awtrixclock:myBroker:myAwtrix").playSound("complete")
    }
end
```

These rules demonstrate:

- Using indicators to show battery status
- Creating custom notifications with icons and colors
- Playing RTTTL melodies and sound files
- Displaying progress bars
