<div align="center">
   
![GitHub License](https://img.shields.io/github/license/umer0586/DroidPad?style=for-the-badge)
   ![Jetpack Compose Badge](https://img.shields.io/badge/Jetpack%20Compose-4285F4?logo=jetpackcompose&logoColor=fff&style=for-the-badge) ![GitHub Release](https://img.shields.io/github/v/release/umer0586/DroidPad?include_prereleases&style=for-the-badge)

   
[<img src="https://github.com/user-attachments/assets/0f628053-199f-4587-a5b2-034cf027fb99" height="80">](https://github.com/umer0586/DroidPad/releases)
[<img src="https://github.com/user-attachments/assets/6d0d2095-876c-4248-89d5-43b53ad21c35" height="80">](https://f-droid.org/packages/com.github.umer0586.droidpad/)
[<img src="https://github.com/user-attachments/assets/f311c689-cfd1-4326-8e7d-323e2e117006" height="80">](https://apt.izzysoft.de/fdroid/index/apk/com.github.umer0586.droidpad)



## Create Customizable Control Interfaces for Bluetooth Low Energy, Bluetooth, WebSocket, MQTT, TCP, and UDP Protocols with Simple Drag-and-Drop Functionality. 

<img src="https://github.com/umer0586/DroidPad/blob/main/fastlane/metadata/android/en-US/images/phoneScreenshots/1.png" width="250" heigth="250"> <img src="https://github.com/umer0586/DroidPad/blob/main/fastlane/metadata/android/en-US/images/phoneScreenshots/3.png" width="250" heigth="250"> <img src="https://github.com/umer0586/DroidPad/blob/main/fastlane/metadata/android/en-US/images/phoneScreenshots/2.png" width="250" heigth="250"> <br>
<img src="https://github.com/umer0586/DroidPad/blob/main/fastlane/metadata/android/en-US/images/phoneScreenshots/4.png" width="400" heigth="400"> <img src="https://github.com/umer0586/DroidPad/blob/main/fastlane/metadata/android/en-US/images/phoneScreenshots/5.png" width="400" heigth="400">

</div>

### Key Features:
1. **Drag-and-Drop Control Pad Creation**  
   Design your control pads by dragging and dropping components like buttons, sliders, switches, Joystick and D-PAD.  

2. **Multi-Protocol Support and Seamless Server Connections**  
Easily configure your control pad to support network protocols such as **Bluetooth LE, WebSocket (Client/Server), MQTT, TCP, and UDP**. Once connected, you can interact with the control pad’s components—including **buttons, sliders, switches, joysticks, and D-PADs**—to send real-time commands directly to the connected server or BLE client, where these commands can be processed.

3. **Switch Connection Type Anytime**  
   You can change the connection type of a control pad at any time without creating a duplicate for a different connection.

4. **Update UI From your Script**  
   You can change the state of SWITCH,SLIDER,LED and GAUGE from your script    
   
   
## Supported Components
1. Switch
2. Button
3. Slider
4. DPAD
5. Joystick
6. Steering Wheel
7. LED
8. GAUGE
9. LOG
10. Accelerometer and Gyroscope (If supported by the device)

## How It Works (4 steps) 

### **Step 1: Create a Control Pad**  
Start by creating a new control pad. Provide a unique name to identify your control pad.

   <img src="https://github.com/user-attachments/assets/b0bb34e6-d2ab-4245-ada9-19a8a030ebdc" width="240" height="426" />
   <img src="https://github.com/user-attachments/assets/e2ff23e0-cf4c-4c97-820f-69ec1efa935b" width="240" height="426" />

### **Step 2: Design Your Control Pad**  
After creating the control pad, click on the **Build** icon and use the drag-and-drop interface to add components like switches, buttons, and sliders etc.
   
   <img src="https://github.com/user-attachments/assets/d501e0db-1c4e-426c-b71c-506b6ab497c5" width="240" height="426" />
   <img src="https://github.com/user-attachments/assets/41d1044b-6fbb-4153-9d6c-e2ece02ad540" width="240" height="426" />

Assign a unique **ID** to each component. This ID will be sent to the server during interactions.
   
   <img src="https://github.com/user-attachments/assets/7bec11c8-3b00-4386-9990-13cfcc9576ef" width="240" height="426"/>
   
### **Step 3: Configure Connection Settings**  
Tap **'Settings**, choose a connection type (TCP, Bluetooth LE, UDP, WebSocket, or MQTT), enter the server address and port. You can switch between connection types anytime
   
   <img src="https://github.com/user-attachments/assets/2105f61a-b3e8-42f7-ab8d-c266728efe0c" width="240" height="426" />
   <img src="https://github.com/user-attachments/assets/cbadcd5b-b8ba-4708-9347-fc2bb95497c2" width="240" height="426" />

### **Step 4: Connect and Interact**  
a. Click on the **Play** icon to start interacting with your control pad.  
b. Tap the **Connect** button in the bottom-right corner to establish a connection with the server.
   
   <img src="https://github.com/user-attachments/assets/318b0ca9-e876-47cc-8137-06db15f81fdb" width="240" height="426" />
   <img src="https://github.com/user-attachments/assets/62e69eb3-86ed-4cf0-83fa-0b1091f3da38" width="240" height="426"/>
   <img src="https://github.com/user-attachments/assets/061a8e4a-dc08-4ba4-87ff-9d609ec75ede" width="240" height="426"/>

## **Reading Interactions**  
When users interact with the control pad, JSON-formatted or CSV messages are generated based on the type of component used. 
These string messages enable receivers to understand and process interactions sent from the control pad. Below are the formats and details for each interaction:  

---

### **SWITCH**  
Toggling a switch generates the following JSON:  
```json
{
  "id": "the id you specified",
  "type": "SWITCH",
  "state": true
}
```
For **Bluetooth** and **Bluetooth LE** connections, toggling a switch generates a `CSV` message in the format: `<id>,SWITCH,<state>`.

- The `state` field indicates whether the switch is **on** (`true`) or **off** (`false`).  

---

### **BUTTON**  
Pressing or releasing a button generates this JSON:  
```json
{
  "id": "the id you specified",
  "type": "BUTTON",
  "state": "PRESS"
}
```
For **Bluetooth** and **Bluetooth LE** connections, pressing or releasing a button generates a `CSV` message in the format: `<id>,BUTTON,<state>`.

- The `state` field can have following values:  
  - **"PRESS"**: When the button is being pressed (finger on the button).  
  - **"RELEASE"**: When the button is released (finger lifted off after pressing).
  - **"CLICK"**: Indicates tap gesture   

---

### **DPAD (Directional Pad)**  
Pressing or releasing a button on DPAD generates this JSON:  
```json
{
  "id": "the id you specified",
  "type": "DPAD",
  "button": "RIGHT",
  "state": "CLICK"
}
```
For **Bluetooth** and **Bluetooth LE** connections, pressing or releasing a button on DPAD generates a `CSV` message in the format: `<id>,DPAD,<button>,<state>`.

- The `state` field can have following values:  
  - **"PRESS"**: When the button is being pressed (finger on the button).  
  - **"RELEASE"**: When the button is released (finger lifted off after pressing).
  - **"CLICK"**: Indicates tap gesture
- The `button` field can be **"LEFT"**,**"RIGHT"**,**"UP"** or **"DOWN"**    

---
### **STEERING WHEEL**
Rotating a steering wheel generates this JSON:
```json
{
  "id": "your id",
  "type": "STEERING_WHEEL",
  "angle": 45.233445
}
```
For **Bluetooth** and **Bluetooth LE** connections the `CSV` is `<id>,STEERING_WHEEL,<angle>`

 - where `angle` is rotation angle of the steering wheel in degrees
   - **Positive values** indicate clockwise rotation
   - **Negative values** indicate counter-clockwise (anti-clockwise) rotation 

   

---

### **JOYSTICK**  
Moving joystick handle generates this JSON:  
```json
{
  "id": "the id you specified",
  "type": "JOYSTICK",
  "x": 0.71150637,
  "y": -0.13367589
}
```
For **Bluetooth** and **Bluetooth LE** : `<id>,JOYSTICK,<x>,<y>`

<img src="https://github.com/user-attachments/assets/fd3b1b14-d1c5-42d5-8813-f01745856191" width="150" height="150">

_Note : Joystick is not rotatable in the Builder Screen_

The values of x and y range:
 - From -1.0 to 1.0 for both axes.
 - Positive x values indicate movement to the right, and negative values indicate movement to the left.
 - Positive y values indicate upward movement, and negative values indicate downward movement.    

### **SLIDER**  
Dragging the slider thumb generates the following JSON:  
```json
{
  "id": "the id you specified",
  "type": "SLIDER",
  "value": 1.4
}
```
For **Bluetooth** and **Bluetooth LE** connections, dragging the slider thumb generates a `CSV` message in the format: `<id>,SLIDER,<value>`.

- The `value` field represents the current position of the slider.  
- The value is always within the range of the minimum and maximum values specified during the slider's configuration.  

---

### **LED**
To update the LED, send the following JSON message to the app:
```json
{
  "id": "the id you specified",
  "type": "LED",
  "state": "ON"
}
```
- `state` accepts the following values:

  - `"ON"` – Turns the LED on
  - `"OFF"` – Turns the LED off
  - `"BLINK"` – Makes the LED blink
 
🎥 [Video Demo | Changing LED state in DroidPad](https://youtu.be/yC-jo941l2Y)  
 
---

### **GAUGE**
To update the Gauge, send the following JSON message to the app::
```json
{
  "id": "the id you specified",
  "type": "GAUGE",
  "value": 120
}
```
- `value` is a number input used to update the gauge reading (such as speed, temperature, or progress).

🎥 [Video Demo | Updating GAUGE value in DroidPad](https://youtu.be/qz2xfBvb8k8)
  
---

### **LOG**

Each control pad includes an associated log terminal that displays logs sent from your script.  
When you click the **list icon** on the control pad, a bottom sheet will appear showing these logs.

To send a log message from your script, use the following JSON payload:

```json
{
  "type": "LOG",
  "message": "hello world"
}
```

---
  
## Sensor Readings

![image](https://github.com/user-attachments/assets/eeb83142-342e-4cfb-974e-4fd45563dd1d)

### **Accelerometer**

Accelerometer data is sent in the following JSON format:

```json
{
  "type": "ACCELEROMETER",
  "x": 0.31892395,
  "y": -0.97802734,
  "z": 10.049896
}
```
For **Bluetooth** and **Bluetooth LE** : `ACCELEROMTER,<x>,<y>,<z>`

#### **Fields**
* `x`: Acceleration force (in m/s²) applied along the **x-axis**, including the force of gravity.
* `y`: Acceleration force (in m/s²) applied along the **y-axis**, including the force of gravity.
* `z`: Acceleration force (in m/s²) applied along the **z-axis**, including the force of gravity.

---

### **Gyroscope**

Gyroscope data is sent in the following JSON format:

```json
{
  "type": "GYROSCOPE",
  "x": 0.15387291,
  "y": -0.22954187,
  "z": 0.08163925
}
```
For **Bluetooth** and **Bluetooth LE** : `GYROSCOPE,<x>,<y>,<z>`

#### **Fields**

* `x`: Rate of rotation around the **x-axis** in **radians per second (rad/s)**.
* `y`: Rate of rotation around the **y-axis** in **radians per second (rad/s)**.
* `z`: Rate of rotation around the **z-axis** in **radians per second (rad/s)**.

---

## Sending JSON Messages to DroidPad

You can send JSON messages to DroidPad to update the UI. All connection types are supported except for **BLE**. You can update following component

- SWITCH
- SLIDER
- LED
- GAUGE

To update a **SWITCH**, **SLIDER**, **LED**, or **GAUGE** send a JSON object message similar to the ones specified in the [SWITCH](#switch), [SLIDER](#slider), [LED](#led), [GAUGE](#gauge) sections, with the desired value or state.

For **Bluetooth Classic** and **TCP** connections, you must send each JSON message on a new line. This is because DroidPad reads the incoming stream line by line. Each JSON message should be on a single line, and multiple messages should be separated by a line feed (`\n`).

For example:

```
{"id":"s1","type":"SLIDER","value":1.4}\n{"id":"s1","type":"SLIDER","value":1.5}\n{"id":"s1","type":"SLIDER","value":1.4}
```

For **MQTT**, **WebSocket**, and **UDP** connections, you can send formatted JSON without the one-line and line feed restrictions, as these are message-based protocols. For **MQTT** you have to publish to `DroidPad/feed` topic


## Important Note for Bluetooth and Bluetooth Low Energy  
A long Bluetooth device name can cause advertisement failure (In case of BLE). To avoid this issue, use a shorter name. In your device's Bluetooth settings, change the Bluetooth device name to five or fewer characters, such as `dev`.

For devices running **Android 12 or higher**, you also need to ensure that your app has the necessary Bluetooth permissions. To do this, go to the app's system settings and grant the required **Nearby Devices** permission. Without this permission, the app won't be able to advertise Bluetooth LE services or access paired devices when using Bluetooth Classic.

## ⚠️ Limitations of Control Pad Sharing via QRCode and Json file
Although DroidPad allows you to share your control pads via QR code and JSON file, this feature has certain limitations. The app does not implement a grid system for item placement; instead, all items are positioned relative to the origin point at the top-left corner (0,0) of the control pad. As a result, when you import a control pad onto a device with a different screen size, DroidPad applies an offset shift to all items to adapt them to the new dimensions. In some cases, this automatic adjustment may require manual fine-tuning by the user importing it

## Testing the connection
You can test the connections with Websocket,TCP, UDP servers and BLE client provided in [https://github.com/UmerCodez/droidpad-python-examples](https://github.com/UmerCodez/droidpad-python-examples)

## Ardunio Template
See Ardunio code template for CSV and JSON parsing [UmerCodez/DroidPad-Arduino-template](https://github.com/UmerCodez/DroidPad-Arduino-template)

## Projects
1. A Tank controlled by an Arduino Uno R4 WiFi using the DroidPad App [https://github.com/Klixxy/ArduTank](https://github.com/Klixxy/ArduTank) by [Klixxy](https://github.com/Klixxy)
2. 3D-printed hexapod with 6-DOF pose control, adaptive gait, and FPV video streaming. Controlled via a Python GUI (TCP/UDP) or the DroidPad Android app. Powered by XIAO ESP32-S3 Sense [https://github.com/Ozzi06/ESP_Hexapod](https://github.com/Ozzi06/ESP_Hexapod) by [Ozzi06](https://github.com/Ozzi06)
3. Simple websocket server that allows you to use droidpad as a game controller [https://github.com/Tofixrs/droidpad-gamepad](https://github.com/Tofixrs/droidpad-gamepad) by [Tofixrs](https://github.com/Tofixrs)
4.  A simple server for DroidPad, that can convert DroidPad messages into actual input events on your PC. It can be used alongside DroidPad, to emulate joysticks/keyboards/mouses, and is scriptable using Janet. [https://github.com/agent-kilo/jumper](https://github.com/agent-kilo/jumper)
5. Parse structured data from Sockets Services for Vizzy, written to parse data sended by Droid Pad to control vehicles in Juno:New Origins with a mobile phone or tablet [https://github.com/FourthDing/DataProcessingHelper](https://github.com/FourthDing/DataProcessingHelper)
6. [droidpad.py](https://github.com/mlimonv12/MISE/blob/main/sw/MaqueenLib_proj/droidpad.py) is a Python script that serves as a network bridge between an Android device running DroidPad and the Maqueen robot. It receives commands from DroidPad and translates them into Maqueen’s actions, such as movement, LED control, and buzzer activation.
7. Linux app that can run commands based on YAML configuration. [https://github.com/filintodelgado/droidpad-companion](https://github.com/filintodelgado/droidpad-companion)



### TODOs
1. TouchPad

