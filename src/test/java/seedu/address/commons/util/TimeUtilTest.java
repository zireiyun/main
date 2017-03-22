//@@author A0135795R
package seedu.address.commons.util;

import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class TimeUtilTest {

	@Rule
    public ExpectedException thrown = ExpectedException.none();
	
	@Test
	public void isInvalidDateTime() {
		//invalid date
		assertNull(TimeUtil.getDateTime("mom's birthday"));
		assertNull(TimeUtil.getDateTime("CS2103 tutorial"));
		assertNull(TimeUtil.getDateTime("national day"));
		assertNull(TimeUtil.getDateTime("christmas"));
		assertNull(TimeUtil.getDateTime("that day"));
	}
	
	@Test
	public void isValidDateTime(){
		assertNotNull(TimeUtil.getDateTime("Wednesday"));
		assertNotNull(TimeUtil.getDateTime("ThUrSDay"));
		assertNotNull(TimeUtil.getDateTime("day after tomorrow"));
		assertNotNull(TimeUtil.getDateTime("day before next thursday"));
		assertNotNull(TimeUtil.getDateTime("4 o'clock"));
	}
	
}