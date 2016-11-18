package org.andork.segment;

import java.math.BigDecimal;
import java.util.regex.Pattern;

public class SegmentParser {
	private static final Pattern WHITESPACE = Pattern.compile("\\s+");
	private static final Pattern NONWHITESPACE = Pattern.compile("\\S+");
	private static final Pattern BIG_DECIMAL_STRING = Pattern.compile("[-+]?(\\d+(\\.\\d*)?|\\.\\d+)([eE][+-]?\\d+)?");

	private final Segment segment;
	public int index = 0;

	public SegmentParser(Segment segment) {
		super();
		this.segment = segment;
	}

	public SegmentParser advanceToWhitespace() {
		while (index < segment.length() && !Character.isWhitespace(segment.charAt(index))) {
			index++;
		}
		return this;
	}

	public boolean atEnd() {
		return index >= segment.length();
	}

	public BigDecimal bigDecimal(String errorMessage) throws SegmentParseException {
		Segment segment = match(BIG_DECIMAL_STRING, errorMessage);
		try {
			return new BigDecimal(segment.toString());
		} catch (Exception ex) {
			throw new SegmentParseException(errorMessage, segment);
		}
	}

	public SegmentParser character(char c, String errorMessage) throws SegmentParseException {
		if (index >= segment.length() || segment.charAt(index) != c) {
			throw new SegmentParseException(errorMessage,
					segment.charAtAsSegment(index));
		}
		index++;
		return this;
	}

	public char character(String errorMessage) throws SegmentParseException {
		if (index >= segment.length()) {
			throw new SegmentParseException(errorMessage,
					segment.charAtAsSegment(index));
		}
		return segment.charAt(index++);
	}

	public char charAtIndex() {
		return segment.charAt(index);
	}

	public int getIndex() {
		return index;
	}

	public Segment getSegment() {
		return segment;
	}

	public Segment match(Pattern p, String errorMessage) throws SegmentParseException {
		SegmentMatcher m = new SegmentMatcher(segment, p);
		m.region(index, segment.length());
		if (!m.find() || m.start() > index) {
			throw new SegmentParseException(errorMessage, segment.charAtAsSegment(index));
		}
		index = m.end();
		return m.group();
	}

	public Segment match(String pattern, String errorMessage) throws SegmentParseException {
		return match(Pattern.compile(pattern), errorMessage);
	}

	public SegmentParser move(int amount) {
		index += amount;
		return this;
	}

	public SegmentParser moveTo(int index) {
		this.index = index;
		return this;
	}

	public Segment nonwhitespace(String errorMessage) throws SegmentParseException {
		return match(NONWHITESPACE, errorMessage);
	}

	public void throwError(String message) throws SegmentParseException {
		throw new SegmentParseException(message, segment.charAtAsSegment(index));
	}

	public SegmentParser whitespace(String errorMessage) throws SegmentParseException {
		match(WHITESPACE, errorMessage);
		return this;
	}
}
