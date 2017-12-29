# KWSocket

Simple Web Socket library in Kotlin and RxJava2.

With android example apps.

### How to test commander-robot communication with two emulators

- Launch two emulators
- check their serial numbers: `adb devices -l`
  - (for example `emulator-5554` *is* a serial number)
- run the robot app on one emulator and the commander app on another
- forward port 9999 from dev machine to emulator with robot running:
  - `adb -s emulator-5554 forward tcp:9999 tcp:9999`
- Click connect (button with arrow) in commander app
  - If robot address is correct it should say: Connected
- Use other buttons to play with robot
- This robot only pretend to do stuff (but it speaks!)
  - check out our original IoT-Guard project (abandoned but working and fun) if you want to create a real robot: https://github.com/elpassion/iot-guard
- If you use real devices you can ask robot to "say something funny" or to "say something smart"
- You can send commands to the same robot using many commanders at the same time
