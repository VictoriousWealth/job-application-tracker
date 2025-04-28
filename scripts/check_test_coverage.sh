#!/bin/bash

# -------------------------------------------
# ✅ Job Application Tracker - Coverage Checker (Mac-compatible)
# -------------------------------------------

# Flags
SHOW_MISSING=false
SHOW_HELP=false
WITH_GRADLE=false

# Directories
MAIN_DIR="src/main/java/com/nick/job_application_tracker"
TEST_DIR="src/test/java/com/nick/job_application_tracker"

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Helpers
trap ctrl_c INT

ctrl_c() {
  echo -e "\nScript interrupted by control-event. Exiting gracefully..."
  exit 1
}

progress_bar() {
  local percentage=$1
  local filled=$((percentage / 10))
  local empty=$((10 - filled))

  local color="$RED"
  if (( percentage >= 90 )); then
    color="$GREEN"
  elif (( percentage >= 65 )); then
    color="$YELLOW"
  fi

  printf "["
  for ((i=0; i<$filled; i++)); do
    printf "${color}█${NC}"
  done
  for ((i=0; i<$empty; i++)); do
    printf "░"
  done
  printf "]"
}

colorize_percentage() {
  local percentage=$1

  local GREEN='\033[0;32m'
  local YELLOW='\033[1;33m'
  local RED='\033[0;31m'
  local NC='\033[0m'

  local color="$RED"
  if (( $(echo "$percentage >= 90" | bc -l) )); then
    color="$GREEN"
  elif (( $(echo "$percentage >= 65" | bc -l) )); then
    color="$YELLOW"
  fi

  printf "%b" "${color}$(printf "%.2f" "$percentage")%${NC}"
}




show_help() {
  echo "-----------------------------------------------------------"
  echo " Job Application Tracker Coverage Checker - Help"
  echo "-----------------------------------------------------------"
  echo "Usage:"
  echo "  ./scripts/check_test_coverage.sh [options]"
  echo
  echo "Options:"
  echo "  -h, --help          Show this help message and exit."
  echo "  --show-missing, --s-m  Show classes that do not have a corresponding test class."
  echo
  echo "Description:"
  echo "  This script analyzes the Java source code under:"
  echo "    src/main/java/com/nick/job_application_tracker"
  echo "  and the test code under:"
  echo "    src/test/java/com/nick/job_application_tracker"
  echo
  echo "  It calculates:"
  echo "    - Overall class-to-test coverage percentage."
  echo "    - Per-folder breakdown with progress bars."
  echo
  echo "Examples:"
  echo "  ./scripts/check_test_coverage.sh"
  echo "  ./scripts/check_test_coverage.sh --show-missing"
  echo "  ./scripts/check_test_coverage.sh --s-m"
  echo
  echo "Done."
}

# Parse arguments
for arg in "$@"; do
  case $arg in
    -h|--help)
      SHOW_HELP=true
      ;;
    --show-missing|--s-m)
      SHOW_MISSING=true
      ;;
    --with-gradle)
      WITH_GRADLE=true
      ;;
    *)
      echo "Unknown option: $arg"
      exit 1
      ;;
  esac
done


if $SHOW_HELP; then
  show_help
  exit 0
fi

# Gather classes
main_classes=$(find "$MAIN_DIR" -type f -name '*.java' ! -name '*Test.java')
test_classes=$(find "$TEST_DIR" -type f -name '*Test.java')

# Count
main_count=$(echo "$main_classes" | wc -l | tr -d ' ')
test_count=$(echo "$test_classes" | wc -l | tr -d ' ')

# Avoid division by zero
if [ "$main_count" -eq 0 ]; then
  overall_coverage="0.00"
else
  overall_coverage=$(awk "BEGIN {printf \"%.2f\", ($test_count/$main_count)*100}")
fi

echo
echo
echo "---------------------------------------------"
echo "📊 Checking overall class-to-test coverage..."
echo "---------------------------------------------"
printf "Production classes:\t%5d\n" "$main_count"
printf "Test classes:\t\t%5d\n" "$test_count"
printf "Overall class test coverage: %b\n" "$(colorize_percentage "$overall_coverage")"
echo
echo
echo
echo "---------------------------------------------"
echo "📂 Breakdown by top-level project folders:"
echo "---------------------------------------------"

# Prepare folder lists
folders=$(find "$MAIN_DIR" -mindepth 1 -maxdepth 1 -type d -exec basename {} \;)

for folder in $folders; do

  main_in_folder=$(find "$MAIN_DIR/$folder" -type f -name '*.java' ! -name '*Test.java' | wc -l | tr -d ' ')
  test_in_folder=$(find "$TEST_DIR/$folder" -type f -name '*Test.java' 2>/dev/null | wc -l | tr -d ' ')

  if [ "$main_in_folder" -eq 0 ]; then
    coverage="0.00"
  else
    coverage=$(awk "BEGIN {printf \"%.2f\", ($test_in_folder/$main_in_folder)*100}")
  fi

  colored_coverage=$(colorize_percentage "$coverage")

  printf "📁 %-20s: %4d main classes, %4d test classes --> %s coverage " "$folder" "$main_in_folder" "$test_in_folder" "$colored_coverage"
  progress_bar "${coverage%.*}"
  echo

  # Optionally run Gradle tests
  if $WITH_GRADLE; then

    package_path="com.nick.job_application_tracker.$folder"

    # Check if any test classes exist first
    test_count_in_folder=$(find "$TEST_DIR/$folder" -type f -name '*Test.java' 2>/dev/null | wc -l | tr -d ' ')
    
    if [ "$test_count_in_folder" -eq 0 ]; then
      echo -e "${YELLOW}No tests found. Skipping Gradle run for this folder.${NC}"
      echo
    else
      ./gradlew test --tests "${package_path}.*" > /dev/null 2>&1

      if [ $? -eq 0 ]; then
        echo -e "All tests ${GREEN}PASSED${NC} for this folder.\n"
      else
        echo -e "Some tests ${RED}FAILED${NC} for this folder.\n"
      fi
    fi
  fi

  # Show missing classes
  if $SHOW_MISSING && [ "$test_in_folder" -lt "$main_in_folder" ]; then
    echo "  ➔ Classes without matching test:"
    missing=$(find "$MAIN_DIR/$folder" -type f -name '*.java' ! -name '*Test.java' | while read -r file; do
      class_name=$(basename "$file" .java)
      if ! find "$TEST_DIR/$folder" -type f -name "${class_name}Test.java" >/dev/null 2>&1; then
        echo "     - $class_name"
      fi
    done)
    echo "$missing"
  fi

  echo "---------------------------------------------"

done

