package org.andork.segment;

import java.math.BigDecimal;
import java.util.concurrent.Callable;

import org.junit.Assert;
import org.junit.Test;

@FunctionalInterface
interface ExceptionRunnable {
	public void run() throws Exception;
}

public class SegmentParserTests {
	private static void assertThrowsParseError(Callable<?> c, int index, String message) {
		try {
			c.call();
			Assert.fail("expected function to throw a SegmentParseException");
		} catch (Exception e) {
			SegmentParseException error = (SegmentParseException) e;
			Assert.assertEquals(index, error.getSegment().startCol);
			Assert.assertEquals(message, error.getMessage());
		}
	}

	private static void assertThrowsParseError(ExceptionRunnable r, int index, String message) {
		try {
			r.run();
			Assert.fail("expected function to throw a SegmentParseException");
		} catch (Exception e) {
			SegmentParseException error = (SegmentParseException) e;
			Assert.assertEquals(index, error.getSegment().startCol);
			Assert.assertEquals(message, error.getMessage());
		}
	}

	private static SegmentParser parser(String text) {
		return new SegmentParser(new Segment(text, "", 0, 0));
	}

	@Test
	public void testBigDecimal() throws SegmentParseException {
		assertThrowsParseError(() -> parser(" 3.5").bigDecimal("test"), 0, "test");
		assertThrowsParseError(() -> parser("a3.5").bigDecimal("test"), 0, "test");
		assertThrowsParseError(() -> parser("e3.5").bigDecimal("test"), 0, "test");
		SegmentParser p = parser("3.5kj");
		Assert.assertEquals(new BigDecimal("3.5"), p.bigDecimal("test"));
		Assert.assertEquals(3, p.getIndex());
		p = parser("-3.5e4");
		Assert.assertEquals(new BigDecimal("-3.5e4"), p.bigDecimal("test"));
		Assert.assertEquals(p.getSegment().length(), p.getIndex());
		p = parser("-.5e-2");
		Assert.assertEquals(new BigDecimal("-.5e-2"), p.bigDecimal("test"));
		Assert.assertEquals(p.getSegment().length(), p.getIndex());
		p = parser("-5");
		Assert.assertEquals(new BigDecimal("-5"), p.bigDecimal("test"));
		Assert.assertEquals(p.getSegment().length(), p.getIndex());
	}

	@Test
	public void testCharacter() throws SegmentParseException {
		SegmentParser p = parser("abcd");
		p.character('a', "test");
		assertThrowsParseError(() -> p.character('a', "test"), 1, "test");
		p.character('b', "test");
		assertThrowsParseError(() -> p.character('b', "test"), 2, "test");
	}

	@Test
	public void testEmptySegment() {
		SegmentParser p = new SegmentParser(new Segment("", "", 0, 0));
		assertThrowsParseError(() -> p.whitespace("test"), 0, "test");
		assertThrowsParseError(() -> p.nonwhitespace("test"), 0, "test");
		assertThrowsParseError(() -> p.bigDecimal("test"), 0, "test");
		assertThrowsParseError(() -> p.character('c', "test"), 0, "test");
	}

	@Test
	public void testEndOfSegment() {
		SegmentParser p = new SegmentParser(new Segment("hello", "", 0, 0));
		p.moveTo(p.getSegment().length());
		assertThrowsParseError(() -> p.whitespace("test"), 5, "test");
		assertThrowsParseError(() -> p.nonwhitespace("test"), 5, "test");
		assertThrowsParseError(() -> p.bigDecimal("test"), 5, "test");
		assertThrowsParseError(() -> p.character('c', "test"), 5, "test");
	}

	@Test
	public void testNonwhitespace() throws SegmentParseException {
		SegmentParser p = parser("hello  ");
		p.nonwhitespace("test");
		Assert.assertEquals(5, p.getIndex());
		SegmentParser p2 = parser("   hello");
		assertThrowsParseError(() -> p2.nonwhitespace("test"), 0, "test");
	}

	@Test
	public void testWhitespace() throws SegmentParseException {
		SegmentParser p = parser("   hello");
		p.whitespace("test");
		Assert.assertEquals(3, p.getIndex());
		SegmentParser p2 = parser("hello");
		assertThrowsParseError(() -> p2.whitespace("test"), 0, "test");
	}
}
