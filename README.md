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
  - [ ] Click the gear icon (:gear:) for Advanced Options
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

1. Install dependencies for `cage`:

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

2. Build and install `cage`

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

3. Clone this repository

    ```sh
    cd ~/devel
    git clone git@github.com:embdur/PiKiosk.git -b cage-setup
    cd PiKiosk
    ```

4. Start `cage` on boot

    Copy the systemd unit file for cage:
    ```sh
    sudo cp etc/systemd/system/cage@.service /etc/systemd/system/cage@.service
    ```

    <details>
    <summary>Notes</summary>
    This is the same template as in the <a href="https://github.com/cage-kiosk/cage/wiki/Starting-Cage-on-boot-with-systemd">cage wiki</a>

    The only difference is that we replace the following line

    ```sh
    ExecStart=/usr/bin/cage /usr/bin/gtk3-widget-factory
    ```
    with

    ```sh
    ExecStart=/usr/bin/cage /usr/bin/galculator
    ```
    </details>

    Enable an instantiated service of `cage`
    ```sh
    sudo ln -s /etc/systemd/system/cage@.service \
        /etc/systemd/system/graphical.target.wants/cage@tty1.service
    ```

    Change systemd's default target to the graphical target
    ```sh
    sudo systemctl set-default graphical.target
    ```

5. PAM configuration

    Copy the PAM configuration file for `cage`
    ```sh
    sudo cp etc/pam.d/cage /etc/pam.d/cage
    ```

    <details>
    <summary>Notes</summary>
    This is the same PAM config as in the<a href="https://github.com/cage-kiosk/cage/wiki/Starting-Cage-on-boot-with-systemd">cage wiki</a>
    </details>

6. Reboot the Pi 5

    ```sh
    sudo reboot
    ```
