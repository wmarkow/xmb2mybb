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
import vtech.xmb.grabber.db.mybb.entities.MybbPost;
import vtech.xmb.grabber.db.mybb.repositories.MybbPostsRepository;
import vtech.xmb.grabber.db.services.fixers.FileFixer;
import vtech.xmb.grabber.db.services.fixers.FixersChain;
import vtech.xmb.grabber.db.services.fixers.HtmlEntityFixer;
import vtech.xmb.grabber.db.services.fixers.QuotesCharactersFixer;
import vtech.xmb.grabber.db.services.fixers.RquoteFixer;

@Service
public class PostsFixerService {
  private final static Logger LOGGER = Logger.getLogger(PostsFixerService.class);

  @Autowired
  private MybbPostsRepository mybbPostsRepository;

  @Autowired
  private HtmlEntityFixer htmlEntityFixer;
  @Autowired
  private QuotesCharactersFixer quotesCharactersFixer;
  @Autowired
  private RquoteFixer rquoteFixer;
  @Autowired
  private FileFixer fileFixer;

  public void fixPostsContent() {
    final int pageSize = 1000;
    int pageNumber = 0;
    boolean shouldContinue = true;

    Pageable pageRequest = new PageRequest(pageNumber, pageSize);

    FixersChain fixersChain = new FixersChain();
    fixersChain.addFixerToChain(htmlEntityFixer);
    fixersChain.addFixerToChain(quotesCharactersFixer);
    fixersChain.addFixerToChain(rquoteFixer);
    fixersChain.addFixerToChain(fileFixer);

    while (shouldContinue) {
      LOGGER.info(String.format("Processing the batch number %s", pageRequest.getPageNumber()));

      Page<MybbPost> mybbPostsPage = (Page<MybbPost>) mybbPostsRepository.findAll(pageRequest);
      List<MybbPost> mybbPosts = mybbPostsPage.getContent();

      if (mybbPosts.size() == 0) {
        shouldContinue = false;
        break;
      }

      for (MybbPost mybbPost : mybbPosts) {
        try {
          FixResult fixResult = fixersChain.fix(mybbPost.message);

          if (fixResult.isFixRequired()) {
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
