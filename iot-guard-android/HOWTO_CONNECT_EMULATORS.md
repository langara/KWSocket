## How to test iot-guard-android with two emulators
### (temporary instruction)

- Launch two emulators
- check their serial numbers: adb devices -l
  - (for example emulator-5554 IS a serial number)
- forward some port from dev machine to one of emulators: adb -s emulator-5554 forward tcp:9909 tcp:9999
- run chat server (ExampleWSActivity) on emulator with forwarded port (chat server listens on port 9999)
    - click "connect and..." to test communication with local clients (watch logcat)
- run client (commander) (CommanderActivity) on second emulator
    - enter host loopback addr (as available from emulator virtual router) and forwarded port to activity edit text: ws://10.0.2.2:9909
        - 10.0.2.2 is always forwarded to dev machine loopback interface
    - click connect to try to connect to chat server on first emulator
        - app will crash if it can not connect
- check logcat windows for both emulators in android studio
- click some buttons in commander (like: forward, left, stop, ...) and watch logcat for both emulators
