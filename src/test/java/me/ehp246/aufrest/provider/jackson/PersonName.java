package me.ehp246.aufrest.provider.jackson;

/**
 * @author Lei Yang
 *
 */
class PersonName {
    private final String firstName;
    private final String LastName;

    PersonName(final String firstName, final String lastName) {
        super();
        this.firstName = firstName;
        LastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return LastName;
    }
}
