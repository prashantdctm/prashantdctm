package com.mwpa.agent;

import java.awt.AWTException;

import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.awt.datatransfer.StringSelection;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.util.logging.Logger;
import com.mwpa.utils.GeneralUtil;
import com.mwpa.utils.PropertyReader;

/** 
 * IDAgent - for ID in US, contains scratch rewards
 * @author prashant_dctm_gmail_com pushed to git
 *
 */
public class IDAgent {
	static Robot robot = null;
	private static final Logger log = Logger.getLogger(IDAgent.class.getName());

	private static int maxSearchCount = 0; // max search count

	// search count for today till now, includes searches in all last sessions
	private static int idSearchCount = 0;
	// search count for this session,

	private static int searchCountThisSession = 0;
	private static String propsFilePath = null; // properties file path
	private static final String propFileName = "mwpa.properties";
	private static Properties props = null;


	public static void main(String args[]) throws AWTException, InterruptedException, IOException {
		
		robot = new Robot();
		robot.setAutoDelay(250);
		ArrayList<Object> p = PropertyReader.loadProperties(propFileName);
		props = (Properties) p.get(0);
		propsFilePath = (String) p.get(1);

		log.info("Starting ");

		try {
			if(checkInitCondition()) {
				closeRestorePopup();
				search();
				
				//robot.delay(2000);
				//scratch();
				email();
				//this value is read in shell script to check if account status is to be published
				// return 0 for success, 1 failure
				System.out.println(0); 
			} else {
				System.out.println(1);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static boolean checkInitCondition() {
		boolean canRun = false;

		try {
			props = (Properties) PropertyReader.loadProperties(propFileName).get(0);

			// If first invocation for today reset variables
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date lastRunDate = sdf.parse(props.getProperty("last.run.date"));
			Date today = sdf.parse(GeneralUtil.getDate());

			if (lastRunDate.compareTo(today) < 0) {
				props.setProperty("last.run.date", GeneralUtil.getDate());
				props.setProperty("id.cnt.cur", "0");
				props.setProperty("id.em.chk", "false");
				PropertyReader.saveProperties(props, propsFilePath);
			}

			// read properties
			maxSearchCount = Integer.parseInt(props.getProperty("srch.cnt.max"));
			idSearchCount = Integer.parseInt(props.getProperty("id.cnt.cur"));

			// Search only if current search less than max search count
			if (idSearchCount < maxSearchCount) {
				canRun = true;
				searchCountThisSession = maxSearchCount - idSearchCount;
			} else {
				canRun = false;
			}
		} catch (Exception e) {
			log.info("Error in starting " + e.getMessage());
			e.printStackTrace();
		}
		log.info("Can run? "+canRun);
		return canRun;
	}

	//Close chrome restore pop up
	private static void closeRestorePopup() {
		log.info("Closing chrome restore popup");
		robot.mouseMove(1220, 300);
		robot.mousePress(InputEvent.BUTTON1_MASK);
		robot.mouseRelease(InputEvent.BUTTON1_MASK);
	}
	
	private static void search() throws Exception {
		String classPath = IDAgent.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		classPath = classPath.substring(0, classPath.lastIndexOf("/bin"));

		List<String> searchTerms = Files.readAllLines(
				Paths.get(classPath+"/config/searchterms.txt"));
		Random rand = new Random();

		for (int i = 0; i < searchCountThisSession; i++) {
			int r = rand.nextInt(searchTerms.size());
			log.info("searching " + i + "  " + searchTerms.get(r));

			StringSelection stringSelection = new StringSelection(searchTerms.get(r));
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents(stringSelection, null);

			//on first search, search input is little above
			if(i==0) {
				robot.mouseMove(298, 298);
			} else {
				robot.mouseMove(316, 304);
				
			}


			robot.mousePress(InputEvent.BUTTON1_MASK);
			robot.mouseRelease(InputEvent.BUTTON1_MASK);

			robot.keyPress(KeyEvent.VK_CONTROL);
			robot.keyPress(KeyEvent.VK_A);
			robot.keyRelease(KeyEvent.VK_A);
			robot.keyPress(KeyEvent.VK_V);
			robot.keyRelease(KeyEvent.VK_V);
			robot.keyRelease(KeyEvent.VK_CONTROL);

			robot.delay(250);

			System.out.println("enter");
			robot.keyPress(KeyEvent.VK_ENTER);
			robot.keyRelease(KeyEvent.VK_ENTER);

			robot.delay(5000);

			idSearchCount++;
		}
		// save current search count
		saveCurrentCount("id.cnt.cur", idSearchCount);
	}

	private static void scratch() throws Exception {
		// scratch bar
		robot.mouseMove(507, 247);
		robot.mousePress(InputEvent.BUTTON1_MASK);
		robot.mouseRelease(InputEvent.BUTTON1_MASK);
		robot.delay(5000);

		// scratch now icon
		robot.mouseMove(670, 324);
		robot.mousePress(InputEvent.BUTTON1_MASK);
		robot.mouseRelease(InputEvent.BUTTON1_MASK);
		robot.delay(2000);

		//continue other clicks
		
		// scratch claim
		robot.mouseMove(826, 377);
		robot.mousePress(InputEvent.BUTTON1_MASK);
		robot.mouseRelease(InputEvent.BUTTON1_MASK);
		robot.delay(2000);

		// first tab
		robot.mouseMove(126, 47);
		robot.mousePress(InputEvent.BUTTON1_MASK);
		robot.mouseRelease(InputEvent.BUTTON1_MASK);
		robot.delay(2000);
	}

	private static void email() throws Exception {
		for (int i = 0; i < 2; i++) {
			// email page
			robot.mouseMove(973, 173);
			robot.mousePress(InputEvent.BUTTON1_MASK);
			robot.mouseRelease(InputEvent.BUTTON1_MASK);
			robot.delay(5000);

			// email click
			robot.mouseMove(233, 333);
			robot.mousePress(InputEvent.BUTTON1_MASK);
			robot.mouseRelease(InputEvent.BUTTON1_MASK);
			robot.delay(2000);

			// body click
			robot.mouseMove(780, 500);
			robot.mousePress(InputEvent.BUTTON1_MASK);
			robot.mouseRelease(InputEvent.BUTTON1_MASK);
			robot.delay(2000);

			// first tab
			robot.mouseMove(126, 47);
			robot.mousePress(InputEvent.BUTTON1_MASK);
			robot.mouseRelease(InputEvent.BUTTON1_MASK);
			robot.delay(2000);
		}
	}

	private static void saveProperties(Properties props) {
		try {
			PropertyReader.saveProperties(props, propsFilePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void saveCurrentCount(String key, int currentSearchCount) {
		try {
			props.setProperty(key, currentSearchCount + "");
			PropertyReader.saveProperties(props, propsFilePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

