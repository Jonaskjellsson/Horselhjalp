#!/bin/bash

# Skript för att bygga release AAB och visa var den finns
# Script to build release AAB and show where it is located
#
# Note: This script should be executable. If not, run: chmod +x build-and-find-aab.sh

set -e

echo "================================================"
echo "  Hörselhjälp - Bygg och hitta release AAB"
echo "================================================"
echo ""

# Färger för output (om terminalen stödjer det)
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
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

echo -e "${BLUE}Steg 1:${NC} Rensar gamla byggfiler..."
./gradlew clean

echo ""
echo -e "${BLUE}Steg 2:${NC} Bygger release AAB (Android App Bundle)..."
./gradlew bundleRelease

echo ""
echo -e "${GREEN}✓ Bygget är klart!${NC}"
echo ""

# Sökväg till AAB-filen
AAB_PATH="app/build/outputs/bundle/release/app-release.aab"
FULL_PATH="$(pwd)/$AAB_PATH"

# Kontrollera att filen finns
if [ -f "$AAB_PATH" ]; then
    echo "================================================"
    echo -e "${GREEN}✓ AAB-filen har skapats!${NC}"
    echo "================================================"
    echo ""
    echo -e "${YELLOW}Sökväg till AAB:${NC}"
    echo "  $FULL_PATH"
    echo ""
    echo -e "${YELLOW}Relativ sökväg:${NC}"
    echo "  $AAB_PATH"
    echo ""
    
    # Visa filstorlek - cross-platform compatible
    FILE_SIZE=$(get_file_size "$AAB_PATH")
    echo -e "${YELLOW}Filstorlek:${NC} $FILE_SIZE"
    echo ""
    
    echo "================================================"
    echo "Om Android App Bundle (AAB):"
    echo "================================================"
    echo ""
    echo "AAB är Googles rekommenderade publiceringsformat för Android-appar."
    echo ""
    echo "Fördelar med AAB:"
    echo "  • Mindre nedladdningsstorlek för användare"
    echo "  • Google Play genererar optimerade APK:er för varje enhet"
    echo "  • Stöd för dynamiska funktionsmoduler"
    echo "  • Krävs för nya appar på Google Play (sedan augusti 2021)"
    echo ""
    echo "Nästa steg:"
    echo "================================================"
    echo ""
    echo "1. För att publicera på Google Play:"
    echo "   - Logga in på Google Play Console"
    echo "   - Ladda upp AAB-filen: $AAB_PATH"
    echo "   - Följ publiceringsprocessen"
    echo ""
    echo "2. För att testa lokalt (konvertera till APK):"
    echo "   - Installera bundletool från Google"
    echo "   - Kör: bundletool build-apks --bundle=$AAB_PATH --output=app.apks"
    echo "   - Installera: bundletool install-apks --apks=app.apks"
    echo ""
    echo "3. För att öppna mappen i filhanteraren:"
    echo "   xdg-open app/build/outputs/bundle/release/   # Linux"
    echo "   open app/build/outputs/bundle/release/       # Mac"
    echo "   explorer app\\build\\outputs\\bundle\\release\\    # Windows"
    echo ""
else
    echo -e "${YELLOW}⚠ Varning:${NC} AAB-filen kunde inte hittas på den förväntade platsen."
    echo "Kontrollera att bygget slutfördes utan fel."
fi
