#!/bin/bash

# Skript för att visa var APK-filen finns (eller kommer att finnas)
# Script to show where the APK file is (or will be) located
#
# Note: This script should be executable. If not, run: chmod +x find-apk.sh

# Färger för output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Function to get file size in human-readable format (cross-platform)
get_file_size() {
    local file="$1"
    if command -v stat > /dev/null 2>&1; then
        if [[ "$OSTYPE" == "darwin"* ]]; then
            # macOS
            stat -f%z "$file" | awk '{ split( "B KB MB GB" , v ); s=1; while( $1>1024 ){ $1/=1024; s++ } printf "%.1f%s", $1, v[s] }'
        else
            # Linux
            stat -c%s "$file" | awk '{ split( "B KB MB GB" , v ); s=1; while( $1>1024 ){ $1/=1024; s++ } printf "%.1f%s", $1, v[s] }'
        fi
    else
        ls -lh "$file" | awk '{print $5}'
    fi
}

echo "================================================"
echo "  Hörselhjälp - Hitta APK och AAB filer"
echo "================================================"
echo ""

# Sökvägar
DEBUG_APK="app/build/outputs/apk/debug/app-debug.apk"
RELEASE_APK="app/build/outputs/apk/release/app-release.apk"
RELEASE_AAB="app/build/outputs/bundle/release/app-release.aab"
FULL_DEBUG_PATH="$(pwd)/$DEBUG_APK"
FULL_RELEASE_PATH="$(pwd)/$RELEASE_APK"
FULL_AAB_PATH="$(pwd)/$RELEASE_AAB"

echo -e "${BLUE}Debug APK:${NC}"
if [ -f "$DEBUG_APK" ]; then
    echo -e "  ${GREEN}✓ Finns!${NC}"
    echo "  Sökväg: $FULL_DEBUG_PATH"
    FILE_SIZE=$(get_file_size "$DEBUG_APK")
    echo "  Storlek: $FILE_SIZE"
else
    echo -e "  ${YELLOW}✗ Finns inte ännu${NC}"
    echo "  Bygg med: ./gradlew assembleDebug"
    echo "  Eller använd: ./build-and-find-apk.sh"
    echo "  Kommer att skapas i: $FULL_DEBUG_PATH"
fi

echo ""
echo -e "${BLUE}Release APK:${NC}"
if [ -f "$RELEASE_APK" ]; then
    echo -e "  ${GREEN}✓ Finns!${NC}"
    echo "  Sökväg: $FULL_RELEASE_PATH"
    FILE_SIZE=$(get_file_size "$RELEASE_APK")
    echo "  Storlek: $FILE_SIZE"
else
    echo -e "  ${YELLOW}✗ Finns inte ännu${NC}"
    echo "  Bygg med: ./gradlew assembleRelease"
    echo "  Kommer att skapas i: $FULL_RELEASE_PATH"
fi

echo ""
echo -e "${BLUE}Release AAB (Android App Bundle):${NC}"
if [ -f "$RELEASE_AAB" ]; then
    echo -e "  ${GREEN}✓ Finns!${NC}"
    echo "  Sökväg: $FULL_AAB_PATH"
    FILE_SIZE=$(get_file_size "$RELEASE_AAB")
    echo "  Storlek: $FILE_SIZE"
    echo "  Info: AAB är Googles rekommenderade format för Google Play"
else
    echo -e "  ${YELLOW}✗ Finns inte ännu${NC}"
    echo "  Bygg med: ./gradlew bundleRelease"
    echo "  Eller använd: ./build-and-find-aab.sh"
    echo "  Kommer att skapas i: $FULL_AAB_PATH"
fi

echo ""
echo "================================================"
echo "Snabbkommandon:"
echo "================================================"
echo ""
echo "Bygg och hitta debug APK automatiskt:"
echo "  ./build-and-find-apk.sh"
echo ""
echo "Bygg och hitta release AAB automatiskt:"
echo "  ./build-and-find-aab.sh"
echo ""
echo "Bygg endast debug APK:"
echo "  ./gradlew assembleDebug"
echo ""
echo "Bygg endast release AAB:"
echo "  ./gradlew bundleRelease"
echo ""
echo "Öppna mappen med APK-filer (fungerar endast efter bygget):"
echo "  xdg-open app/build/outputs/apk/debug/  # Linux"
echo "  open app/build/outputs/apk/debug/      # Mac"
echo "  explorer app\\build\\outputs\\apk\\debug\\  # Windows"
echo ""
