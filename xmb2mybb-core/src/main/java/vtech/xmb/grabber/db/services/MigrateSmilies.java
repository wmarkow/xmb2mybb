package vtech.xmb.grabber.db.services;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

import vtech.xmb.grabber.db.mybb.entities.MybbSmiley;
import vtech.xmb.grabber.db.mybb.repositories.MybbSmiliesRepository;
import vtech.xmb.grabber.db.xmb.entities.XmbSmiley;
import vtech.xmb.grabber.db.xmb.repositories.XmbSmiliesRepository;

@Service
public class MigrateSmilies {
  private final static Logger LOGGER = Logger.getLogger(MigrateSmilies.class);
  private final static Logger ROOT_LOGGER = Logger.getRootLogger();

  @Autowired
  private XmbSmiliesRepository xmbSmiliesRepository;
  @Autowired
  private MybbSmiliesRepository mybbSmiliesRepository;

  @Value("${xmb.smilies.path}")
  private String xmbSmiliesPath;
  @Value("${mybb.smilies.path}")
  private String mybbSmiliesPath;
  
  public void migrateSmilies() {
    LOGGER.info("Smilies migration started.");
    ROOT_LOGGER.info("Smilies migration started.");

    List<XmbSmiley> xmbSmilies = (List<XmbSmiley>) xmbSmiliesRepository.findAll();

    LOGGER.info(String.format("Found %s smilies to migrate from XMB.", xmbSmilies.size()));
    ROOT_LOGGER.info(String.format("Found %s smilies to migrate from XMB.", xmbSmilies.size()));

    copySmilies();

    int counter = 0;
    int index = 1;
    for (XmbSmiley xmbSmiley : xmbSmilies) {
      if (xmbSmiley.type == null) {
        continue;
      }

      if (!xmbSmiley.type.equals("smiley")) {
        LOGGER.warn(String.format("XMB smiley with URL=%s not migrated as it's type is %s", xmbSmiley.url, xmbSmiley.type));
        continue;
      }

      MybbSmiley mybbSmiley = new MybbSmiley();

      mybbSmiley.disporder = index;
      mybbSmiley.find = xmbSmiley.code;
      mybbSmiley.image = "images/smilies/" + xmbSmiley.url;
      mybbSmiley.showclickable = 1;
      mybbSmiley.name = xmbSmiley.code;

      mybbSmiliesRepository.save(mybbSmiley);
      
      index++;
      counter++;
    }

    LOGGER.info(String.format("Migrated %s smileys.", counter));
    ROOT_LOGGER.info(String.format("Migrated %s smileys.", counter));

    LOGGER.info("Smilies migration finished.");
    ROOT_LOGGER.info("Smilies migration finished.");
  }
  
  private void copySmilies() {
    File xmbSmiliesDirFile = new File(xmbSmiliesPath);
    File mybbSmiliesDirFile = new File(mybbSmiliesPath);

    mybbSmiliesDirFile.mkdirs();

    try {
      FileSystemUtils.copyRecursively(xmbSmiliesDirFile, mybbSmiliesDirFile);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
