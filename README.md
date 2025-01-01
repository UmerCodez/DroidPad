
![GitHub License](https://img.shields.io/github/license/umer0586/DroidPad?style=for-the-badge) ![Jetpack Compose Badge](https://img.shields.io/badge/Jetpack%20Compose-4285F4?logo=jetpackcompose&logoColor=fff&style=for-the-badge) ![GitHub Release](https://img.shields.io/github/v/release/umer0586/DroidPad?include_prereleases&style=for-the-badge)

## Create custom control interfaces with a simple drag-and-drop and transform your Android device into a controller by transmitting commands over WebSocket, MQTT, TCP, and UDP protocols.

<img src="https://github.com/umer0586/DroidPad/blob/main/fastlane/metadata/android/en-US/images/phoneScreenshots/1.png" width="250" heigth="250"> <img src="https://github.com/umer0586/DroidPad/blob/main/fastlane/metadata/android/en-US/images/phoneScreenshots/3.png" width="250" heigth="250"> <img src="https://github.com/umer0586/DroidPad/blob/main/fastlane/metadata/android/en-US/images/phoneScreenshots/2.png" width="250" heigth="250">

### Key Features:
1. **Drag-and-Drop Control Pad Creation**  
   Design your control pads by dragging and dropping components like buttons, sliders, and switches.  

2. **Multi-Protocol Support and Seamless Server Connections**  
Easily configure your control pad to act as a client for network protocols such as **WebSocket, MQTT, TCP, and UDP**. Once configured, the control pad can connect to servers using any of these protocols. Interact with your control pad’s components—like buttons, switches, and sliders—to send real-time commands directly to the connected server and process those commands at server side 
 

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
Tap **'Settings**, choose a connection type (TCP, UDP, WebSocket, or MQTT), enter the server address and port. You can switch between connection types anytime
   
   <img src="https://github.com/user-attachments/assets/2105f61a-b3e8-42f7-ab8d-c266728efe0c" width="240" height="426" />
   <img src="https://github.com/user-attachments/assets/cbadcd5b-b8ba-4708-9347-fc2bb95497c2" width="240" height="426" />

### **Step 4: Connect and Interact**  
a. Click on the **Play** icon to start interacting with your control pad.  
b. Tap the **Connect** button in the bottom-right corner to establish a connection with the server.
   
   <img src="https://github.com/user-attachments/assets/318b0ca9-e876-47cc-8137-06db15f81fdb" width="240" height="426" />
   <img src="https://github.com/user-attachments/assets/62e69eb3-86ed-4cf0-83fa-0b1091f3da38" width="240" height="426"/>
   <img src="https://github.com/user-attachments/assets/061a8e4a-dc08-4ba4-87ff-9d609ec75ede" width="240" height="426"/>

## **Reading Interactions at the Server Side**  
When users interact with the control pad, JSON-formatted messages are generated based on the type of component used. 
These JSON messages enable servers to understand and process interactions sent from the control pad. Below are the formats and details for each interaction:  

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
- The `state` field can have following values:  
  - **"PRESS"**: When the button is being pressed (finger on the button).  
  - **"RELEASE"**: When the button is released (finger lifted off after pressing).
  - **"CLICK"**: Indicates tap gesture   

---

### **Slider**  
Dragging the slider thumb generates the following JSON:  
```json
{
  "id": "the id you specified",
  "type": "SLIDER",
  "value": 1.4
}
```  
- The `value` field represents the current position of the slider.  
- The value is always within the range of the minimum and maximum values specified during the slider's configuration.  

--- 

### TODO
1. Bluetooth classic support
2. Bluetooth Low Energy support
3. Attaching sensors to control pads
4. TouchPad
5. Directional Pad

## Installlation
Download APK from [Release Page](https://github.com/umer0586/DroidPad/releases)
