# PiKiosk
Tutorial and example on how to create a Kotlin Desktop Compose app running on Raspberry Pi 5 in kiosk mode.

Before running a Compose app on RPi, run this command first:
export MESA_EXTENSION_OVERRIDE="-GL_ARB_invalidate_subdata"
For expalation see: https://www.reddit.com/r/Kotlin/comments/1c5jikl/how_do_i_get_compose_working_on_my_raspberry_pi/
