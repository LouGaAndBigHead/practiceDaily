package redis001;

import org.junit.Before;
import org.junit.Test;

public class CalculatorTest {
	private static Calculator calculator = new Calculator();
	
	@Before
	public void setUp() throws Exception {
		calculator.clear();
	}

	@Test
	public void testAdd() {
		calculator.add(30);
	}

	@Test
	public void testSubstract() {
		calculator.substract(10);
	}

}
