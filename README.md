#### Create custom control interfaces easily with a simple drag-and-drop tool and transform your Android device into a remote control by transmitting commands over WebSocket, MQTT, TCP, and UDP protocols.

<img src="https://github.com/umer0586/DroidPad/blob/main/fastlane/metadata/android/en-US/images/phoneScreenshots/1.png" width="250" heigth="250"> <img src="https://github.com/umer0586/DroidPad/blob/main/fastlane/metadata/android/en-US/images/phoneScreenshots/3.png" width="250" heigth="250"> <img src="https://github.com/umer0586/DroidPad/blob/main/fastlane/metadata/android/en-US/images/phoneScreenshots/2.png" width="250" heigth="250">

### Key Features:
1. **Drag-and-Drop Control Pad Creation**  
   Design your control pads by dragging and dropping components like buttons, sliders, and switches.  

2. **Multi-Protocol Support and Seamless Server Connections**  
Easily configure your control pad to act as a client for network protocols such as **WebSocket, MQTT, TCP, and UDP**. Once configured, the control pad can connect to servers using any of these protocols. Interact with your control pad’s components—like buttons, switches, and sliders—to send real-time commands directly to the connected server and process those commands at server side 
 

## How It Works (4 steps) 

### **Step 1: Create a Control Pad**  
Start by creating a new control pad. Provide a unique name to identify your control pad.

   <img src="https://github.com/user-attachments/assets/a9cfdf63-79ec-4e15-a5a1-1cda41aff1b8" width="240" height="426" />
   <img src="https://github.com/user-attachments/assets/3d03c44a-4c5e-4e31-bd15-b2b8412a8eb1" width="240" height="426" />

### **Step 2: Design Your Control Pad**  
After creating the control pad, click on the **Build** icon and use the drag-and-drop interface to add components like switches, buttons, and sliders etc.
   
   <img src="https://github.com/user-attachments/assets/7c0019bc-0c03-41d4-be74-930f3561b85d" width="240" height="426" />
   <img src="https://github.com/user-attachments/assets/c16e5fe7-3b76-4c90-84ff-29b48f9cf4ae" width="240" height="426" />

Assign a unique **ID** to each component. This ID will be sent to the server during interactions.
   
   <img src="https://github.com/user-attachments/assets/dfa20c12-1da0-42a5-81e7-8aa8f0e07b6c" width="240" height="426"/>

### **Step 3: Configure Connection Settings**  
Tap **'Settings**, choose a connection type (TCP, UDP, WebSocket, or MQTT), enter the server address and port. You can switch between connection types anytime
   
   <img src="https://github.com/user-attachments/assets/abb13abf-9804-48cf-92d8-147d1950c630" width="240" height="426" />
   <img src="https://github.com/user-attachments/assets/7f684f10-0b35-4431-8b15-ce6806f5df25" width="240" height="426" />
   
### **Step 4: Connect and Interact**  
a. Click on the **Play** icon to start interacting with your control pad.  
b. Tap the **Connect** button in the bottom-right corner to establish a connection with the server.
   
   <img src="https://github.com/user-attachments/assets/28e3597f-a6a3-4236-9c33-8da2a0510f29" width="240" height="426" />
   <img src="https://github.com/user-attachments/assets/e1a7ebb8-7f65-49c7-85cc-2d6589c3e155" width="240" height="426"/>
   <img src="https://github.com/user-attachments/assets/23653638-547f-4e0b-8384-62d7c7965b51" width="240" height="426"/>

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
- The `state` field can have two values:  
  - **"PRESS"**: When the button is being pressed (finger on the button).  
  - **"RELEASE"**: When the button is released (finger lifted off after pressing).  

---

### **Click Button**  
A **Click Button** differs from a regular button as it sends a single **tap** gesture instead of separate **PRESS** or **RELEASE** events. The generated JSON is:  
```json
{
  "id": "the id you specified",
  "type": "CLICK_BUTTON",
  "state": "CLICK"
}
```  
- The `state` field is always **"CLICK"** to indicate the tap gesture.  

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
