# Streaming Service with Android Development and Oven Media Engine - From Scratch

Welcome to the GitHub repository for our comprehensive tutorial on building a streaming service using Android development with Kotlin, Jetpack Compose, and WebRTC, alongside deploying the Oven Media Engine. This course will guide you through creating a complete streaming solution, including both the Android client and the server-side setup with Oven Media Engine.

**Watch the Tutorial here:** [www.youtube.com/@codewithkael](https://youtube.com/playlist?list=PLFelST8t9nqgkQsqbapT2JJeQJ8F4JU2g&si=yDxsuzwp-Lw3zA7S)

## What You Will Learn
1. **Kotlin for Android Development:** Master Kotlin programming for Android to build robust and scalable applications.
2. **Jetpack Compose Basics:** Learn to design modern UIs with Jetpack Compose.
3. **WebRTC Integration:** Implement real-time communication features using WebRTC for live streaming and video calls on Android.
4. **Oven Media Engine Setup:** Deploy and configure the Oven Media Engine using Docker for managing streaming content.
5. **Streaming Architecture:** Understand the complete architecture of a streaming service, including the client-server interaction and media streaming protocols.
6. **Client-Server Communication:** Develop an Android streaming client and viewer application, and integrate it with the Oven Media Engine server.

## Prerequisites
- Basic knowledge of Kotlin and Android development.
- Familiarity with Jetpack Compose for UI development.
- Understanding of WebRTC for real-time communication.
- Basic knowledge of Docker and Linux command line.
- Docker installed on your system.
- Android Studio installed on your computer.
- An Android device or emulator for testing the application.

## Tutorial Video
For a detailed explanation and a step-by-step guide through the project, watch our tutorial on YouTube. This video complements the written guide in this repository, providing visual aid and additional commentary.

## Setup Instructions

### 1. Setting Up the Oven Media Engine

Ensure Docker is installed and running on your Linux system. Follow the official [Docker installation guide for Linux](https://docs.docker.com/engine/install/) if needed.

To deploy the Oven Media Engine, use the following Docker command:

----to setup the oven media engine simply run this----
docker run --name ome -d -e OME_HOST_IP=YOUR_IP_ADDRESS \
-p 1935:1935 -p 9999:9999/udp -p 9000:9000 -p 3333:3333 \
-p 3478:3478 -p 10000-10009:10000-10009/udp \
airensoft/ovenmediaengine:0.15.13

----for oven player use this----
docker run -d -p 8090:80 airensoft/ovenplayerdemo:latest

----oven player url is here----
http://YOUR_IP_ADDRESS:8090/

## Tutorial Video
For a detailed explanation and a step-by-step guide through the project, watch our tutorial on YouTube. This video complements the written guide in this repository, providing visual aid and additional commentary.

## Resources
In this repository, you will find:
- **Source Code:** The complete source code for the local video call application demonstrated in the tutorial.
- **Documentation:** Additional documentation on Jetpack Compose and WebRTC.

## Contributing
We welcome contributions to this project! If you have suggestions, improvements, or bug fixes, please fork the repository and submit a pull request.

## Support
If you have any questions or need further assistance, please reach out by opening an issue in this repository, or contact us directly through the comments section of the YouTube tutorial.

## License
This project is licensed under the MIT License - see the LICENSE file for details.

Thank you for visiting this repository, and we hope you find the tutorial helpful and informative!
