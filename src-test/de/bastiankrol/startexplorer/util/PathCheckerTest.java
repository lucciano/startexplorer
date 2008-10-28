package de.bastiankrol.startexplorer.util;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.io.File;

import org.eclipse.core.commands.ExecutionEvent;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class
 * 
 * @author Bastian Krol
 * @version $Revision:$ $Date:$
 */
public class PathCheckerTest
{

  private PathChecker pathChecker;
  private IMessageDialogHelper mockMessageDialogHelper;
  private ExecutionEvent executionEvent;

  /**
   * JUnit before
   */
  @Before
  public void setUp()
  {
    this.pathChecker = new PathChecker();
    this.mockMessageDialogHelper = createStrictMock(IMessageDialogHelper.class);
    this.pathChecker.setMessageDialogHelper(this.mockMessageDialogHelper);
    this.executionEvent = new ExecutionEvent();
  }

  /**
   * JUnit test method
   * 
   * @throws Exception ...
   */
  @Test
  public void testNullPath() throws Exception
  {
    try
    {
      this.pathChecker.checkPath(null, PathChecker.ResourceType.BOTH,
          this.executionEvent);
      fail("IllegalArgumentException expected");
    }
    catch (IllegalArgumentException e)
    {
      // IllegalArgumentException expected
    }
  }

  /**
   * JUnit test method
   * 
   * @throws Exception ...
   */
  @Test
  public void testNullResourceType() throws Exception
  {
    try
    {
      this.pathChecker
          .checkPath("/path/to/resource", null, this.executionEvent);
      fail("IllegalArgumentException expected");
    }
    catch (IllegalArgumentException e)
    {
      // IllegalArgumentException expected
    }
  }

  /**
   * JUnit test method
   * 
   * @throws Exception ...
   */
  @Test
  public void testNullExecutionEvent() throws Exception
  {
    try
    {
      this.pathChecker.checkPath("/path/to/resource",
          PathChecker.ResourceType.BOTH, null);
      fail("IllegalArgumentException expected");
    }
    catch (IllegalArgumentException e)
    {
      // IllegalArgumentException expected
    }
  }

  /**
   * JUnit test method
   * 
   * @throws Exception ...
   */
  @Test
  public void testResourceDoesNotExistNoParentAvailable() throws Exception
  {
    String pathString = "doesnotexist";
    this.mockMessageDialogHelper
        .displayErrorMessage(
            "Resource does not exist",
            "The path "
                + pathString
                + " does not point to an existing file or folder and there is no parent folder available.",
            this.executionEvent);
    replay(this.mockMessageDialogHelper);
    File checkedFile =
        this.pathChecker.checkPath(pathString, PathChecker.ResourceType.BOTH,
            this.executionEvent);
    verify(this.mockMessageDialogHelper);
    assertNull(checkedFile);
  }

  /**
   * JUnit test method
   * 
   * @throws Exception ...
   */
  @Test
  public void testResourceDoesNotExistNorDoesParent() throws Exception
  {
    String pathString = "test-resources/does/not/exist";
    this.mockMessageDialogHelper
        .displayErrorMessage(
            "Resource does not exist",
            "The path "
                + pathString
                + " does not point to an existing file or folder nor does it's parent.",
            this.executionEvent);
    replay(this.mockMessageDialogHelper);
    File checkedFile =
        this.pathChecker.checkPath(pathString, PathChecker.ResourceType.BOTH,
            this.executionEvent);
    verify(this.mockMessageDialogHelper);
    assertNull(checkedFile);
  }

  /**
   * JUnit test method
   * 
   * @throws Exception ...
   */
  @Test
  public void testDirectoryInsteadOfFile() throws Exception
  {
    String pathString = "test-resources/path/to/resource";
    this.mockMessageDialogHelper.displayErrorMessage("Not a file", "The path "
        + pathString + " points to a directory, not to a file.",
        this.executionEvent);
    replay(this.mockMessageDialogHelper);
    File checkedFile =
        this.pathChecker.checkPath(pathString, PathChecker.ResourceType.FILE,
            this.executionEvent);
    verify(this.mockMessageDialogHelper);
    assertNull(checkedFile);
  }

  /**
   * JUnit test method
   * 
   * @throws Exception ...
   */
  @Test
  public void testFileInsteadOfDirectoryReturnParentDirectory()
      throws Exception
  {
    String pathString = "test-resources/path/to/resource/file.txt";
    replay(this.mockMessageDialogHelper);
    File checkedFile =
        this.pathChecker.checkPath(pathString,
            PathChecker.ResourceType.DIRECTORY, this.executionEvent);
    verify(this.mockMessageDialogHelper);
    assertEquals(new File(pathString).getParentFile(), checkedFile);
  }

  /**
   * JUnit test method
   * 
   * @throws Exception ...
   */
  @Test
  public void testFile() throws Exception
  {
    String pathString = "test-resources/path/to/resource/file.txt";
    replay(this.mockMessageDialogHelper);
    File checkedFile =
        this.pathChecker.checkPath(pathString, PathChecker.ResourceType.FILE,
            this.executionEvent);
    verify(this.mockMessageDialogHelper);
    assertEquals(new File(pathString), checkedFile);
  }

  /**
   * JUnit test method
   * 
   * @throws Exception ...
   */
  @Test
  public void testDirectoryNoTrailingSlash() throws Exception
  {
    String pathString = "test-resources/path/to/resource";
    replay(this.mockMessageDialogHelper);
    File checkedFile =
        this.pathChecker.checkPath(pathString,
            PathChecker.ResourceType.DIRECTORY, this.executionEvent);
    verify(this.mockMessageDialogHelper);
    assertEquals(new File(pathString), checkedFile);

  }

  /**
   * JUnit test method
   * 
   * @throws Exception ...
   */
  @Test
  public void testDirectoryTrailingSlash() throws Exception
  {
    String pathString = "test-resources/path/to/resource/";
    replay(this.mockMessageDialogHelper);
    File checkedFile =
        this.pathChecker.checkPath(pathString,
            PathChecker.ResourceType.DIRECTORY, this.executionEvent);
    verify(this.mockMessageDialogHelper);
    assertEquals(new File(pathString), checkedFile);
  }
}
