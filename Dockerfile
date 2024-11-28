FROM eclipse-temurin:17-jdk-jammy

ENV PORT=8500

WORKDIR /app

COPY ./src ./src
COPY ./build.gradle.kts ./build.gradle.kts
COPY ./settings.gradle.kts ./settings.gradle.kts
COPY ./gradlew ./gradlew
COPY ./gradlew.bat ./gradlew.bat
COPY ./gradle/wrapper/gradle-wrapper-original.properties ./gradle/wrapper/gradle-wrapper.properties
COPY ./gradle/wrapper/gradle-wrapper.jar ./gradle/wrapper/gradle-wrapper.jar

RUN chmod +x gradlew
RUN ./gradlew bootJar

RUN apt-get update && apt-get install -y \
    libnss3-dev \
    libgdk-pixbuf2.0-dev \
    libgtk-3-dev \
    libxss-dev \
    libasound2 \
    wget \
    gnupg \
    ca-certificates \
    fonts-liberation \
    libatk-bridge2.0-0 \
    libgtk-3-0 \
    libnspr4 \
    libnss3 \
    libx11-xcb1 \
    libxcomposite1 \
    libxcursor1 \
    libxdamage1 \
    libxrandr2 \
    libxss1 \
    libxtst6 \
    xdg-utils \
    libgbm-dev \
    libdrm2 \
    libxshmfence1 \
    libatk1.0-0 \
    libatk-bridge2.0-0 \
    libx11-xcb-dev \
    libxcb-dri3-0 \
    libx11-6 \
    libxext6 \
    libxfixes3 \
    libxrender1 \
    libxtst6 \
    libgtk-3-0 \
    libxss1 \
    libgconf-2-4 \
    libnotify4 \
    libfontconfig1 \
    libxcomposite1 \
    libxdamage1 \
    libxi6 \
    libsrtp2-1 \
    libxslt1.1 \
    libappindicator3-1 \
    libsecret-1-0 \
    libxkbcommon0 \
    libpangocairo-1.0-0 \
    libatk1.0-0 \
    libcairo2 \
    libgdk-pixbuf2.0-0 \
    libgdk-pixbuf2.0-dev \
    libpng16-16 \
    libevent-2.1-7 \
    libjpeg62 \
    libgif7 \
    libpango-1.0-0 \
    libpangocairo-1.0-0 \
    libx11-dev \
    libcairo2-dev \
    libpango1.0-dev \
    libjpeg-dev \
    libgif-dev \
    libwebp-dev \
    libxslt-dev \
    libappindicator3-dev \
    libsecret-1-dev \
    xvfb

EXPOSE $PORT
ENTRYPOINT ["java", "-jar", "build/libs/html2pdf-0.0.1-SNAPSHOT.jar"]

