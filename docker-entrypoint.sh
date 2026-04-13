#!/bin/bash
set -e

# -----------------------------------------------------------------------------
# Start a virtual X11 framebuffer so JavaFX has a display to render to without
# requiring any X server on the host machine.
#   :99       – display number (arbitrary, just must be unused inside container)
#   1280x800  – initial window size; can be changed freely
#   24        – colour depth
#   -ac       – disable access control (safe inside the container)
# -----------------------------------------------------------------------------
Xvfb :99 -screen 0 1280x800x24 -ac +extension GLX +render -noreset &

# Give Xvfb a moment to initialise before other processes try to connect.
sleep 1

# -----------------------------------------------------------------------------
# Start a lightweight window manager so that JavaFX popup windows (ComboBox
# dropdowns, DatePicker, ContextMenu, tooltips) receive proper focus and
# pointer-grab handling. Without a WM, Xvfb has no process managing window
# stacking, so any popup loses its grab the moment the mouse moves and closes
# immediately. Openbox is minimal and has no visible chrome of its own.
# -----------------------------------------------------------------------------
export DISPLAY=:99
openbox &

# -----------------------------------------------------------------------------
# Expose the virtual display as a VNC stream on localhost:5900.
#   -forever  – keep running after client disconnects (allows reconnects)
#   -nopw     – no VNC password (container is local-only)
#   -listen   – restrict to loopback so VNC is not directly reachable
# -----------------------------------------------------------------------------
x11vnc -display :99 -forever -nopw -listen 127.0.0.1 -rfbport 5900 -quiet &

# -----------------------------------------------------------------------------
# Start noVNC: websockify proxies WebSocket → VNC and also serves the bundled
# HTML5 client from /usr/share/novnc/.
# Open http://localhost:6080/vnc.html in any browser to view the UI.
# -----------------------------------------------------------------------------
websockify --web /usr/share/novnc/ --wrap-mode=ignore 6080 127.0.0.1:5900 &

# Launch the application as PID 1 so Docker stop/kill signals are delivered
# directly to the JVM.
#
# --module-path /opt/javafx   : Linux-native JavaFX 21.0.2 JARs (built into the
#                               image). Putting them here as named modules makes
#                               the JVM module system load them at the highest
#                               priority, before Spring Boot's classloader reads
#                               any JavaFX JARs from BOOT-INF/lib. This prevents
#                               the NoSuchMethodError caused by an older JavaFX
#                               version taking precedence on the module path.
# --add-modules               : Explicitly root the required JavaFX modules so
#                               the JVM resolves their transitive dependencies
#                               (javafx.graphics, javafx.base, etc.) automatically.
exec java \
    --module-path /opt/javafx \
    --add-modules javafx.controls,javafx.fxml \
    -jar /app/app.jar
