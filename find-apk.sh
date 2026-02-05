#!/bin/bash

# Skript för att visa var APK-filen finns (eller kommer att finnas)
# Script to show where the APK file is (or will be) located

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
    FILE_SIZE=$(du -h "$DEBUG_APK" | cut -f1)
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
    FILE_SIZE=$(du -h "$RELEASE_APK" | cut -f1)
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
