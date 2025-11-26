#!/bin/bash
#
# TeamCaptain Credentials Setup Script
#
# This script creates a credentials file OUTSIDE your project directory
# in a secure location: ~/.teamcaptain/credentials.properties
#
# Usage: ./setup-credentials.sh

set -e

CREDENTIALS_DIR="$HOME/.teamcaptain"
CREDENTIALS_FILE="$CREDENTIALS_DIR/credentials.properties"

echo "=========================================="
echo "TeamCaptain OAuth Credentials Setup"
echo "=========================================="
echo ""
echo "This script will store your OAuth credentials in:"
echo "  $CREDENTIALS_FILE"
echo ""
echo "This file is OUTSIDE your project directory and will"
echo "NEVER be committed to Git."
echo ""

# Create directory if it doesn't exist
if [ ! -d "$CREDENTIALS_DIR" ]; then
    echo "Creating directory: $CREDENTIALS_DIR"
    mkdir -p "$CREDENTIALS_DIR"
    chmod 700 "$CREDENTIALS_DIR"  # Only you can read/write/execute
fi

# Check if credentials file already exists
if [ -f "$CREDENTIALS_FILE" ]; then
    echo "⚠️  Credentials file already exists!"
    echo ""
    read -p "Do you want to overwrite it? (y/N): " -n 1 -r
    echo ""
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        echo "Setup cancelled."
        exit 0
    fi
fi

echo ""
echo "Please enter your OAuth credentials:"
echo "(See OAUTH_SETUP.md for how to obtain these)"
echo ""

# Read Google Client ID
read -p "Google Client ID: " GOOGLE_ID
if [ -z "$GOOGLE_ID" ]; then
    echo "⚠️  Warning: Google Client ID is empty"
fi

# Read GitHub Client ID
read -p "GitHub Client ID: " GITHUB_ID
if [ -z "$GITHUB_ID" ]; then
    echo "⚠️  Warning: GitHub Client ID is empty"
fi

# Read GitHub Client Secret (hidden input)
read -s -p "GitHub Client Secret: " GITHUB_SECRET
echo ""
if [ -z "$GITHUB_SECRET" ]; then
    echo "⚠️  Warning: GitHub Client Secret is empty"
fi

# Write credentials file
cat > "$CREDENTIALS_FILE" <<EOF
# TeamCaptain OAuth Credentials
# Generated: $(date)
#
# This file is stored OUTSIDE your project directory
# It will NEVER be committed to Git
#
# To update credentials, run: ./setup-credentials.sh
# To remove credentials, delete: rm ~/.teamcaptain/credentials.properties

CAPTAIN_GOOGLE_CLIENT_ID=$GOOGLE_ID
CAPTAIN_GITHUB_CLIENT_ID=$GITHUB_ID
CAPTAIN_GITHUB_CLIENT_SECRET=$GITHUB_SECRET
EOF

# Secure the file (only you can read/write)
chmod 600 "$CREDENTIALS_FILE"

echo ""
echo "✅ Credentials saved successfully!"
echo ""
echo "File location: $CREDENTIALS_FILE"
echo "Permissions: $(ls -l "$CREDENTIALS_FILE" | awk '{print $1}')"
echo ""
echo "You can now build the project:"
echo "  ./gradlew build"
echo ""
echo "To view your credentials:"
echo "  cat $CREDENTIALS_FILE"
echo ""
echo "To update credentials, run this script again:"
echo "  ./setup-credentials.sh"
echo ""
echo "To remove credentials:"
echo "  rm $CREDENTIALS_FILE"
echo ""
