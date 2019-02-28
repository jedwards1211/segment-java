package org.andork.segment;

import static org.junit.Assert.assertEquals;

import java.util.regex.Pattern;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;

public class SegmentTests {
	@Test
	public void testSubstring() {
		Segment source = new Segment("foo bar baz\r\n qux\nthis is a\ntest", "foo.txt", 5, 3);
		
		Segment s;
		s = source.substring(13, 28);
		assertEquals(source.toString().substring(13, 28), s.toString());
		assertEquals(13, s.sourceIndex);
		assertEquals(6, s.startLine);
		assertEquals(0, s.startCol);
		assertEquals(7, s.endLine);
		assertEquals("this is a".length(), s.endCol);
		
		s = source.substring(13, 29);
		assertEquals(source.toString().substring(13, 29), s.toString());
		assertEquals(13, s.sourceIndex);
		assertEquals(6, s.startLine);
		assertEquals(0, s.startCol);
		assertEquals(8, s.endLine);
		assertEquals(0, s.endCol);

		s = source.substring(13, 32);
		assertEquals(source.toString().substring(13, 32), s.toString());
		assertEquals(13, s.sourceIndex);
		assertEquals(6, s.startLine);
		assertEquals(0, s.startCol);
		assertEquals(8, s.endLine);
		assertEquals(3, s.endCol);

		s = source.substring(source.length());
		assertEquals("", s.toString());
		assertEquals(source.length(), s.sourceIndex);
		assertEquals(source.endLine, s.startLine);
		assertEquals(source.endCol + 1, s.startCol);
		assertEquals(source.endLine, s.endLine);
		assertEquals(source.endCol, s.endCol);
	}
	
	@Test
	public void testSplit() {
		Segment source = new Segment("foo bar baz\r\n qux\nthis is a\ntest", "foo.txt", 5, 3);

		Segment[] parts;
		parts = source.split("a");
		assertArrayEquals(parts, new Segment[] {
			source.substring(0, 5),
			source.substring(6, 9),
			source.substring(10, 26),
			source.substring(27)
		});
		

		parts = source.split("a", 2);
		assertArrayEquals(parts, new Segment[] {
			source.substring(0, 5),
			source.substring(6)
		});
	}
}
