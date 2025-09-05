for filepath in dto/create/*CreateDTO.java; do
  filename=$(basename "$filepath")
  
  # Skip specific files
  if [[ "$filename" == "ApplicationTimelineCreateDTO.java" || "$filename" == "JobApplicationCreateDTO.java" ]]; then
    continue
  fi

  newfilename=${filename/CreateDTO/UpdateDTO}
  newpath="dto/update/$newfilename"
  
  cp "$filepath" "$newpath"
  
  sed -i '' \
    -e 's/package com\.nick\.job_application_tracker\.dto\.create;/package com.nick.job_application_tracker.dto.update;/' \
    -e "s/public class ${filename%.java}/public class ${newfilename%.java}/" \
    "$newpath"
done

