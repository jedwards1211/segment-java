package org.andork.segment;

public class SegmentParseException extends Exception {
	private static final long serialVersionUID = 4236010521090319431L;

	private final Segment segment;

	public SegmentParseException(String message, Segment segment) {
		super(message);
		this.segment = segment;
	}

	public SegmentParseException(String message, Throwable cause, Segment segment) {
		super(message, cause);
		this.segment = segment;
	}

	public Segment getSegment() {
		return segment;
	}

	@Override
	public String toString() {
		return getLocalizedMessage() +
				" (in " + segment.source + ", line " + (segment.startLine + 1) +
				", column " + (segment.startCol + 1) + "):\n" +
				segment.underlineInContext();
	}
}
