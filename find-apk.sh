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

echo "================================================"
echo "  Hörselhjälp - Hitta APK-fil"
echo "================================================"
echo ""

# Sökvägar
DEBUG_APK="app/build/outputs/apk/debug/app-debug.apk"
RELEASE_APK="app/build/outputs/apk/release/app-release.apk"
FULL_DEBUG_PATH="$(pwd)/$DEBUG_APK"
FULL_RELEASE_PATH="$(pwd)/$RELEASE_APK"

echo -e "${BLUE}Debug APK:${NC}"
if [ -f "$DEBUG_APK" ]; then
    echo -e "  ${GREEN}✓ Finns!${NC}"
    echo "  Sökväg: $FULL_DEBUG_PATH"
    # Cross-platform file size display
    if command -v stat > /dev/null 2>&1; then
        if [[ "$OSTYPE" == "darwin"* ]]; then
            # macOS
            FILE_SIZE=$(stat -f%z "$DEBUG_APK" | awk '{ split( "B KB MB GB" , v ); s=1; while( $1>1024 ){ $1/=1024; s++ } printf "%.1f%s", $1, v[s] }')
        else
            # Linux
            FILE_SIZE=$(stat -c%s "$DEBUG_APK" | awk '{ split( "B KB MB GB" , v ); s=1; while( $1>1024 ){ $1/=1024; s++ } printf "%.1f%s", $1, v[s] }')
        fi
    else
        FILE_SIZE=$(ls -lh "$DEBUG_APK" | awk '{print $5}')
    fi
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
    # Cross-platform file size display
    if command -v stat > /dev/null 2>&1; then
        if [[ "$OSTYPE" == "darwin"* ]]; then
            # macOS
            FILE_SIZE=$(stat -f%z "$RELEASE_APK" | awk '{ split( "B KB MB GB" , v ); s=1; while( $1>1024 ){ $1/=1024; s++ } printf "%.1f%s", $1, v[s] }')
        else
            # Linux
            FILE_SIZE=$(stat -c%s "$RELEASE_APK" | awk '{ split( "B KB MB GB" , v ); s=1; while( $1>1024 ){ $1/=1024; s++ } printf "%.1f%s", $1, v[s] }')
        fi
    else
        FILE_SIZE=$(ls -lh "$RELEASE_APK" | awk '{print $5}')
    fi
    echo "  Storlek: $FILE_SIZE"
else
    echo -e "  ${YELLOW}✗ Finns inte ännu${NC}"
    echo "  Bygg med: ./gradlew assembleRelease"
    echo "  Kommer att skapas i: $FULL_RELEASE_PATH"
fi

echo ""
echo "================================================"
echo "Snabbkommandon:"
echo "================================================"
echo ""
echo "Bygg och hitta debug APK automatiskt:"
echo "  ./build-and-find-apk.sh"
echo ""
echo "Bygg endast debug APK:"
echo "  ./gradlew assembleDebug"
echo ""
echo "Öppna mappen med APK-filer:"
echo "  xdg-open app/build/outputs/apk/debug/  # Linux"
echo "  open app/build/outputs/apk/debug/      # Mac"
echo "  explorer app\\build\\outputs\\apk\\debug\\  # Windows"
echo ""
