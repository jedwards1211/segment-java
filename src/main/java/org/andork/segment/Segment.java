package org.andork.segment;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A {@link String} wrapper that tracks its location in a source file. Even
 * taking a substring, trimming, splitting, etc. return Segments with correct
 * location information. Line numbers and column numbers should start with 0 --
 * otherwise inconsistent numbering may result.
 *
 * @author Andy Edwards
 */
public class Segment implements CharSequence {
	private static final Pattern LINE_BREAK = Pattern.compile("\r\n|\r|\n");

	private final String value;
	public final Object source;
	public final Integer sourceIndex;
	public final Segment sourceSegment;
	public final int startLine;
	public final int endLine;
	public final int startCol;
	/**
	 * The column of the last character in this Segment. If the segment is empty
	 * this will be one less than {@link #startCol} (and possibly even
	 * negative).
	 */
	public final int endCol;

	protected Segment(Segment sourceSegment, Integer sourceIndex, String value, Object source, int startLine,
			int startCol) {
		super();
		this.sourceSegment = sourceSegment;
		this.sourceIndex = sourceIndex;
		this.value = value;
		this.source = source;
		this.startLine = startLine;
		this.startCol = startCol;
		int endLine = startLine;
		int endCol = startCol + value.length() - 1;
		Matcher m = LINE_BREAK.matcher(value);
		while (m.find() && m.end() < value.length()) {
			endLine++;
			endCol = value.length() - m.end() - 1;
		}
		this.endLine = endLine;
		this.endCol = endCol;
	}

	protected Segment(Segment sourceSegment, Integer sourceIndex, String value, Object source, int startLine,
			int startCol,
			int endLine, int endCol) {
		this.sourceSegment = sourceSegment;
		this.sourceIndex = sourceIndex;
		this.value = value;
		this.source = source;
		this.startLine = startLine;
		this.startCol = startCol;
		this.endLine = endLine;
		this.endCol = endCol;
	}

	public Segment(String value, Object source, int startLine, int startCol) {
		this(null, null, value, source, startLine, startCol);
	}

	public Segment charAfter() {
		return sourceIndex == null || sourceIndex + length() >= sourceSegment.length() ? substring(length())
				: sourceSegment.substring(sourceIndex + length(), sourceIndex + length() + 1);
	}

	@Override
	public char charAt(int index) {
		return value.charAt(index);
	}

	/**
	 * @param index
	 *            the index, from 0 to {@code length()} <b>(inclusive)</b>.
	 * @return a {@code Segment} representing the character at {@code index}, or
	 *         a {@code Segment} representing the empty string at the end of
	 *         this {@code Segment} if {@code index == this.length()}.
	 */
	public Segment charAtAsSegment(int index) {
		return substring(index, Math.min(index + 1, value.length()));
	}

	public Segment charBefore() {
		return sourceIndex == null || sourceIndex == 0 ? substring(0, 0)
				: sourceSegment.substring(sourceIndex - 1, sourceIndex);
	}

	public int codePointAt(int index) {
		return value.codePointAt(index);
	}

	public int codePointBefore(int index) {
		return value.codePointBefore(index);
	}

	public int codePointCount(int beginIndex, int endIndex) {
		return value.codePointCount(beginIndex, endIndex);
	}

	public int compareTo(String anotherString) {
		return value.compareTo(anotherString);
	}

	public int compareToIgnoreCase(String str) {
		return value.compareToIgnoreCase(str);
	}

	public boolean contains(CharSequence s) {
		return value.contains(s);
	}

	public boolean contentEquals(CharSequence cs) {
		return value.contentEquals(cs);
	}

	public boolean contentEquals(StringBuffer sb) {
		return value.contentEquals(sb);
	}

	public boolean endsWith(String suffix) {
		return value.endsWith(suffix);
	}

	@Override
	public boolean equals(Object anObject) {
		if (anObject instanceof Segment) {
			return value.equals(((Segment) anObject).value);
		}
		return value.equals(anObject);
	}

	public boolean equalsIgnoreCase(String anotherString) {
		return value.equalsIgnoreCase(anotherString);
	}

	public byte[] getBytes() {
		return value.getBytes();
	}

	public byte[] getBytes(Charset charset) {
		return value.getBytes(charset);
	}

	@SuppressWarnings("deprecation")
	public void getBytes(int srcBegin, int srcEnd, byte[] dst, int dstBegin) {
		value.getBytes(srcBegin, srcEnd, dst, dstBegin);
	}

	public byte[] getBytes(String charsetName) throws UnsupportedEncodingException {
		return value.getBytes(charsetName);
	}

	public void getChars(int srcBegin, int srcEnd, char[] dst, int dstBegin) {
		value.getChars(srcBegin, srcEnd, dst, dstBegin);
	}

	@Override
	public int hashCode() {
		return value.hashCode();
	}

	public int indexOf(int ch) {
		return value.indexOf(ch);
	}

	public int indexOf(int ch, int fromIndex) {
		return value.indexOf(ch, fromIndex);
	}

	public int indexOf(String str) {
		return value.indexOf(str);
	}

	public int indexOf(String str, int fromIndex) {
		return value.indexOf(str, fromIndex);
	}

	public boolean isEmpty() {
		return value.isEmpty();
	}

	public int lastIndexOf(int ch) {
		return value.lastIndexOf(ch);
	}

	public int lastIndexOf(int ch, int fromIndex) {
		return value.lastIndexOf(ch, fromIndex);
	}

	public int lastIndexOf(String str) {
		return value.lastIndexOf(str);
	}

	public int lastIndexOf(String str, int fromIndex) {
		return value.lastIndexOf(str, fromIndex);
	}

	@Override
	public int length() {
		return value.length();
	}

	public boolean matches(String regex) {
		return value.matches(regex);
	}

	public int offsetByCodePoints(int index, int codePointOffset) {
		return value.offsetByCodePoints(index, codePointOffset);
	}

	public boolean regionMatches(boolean ignoreCase, int toffset, String other, int ooffset, int len) {
		return value.regionMatches(ignoreCase, toffset, other, ooffset, len);
	}

	public boolean regionMatches(int toffset, String other, int ooffset, int len) {
		return value.regionMatches(toffset, other, ooffset, len);
	}

	public Segment[] split(Pattern p) {
		return split(p, -1);
	}

	public Segment[] split(Pattern p, int limit) {
		int index = 0;
		boolean matchLimited = limit > 0;
		ArrayList<Segment> matchList = new ArrayList<>();
		Matcher m = p.matcher(value);

		Segment lastMatch = null;
		// Add segments before each match found
		while (m.find()) {
			if (!matchLimited || matchList.size() < limit - 1) {
				if (index == 0 && index == m.start() && m.start() == m.end()) {
					// no empty leading substring included for zero-width match
					// at the beginning of the input char sequence.
					continue;
				}
				Segment match = substring(lastMatch, index, m.start());
				lastMatch = match;
				matchList.add(match);
				index = m.end();
			} else if (matchList.size() == limit - 1) { // last one
				Segment match = substring(lastMatch, index, value.length());
				lastMatch = match;
				matchList.add(match);
				index = m.end();
			}
		}

		// If no match was found, return this
		if (index == 0) {
			return new Segment[] { this };
		}

		// Add remaining segment
		if (!matchLimited || matchList.size() < limit) {
			Segment match = substring(lastMatch, index, value.length());
			matchList.add(match);
		}

		// Construct result
		int resultSize = matchList.size();
		if (limit == 0) {
			while (resultSize > 0 && matchList.get(resultSize - 1).equals("")) {
				resultSize--;
			}
		}
		Segment[] result = new Segment[resultSize];
		return matchList.subList(0, resultSize).toArray(result);
	}

	public Segment[] split(String regex) {
		return split(regex, 0);
	}

	public Segment[] split(String regex, int limit) {
		/*
		 * fastpath if the regex is a (1)one-char String and this character is
		 * not one of the RegEx's meta characters ".$|()[{^?*+\\", or
		 * (2)two-char String and the first char is the backslash and the second
		 * is not the ascii digit or ascii letter.
		 */
		char ch = 0;
		if ((regex.length() == 1 &&
				".$|()[{^?*+\\".indexOf(ch = regex.charAt(0)) == -1 ||
				regex.length() == 2 &&
						regex.charAt(0) == '\\' &&
						((ch = regex.charAt(1)) - '0' | '9' - ch) < 0 &&
						(ch - 'a' | 'z' - ch) < 0 &&
						(ch - 'A' | 'Z' - ch) < 0)
				&&
				(ch < Character.MIN_HIGH_SURROGATE ||
						ch > Character.MAX_LOW_SURROGATE)) {
			int off = 0;
			int next = 0;
			boolean limited = limit > 0;
			ArrayList<Segment> list = new ArrayList<>();
			while ((next = indexOf(ch, off)) != -1) {
				if (!limited || list.size() < limit - 1) {
					list.add(substring(off, next));
					off = next + 1;
				} else { // last one
							// assert (list.size() == limit - 1);
					list.add(substring(off, value.length()));
					off = value.length();
					break;
				}
			}
			// If no match was found, return this
			if (off == 0) {
				return new Segment[] { this };
			}

			// Add remaining segment
			if (!limited || list.size() < limit) {
				list.add(substring(off, value.length()));
			}

			// Construct result
			int resultSize = list.size();
			if (limit == 0) {
				while (resultSize > 0 && list.get(resultSize - 1).length() == 0) {
					resultSize--;
				}
			}
			Segment[] result = new Segment[resultSize];
			return list.subList(0, resultSize).toArray(result);
		}
		return split(Pattern.compile(regex), limit);
	}

	public boolean startsWith(String prefix) {
		return value.startsWith(prefix);
	}

	public boolean startsWith(String prefix, int toffset) {
		return value.startsWith(prefix, toffset);
	}

	@Override
	public CharSequence subSequence(int beginIndex, int endIndex) {
		return substring(beginIndex, endIndex);
	}

	public Segment substring(int beginIndex) {
		return substring(beginIndex, value.length());
	}

	public Segment substring(int beginIndex, int endIndex) {
		if (startLine == endLine) {
			return new Segment(sourceSegment != null ? sourceSegment : this,
					sourceIndex != null ? sourceIndex + beginIndex : beginIndex,
					value.substring(beginIndex, endIndex), source, startLine,
					startCol + beginIndex, startLine, startCol + endIndex - 1);
		}

		int newStartLine = startLine;
		int newStartCol = startCol + beginIndex;

		int toIndex = beginIndex;
		if (toIndex < value.length() && toIndex > 0 && value.charAt(toIndex) == '\n'
				&& value.charAt(toIndex - 1) == '\r') {
			toIndex--;
		}

		Matcher m = LINE_BREAK.matcher(value).region(0, toIndex);

		while (m.find()) {
			newStartLine++;
			newStartCol = beginIndex - m.end();
		}

		return new Segment(sourceSegment != null ? sourceSegment : this,
				sourceIndex != null ? sourceIndex + beginIndex : beginIndex,
				value.substring(beginIndex, endIndex), source, newStartLine, newStartCol);
	}

	Segment substring(Segment beforeSegment, int startIndex, int endIndex) {
		if (beforeSegment == null) {
			return substring(startIndex, endIndex);
		}
		int thisSourceIndex = sourceIndex != null ? sourceIndex : 0;
		int sourceIndex = thisSourceIndex + startIndex;
		int startLine = beforeSegment.startLine;
		int startCol = beforeSegment.startCol;
		Matcher m = LINE_BREAK.matcher(value);
		m.region(beforeSegment.sourceIndex - thisSourceIndex, startIndex);
		while (m.find()) {
			startLine++;
			startCol = startIndex - m.end();
		}

		return new Segment(sourceSegment != null ? sourceSegment : this, sourceIndex,
				value.substring(startIndex, endIndex),
				source, startLine, startCol);
	}

	public char[] toCharArray() {
		return value.toCharArray();
	}

	@Override
	public String toString() {
		return value.toString();
	}

	public Segment trim() {
		int len = value.length();
		int st = 0;

		while (st < len && value.charAt(st) <= ' ') {
			st++;
		}
		while (st < len && value.charAt(len - 1) <= ' ') {
			len--;
		}
		return st > 0 || len < value.length() ? substring(st, len) : this;
	}

	/**
	 * @return the lines of {@link #sourceSegment} this {@code Segment} occurs
	 *         in, interspersed with lines of {@code ^^^} underlining the region
	 *         it covers
	 */
	public String underlineInContext() {
		StringBuilder sb = new StringBuilder();
		Segment[] lines = sourceSegment == null ? this.split(LINE_BREAK) : sourceSegment.split("\r\n|\r|\n");

		for (Segment line : lines) {
			if (line.startLine < startLine || line.startLine > endLine) {
				continue;
			}
			sb.append(line).append(System.lineSeparator());
			int k = 0;
			if (startLine == line.startLine) {
				while (k < startCol) {
					sb.append(' ');
					k++;
				}
			}
			if (line.startLine < endLine) {
				while (k < line.length()) {
					sb.append('^');
					k++;
				}
			} else if (endLine == line.startLine) {
				if (endCol < startCol) {
					sb.append('^');
				} else {
					while (k <= endCol) {
						sb.append('^');
						k++;
					}
				}
			}
			if (line.startLine < endLine) {
				sb.append(System.lineSeparator());
			}
		}
		return sb.toString();
	}
}
