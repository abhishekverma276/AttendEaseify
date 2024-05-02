# AttendEaseify

## Overview
This NFC Attendance App allows users to send and receive attendance data via NFC. The app consists of two main functionalities: sending attendance data from a sender device and receiving it on a receiver device.

## Features
- **Send Data**: Users can enter their details and send them via NFC to another device.
- **Receive Data**: Users can receive NFC data from another device.

## Screenshots
![Main Page](https://github.com/abhishekverma276/AttendEase/assets/96565154/b5389413-6428-4fc3-a92f-7a7f8c7292f2)
![Transmit Attendance](https://github.com/abhishekverma276/AttendEase/assets/96565154/f9cb663a-3968-42b3-a982-612fc8b6c0a7)
![receive attendance](https://github.com/abhishekverma276/AttendEase/assets/96565154/241905ba-248e-499d-b9ab-e9ffb7e80712)


## How It Works
1. **Sender Device**:
   - The user fills out their name, enrollment number, batch, and branch.
   - Upon pressing the "Send Data" button, the data is formatted into an NDEF message and sent via NFC.

2. **Receiver Device**:
   - The device waits for an NFC transmission.
   - Once data is received, it is displayed to the user.

## Setup
To run this project, clone the repo and open it in Android Studio. Make sure you have the latest Android SDK installed and that NFC is supported on your testing devices.

## Prerequisites
- Android SDK with NFC support.
- Two NFC-capable devices.

## Installation
Clone this repository and import into **Android Studio**
