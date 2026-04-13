# =============================================================================
# Stage 1: Builder
# Uses the full Maven + JDK image to compile the project and produce a fat JAR.
# =============================================================================
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /build

# Copy the POM first so Maven dependency resolution is cached as a separate
# Docker layer. This layer is only re-run when pom.xml changes.
COPY pom.xml .
RUN mvn dependency:go-offline -q

# Copy source and build the fat JAR (skip tests; tests run in CI separately).
COPY src ./src
RUN mvn clean package -DskipTests

# Download the Linux-platform JavaFX 21.0.2 JARs (classifier: linux).
# These include the native .so libraries and are placed on the explicit module
# path at runtime so the correct version is always loaded, avoiding a
# classloader conflict with any system-level JavaFX installation.
RUN for module in javafx-base javafx-graphics javafx-controls javafx-fxml; do \
        mvn dependency:copy \
            -Dartifact=org.openjfx:${module}:21.0.2:jar:linux \
            -DoutputDirectory=/build/javafx-modules \
            -q; \
    done

# =============================================================================
# Stage 2: Runtime
# =============================================================================
FROM eclipse-temurin:21-jre AS runtime

# Install:
#   JavaFX native dependencies  – GTK3, X11 client libs, OpenGL/Mesa, fonts
#   Xvfb                        – virtual X11 framebuffer (no physical display)
#   x11vnc                      – exposes the virtual display as a VNC stream
#   novnc                       – HTML5 VNC client + websockify WebSocket proxy
#
# novnc pulls in python3-websockify automatically.
RUN apt-get update && apt-get install -y --no-install-recommends \
    libgtk-3-0 \
    libx11-6 \
    libxext6 \
    libxrender1 \
    libxtst6 \
    libxi6 \
    libxrandr2 \
    libxxf86vm1 \
    libgl1 \
    libglx-mesa0 \
    libglib2.0-0 \
    libdbus-1-3 \
    libatk1.0-0 \
    libatk-bridge2.0-0 \
    fontconfig \
    fonts-dejavu-core \
    xvfb \
    x11vnc \
    novnc \
    openbox \
    && rm -rf /var/lib/apt/lists/*

# Directory that will be mounted as a volume for SQLite persistence.
RUN mkdir -p /data

WORKDIR /app

# Copy the fat JAR produced by the builder stage.
COPY --from=builder /build/target/cmsc495-expense-tracker-1.0-SNAPSHOT.jar app.jar

# Copy Linux-native JavaFX 21.0.2 JARs to an explicit module path directory.
# Placing them here (outside the fat JAR) ensures the JVM module system loads
# the correct version before Spring Boot's classloader has a chance to serve
# an older or mismatched version from BOOT-INF/lib.
COPY --from=builder /build/javafx-modules /opt/javafx

# Configure Openbox to maximise every normal window on creation so the JavaFX
# application fills the noVNC viewport without the user having to resize it.
RUN mkdir -p /root/.config/openbox && printf '\
<?xml version="1.0" encoding="UTF-8"?>\n\
<openbox_config xmlns="http://openbox.org/3.4/rc">\n\
  <applications>\n\
    <application title="Personal Expense Tracker">\n\
      <maximized>yes</maximized>\n\
    </application>\n\
  </applications>\n\
</openbox_config>\n' > /root/.config/openbox/rc.xml

# Copy and permission the entrypoint script.
COPY docker-entrypoint.sh /docker-entrypoint.sh
RUN chmod +x /docker-entrypoint.sh

# Declare the data directory as a mount point so Docker knows it holds
# external state (the SQLite database).
VOLUME ["/data"]

# Expose the noVNC web port. Users open http://localhost:6080/vnc.html
EXPOSE 6080

ENTRYPOINT ["/docker-entrypoint.sh"]
