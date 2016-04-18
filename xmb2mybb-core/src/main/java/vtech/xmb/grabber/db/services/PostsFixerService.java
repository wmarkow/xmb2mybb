package vtech.xmb.grabber.db.services;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import vtech.xmb.grabber.db.domain.ProgressCalculator;
import vtech.xmb.grabber.db.domain.fixers.FixResult;
import vtech.xmb.grabber.db.domain.fixers.LinkFixResult;
import vtech.xmb.grabber.db.mybb.entities.MybbPost;
import vtech.xmb.grabber.db.mybb.repositories.MybbPostsRepository;
import vtech.xmb.grabber.db.services.fixers.FileFixer;
import vtech.xmb.grabber.db.services.fixers.RquoteFixer;
import vtech.xmb.grabber.db.services.fixers.links.ForumLinksFixer;

@Service
public class PostsFixerService {
  private final static Logger ROOT_LOGGER = Logger.getRootLogger();

  @Autowired
  private MybbPostsRepository mybbPostsRepository;

  @Autowired
  private RquoteFixer rquoteFixer;
  @Autowired
  private FileFixer fileFixer;
  @Autowired
  private ForumLinksFixer linksFixer;

  public void fixPostsContent() {
    ROOT_LOGGER.info("Posts links fixing started.");

    final long xmbCount = mybbPostsRepository.count();
    ProgressCalculator progressCalc = new ProgressCalculator(xmbCount);
    ROOT_LOGGER.info(String.format("Found %s posts to check.", xmbCount));

    final int pageSize = 1000;
    int pageNumber = 0;
    boolean shouldContinue = true;

    Pageable pageRequest = new PageRequest(pageNumber, pageSize);

    while (shouldContinue) {
      ROOT_LOGGER.info(String.format("Fixing posts links: processing the batch number %s", pageRequest.getPageNumber()));

      Page<MybbPost> mybbPostsPage = (Page<MybbPost>) mybbPostsRepository.findAll(pageRequest);
      List<MybbPost> mybbPosts = mybbPostsPage.getContent();

      if (mybbPosts.size() == 0) {
        shouldContinue = false;
        break;
      }

      for (MybbPost mybbPost : mybbPosts) {
        LinkFixResult linkFixResult = linksFixer.fix(mybbPost.message, mybbPost);
        FixResult rquoteFixResult = rquoteFixer.fix(linkFixResult.getFixedText(), mybbPost.pid, mybbPost.xmbpid);
        FixResult fileFixResult = fileFixer.fix(rquoteFixResult.getFixedText(), mybbPost.pid, mybbPost.xmbpid);

        if (linkFixResult.isFixRequired() || rquoteFixResult.isFixRequired() || fileFixResult.isFixRequired()) {
          mybbPost.message = fileFixResult.getFixedText();
          mybbPostsRepository.save(mybbPost);
        }
      }
      pageRequest = pageRequest.next();

      progressCalc.hit(mybbPosts.size());
      progressCalc.logProgress(1, ROOT_LOGGER);
    }

    ROOT_LOGGER.info("Posts links fixing finished.");
  }
}
