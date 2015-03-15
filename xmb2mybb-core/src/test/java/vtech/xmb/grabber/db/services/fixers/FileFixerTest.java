package vtech.xmb.grabber.db.services.fixers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.runners.MockitoJUnitRunner;

import vtech.xmb.grabber.db.domain.fixers.FixResult;
import vtech.xmb.grabber.db.mybb.entities.MybbAttachment;
import vtech.xmb.grabber.db.mybb.repositories.MybbAttachmentsRepository;

@RunWith(MockitoJUnitRunner.class)
public class FileFixerTest {

  @Mock
  private MybbAttachmentsRepository mybbAttachmentsRepository;

  private FileFixer fixer;

  @Before
  public void init() {
    fixer = new FileFixer();
    Whitebox.setInternalState(fixer, "mybbAttachmentsRepository", mybbAttachmentsRepository);
  }

  @Test
  public void test() throws ParseException {
    final String test = "a teraz mam [file]1500[/file] [file]2900[/file] heheh";
    final String fixed = "a teraz mam [attachment=500] [attachment=900] heheh";

    MybbAttachment attachment1 = new MybbAttachment();
    attachment1.aid = 500L;
    MybbAttachment attachment2 = new MybbAttachment();
    attachment2.aid = 900L;

    Mockito.when(mybbAttachmentsRepository.findByXmbAid(Mockito.eq(1500L))).thenReturn(attachment1);
    Mockito.when(mybbAttachmentsRepository.findByXmbAid(Mockito.eq(2900L))).thenReturn(attachment2);

    FixResult fixResult = fixer.fix(test, 0, 0);
    
    assertTrue(fixResult.isFixRequired());
    assertEquals(fixed, fixResult.getFixedText());
  }
}
