package vtech.xmb.grabber.db.services.fixers;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import vtech.xmb.grabber.db.domain.fixers.FixResult;
import vtech.xmb.grabber.db.mybb.entities.MybbAttachment;
import vtech.xmb.grabber.db.mybb.repositories.MybbAttachmentsRepository;

@Component
public class FileFixer extends StringFixer {
  private final static Logger LOGGER = Logger.getLogger(FileFixer.class);

  @Autowired
  private MybbAttachmentsRepository mybbAttachmentsRepository;

  @Override
  public FixResult fix(final String textToFix) throws ParseException {
    String result = textToFix;
    boolean fixed = false;

    Pattern pattern = Pattern.compile("\\[file.*?\\].*?\\[/file\\]");
    Matcher matcher = pattern.matcher(result);

    Map<String, String> files = new HashMap<String, String>();

    while (matcher.find()) {
      final String fileAsString = matcher.group();
      final String attachmentAsString = replaceFileWithAttachment(fileAsString);

      if (attachmentAsString == null) {
        LOGGER.warn(String.format("Attachment %s will not be fixed as can not map it to MyBB Attachment", fileAsString));

        continue;
      }

      files.put(fileAsString, attachmentAsString);
    }

    for (String file : files.keySet()) {
      result = result.replace(file, files.get(file));
      fixed = true;
    }

    FixResult fixResult = new FixResult();
    fixResult.setFixedText(result);
    fixResult.setFixRequired(fixed);
    return fixResult;
  }

  private String replaceFileWithAttachment(String fileAsString) {
    final long xmbAttachmentId = Long.valueOf(fileAsString.replace("[file]", "").replace("[/file]", "").trim());

    MybbAttachment mybbAttachment = mybbAttachmentsRepository.findByXmbAid(xmbAttachmentId);
    if (mybbAttachment == null) {
      return null;
    }

    return String.format("[attachment=%s]", mybbAttachment.aid);
  }
}
