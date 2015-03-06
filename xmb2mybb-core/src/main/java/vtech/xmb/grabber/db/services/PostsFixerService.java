package vtech.xmb.grabber.db.services;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import vtech.xmb.grabber.db.mybb.entities.MybbPost;
import vtech.xmb.grabber.db.mybb.repositories.MybbPostsRepository;
import vtech.xmb.grabber.db.services.fixers.FixResult;
import vtech.xmb.grabber.db.services.fixers.QuotesCharactersFixer;

@Service
public class PostsFixerService {
  private final static Logger LOGGER = Logger.getLogger(PostsFixerService.class);

  @Autowired
  private MybbPostsRepository mybbPostsRepository;

  @Autowired
  private QuotesCharactersFixer quotesCharactersFixer;

  public void fixPostsContent() {
    final int pageSize = 1000;
    int pageNumber = 0;
    boolean shouldContinue = true;

    Pageable pageRequest = new PageRequest(pageNumber, pageSize);

    while (shouldContinue) {
      LOGGER.info(String.format("Processing the batch number %s", pageRequest.getPageNumber()));

      Page<MybbPost> mybbPostsPage = (Page<MybbPost>) mybbPostsRepository.findAll(pageRequest);
      List<MybbPost> mybbPosts = mybbPostsPage.getContent();

      if (mybbPosts.size() == 0) {
        shouldContinue = false;
        break;
      }

      for (MybbPost mybbPost : mybbPosts) {
        FixResult quoteCharactersFixResult = quotesCharactersFixer.fix(mybbPost.message);
        if (quoteCharactersFixResult.isFixRequired()) {
          mybbPost.message = quoteCharactersFixResult.getFixedText();
          mybbPostsRepository.save(mybbPost);
        }
      }
      pageRequest = pageRequest.next();
    }
  }
}