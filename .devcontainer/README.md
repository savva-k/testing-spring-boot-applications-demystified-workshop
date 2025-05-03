# Development Container for Spring Boot Testing Workshop

This directory contains configuration for a development container that can be used with GitHub Codespaces or local development with VS Code Remote Containers.

## Features

- Java 21 (OpenJDK)
- Maven 3.9.6
- Docker-in-Docker (for TestContainers)
- PlantUML for diagram generation
- VS Code extensions for Spring Boot development
- Pre-configured settings

## Getting Started

### Using GitHub Codespaces

1. On the GitHub repository page, click on the "Code" button
2. Select the "Codespaces" tab
3. Click "Create codespace on main"
4. Wait for the codespace to start and setup to complete

### Using VS Code Remote Containers

1. Install the [Remote - Containers](https://marketplace.visualstudio.com/items?itemName=ms-vscode-remote.remote-containers) extension
2. Clone the repository locally
3. Open the repository in VS Code
4. When prompted "Folder contains a Dev Container configuration file. Reopen folder to develop in a container", click "Reopen in Container"
5. Alternatively, press F1, select "Remote-Containers: Open Folder in Container..."

## Post-Setup Actions

After the container is set up, it automatically:

1. Configures Git settings
2. Installs additional dependencies
3. Sets up Java security properties for TestContainers
4. Generates PNG versions of PlantUML diagrams
5. Makes Maven wrapper executable
6. Downloads Maven dependencies for faster workshop experience

## Troubleshooting

If you encounter issues with the setup:

1. Check the terminal output during container creation for errors
2. Try running the setup script manually: `.devcontainer/setup.sh`
3. Ensure Docker is running properly by executing `docker ps`
4. For TestContainers issues, check the Docker socket is correctly mounted