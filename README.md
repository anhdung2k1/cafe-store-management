# Cafe Store Management

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=java&logoColor=white)
![Socket.IO](https://img.shields.io/badge/Socket.io-010101?&style=for-the-badge&logo=Socket.io&logoColor=white)
![Apache Maven](https://img.shields.io/badge/Apache%20Maven-C71A36?style=for-the-badge&logo=Apache%20Maven&logoColor=white)
![Apache Tomcat](https://img.shields.io/badge/apache%20tomcat-%23F8DC75.svg?style=for-the-badge&logo=apache-tomcat&logoColor=black)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-F2F4F9?style=for-the-badge&logo=spring-boot)
![C++](https://img.shields.io/badge/C%2B%2B-blue?style=for-the-badge&logo=c++&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-blue?style=for-the-badge&logo=MYSQL&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-green?style=for-the-badge&logo=spring-boot&logoColor=white)
![Kotlin](https://img.shields.io/badge/kotlin-%23ER8B41.svg?style=for-the-badge&logo=kotlin&logoColor=white)

Cafe Store Management repository works as TPC client-server connection running on Docker environment.

---
The repository using socket connection with Spring Boot web as main hosting APIs to ensure the security connection in each API request. The MYSQL database serves as a relational-database to perform query and storage datasets.

## Contents

- [Cafe Store](#cafe-store-management)
  - [Contents](#contents)
  - [Developer's Guide](#developers-guide)
    - [Getting Started](#getting-started)
      - [Development Environment](#development-environment)
      - [How to use](#how-to-use)

## Developer's Guide

### Getting Started

#### Development Environment

The recommend standard development environment is Ubuntu 18.04 LTS or later

#### How to use

1. Install docker: [docker installation](https://docs.docker.com/engine/install/ubuntu/)

    ```bash
    sudo apt-get update
    sudo apt-get install ca-certificates curl
    sudo install -m 0755 -d /etc/apt/keyrings
    sudo curl -fsSL https://download.docker.com/linux/ubuntu/gpg -o /etc/apt/keyrings/docker.asc
    sudo chmod a+r /etc/apt/keyrings/docker.asc
    
    # Add the repository to Apt sources:
    echo \
    "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.asc] https://download.docker.com/linux/ubuntu \
    $(. /etc/os-release && echo "$VERSION_CODENAME") stable" | \
    sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
    sudo apt-get update
    
    sudo apt-get install docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
    sudo groupadd docker
    sudo usermod -aG docker $USER
    newgrp docker
    ```

2. Run docker compose to start docker container and build docker images
```bash
$ docker compose up -d
```
If you run the Back end API servers with docker, this automatically sync the SQL data.
```
#Start docker mysql container
docker run -d --name mysql \
    -e MYSQL_ROOT_PASSWORD=root \
    -e MYSQL_DATABASE=${COMMON_DB} \
    -e MYSQL_USER=${COMMON_DB} \
    -e MYSQL_PASSWORD=${COMMON_DB} \
    -v ${VAS_GIT}/sql:/docker-entrypoint-initdb.d \
    -p 3306:3306 \
    mysql:latest \
|| die "[ERROR]: Failed to run mysql docker"
```
