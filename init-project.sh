#!/usr/bin/env bash
set -euo pipefail

# =============================================================================
# init-project.sh — Initialize a new project from SpringBootTemplate
# Usage: ./init-project.sh --group-id com.acme --artifact myapp --name "My App" --author "John Doe"
# =============================================================================

# ── Color helpers ──
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m'

info()  { printf "${CYAN}[INFO]${NC}  %s\n" "$1"; }
ok()    { printf "${GREEN}[OK]${NC}    %s\n" "$1"; }
warn()  { printf "${YELLOW}[WARN]${NC}  %s\n" "$1"; }
error() { printf "${RED}[ERROR]${NC} %s\n" "$1" >&2; }

# ── Detect OS for sed -i compatibility ──
if [[ "$(uname)" == "Darwin" ]]; then
    SED_I=(sed -i '')
else
    SED_I=(sed -i)
fi

# ── Parse arguments ──
GROUP_ID=""
ARTIFACT=""
NAME=""
AUTHOR=""

while [[ $# -gt 0 ]]; do
    case $1 in
        --group-id) GROUP_ID="$2"; shift 2 ;;
        --artifact) ARTIFACT="$2"; shift 2 ;;
        --name)     NAME="$2";     shift 2 ;;
        --author)   AUTHOR="$2";   shift 2 ;;
        -h|--help)
            echo "Usage: ./init-project.sh --group-id <groupId> --artifact <artifact> [--name <name>] [--author <author>]"
            echo ""
            echo "Options:"
            echo "  --group-id   Maven groupId / Java package root (e.g. com.acme)       [required]"
            echo "  --artifact   Artifact name in kebab-case (e.g. myapp, demo-app)       [required]"
            echo "  --name       Human-readable project name (default: derived from artifact)"
            echo "  --author     Author name for @author tags (default: keeps 'tzesh')"
            exit 0
            ;;
        *) error "Unknown option: $1"; exit 1 ;;
    esac
done

# ── Interactive prompts for missing required values ──
if [[ -z "$GROUP_ID" ]]; then
    read -rp "Enter groupId (e.g. com.acme): " GROUP_ID
fi
if [[ -z "$ARTIFACT" ]]; then
    read -rp "Enter artifact name in kebab-case (e.g. myapp): " ARTIFACT
fi

# ── Validate inputs ──
if [[ -z "$GROUP_ID" ]]; then
    error "group-id is required"; exit 1
fi
if [[ -z "$ARTIFACT" ]]; then
    error "artifact is required"; exit 1
fi
if ! echo "$GROUP_ID" | grep -qE '^[a-z][a-z0-9]*(\.[a-z][a-z0-9]*)+$'; then
    error "Invalid group-id format: '$GROUP_ID' (expected e.g. com.acme)"; exit 1
fi
if ! echo "$ARTIFACT" | grep -qE '^[a-z][a-z0-9]*(-[a-z][a-z0-9]*)*$'; then
    error "Invalid artifact format: '$ARTIFACT' (expected lowercase kebab-case, e.g. myapp or my-app)"; exit 1
fi

# ── Idempotency check ──
if [[ ! -d "spring-boot-template-core" ]]; then
    error "Directory 'spring-boot-template-core' not found. Project may already be initialized."
    exit 1
fi

# ── Derive values ──
ARTIFACT_NO_HYPHENS="${ARTIFACT//-/}"
PACKAGE_NAME="${GROUP_ID}.${ARTIFACT_NO_HYPHENS}"
PACKAGE_PATH="${PACKAGE_NAME//.//}"
ARTIFACT_LOWER="${ARTIFACT//-/_}"

# PascalCase: split on hyphens, capitalize each word
ARTIFACT_PASCAL=""
IFS='-' read -ra PARTS <<< "$ARTIFACT"
for part in "${PARTS[@]}"; do
    ARTIFACT_PASCAL+="$(tr '[:lower:]' '[:upper:]' <<< "${part:0:1}")${part:1}"
done

CORE_MODULE="${ARTIFACT}-core"
API_MODULE="${ARTIFACT}-api"
DB_NAME_PREFIX="${ARTIFACT_LOWER}"
CONTAINER_PREFIX="${ARTIFACT_LOWER}"

# Derive human-readable name from artifact if not provided
if [[ -z "$NAME" ]]; then
    NAME=""
    IFS='-' read -ra PARTS <<< "$ARTIFACT"
    for part in "${PARTS[@]}"; do
        NAME+="$(tr '[:lower:]' '[:upper:]' <<< "${part:0:1}")${part:1} "
    done
    NAME="${NAME% }"
fi

# Kafka group id
KAFKA_GROUP="${ARTIFACT}-group"

# ── Print summary ──
echo ""
info "Project initialization settings:"
echo "  Group ID:        $GROUP_ID"
echo "  Artifact:        $ARTIFACT"
echo "  Name:            $NAME"
echo "  Author:          ${AUTHOR:-tzesh (unchanged)}"
echo "  Package:         $PACKAGE_NAME"
echo "  Package path:    $PACKAGE_PATH"
echo "  Core module:     $CORE_MODULE"
echo "  API module:      $API_MODULE"
echo "  Container prefix: $CONTAINER_PREFIX"
echo "  PascalCase:      $ARTIFACT_PASCAL"
echo ""

# ── Step 1: Text replacements in file contents (before directory renames) ──
info "Replacing text in file contents..."

# Build list of files to process (exclude .git, binaries, target dirs)
FILES=$(find . -type f \
    -not -path './.git/*' \
    -not -path '*/target/*' \
    -not -path './init-project.sh' \
    -not -path './.idea/*' \
    -not -name '*.jar' \
    -not -name '*.class' \
    -not -name '*.png' \
    -not -name '*.jpg' \
    -not -name '*.jpeg' \
    -not -name '*.gif' \
    -not -name '*.ico' \
    -not -name '*.svg' \
    -not -name '*.woff' \
    -not -name '*.woff2' \
    -not -name '*.ttf' \
    -not -name '*.eot' \
    -not -name '.DS_Store' \
    -not -name 'mvnw' \
    -not -name 'mvnw.cmd' \
    -not -name 'maven-wrapper.jar' \
)

# Order matters: longer/more-specific strings first to avoid partial matches

# 1. Java package name (fully qualified)
"${SED_I[@]}" "s|com\.tzesh\.springtemplate|${PACKAGE_NAME}|g" $FILES 2>/dev/null || true
ok "Replaced com.tzesh.springtemplate -> $PACKAGE_NAME"

# 2. Java package path (slashes)
"${SED_I[@]}" "s|com/tzesh/springtemplate|${PACKAGE_PATH}|g" $FILES 2>/dev/null || true
ok "Replaced com/tzesh/springtemplate -> $PACKAGE_PATH"

# 3. Module names (longer first to avoid partial match)
"${SED_I[@]}" "s|spring-boot-template-core|${CORE_MODULE}|g" $FILES 2>/dev/null || true
ok "Replaced spring-boot-template-core -> $CORE_MODULE"

"${SED_I[@]}" "s|spring-boot-template-api|${API_MODULE}|g" $FILES 2>/dev/null || true
ok "Replaced spring-boot-template-api -> $API_MODULE"

# 4. Parent artifact name (after module-specific replacements)
"${SED_I[@]}" "s|spring-boot-template|${ARTIFACT}|g" $FILES 2>/dev/null || true
ok "Replaced spring-boot-template -> $ARTIFACT"

# 5. Kafka group id
"${SED_I[@]}" "s|spring-template-group|${KAFKA_GROUP}|g" $FILES 2>/dev/null || true
ok "Replaced spring-template-group -> $KAFKA_GROUP"

# 6. Docker container names (underscored variants)
"${SED_I[@]}" "s|spring_boot_template|${CONTAINER_PREFIX}|g" $FILES 2>/dev/null || true
ok "Replaced spring_boot_template -> $CONTAINER_PREFIX"

"${SED_I[@]}" "s|spring_template|${CONTAINER_PREFIX}|g" $FILES 2>/dev/null || true
ok "Replaced spring_template -> $CONTAINER_PREFIX"

# 7. Core pom.xml <name> field (uses spring-template-core without "boot")
"${SED_I[@]}" "s|spring-template-core|${CORE_MODULE}|g" $FILES 2>/dev/null || true
ok "Replaced spring-template-core -> $CORE_MODULE"

# 8. Human-readable names
"${SED_I[@]}" "s|Spring Boot Template|${NAME}|g" $FILES 2>/dev/null || true
ok "Replaced Spring Boot Template -> $NAME"

"${SED_I[@]}" "s|Spring Template|${NAME}|g" $FILES 2>/dev/null || true
ok "Replaced Spring Template -> $NAME"

# 9. PascalCase (JWT issuer, GitHub repo name)
"${SED_I[@]}" "s|SpringBootTemplate|${ARTIFACT_PASCAL}|g" $FILES 2>/dev/null || true
ok "Replaced SpringBootTemplate -> $ARTIFACT_PASCAL"

"${SED_I[@]}" "s|SpringTemplate|${ARTIFACT_PASCAL}|g" $FILES 2>/dev/null || true
ok "Replaced SpringTemplate -> $ARTIFACT_PASCAL"

# 10. groupId in pom.xml and log4j2
"${SED_I[@]}" "s|com\.tzesh|${GROUP_ID}|g" $FILES 2>/dev/null || true
ok "Replaced com.tzesh -> $GROUP_ID"

"${SED_I[@]}" "s|com/tzesh|${GROUP_ID//./\/}|g" $FILES 2>/dev/null || true
ok "Replaced com/tzesh -> ${GROUP_ID//./\/}"

# 11. GitHub URL references
"${SED_I[@]}" "s|github\.com/tzesh/SpringBootTemplate|github.com/${ARTIFACT_PASCAL}|g" $FILES 2>/dev/null || true
ok "Replaced GitHub URL references"

# 12. Author tags (only if --author provided)
if [[ -n "$AUTHOR" ]]; then
    "${SED_I[@]}" "s|@author tzesh|@author ${AUTHOR}|g" $FILES 2>/dev/null || true
    ok "Replaced @author tzesh -> @author $AUTHOR"
fi

# ── Step 2: Rename Java package directories ──
info "Renaming Java package directories..."

OLD_PKG_PATH="com/tzesh/springtemplate"
NEW_PKG_PATH="$PACKAGE_PATH"

for SOURCE_ROOT in \
    "spring-boot-template-core/src/main/java" \
    "spring-boot-template-core/src/test/java" \
    "spring-boot-template-api/src/main/java" \
    "spring-boot-template-api/src/test/java"; do

    OLD_DIR="${SOURCE_ROOT}/${OLD_PKG_PATH}"
    NEW_DIR="${SOURCE_ROOT}/${NEW_PKG_PATH}"

    if [[ -d "$OLD_DIR" ]]; then
        mkdir -p "$NEW_DIR"
        # Move all contents (files and dirs) from old to new
        if ls -A "$OLD_DIR" | head -1 > /dev/null 2>&1; then
            cp -R "$OLD_DIR"/* "$NEW_DIR"/ 2>/dev/null || true
            cp -R "$OLD_DIR"/.[!.]* "$NEW_DIR"/ 2>/dev/null || true
            rm -rf "$OLD_DIR"
        fi

        # Clean up empty parent directories
        OLD_PARENT="${SOURCE_ROOT}/com/tzesh"
        rmdir "$OLD_PARENT" 2>/dev/null || true
        OLD_GRANDPARENT="${SOURCE_ROOT}/com"
        # Only remove 'com' if groupId doesn't start with 'com'
        if [[ "${GROUP_ID}" != com* ]]; then
            rmdir "$OLD_GRANDPARENT" 2>/dev/null || true
        fi

        ok "Moved $OLD_DIR -> $NEW_DIR"
    else
        warn "Directory not found: $OLD_DIR (skipped)"
    fi
done

# ── Step 3: Rename module directories ──
info "Renaming module directories..."

if [[ -d "spring-boot-template-core" ]]; then
    mv "spring-boot-template-core" "$CORE_MODULE"
    ok "Renamed spring-boot-template-core -> $CORE_MODULE"
fi

if [[ -d "spring-boot-template-api" ]]; then
    mv "spring-boot-template-api" "$API_MODULE"
    ok "Renamed spring-boot-template-api -> $API_MODULE"
fi

# ── Step 4: Clean up ──
info "Cleaning up..."

# Remove this script (self-delete)
SCRIPT_PATH="$(cd "$(dirname "$0")" && pwd)/$(basename "$0")"
rm -f "$SCRIPT_PATH"
ok "Removed init-project.sh"

# ── Done ──
echo ""
printf "${GREEN}========================================${NC}\n"
printf "${GREEN}  Project initialized successfully!${NC}\n"
printf "${GREEN}========================================${NC}\n"
echo ""
echo "  Project:   $NAME"
echo "  Package:   $PACKAGE_NAME"
echo "  Modules:   $CORE_MODULE, $API_MODULE"
echo ""
echo "Next steps:"
echo "  1. Review the changes: git diff"
echo "  2. Build the project:  mvn clean compile"
echo "  3. Run the tests:      mvn test"
echo ""
