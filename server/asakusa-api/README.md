# Asakusa API

## Requirements
The following are some requirements for building the app:
* Make sure to have [Docker Desktop](https://www.docker.com/products/docker-desktop/) installed
* If using [Visual Studio Code](https://code.visualstudio.com/) as your IDE, it is recommended to install the Docker extension from Microsoft. You can find it by searching for `docker` in the `Extensions` panel.

## Building the Docker Image
Run the following command, making sure to replace `{tag}` with the next API version

    `docker build -t asakusa-api:{tag} .`

Tests are automatically run during the build initial image creation. And because layers are extracted during the build, those tests won't run again in subsequent builds unless changes are made to the test files.


## Running the Image in Your Local Container
Run the following command to launch the app on your local machine

    `docker run -it --rm -p 9443:9443 -e SPRING_PROFILES_ACTIVE=dev asakusa-api:{version} sh`

Note:
* The environment is provided as a container argument, so make sure to specify it. It is recommended to use `dev`.
* Replace `{tag}` with the build version from the previous section.
* Add the `--rm` flag so that the container can be automatically removed on exit
* Since the container is being launched in `dev` mode, the `-it` and `sh` flags are added so that the log output are displayed in the current terminal window. This also makes it easy to exit the session by pressing `CTRL + X`.


## Useful Links

### Docker Hub
https://hub.docker.com

### Docker Docs
https://docs.docker.com/manuals/
