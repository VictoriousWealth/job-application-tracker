#!/bin/bash

# Define source and target directories
MODEL_DIR="model"
DTO_DIR="dto"
CREATE_DIR="$DTO_DIR/create"
RESPONSE_DIR="$DTO_DIR/response"
UPDATE_DIR="$DTO_DIR/update"
DETAIL_DIR="$DTO_DIR/detail"

# Create dto subdirectories if they don't exist
mkdir -p "$CREATE_DIR" "$RESPONSE_DIR" "$UPDATE_DIR" "$DETAIL_DIR"

# Process each file in the model directory
for filepath in "$MODEL_DIR"/*; do
    filename=$(basename -- "$filepath")
    extension="${filename##*.}"
    modelname="${filename%.*}"

    # Create corresponding DTO files with prefix and proper extension
    touch "$CREATE_DIR/${modelname}CreateDTO.$extension"
    touch "$RESPONSE_DIR/${modelname}ResponseDTO.$extension"
    touch "$UPDATE_DIR/${modelname}UpdateDTO.$extension"
    touch "$DETAIL_DIR/${modelname}DetailDTO.$extension"
done

