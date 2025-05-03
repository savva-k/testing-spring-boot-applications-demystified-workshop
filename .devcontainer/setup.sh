#!/bin/bash
set -e

echo "Setting up Spring Boot Testing Workshop environment..."

# Configure Git
git config --global pull.rebase true
git config --global core.autocrlf input

# Install additional dependencies
echo "Installing additional dependencies..."
sudo apt-get update
sudo apt-get install -y graphviz

# Setup Java security properties to allow Testcontainers to work in a container
echo "Setting up Java security properties..."
cat > .devcontainer/java-security.properties << EOF
jdk.tls.disabledAlgorithms=SSLv3, RC4, DES, MD5withRSA, DH keySize < 1024, EC keySize < 224, 3DES_EDE_CBC, anon, NULL
EOF

# Generate PlantUML diagrams as PNG
echo "Generating PlantUML diagrams..."
mkdir -p diagrams/generated
for file in diagrams/*.plantuml; do
    echo "Processing $file..."
    base_name=$(basename "$file" .plantuml)
    java -jar /usr/local/plantuml/plantuml.jar -tpng "$file" -o generated
done

# Add execution permission to Maven wrapper
echo "Making Maven wrapper executable..."
chmod +x ./mvnw

# Build the projects to download dependencies
echo "Building projects to download dependencies..."
for lab in labs/lab-1 labs/lab-2 labs/lab-3 labs/lab-4; do
    echo "Building $lab..."
    cd "$lab" || exit
    ../../mvnw dependency:resolve -DskipTests
    cd - || exit
done

echo "Setup complete! You're ready to start the workshop."