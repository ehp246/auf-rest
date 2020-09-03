package me.ehp246.aufrest.provider.jackson;

import java.time.Instant;

/**
 * @author Lei Yang
 *
 */
class Person {
	private final Instant dob;
	private final String firstName;
	private final String LastName;

	public Person(final Instant dob, final String firstName, final String lastName) {
		super();
		this.dob = dob;
		this.firstName = firstName;
		LastName = lastName;
	}

	/**
	 * @return the dob
	 */
	public Instant getDob() {
		return dob;
	}

	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return LastName;
	}
}
