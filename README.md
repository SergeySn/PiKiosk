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
