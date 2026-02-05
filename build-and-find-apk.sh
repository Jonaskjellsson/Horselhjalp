#!/bin/bash

# Skript för att bygga debug APK och visa var den finns
# Script to build debug APK and show where it is located
#
# Note: This script should be executable. If not, run: chmod +x build-and-find-apk.sh

set -e

echo "================================================"
echo "  Hörselhjälp - Bygg och hitta debug APK"
echo "================================================"
echo ""

# Färger för output (om terminalen stödjer det)
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${BLUE}Steg 1:${NC} Rensar gamla byggfiler..."
./gradlew clean

echo ""
echo -e "${BLUE}Steg 2:${NC} Bygger debug APK..."
./gradlew assembleDebug

echo ""
echo -e "${GREEN}✓ Bygget är klart!${NC}"
echo ""

# Sökväg till APK-filen
APK_PATH="app/build/outputs/apk/debug/app-debug.apk"
FULL_PATH="$(pwd)/$APK_PATH"

# Kontrollera att filen finns
if [ -f "$APK_PATH" ]; then
    echo "================================================"
    echo -e "${GREEN}✓ APK-filen har skapats!${NC}"
    echo "================================================"
    echo ""
    echo -e "${YELLOW}Sökväg till APK:${NC}"
    echo "  $FULL_PATH"
    echo ""
    echo -e "${YELLOW}Relativ sökväg:${NC}"
    echo "  $APK_PATH"
    echo ""
    
    # Visa filstorlek - cross-platform compatible
    if command -v stat > /dev/null 2>&1; then
        if [[ "$OSTYPE" == "darwin"* ]]; then
            # macOS
            FILE_SIZE=$(stat -f%z "$APK_PATH" | awk '{ split( "B KB MB GB" , v ); s=1; while( $1>1024 ){ $1/=1024; s++ } printf "%.1f%s", $1, v[s] }')
        else
            # Linux
            FILE_SIZE=$(stat -c%s "$APK_PATH" | awk '{ split( "B KB MB GB" , v ); s=1; while( $1>1024 ){ $1/=1024; s++ } printf "%.1f%s", $1, v[s] }')
        fi
    else
        FILE_SIZE=$(ls -lh "$APK_PATH" | awk '{print $5}')
    fi
    echo -e "${YELLOW}Filstorlek:${NC} $FILE_SIZE"
    echo ""
    
    echo "================================================"
    echo "Nästa steg:"
    echo "================================================"
    echo ""
    echo "1. För att installera på ansluten Android-enhet:"
    echo "   ./gradlew installDebug"
    echo ""
    echo "2. För att manuellt överföra till din enhet:"
    echo "   - Kopiera filen från: $APK_PATH"
    echo "   - Överför den till din Android-enhet"
    echo "   - Öppna filen på enheten för att installera"
    echo ""
    echo "3. För att öppna mappen i filhanteraren (Linux):"
    echo "   xdg-open app/build/outputs/apk/debug/"
    echo ""
else
    echo -e "${YELLOW}⚠ Varning:${NC} APK-filen kunde inte hittas på den förväntade platsen."
    echo "Kontrollera att bygget slutfördes utan fel."
fi
