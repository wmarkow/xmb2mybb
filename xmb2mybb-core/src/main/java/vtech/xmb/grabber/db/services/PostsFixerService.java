package vtech.xmb.grabber.db.services;

import java.text.ParseException;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import vtech.xmb.grabber.db.domain.fixers.FixResult;
import vtech.xmb.grabber.db.domain.fixers.LinkFixResult;
import vtech.xmb.grabber.db.mybb.entities.MybbPost;
import vtech.xmb.grabber.db.mybb.repositories.MybbPostsRepository;
import vtech.xmb.grabber.db.services.fixers.FileFixer;
import vtech.xmb.grabber.db.services.fixers.FixersChain;
import vtech.xmb.grabber.db.services.fixers.RquoteFixer;
import vtech.xmb.grabber.db.services.fixers.links.ForumLinksFixer;

@Service
public class PostsFixerService {
  private final static Logger LOGGER = Logger.getLogger(PostsFixerService.class);
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
    final int pageSize = 1000;
    int pageNumber = 0;
    boolean shouldContinue = true;

    Pageable pageRequest = new PageRequest(pageNumber, pageSize);

    FixersChain fixersChain = new FixersChain();
    fixersChain.addFixerToChain(rquoteFixer);
    fixersChain.addFixerToChain(fileFixer);

    while (shouldContinue) {
      ROOT_LOGGER.info(String.format("Fixing posts links: processing the batch number %s", pageRequest.getPageNumber()));

      Page<MybbPost> mybbPostsPage = (Page<MybbPost>) mybbPostsRepository.findAll(pageRequest);
      List<MybbPost> mybbPosts = mybbPostsPage.getContent();

      if (mybbPosts.size() == 0) {
        shouldContinue = false;
        break;
      }

      for (MybbPost mybbPost : mybbPosts) {
        try {
          LinkFixResult linkFixResult = linksFixer.fix(mybbPost.message);
          if (linkFixResult.isHasInvalidLinks()) {
            // TODO: put additional log here
            LOGGER.warn(String.format("Post with XMB pid=%s and MyBB pid=%s has invalid links.", mybbPost.xmbpid, mybbPost.pid));
          }

          FixResult fixResult = fixersChain.fix(linkFixResult.getFixedText());

          if (linkFixResult.isFixRequired() || fixResult.isFixRequired()) {
            mybbPost.message = fixResult.getFixedText();
            mybbPostsRepository.save(mybbPost);
          }

        } catch (ParseException e) {
          LOGGER.warn(String.format("Parse exception while fixing the post with pid=%s. Post will not be fixed.", mybbPost.pid), e);
        }
      }
      pageRequest = pageRequest.next();
    }
  }
}
