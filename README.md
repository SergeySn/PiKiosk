# PiKiosk
Tutorial and example on how to create a Kotlin Desktop Compose app running on Raspberry Pi 5 in kiosk mode.

To create the jar file from scratch, open Intellij IDEA and click on the New_Project button.
Choose Compose Desktop as a project type. It would create the Hello World app automatically.
To create a fat jar file, click on the gradle button on the right side of IDE to open the Gradle panel.
Expand on Tasks/compose_desktop/ scroll down and right-click on "packageUberJarForCurrentOS" and select Run.
This will produce the "build/compose/jars/PiKiosk-linux-x64-1.0.0.jar" file.

You can build the project on RPi from sources with this command:

./gradlew build

But before running a Compose app on RPi, run this command first:

export MESA_EXTENSION_OVERRIDE="-GL_ARB_invalidate_subdata"

Otherwise you'll get an error.  For expalation see: https://www.reddit.com/r/Kotlin/comments/1c5jikl/how_do_i_get_compose_working_on_my_raspberry_pi/

Then run it with:

./gradlew run

You can build fat jar on RPi with:

./gradlew packageUberJarForCurrentOS

## OS Installation

- [ ] Use [Raspberry Pi Imager](https://www.raspberrypi.com/software/)
  to flash Raspberry Pi OS 64-bit to a microSD card
  - [ ] Click the :gear: icon for Advanced Options
    - [ ] Check `Enable SSH`
      - [ ] Check `Set username and password`
      - [ ] Set `Username` to `cage` and enter a `Password`
    - [ ] Check `Configure Wireless LAN`
      - [ ] Enter `SSID` and `Password` for your Wi-Fi network
      - [ ] Select `Wireless LAN Country`
    - [ ] Check `Set locale settings`
      - [ ] Select `Time zone` and `Keyboard layout`
    - [ ] Click `Save`
  - [ ] Select `RASPBERRY PI OS(64-BIT)` as `Operating System`
  - [ ] Click `CHOOSE STORAGE` and select the microSD card
  - [ ] Click `WRITE`

If you need to change any of these settings later, run `sudo raspi-config` in a
terminal on the Pi 5.

The username `cage` is important here as it is used by the systemd service.
If you use a different username, you will need to update the systemd service
or add the `cage` user later(e.g. with `sudo useradd cage`)

## Hardware Setup

- [ ] Connect the following h/w with the Pi 5
  - [ ] a monitor/screen
  - [ ] a keyboard
  - [ ] and a mouse
- [ ] Insert the microSD card into the Pi 5
- [ ] Connect Pi 5 to the power supply

Pi 5 will boot up and show the desktop environment.


## Setup Cage

`ssh` into the Pi 5 with the username and password you set during the OS installation.

```sh
ssh cage@raspberrypi
```

Follow the steps below to install `cage` on Pi 5.

1. Make sure our OS is up-to-date

    ```sh
    sudo apt-get update && sudo apt-get -y upgrade
    ```

2. Install dependencies for `cage`:

    ```sh
    sudo apt-get -y install cmake libvulkan-dev libwlroots-dev
    ```

    <details>
    <summary>Optional: Building man pages</summary>
        Append `scdoc` to above command if you need cage man pages.
    </details>

    <details>
    <summary>Notes</summary>
        Without the dependencies, `meson setup build` will fail with the
    following error message(s)/warning(s):

        Found CMake: NO
        Run-time dependency wlroots found: NO (tried pkgconfig and cmake)
        Build-time dependency scdoc found: NO (tried pkgconfig and cmake)
    </details>

3. Build and install `cage`

    ```sh
    # our workspace
    mkdir ~/devel && cd ~/devel

    # master branch requires a higher version of libwlroots-dev
    git clone https://github.com/cage-kiosk/cage.git -b v0.1.5
    cd cage

    # Configure
    meson setup build --buildtype=release -Dxwayland=true -Dprefix=/usr

    # Build
    ninja -C build

    # Install
    sudo ninja -C build install
    ```

4. Clone this repository

    ```sh
    cd ~/devel
    git clone https://github.com/SergeySn/PiKiosk.git
    cd PiKiosk
    ```

5. Start `cage` on boot

    1. Install the systemd unit file for `cage` from this repository to the system.

        The [systemd unit file template](files/cage-template.service) is set
        up to run `galculator` by default as it is available in stock Pi OS.

        ```sh
        sudo cp files/cage-template.service /etc/systemd/system/cage@.service
        ```

        <details>
        <summary>Notes</summary>
        This is the same template as in the <a href="https://github.com/cage-kiosk/cage/wiki/Starting-Cage-on-boot-with-systemd">cage wiki</a>

        The only difference is that the following line

        ```sh
        ExecStart=/usr/bin/cage /usr/bin/gtk3-widget-factory
        ```
        has been replaced with

        ```sh
        ExecStart=/usr/bin/cage /usr/bin/galculator
        ```
        </details>

    2. Disable the default display manager(`lightdm`)

        ```sh
        sudo systemctl disable display-manager
        ```

    3. Enable an instantiated service of `cage`

        ```sh
        sudo ln -s /etc/systemd/system/cage@.service \
            /etc/systemd/system/graphical.target.wants/cage@tty1.service
        ```

    4. Change systemd's default target to the graphical target

        ```sh
        sudo systemctl set-default graphical.target
        ```

6. PAM configuration

    Copy the PAM configuration file for `cage`
    ```sh
    sudo cp files/cage-pam-cfg /etc/pam.d/cage
    ```

    <details>
    <summary>Notes</summary>
    This is the same PAM config as in the<a href="https://github.com/cage-kiosk/cage/wiki/Starting-Cage-on-boot-with-systemd">cage wiki</a>
    </details>

7. Reboot the Pi 5

    ```sh
    sudo reboot
    ```

After reboot, `cage` will start the `galculator` app in kiosk mode.

## Run the sample Compose app with Cage

1. SSH into Pi again

    ```sh
    ssh <user>@<hostname/IP>
    ```

2. Install JRE, required to run compose app

   ```sh
   sudo apt-get update && sudo apt install default-jre
   ```

3. Build the sample app

    ```sh
    cd ~/devel/PiKiosk
    ./gradlew build
    ```

    This will produce a JAR file at
    `/home/cage/devel/PiKiosk/build/libs/PiKiosk-1.0-SNAPSHOT.jar`.

4. Update `cage`'s systemd unit file (`/etc/systemd/system/cage@.service`) to
run your compose app instead of `galculator`

    ```sh
    #JAR=</path/to/jar>
    # replcae </path/to/jar> with actual path of the JAR file that you want to
    # run with cage
    #. e.g. if you want to run the JAR file created in the
    # previous step do:
    JAR=/home/cage/devel/PiKiosk/build/libs/PiKiosk-1.0-SNAPSHOT.jar

    sed -i -e \
      's@ExecStart=.*@ExecStart=/usr/bin/cage -- java -jar ${JAR}@' \
      /etc/systemd/system/cage@.service
    ```

5. Optional: Force a specific resolution in Compose app

    As of
    https://github.com/SergeySn/PiKiosk/commit/d4b8842c9770020d417a28ebe111b171a65eb67c,
    the Compose app is forced to use 1920x1080 resolution. To force a different
    resolution replace `WindowSize(1920.dp, 1080.dp)` in
    [Main.kt](src/main/kotlin/Main.kt) with the desired values for width and
    height.

6. Optional: Force Pi to use a specific Resolution

    ```sh
    # Replace 1920x1080@60 with desired <width>x<height>x<refresh-rate>.
    # For the app to appear fullscreen, make sure to use the same values as in
    # the previous step.
    sed -i -e \
    's@#ExecStartPre=.*@ExecStartPre=wlr-randr --output HDMI-A-2 --mode 1920x1080@60@' \
    /etc/systemd/system/cage@.service
    ```

7. Workaround https://github.com/JetBrains/skiko/issues/649

    ```sh
    sed -i -e \
    's@#Environment=.*@Environment="MESA_EXTENSION_OVERRIDE=-GL_ARB_invalidate_subdata"@' \
    /etc/systemd/system/cage@.service
    ```

    Please see the discussion [here](https://www.reddit.com/r/Kotlin/comments/1c5jikl/how_do_i_get_compose_working_on_my_raspberry_pi) for more info.

8. Restart `cage` service for the changes to take effect

    ```sh
    sudo systemctl daemon-reload
    sudo systemctl restart cage@tty1.service
    ```
