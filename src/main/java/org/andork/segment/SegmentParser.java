package org.andork.segment;

import java.math.BigDecimal;
import java.util.function.Function;
import java.util.regex.Pattern;

public class SegmentParser {
	private static final Pattern WHITESPACE = Pattern.compile("\\s+");
	private static final Pattern NONWHITESPACE = Pattern.compile("\\S+");
	private static final Pattern BIG_DECIMAL_STRING = Pattern.compile("[-+]?(\\d+(\\.\\d*)?|\\.\\d+)([eE][+-]?\\d+)?");
	public static Function<SegmentParser, String> missingOrInvalid(String what) {
		return p -> (p.atEnd() ? "missing " : "invalid ") + what;
	}

	private final Segment segment;

	public int index = 0;

	public SegmentParser(Segment segment) {
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

	public BigDecimal bigDecimal() throws SegmentParseException {
		return bigDecimal("invalid number");
	}

	public BigDecimal bigDecimal(Function<SegmentParser, String> errorMessage) throws SegmentParseException {
		Segment segment = match(BIG_DECIMAL_STRING, errorMessage);
		try {
			return new BigDecimal(segment.toString());
		} catch (Exception ex) {
			throw new SegmentParseException(errorMessage.apply(this), ex, segment);
		}
	}

	public BigDecimal bigDecimal(String errorMessage) throws SegmentParseException {
		return bigDecimal(p -> errorMessage);
	}

	public SegmentParser character(char c) throws SegmentParseException {
		return character(c, "expected '" + c + "'");
	}

	public SegmentParser character(char c, Function<SegmentParser, String> errorMessage) throws SegmentParseException {
		if (index >= segment.length() || segment.charAt(index) != c) {
			throw new SegmentParseException(errorMessage.apply(this),
					segment.charAtAsSegment(index));
		}
		index++;
		return this;
	}

	public SegmentParser character(char c, String errorMessage) throws SegmentParseException {
		return character(c, p -> errorMessage);
	}

	public char character(Function<SegmentParser, String> errorMessage) throws SegmentParseException {
		if (index >= segment.length()) {
			throw new SegmentParseException(errorMessage.apply(this),
					segment.charAtAsSegment(index));
		}
		return segment.charAt(index++);
	}

	public char character(String errorMessage) throws SegmentParseException {
		return character(p -> errorMessage);
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

	public Segment match(Pattern p, Function<SegmentParser, String> errorMessage) throws SegmentParseException {
		SegmentMatcher m = new SegmentMatcher(segment, p);
		m.region(index, segment.length());
		if (!m.find() || m.start() > index) {
			throw new SegmentParseException(errorMessage.apply(this), segment.charAtAsSegment(index));
		}
		index = m.end();
		return m.group();
	}

	public Segment match(Pattern p, String errorMessage) throws SegmentParseException {
		return match(p, p2 -> errorMessage);
	}

	public Segment match(String pattern, Function<SegmentParser, String> errorMessage) throws SegmentParseException {
		return match(Pattern.compile(pattern), errorMessage);
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

	public Segment nonwhitespace() throws SegmentParseException {
		return nonwhitespace("expected non-whitespace");
	}

	public Segment nonwhitespace(Function<SegmentParser, String> errorMessage) throws SegmentParseException {
		return match(NONWHITESPACE, errorMessage);
	}

	public Segment nonwhitespace(String errorMessage) throws SegmentParseException {
		return nonwhitespace(p -> errorMessage);
	}

	public Segment rest() {
		Segment result = segment.substring(index);
		index = segment.length();
		return result;
	}

	public void throwException(String message) throws SegmentParseException {
		throw new SegmentParseException(message, segment.charAtAsSegment(index));
	}

	public SegmentParser whitespace() throws SegmentParseException {
		return whitespace("expected whitespace");
	}

	public SegmentParser whitespace(String errorMessage) throws SegmentParseException {
		match(WHITESPACE, errorMessage);
		return this;
	}
}
