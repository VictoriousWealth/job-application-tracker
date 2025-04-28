#!/bin/bash

# Set paths
MAIN_DIR="src/main/java/com/nick/job_application_tracker"
TEST_DIR="src/test/java/com/nick/job_application_tracker"

# Create test directory if it doesn't exist
mkdir -p "$TEST_DIR"

# Find all .java files in the 'mapper' subdirectories in 'main'
find "$MAIN_DIR" -type f -path "*/mapper/*.java" | while read mapper_class; do
  # Extract the relative path from the base directory (i.e., relative to src/main/java/)
  relative_path="${mapper_class#$MAIN_DIR/}"
  
  # Get the class name (basename without extension)
  class_name=$(basename "$mapper_class" .java)

  # Build the corresponding test class file path in the 'test' directory, preserving structure
  test_class_dir="$TEST_DIR/$(dirname "$relative_path" | sed 's|/main/java|/test/java|')"
  
  # Create the test directory if it doesn't exist
  mkdir -p "$test_class_dir"
  
  # Define the test class file name
  test_class_name="${class_name}Test.java"
  
  # Check if the test class already exists, if not, create it
  if [ ! -f "$test_class_dir/$test_class_name" ]; then
    # Create the test class file in the test directory
    cat > "$test_class_dir/$test_class_name" <<EOF
package com.nick.job_application_tracker.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
class ${class_name}Test {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testMapper() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/${class_name}"))
               .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
EOF
    echo "Created test file for $class_name at $test_class_dir/$test_class_name"
  else
    echo "Test file for $class_name already exists, skipping creation."
  fi
done
