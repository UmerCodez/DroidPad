<div align="center">
   
![GitHub License](https://img.shields.io/github/license/umer0586/DroidPad?style=for-the-badge)
   ![Jetpack Compose Badge](https://img.shields.io/badge/Jetpack%20Compose-4285F4?logo=jetpackcompose&logoColor=fff&style=for-the-badge) ![GitHub Release](https://img.shields.io/github/v/release/umer0586/DroidPad?include_prereleases&style=for-the-badge)

   
[<img src="https://github.com/user-attachments/assets/0f628053-199f-4587-a5b2-034cf027fb99" height="100">](https://github.com/umer0586/DroidPad/releases)
[<img src="https://github.com/user-attachments/assets/f311c689-cfd1-4326-8e7d-323e2e117006" height="100">](https://apt.izzysoft.de/fdroid/index/apk/com.github.umer0586.droidpad)



## Create Customizable Control Interfaces for Bluetooth Low Energy, WebSocket, MQTT, TCP, and UDP Protocols with Simple Drag-and-Drop Functionality. 

<img src="https://github.com/umer0586/DroidPad/blob/main/fastlane/metadata/android/en-US/images/phoneScreenshots/1.png" width="250" heigth="250"> <img src="https://github.com/umer0586/DroidPad/blob/main/fastlane/metadata/android/en-US/images/phoneScreenshots/3.png" width="250" heigth="250"> <img src="https://github.com/umer0586/DroidPad/blob/main/fastlane/metadata/android/en-US/images/phoneScreenshots/2.png" width="250" heigth="250">

</div>

### Key Features:
1. **Drag-and-Drop Control Pad Creation**  
   Design your control pads by dragging and dropping components like buttons, sliders, switches, Joystick and D-PAD.  

2. **Multi-Protocol Support and Seamless Server Connections**  
Easily configure your control pad to support network protocols such as **Bluetooth LE, WebSocket, MQTT, TCP, and UDP**. Once connected, you can interact with the control pad’s components—including **buttons, sliders, switches, joysticks, and D-PADs**—to send real-time commands directly to the connected server or BLE client, where these commands can be processed.
 

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
When users interact with the control pad, JSON-formatted messages are generated based on the type of component used. 
These JSON messages enable receivers to understand and process interactions sent from the control pad. Below are the formats and details for each interaction:  

---

### **Switch**  
Toggling a switch generates the following JSON:  
```json
{
  "id": "the id you specified",
  "type": "SWITCH",
  "state": true
}
```
For Bluetooth LE connections, toggling a switch generates a `CSV` message in the format: `<id>,<state>`.

- The `state` field indicates whether the switch is **on** (`true`) or **off** (`false`).  

---

### **Button**  
Pressing or releasing a button generates this JSON:  
```json
{
  "id": "the id you specified",
  "type": "BUTTON",
  "state": "PRESS"
}
```
For Bluetooth LE connections, pressing or releasing a button generates a `CSV` message in the format: `<id>,<state>`.

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
For Bluetooth LE connections, pressing or releasing a button on DPAD generates a `CSV` message in the format: `<id>,<button>,<state>`.

- The `state` field can have following values:  
  - **"PRESS"**: When the button is being pressed (finger on the button).  
  - **"RELEASE"**: When the button is released (finger lifted off after pressing).
  - **"CLICK"**: Indicates tap gesture
- The `button` field can be **"LEFT"**,**"RIGHT"**,**"UP"** or **"DOWN"**    

---
### **JoyStick**  
Moving joystick handle generates this JSON:  
```json
{
  "id": "the id you specified",
  "type": "JOYSTICK",
  "x": 0.71150637,
  "y": -0.13367589
}
```
For Bluetooth LE `<id>,<x>,<y>`

<img src="https://github.com/user-attachments/assets/fd3b1b14-d1c5-42d5-8813-f01745856191" width="150" height="150">

_Note : Joystick is not rotatable in the Builder Screen_

The values of x and y range:
 - From -1.0 to 1.0 for both axes.
 - Positive x values indicate movement to the right, and negative values indicate movement to the left.
 - Positive y values indicate upward movement, and negative values indicate downward movement.    

### **Slider**  
Dragging the slider thumb generates the following JSON:  
```json
{
  "id": "the id you specified",
  "type": "SLIDER",
  "value": 1.4
}
```
For Bluetooth LE connections, dragging the slider thumb generates a `CSV` message in the format: `<id>,<value>`.

- The `value` field represents the current position of the slider.  
- The value is always within the range of the minimum and maximum values specified during the slider's configuration.  

--- 

## Important Note for Bluetooth Low Energy  
A long Bluetooth device name can cause advertisement failure. To avoid this issue, use a shorter name. In your device's Bluetooth settings, change the Bluetooth device name to five or fewer characters, such as `dev`.  

## Testing the connection
You can test the connections with Websocket,TCP, UDP servers and BLE client provided in [https://github.com/umer0586/droidpad-python-examples](https://github.com/umer0586/droidpad-python-examples)

### TODO
1. Bluetooth classic support
2. Attaching sensors to control pads
3. TouchPad

